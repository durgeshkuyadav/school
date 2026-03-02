package com.school.academic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "exams")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // e.g. "Unit Test 1", "Mid-Term", "Final"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamType examType;

    @Column(nullable = false)
    private Long classId;

    @Column(nullable = false)
    private Long subjectId;

    private String subjectName;
    private String className;

    @Column(nullable = false)
    private Integer totalMarks;

    @Column(nullable = false)
    private Integer passingMarks;

    @Column(nullable = false)
    private LocalDate examDate;

    @Column(nullable = false)
    private String academicYear;

    private Long createdByTeacherId;
    private boolean resultsPublished = false;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Result> results;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum ExamType {
        UNIT_TEST, MID_TERM, FINAL, QUARTERLY, HALF_YEARLY, ANNUAL, ONLINE_TEST
    }
}
