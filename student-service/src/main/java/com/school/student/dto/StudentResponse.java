package com.school.student.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String admissionNumber;
    private LocalDate dateOfBirth;
    private String gender;
    private String photoUrl;
    private String guardianName;
    private String guardianPhone;
    private Long classId;
    private String className;
    private String section;
    private Integer rollNumber;
    private String academicYear;
    private String status;
}