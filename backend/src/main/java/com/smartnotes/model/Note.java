package com.smartnotes.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private String category;

    @Column(length = 20)
    private String color = "default";

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean pinned = false;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean deleted = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Note() {}

    public Note(String title, String content, String category) {
        this.title    = title;
        this.content  = content;
        this.category = category;
    }

    // Getters & Setters
    public Long getId()                       { return id; }
    public void setId(Long id)                { this.id = id; }

    public String getTitle()                  { return title; }
    public void setTitle(String t)            { this.title = t; }

    public String getContent()                { return content; }
    public void setContent(String c)          { this.content = c; }

    public String getCategory()               { return category; }
    public void setCategory(String c)         { this.category = c; }

    public String getColor()                  { return color; }
    public void setColor(String c)            { this.color = c; }

    public boolean isPinned()                 { return pinned; }
    public void setPinned(boolean p)          { this.pinned = p; }

    public boolean isDeleted()                { return deleted; }
    public void setDeleted(boolean d)         { this.deleted = d; }

    public LocalDateTime getCreatedAt()       { return createdAt; }
    public void setCreatedAt(LocalDateTime t) { this.createdAt = t; }

    public LocalDateTime getUpdatedAt()       { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t) { this.updatedAt = t; }
}
