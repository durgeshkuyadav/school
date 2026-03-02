package com.school.academic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "results", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"exam_id", "student_id"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(nullable = false)
    private Long studentId;

    private String studentName;

    @Column(nullable = false)
    private Integer marksObtained;

    private String grade;

    @Enumerated(EnumType.STRING)
    private ResultStatus status;

    // Teacher explicitly marks cleared/not cleared
    private Boolean examCleared;

    private String remarks;

    private Long updatedByTeacherId;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PostLoad
    @PrePersist
    @PreUpdate
    public void calculateGrade() {
        if (exam == null || marksObtained == null) return;
        double percentage = (marksObtained * 100.0) / exam.getTotalMarks();
        if (percentage >= 90) grade = "A+";
        else if (percentage >= 80) grade = "A";
        else if (percentage >= 70) grade = "B+";
        else if (percentage >= 60) grade = "B";
        else if (percentage >= 50) grade = "C";
        else if (percentage >= 40) grade = "D";
        else grade = "F";

        status = marksObtained >= exam.getPassingMarks() ? ResultStatus.PASS : ResultStatus.FAIL;
        examCleared = status == ResultStatus.PASS;
    }

    public enum ResultStatus {
        PASS, FAIL, ABSENT, EXEMPTED
    }
}
