package com.school.student.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentRequest {
    private String firstName;
    private String lastName;
    private String address;
    private String photoUrl;
    private String guardianPhone;
}