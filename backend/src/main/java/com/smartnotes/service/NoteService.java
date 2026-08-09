package com.smartnotes.service;

import com.smartnotes.model.Note;
import com.smartnotes.model.User;
import com.smartnotes.repository.NoteRepository;
import com.smartnotes.repository.UserRepository;
import com.smartnotes.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // CREATE
    public Note createNote(String authorizationHeader, Note note) {
        User authenticatedUser = getAuthenticatedUser(authorizationHeader);
        note.setUser(authenticatedUser);
        return noteRepository.save(note);
    }

    // READ ALL ACTIVE (with sort)
    public List<Note> getAllNotes(String authorizationHeader, String sort) {
        User authenticatedUser = getAuthenticatedUser(authorizationHeader);
        List<Note> all = noteRepository.findByUserAndDeletedFalse(authenticatedUser);
        sortList(all, sort);
        all.sort(Comparator.comparing(Note::isPinned).reversed());
        return all;
    }

    // READ BY ID
    public Note getNoteById(Long id, String authorizationHeader) {
        User authenticatedUser = getAuthenticatedUser(authorizationHeader);
        return getOwnedNoteOrNotFound(id, authenticatedUser);
    }

    // UPDATE
    public Note updateNote(Long id, String authorizationHeader, Note updatedNote) {
        User authenticatedUser = getAuthenticatedUser(authorizationHeader);
        Note existing = getOwnedNoteOrNotFound(id, authenticatedUser);
        existing.setTitle(updatedNote.getTitle());
        existing.setContent(updatedNote.getContent());
        existing.setCategory(updatedNote.getCategory());
        existing.setColor(updatedNote.getColor() != null ? updatedNote.getColor() : "default");
        return noteRepository.save(existing);
    }

    // SOFT DELETE (move to trash)
    public void deleteNote(Long id, String authorizationHeader) {
        User authenticatedUser = getAuthenticatedUser(authorizationHeader);
        Note note = getOwnedNoteOrNotFound(id, authenticatedUser);
        note.setDeleted(true);
        noteRepository.save(note);
    }

    // RESTORE from trash
    public Note restoreNote(Long id, String authorizationHeader) {
        User authenticatedUser = getAuthenticatedUser(authorizationHeader);
        Note note = getOwnedNoteOrNotFound(id, authenticatedUser);
        note.setDeleted(false);
        return noteRepository.save(note);
    }

    // PERMANENT DELETE
    public void permanentDelete(Long id, String authorizationHeader) {
        User authenticatedUser = getAuthenticatedUser(authorizationHeader);
        Note note = getOwnedNoteOrNotFound(id, authenticatedUser);
        noteRepository.delete(note);
    }

    // GET TRASH
    public List<Note> getTrash(String authorizationHeader) {
        User authenticatedUser = getAuthenticatedUser(authorizationHeader);
        List<Note> trashed = noteRepository.findByUserAndDeletedTrue(authenticatedUser);
        trashed.sort(Comparator.comparing(Note::getUpdatedAt).reversed());
        return trashed;
    }

    // DUPLICATE
    public Note duplicateNote(Long id, String authorizationHeader) {
        User authenticatedUser = getAuthenticatedUser(authorizationHeader);
        Note original = getOwnedNoteOrNotFound(id, authenticatedUser);
        Note copy = new Note("Copy of " + original.getTitle(),
                             original.getContent(), original.getCategory());
        copy.setColor(original.getColor() != null ? original.getColor() : "default");
        copy.setUser(authenticatedUser);
        return noteRepository.save(copy);
    }

    // TOGGLE PIN
    public Note togglePin(Long id, String authorizationHeader) {
        User authenticatedUser = getAuthenticatedUser(authorizationHeader);
        Note note = getOwnedNoteOrNotFound(id, authenticatedUser);
        note.setPinned(!note.isPinned());
        return noteRepository.save(note);
    }

    // STATS
    public Map<String, Object> getStats(String authorizationHeader) {
        User authenticatedUser = getAuthenticatedUser(authorizationHeader);
        List<Note> active = noteRepository.findByUserAndDeletedFalse(authenticatedUser);
        long pinned  = active.stream().filter(Note::isPinned).count();
        long trashed = noteRepository.findByUserAndDeletedTrue(authenticatedUser).size();

        Map<String, Long> byCategory = active.stream()
            .collect(Collectors.groupingBy(
                n -> n.getCategory() != null ? n.getCategory() : "General",
                Collectors.counting()
            ));

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total",      active.size());
        stats.put("pinned",     pinned);
        stats.put("trashed",    trashed);
        stats.put("byCategory", byCategory);
        return stats;
    }

    // SEARCH
    public List<Note> searchNotes(String authorizationHeader, String keyword) {
        User authenticatedUser = getAuthenticatedUser(authorizationHeader);
        return noteRepository.searchByKeywordAndUser(keyword, authenticatedUser);
    }

    // FILTER BY CATEGORY
    public List<Note> getNotesByCategory(String authorizationHeader, String category) {
        User authenticatedUser = getAuthenticatedUser(authorizationHeader);
        return noteRepository.findByUserAndCategoryIgnoreCaseAndDeletedFalse(authenticatedUser, category);
    }

    private User getAuthenticatedUser(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authorization token");
        }

        String token = authorizationHeader.substring(7).trim();
        if (token.isEmpty() || !jwtTokenProvider.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authorization token");
        }

        String username = jwtTokenProvider.getUsernameFromToken(token);
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authorization token");
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
    }

    private Note getOwnedNoteOrNotFound(Long id, User authenticatedUser) {
        return noteRepository.findByIdAndUser(id, authenticatedUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
    }

    // Helper: sort list in place
    private void sortList(List<Note> list, String sort) {
        switch (sort) {
            case "oldest" -> list.sort(Comparator.comparing(Note::getCreatedAt));
            case "title"  -> list.sort(Comparator.comparing(Note::getTitle, String.CASE_INSENSITIVE_ORDER));
            default       -> list.sort(Comparator.comparing(Note::getCreatedAt).reversed());
        }
    }
}
