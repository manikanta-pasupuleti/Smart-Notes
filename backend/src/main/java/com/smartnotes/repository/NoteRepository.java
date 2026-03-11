package com.smartnotes.repository;

import com.smartnotes.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // Active notes only
    List<Note> findByDeletedFalse();

    // Trashed notes
    List<Note> findByDeletedTrue();

    // Search active notes
    @Query("SELECT n FROM Note n WHERE n.deleted = false AND " +
           "(LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Note> searchByKeyword(@Param("keyword") String keyword);

    // Filter active notes by category
    List<Note> findByCategoryIgnoreCaseAndDeletedFalse(String category);
}
