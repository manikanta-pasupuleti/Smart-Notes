package com.smartnotes.repository;

import com.smartnotes.model.Note;
import com.smartnotes.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // Active notes only
    List<Note> findByUserAndDeletedFalse(User user);

    // Trashed notes
    List<Note> findByUserAndDeletedTrue(User user);

    // Note by owner
    Optional<Note> findByIdAndUser(Long id, User user);

    // Search active notes
    @Query("SELECT n FROM Note n WHERE n.deleted = false AND " +
           "n.user = :user AND " +
           "(LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Note> searchByKeywordAndUser(@Param("keyword") String keyword, @Param("user") User user);

    // Filter active notes by category
    List<Note> findByUserAndCategoryIgnoreCaseAndDeletedFalse(User user, String category);
}
