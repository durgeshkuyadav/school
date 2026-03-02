package com.school.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Links to the service-specific entity (student/teacher/admin)
    private Long profileId;

    // Class teacher's assigned class
    private Long classId;

    // Subject teacher's assigned subject IDs (comma-separated)
    private String subjectIds;

    private String schoolId;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean accountNonLocked = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum Role {
        SUPER_ADMIN,
        SCHOOL_ADMIN,
        CLASS_TEACHER,
        SUBJECT_TEACHER,
        STUDENT,
        PARENT
    }
}
