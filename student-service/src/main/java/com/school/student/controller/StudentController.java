package com.school.student.controller;

import com.school.student.dto.*;
import com.school.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Student management APIs")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @Operation(summary = "Get all students (paginated) — Admin only")
    public ResponseEntity<Page<StudentResponse>> getAllStudents(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(studentService.getAllStudents(PageRequest.of(page, size)));
    }

    @GetMapping("/class/{classId}")
    @Operation(summary = "Get all students in a class")
    public ResponseEntity<List<StudentResponse>> getByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(studentService.getStudentsByClass(classId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID")
    public ResponseEntity<StudentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @GetMapping("/admission/{admNo}")
    @Operation(summary = "Get student by admission number")
    public ResponseEntity<StudentResponse> getByAdmission(@PathVariable String admNo) {
        return ResponseEntity.ok(studentService.getStudentByAdmissionNumber(admNo));
    }

    @PostMapping
    @Operation(summary = "Create a new student — Admin only")
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody CreateStudentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.createStudent(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update student info")
    public ResponseEntity<StudentResponse> update(
        @PathVariable Long id,
        @RequestBody UpdateStudentRequest req) {
        return ResponseEntity.ok(studentService.updateStudent(id, req));
    }

    @PutMapping("/{id}/promote/{newClassId}")
    @Operation(summary = "Promote student to next class")
    public ResponseEntity<Void> promote(
        @PathVariable Long id,
        @PathVariable Long newClassId) {
        studentService.promoteStudent(id, newClassId);
        return ResponseEntity.noContent().build();
    }
}
