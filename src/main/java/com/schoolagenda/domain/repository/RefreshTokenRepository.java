//package com.schoolagenda.domain.repository;

//import com.schoolagenda.domain.model.RefreshToken;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
//}

package com.schoolagenda.domain.repository;

import com.schoolagenda.domain.model.RefreshToken;
import com.schoolagenda.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
//    Optional<RefreshToken> findByToken(String token);
//    Optional<RefreshToken> findByUser(User user);
//
//    @Modifying
//    int deleteByUser(User user);

    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    void deleteByUser(@Param("user") User user);

    // TODO: CASO DÊ PROBLEMA COM O MÉTODO ACIMA (DELETE), UTILIZAR O QUE ESTÁ ABAIXO!
//    @Modifying
//    int deleteByUser(User user);
}
