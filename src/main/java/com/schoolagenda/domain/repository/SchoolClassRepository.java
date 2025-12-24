package com.schoolagenda.domain.repository;

import com.schoolagenda.domain.model.SchoolClass;
import com.schoolagenda.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {

    Optional<SchoolClass> findByName(String name);
    // TODO: o método abaixo está muito "simples". fazer uma "query" com "JPQL"!
    Optional<User> findByCoordinatorId(Long coordinatorId);
    boolean existsByName(String name);
}