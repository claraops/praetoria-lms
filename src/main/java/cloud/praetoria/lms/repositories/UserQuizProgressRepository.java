package cloud.praetoria.lms.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.lms.entities.Quiz;
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.entities.UserQuizProgress;

@Repository
public interface UserQuizProgressRepository extends JpaRepository<UserQuizProgress, Long> {
    
    /**
     * Trouver la progression d'un utilisateur pour un quiz spécifique
     */
    Optional<UserQuizProgress> findByUserAndQuiz(User user, Quiz quiz);
    
    /**
     * Trouver toutes les progressions d'un utilisateur
     */
    List<UserQuizProgress> findByUser(User user);
    
    /**
     * Compter le nombre de quiz terminés par un utilisateur
     */
    long countByUserAndCompletedTrue(User user);
    
    /**
     * Vérifier si un utilisateur a terminé un quiz spécifique
     */
    boolean existsByUserAndQuizAndCompletedTrue(User user, Quiz quiz);
    
    /**
     * Trouver tous les quiz démarrés par un utilisateur
     */
    List<UserQuizProgress> findByUserAndStartedAtIsNotNull(User user);
    
    /**
     * Trouver tous les quiz terminés par un utilisateur avec pagination
     */
    Page<UserQuizProgress> findByUserAndCompletedTrue(User user, Pageable pageable);
    
    /**
     * Compter le nombre de quiz démarrés par un utilisateur
     */
    @Query("SELECT COUNT(uqp) FROM UserQuizProgress uqp WHERE uqp.user = :user AND uqp.startedAt IS NOT NULL")
    long countStartedQuizzesByUser(@Param("user") User user);
    
    /**
     * Compter le nombre de quiz terminés par un utilisateur avec un score minimum
     */
    @Query("SELECT COUNT(uqp) FROM UserQuizProgress uqp WHERE uqp.user = :user AND uqp.completed = true AND uqp.score >= :minScore")
    long countCompletedQuizzesByUserWithMinScore(@Param("user") User user, @Param("minScore") int minScore);
    
    /**
     * Supprimer toutes les progressions d'un utilisateur
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM UserQuizProgress uqp WHERE uqp.user = :user")
    void deleteByUser(@Param("user") User user);
    
    /**
     * Supprimer toutes les progressions pour un quiz
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM UserQuizProgress uqp WHERE uqp.quiz = :quiz")
    void deleteByQuiz(@Param("quiz") Quiz quiz);
}