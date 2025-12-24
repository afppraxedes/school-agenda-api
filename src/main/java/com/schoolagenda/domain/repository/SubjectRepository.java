package com.schoolagenda.domain.repository;

import com.schoolagenda.domain.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long>,
        JpaSpecificationExecutor<Subject> {

    List<Subject> findByTeacherId(Long teacherId);

    List<Subject> findBySchoolYear(String schoolYear);

    List<Subject> findByActiveTrue();

    List<Subject> findByActive(Boolean active);

    @Query("SELECT s FROM Subject s WHERE " +
            "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:schoolYear IS NULL OR s.schoolYear = :schoolYear) AND " +
            "(:active IS NULL OR s.active = :active)")
    List<Subject> findByFilters(
            @Param("name") String name,
            @Param("schoolYear") String schoolYear,
            @Param("active") Boolean active);

    boolean existsByNameAndSchoolYear(String name, String schoolYear);

    @Query("SELECT COUNT(s) > 0 FROM Subject s WHERE s.id = :subjectId AND s.teacher.id = :teacherId")
    boolean existsByIdAndTeacherId(@Param("subjectId") Long subjectId,
                                   @Param("teacherId") Long teacherId);

    @Query("SELECT s FROM Subject s JOIN FETCH s.teacher WHERE s.id = :id")
    Optional<Subject> findByIdWithTeacher(@Param("id") Long id);
}
