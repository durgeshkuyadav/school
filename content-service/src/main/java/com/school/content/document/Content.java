package com.school.content.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "contents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Content {

    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated
    private ContentType contentType;

    // File/Link metadata
    private String fileUrl;
    private String fileName;
    private Long fileSizeBytes;
    private String mimeType;
    private String videoLink;

    // ─── VISIBILITY SCOPING ──────────────────────────────────────
    // Determines which students can see this content
    @Indexed
    private Long classId;      // Required always

    private Long subjectId;    // Set by subject teacher (null = class-wide)

    @Indexed
    private ContentScope scope; // CLASS_WIDE or SUBJECT_SPECIFIC

    // Uploader info
    private Long uploadedByUserId;
    private String uploaderName;
    private String uploaderRole; // CLASS_TEACHER or SUBJECT_TEACHER

    private boolean published = true;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;

    public enum ContentType {
        PDF_NOTES, IMAGE, VIDEO_LINK, ASSIGNMENT, ANNOUNCEMENT, WORKSHEET, TEXTBOOK
    }

    public enum ContentScope {
        CLASS_WIDE,       // Uploaded by class teacher — all students in classId see it
        SUBJECT_SPECIFIC  // Uploaded by subject teacher — students enrolled in subjectId+classId see it
    }
}
