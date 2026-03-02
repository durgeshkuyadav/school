package com.school.student.service;

import com.school.student.dto.*;
import com.school.student.entity.SchoolClass;
import com.school.student.entity.Student;
import com.school.student.exception.ResourceNotFoundException;
import com.school.student.repository.SchoolClassRepository;
import com.school.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final SchoolClassRepository classRepository;

    public Page<StudentResponse> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable).map(this::mapToResponse);
    }

    public List<StudentResponse> getStudentsByClass(Long classId) {
        return studentRepository.findBySchoolClassId(classId)
            .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public StudentResponse getStudentById(Long id) {
        return mapToResponse(findStudentById(id));
    }

    public StudentResponse getStudentByAdmissionNumber(String admNo) {
        return mapToResponse(studentRepository.findByAdmissionNumber(admNo)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + admNo)));
    }

    @Transactional
    public StudentResponse createStudent(CreateStudentRequest req) {
        SchoolClass cls = classRepository.findById(req.getClassId())
            .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + req.getClassId()));

        Student student = Student.builder()
            .firstName(req.getFirstName())
            .lastName(req.getLastName())
            .admissionNumber(req.getAdmissionNumber())
            .dateOfBirth(req.getDateOfBirth())
            .gender(req.getGender())
            .guardianName(req.getGuardianName())
            .guardianRelation(req.getGuardianRelation())
            .guardianPhone(req.getGuardianPhone())
            .guardianEmail(req.getGuardianEmail())
            .schoolClass(cls)
            .rollNumber(req.getRollNumber())
            .academicYear(req.getAcademicYear())
            .status(Student.StudentStatus.ACTIVE)
            .build();

        return mapToResponse(studentRepository.save(student));
    }

    @Transactional
    public StudentResponse updateStudent(Long id, UpdateStudentRequest req) {
        Student student = findStudentById(id);
        if (req.getFirstName() != null) student.setFirstName(req.getFirstName());
        if (req.getLastName() != null) student.setLastName(req.getLastName());
        if (req.getAddress() != null) student.setAddress(req.getAddress());
        if (req.getPhotoUrl() != null) student.setPhotoUrl(req.getPhotoUrl());
        if (req.getGuardianPhone() != null) student.setGuardianPhone(req.getGuardianPhone());
        return mapToResponse(studentRepository.save(student));
    }

    @Transactional
    public void promoteStudent(Long studentId, Long newClassId) {
        Student student = findStudentById(studentId);
        SchoolClass newClass = classRepository.findById(newClassId)
            .orElseThrow(() -> new ResourceNotFoundException("Class not found: " + newClassId));
        student.setSchoolClass(newClass);
        student.setStatus(Student.StudentStatus.PROMOTED);
        studentRepository.save(student);
    }

    private Student findStudentById(Long id) {
        return studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + id));
    }

    private StudentResponse mapToResponse(Student s) {
        return StudentResponse.builder()
            .id(s.getId())
            .firstName(s.getFirstName())
            .lastName(s.getLastName())
            .admissionNumber(s.getAdmissionNumber())
            .dateOfBirth(s.getDateOfBirth())
            .gender(s.getGender())
            .photoUrl(s.getPhotoUrl())
            .guardianName(s.getGuardianName())
            .guardianPhone(s.getGuardianPhone())
            .classId(s.getSchoolClass().getId())
            .className(s.getSchoolClass().getName())
            .section(s.getSchoolClass().getSection())
            .rollNumber(s.getRollNumber())
            .academicYear(s.getAcademicYear())
            .status(s.getStatus().name())
            .build();
    }
}
