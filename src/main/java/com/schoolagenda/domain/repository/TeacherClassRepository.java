// src/main/java/com/schoolagenda/domain/repository/TeacherClassRepository.java
package com.schoolagenda.domain.repository;

import com.schoolagenda.domain.model.TeacherClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherClassRepository extends JpaRepository<TeacherClass, Long> {

    // Find all classes by teacher
    List<TeacherClass> findByTeacherId(Long teacherId);

    // Find specific class by teacher and class name
    Optional<TeacherClass> findByTeacherIdAndClassName(Long teacherId, String className);

    // Check if class exists for teacher
    boolean existsByTeacherIdAndClassName(Long teacherId, String className);

    // Find all classes with teacher details
    @Query("SELECT tc FROM TeacherClass tc JOIN FETCH tc.teacher WHERE tc.teacher.id = :teacherId")
    List<TeacherClass> findClassesWithTeacherByTeacherId(@Param("teacherId") Long teacherId);

    // Find all teachers for a specific class name
    @Query("SELECT tc FROM TeacherClass tc JOIN FETCH tc.teacher WHERE tc.className = :className")
    List<TeacherClass> findTeachersByClassName(@Param("className") String className);

    // Count classes by teacher
    long countByTeacherId(Long teacherId);

    // Find distinct class names
    @Query("SELECT DISTINCT tc.className FROM TeacherClass tc ORDER BY tc.className")
    List<String> findDistinctClassNames();

    // Delete by teacher and class name
    void deleteByTeacherIdAndClassName(Long teacherId, String className);
}