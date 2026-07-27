package com.smartnotes.service;

import com.smartnotes.model.Note;
import com.smartnotes.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    // CREATE
    public Note createNote(Note note) {
        return noteRepository.save(note);
    }

    // READ ALL ACTIVE (with sort)
    public List<Note> getAllNotes(String sort) {
        List<Note> all = noteRepository.findByDeletedFalse();
        sortList(all, sort);
        all.sort(Comparator.comparing(Note::isPinned).reversed());
        return all;
    }

    // READ BY ID
    public Optional<Note> getNoteById(Long id) {
        return noteRepository.findById(id);
    }

    // UPDATE
    public Note updateNote(Long id, Note updatedNote) {
        Note existing = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found: " + id));
        existing.setTitle(updatedNote.getTitle());
        existing.setContent(updatedNote.getContent());
        existing.setCategory(updatedNote.getCategory());
        existing.setColor(updatedNote.getColor() != null ? updatedNote.getColor() : "default");
        return noteRepository.save(existing);
    }

    // SOFT DELETE (move to trash)
    public void deleteNote(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found: " + id));
        note.setDeleted(true);
        noteRepository.save(note);
    }

    // RESTORE from trash
    public Note restoreNote(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found: " + id));
        note.setDeleted(false);
        return noteRepository.save(note);
    }

    // PERMANENT DELETE
    public void permanentDelete(Long id) {
        if (!noteRepository.existsById(id))
            throw new RuntimeException("Note not found: " + id);
        noteRepository.deleteById(id);
    }

    // GET TRASH
    public List<Note> getTrash() {
        List<Note> trashed = noteRepository.findByDeletedTrue();
        trashed.sort(Comparator.comparing(Note::getUpdatedAt).reversed());
        return trashed;
    }

    // DUPLICATE
    public Note duplicateNote(Long id) {
        Note original = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found: " + id));
        Note copy = new Note("Copy of " + original.getTitle(),
                             original.getContent(), original.getCategory());
        copy.setColor(original.getColor() != null ? original.getColor() : "default");
        return noteRepository.save(copy);
    }

    // TOGGLE PIN
    public Note togglePin(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found: " + id));
        note.setPinned(!note.isPinned());
        return noteRepository.save(note);
    }

    // STATS
    public Map<String, Object> getStats() {
        List<Note> active = noteRepository.findByDeletedFalse();
        long pinned  = active.stream().filter(Note::isPinned).count();
        long trashed = noteRepository.findByDeletedTrue().size();

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
    public List<Note> searchNotes(String keyword) {
        return noteRepository.searchByKeyword(keyword);
    }

    // FILTER BY CATEGORY
    public List<Note> getNotesByCategory(String category) {
        return noteRepository.findByCategoryIgnoreCaseAndDeletedFalse(category);
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
