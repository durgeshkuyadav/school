package com.school.student.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStudentRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Admission number is required")
    private String admissionNumber;

    private LocalDate dateOfBirth;

    private String gender;

    private String guardianName;

    private String guardianRelation;

    private String guardianPhone;

    private String guardianEmail;

    @NotNull(message = "Class ID is required")
    private Long classId;

    @NotNull(message = "Roll number is required")
    private Integer rollNumber;

    @NotBlank(message = "Academic year is required")
    private String academicYear;
}