package entity;

import java.time.Instant;

public class Note {
    private final String noteId;
    private final Landmark landmark;
    private final String content;
    private final Instant createdAt;
    private Instant updatedAt;

    public Note(Landmark landmark, String content) {
        this.noteId = landmark.getLandmarkName() + Instant.now();
        this.landmark = landmark;
        this.content = content;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public Note(String noteId,
                Landmark landmark,
                String content,
                Instant createdAt,
                Instant updatedAt) {
        this.noteId = noteId;
        this.landmark = landmark;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getNoteId() {
        return noteId;
    }
    public Landmark getLandmark() {
        return landmark;
    }
    public String getContent() {
        return content;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
