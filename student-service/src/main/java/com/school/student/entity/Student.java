package com.school.student.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "students")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String admissionNumber;

    private LocalDate dateOfBirth;
    private String gender;
    private String photoUrl;
    private String address;
    private String bloodGroup;

    // Guardian Info
    private String guardianName;
    private String guardianRelation;
    private String guardianPhone;
    private String guardianEmail;

    // Academic Placement
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;

    @Column(nullable = false)
    private Integer rollNumber;

    @Column(nullable = false)
    private String academicYear;

    @Enumerated(EnumType.STRING)
    private StudentStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum StudentStatus {
        ACTIVE, INACTIVE, PROMOTED, TRANSFERRED, WITHDRAWN
    }
}
