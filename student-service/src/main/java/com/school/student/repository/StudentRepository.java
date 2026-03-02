package com.school.student.repository;

import com.school.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByAdmissionNumber(String admissionNumber);

    List<Student> findBySchoolClassId(Long classId);
}