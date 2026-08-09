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
    public ResponseEntity<Note> createNote(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Valid @RequestBody Note note) {
        return new ResponseEntity<>(noteService.createNote(authorization, note), HttpStatus.CREATED);
    }

    // READ ALL (active, with sort)
    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestParam(defaultValue = "newest") String sort) {
        return ResponseEntity.ok(noteService.getAllNotes(authorization, sort));
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @PathVariable Long id) {
        return ResponseEntity.ok(noteService.getNoteById(id, authorization));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id,
                                           @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                           @Valid @RequestBody Note note) {
        return ResponseEntity.ok(noteService.updateNote(id, authorization, note));
    }

    // SOFT DELETE (move to trash)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id,
                                           @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        noteService.deleteNote(id, authorization);
        return ResponseEntity.noContent().build();
    }

    // RESTORE from trash
    @PatchMapping("/{id}/restore")
    public ResponseEntity<Note> restoreNote(@PathVariable Long id,
                                            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        return ResponseEntity.ok(noteService.restoreNote(id, authorization));
    }

    // PERMANENT DELETE
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> permanentDelete(@PathVariable Long id,
                                                @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        noteService.permanentDelete(id, authorization);
        return ResponseEntity.noContent().build();
    }

    // GET TRASH
    @GetMapping("/trash")
    public ResponseEntity<List<Note>> getTrash(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        return ResponseEntity.ok(noteService.getTrash(authorization));
    }

    // DUPLICATE
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<Note> duplicateNote(@PathVariable Long id,
                                              @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        return new ResponseEntity<>(noteService.duplicateNote(id, authorization), HttpStatus.CREATED);
    }

    // TOGGLE PIN
    @PatchMapping("/{id}/pin")
    public ResponseEntity<Note> togglePin(@PathVariable Long id,
                                          @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        return ResponseEntity.ok(noteService.togglePin(id, authorization));
    }

    // STATS
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        return ResponseEntity.ok(noteService.getStats(authorization));
    }

    // EXPORT as downloadable JSON
    @GetMapping("/export")
    public ResponseEntity<List<Note>> exportNotes(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        List<Note> notes = noteService.getAllNotes(authorization, "newest");
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"smart-notes-export.json\"")
            .contentType(MediaType.APPLICATION_JSON)
            .body(notes);
    }

    // SEARCH
    @GetMapping("/search")
    public ResponseEntity<List<Note>> searchNotes(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestParam String keyword) {
        return ResponseEntity.ok(noteService.searchNotes(authorization, keyword));
    }

    // FILTER BY CATEGORY
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Note>> getNotesByCategory(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @PathVariable String category) {
        return ResponseEntity.ok(noteService.getNotesByCategory(authorization, category));
    }
}
