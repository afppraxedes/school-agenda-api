// src/main/java/com/schoolagenda/domain/repository/StudentRepository.java
package com.schoolagenda.domain.repository;

import com.schoolagenda.domain.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Find students by class name
    List<Student> findByClassName(String className);

    // Find students by class name containing (like search)
    List<Student> findByClassNameContainingIgnoreCase(String className);

    // Find students by full name containing (like search)
    List<Student> findByFullNameContainingIgnoreCase(String fullName);

    // Find student by full name (exact match)
    Optional<Student> findByFullName(String fullName);

    // Count students by class
    long countByClassName(String className);

    // Find all distinct class names
    @Query("SELECT DISTINCT s.className FROM Student s ORDER BY s.className")
    List<String> findAllDistinctClassNames();

    // Find students by birth date range
    @Query("SELECT s FROM Student s WHERE s.birthDate BETWEEN :startDate AND :endDate")
    List<Student> findByBirthDateBetween(@Param("startDate") java.time.LocalDate startDate,
                                         @Param("endDate") java.time.LocalDate endDate);

    // Find latest registered students
    List<Student> findTop10ByOrderByRegistrationDateDesc();

    // Busca o estudante pelo ID do usuário vinculado
    Optional<Student> findByUserId(Long userId);

    /**
     * Busca o ID da turma vinculada a um usuário (Aluno ou Responsável).
     * Se for responsável, busca o ID da turma do primeiro dependente encontrado.
     */
    @Query("""
    SELECT s.schoolClass.id FROM Student s 
    WHERE s.user.id = :userId
""")
    Optional<Long> findClassIdByUserId(@Param("userId") Long userId);
}