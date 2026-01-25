package com.schoolagenda.domain.repository;

import com.schoolagenda.application.web.dto.response.MonthlyAverageDTO;
import com.schoolagenda.domain.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    @Query("SELECT AVG(e.grade) FROM Evaluation e WHERE e.student.id = :studentId")
    Double calculateAverageByStudent(Long studentId);

//    @Query("""
//        SELECT new com.schoolagenda.application.web.dto.response.MonthlyAverageDTO(
//            CAST(MONTH(e.createdAt) AS string),
//            AVG(e.grade)
//        )
//        FROM Evaluation e
//        WHERE e.student.id = :studentId
//        GROUP BY MONTH(e.createdAt)
//        ORDER BY MONTH(e.createdAt)
//    """)
//    List<MonthlyAverageDTO> findMonthlyAveragesByStudent(Long studentId);

    @Query("""
    SELECT new com.schoolagenda.application.web.dto.response.MonthlyAverageDTO(
        CAST(MONTH(e.createdAt) AS string), 
        AVG(e.grade)
    )
    FROM Evaluation e
    WHERE e.student.id = :studentId
    GROUP BY MONTH(e.createdAt)
    ORDER BY MONTH(e.createdAt)
""")
    List<MonthlyAverageDTO> findMonthlyAveragesByStudent(Long studentId);
}
