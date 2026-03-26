package cloud.praetoria.lms.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.lms.entities.PasswordResetToken;
import cloud.praetoria.lms.entities.User;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
    
    Optional<PasswordResetToken> findByToken(String token);
    
    @Query("SELECT prt FROM PasswordResetToken prt WHERE prt.token = :token AND prt.isUsed = false AND prt.expiresAt > :now")
    Optional<PasswordResetToken> findValidToken(@Param("token") String token, @Param("now") LocalDateTime now);
    
    @Query("SELECT prt FROM PasswordResetToken prt WHERE prt.user = :user ORDER BY prt.createdAt DESC")
    java.util.List<PasswordResetToken> findByUser(@Param("user") User user);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken prt WHERE prt.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken prt WHERE prt.user = :user")
    void deleteUserTokens(@Param("user") User user);
    
}
