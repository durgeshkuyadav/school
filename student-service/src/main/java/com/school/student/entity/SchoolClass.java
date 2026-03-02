package com.school.student.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "school_classes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SchoolClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // PLAY_GROUP, NURSERY, KG, CLASS_1 ... CLASS_5
    @Column(nullable = false)
    private String name;

    private String section;

    @Column(nullable = false)
    private String academicYear;

    // Class teacher's user ID (from auth service)
    private Long classTeacherId;

    @Column(nullable = false)
    private Integer capacity;

    @OneToMany(mappedBy = "schoolClass", cascade = CascadeType.ALL)
    private List<Student> students;
}
