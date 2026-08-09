package com.smartnotes.service;

import com.smartnotes.model.Note;
import com.smartnotes.model.User;
import com.smartnotes.repository.NoteRepository;
import com.smartnotes.repository.UserRepository;
import com.smartnotes.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceOwnershipTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private UserRepository userRepository;

    private NoteService noteService;

    private final JwtTokenProvider jwtTokenProvider = new FixedJwtTokenProvider();

    private User authenticatedUser;

    @BeforeEach
    void setUp() {
        noteService = new NoteService();
        ReflectionTestUtils.setField(noteService, "noteRepository", noteRepository);
        ReflectionTestUtils.setField(noteService, "userRepository", userRepository);
        ReflectionTestUtils.setField(noteService, "jwtTokenProvider", jwtTokenProvider);

        authenticatedUser = new User();
        authenticatedUser.setId(1L);
        authenticatedUser.setUsername("user-a");
        authenticatedUser.setEmail("user-a@example.com");
        authenticatedUser.setPassword("hash");

        when(userRepository.findByUsername("user-a")).thenReturn(Optional.of(authenticatedUser));
    }

    @Test
    void createNoteAssignsAuthenticatedUser() {
        Note note = new Note("Title", "Content", "General");

        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Note saved = noteService.createNote("Bearer token-a", note);

        assertEquals(authenticatedUser, saved.getUser());
        verify(noteRepository).save(note);
    }

    @Test
    void getNoteByIdRejectsNotesOwnedByAnotherUser() {
        when(noteRepository.findByIdAndUser(99L, authenticatedUser)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> noteService.getNoteById(99L, "Bearer token-a"));
    }

    @Test
    void searchNotesIsScopedToAuthenticatedUser() {
        Note note = new Note("Private", "Hello world", "General");
        note.setUser(authenticatedUser);

        when(noteRepository.searchByKeywordAndUser(eq("Hello"), eq(authenticatedUser)))
                .thenReturn(List.of(note));

        List<Note> results = noteService.searchNotes("Bearer token-a", "Hello");

        assertEquals(1, results.size());
        assertEquals(authenticatedUser, results.get(0).getUser());
    }

    private static class FixedJwtTokenProvider extends JwtTokenProvider {
        @Override
        public boolean validateToken(String token) {
            return "token-a".equals(token);
        }

        @Override
        public String getUsernameFromToken(String token) {
            return "token-a".equals(token) ? "user-a" : null;
        }
    }
}
