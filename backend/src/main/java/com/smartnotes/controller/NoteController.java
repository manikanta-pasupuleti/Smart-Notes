package com.smartnotes.controller;

import com.smartnotes.model.Note;
import com.smartnotes.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {

    @Autowired
    private NoteService noteService;

    // CREATE
    @PostMapping
    public ResponseEntity<Note> createNote(@Valid @RequestBody Note note) {
        return new ResponseEntity<>(noteService.createNote(note), HttpStatus.CREATED);
    }

    // READ ALL (active, with sort)
    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes(
            @RequestParam(defaultValue = "newest") String sort) {
        return ResponseEntity.ok(noteService.getAllNotes(sort));
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
        Optional<Note> note = noteService.getNoteById(id);
        return note.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id,
                                           @Valid @RequestBody Note note) {
        try {
            return ResponseEntity.ok(noteService.updateNote(id, note));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // SOFT DELETE (move to trash)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        try {
            noteService.deleteNote(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // RESTORE from trash
    @PatchMapping("/{id}/restore")
    public ResponseEntity<Note> restoreNote(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(noteService.restoreNote(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PERMANENT DELETE
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> permanentDelete(@PathVariable Long id) {
        try {
            noteService.permanentDelete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET TRASH
    @GetMapping("/trash")
    public ResponseEntity<List<Note>> getTrash() {
        return ResponseEntity.ok(noteService.getTrash());
    }

    // DUPLICATE
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<Note> duplicateNote(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(noteService.duplicateNote(id), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // TOGGLE PIN
    @PatchMapping("/{id}/pin")
    public ResponseEntity<Note> togglePin(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(noteService.togglePin(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // STATS
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(noteService.getStats());
    }

    // EXPORT as downloadable JSON
    @GetMapping("/export")
    public ResponseEntity<List<Note>> exportNotes() {
        List<Note> notes = noteService.getAllNotes("newest");
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"smart-notes-export.json\"")
            .contentType(MediaType.APPLICATION_JSON)
            .body(notes);
    }

    // SEARCH
    @GetMapping("/search")
    public ResponseEntity<List<Note>> searchNotes(@RequestParam String keyword) {
        return ResponseEntity.ok(noteService.searchNotes(keyword));
    }

    // FILTER BY CATEGORY
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Note>> getNotesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(noteService.getNotesByCategory(category));
    }
}
