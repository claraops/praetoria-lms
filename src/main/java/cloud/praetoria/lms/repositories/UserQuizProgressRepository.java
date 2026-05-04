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

    Optional<UserQuizProgress> findByUserAndQuiz(User user, Quiz quiz);
    List<UserQuizProgress> findByUser(User user);

    long countByUserAndCompletedTrue(User user);
    
    List<UserQuizProgress> findByUserAndStartedAtIsNotNull(User user);
    Page<UserQuizProgress> findByUserAndCompletedTrue(User user, Pageable pageable);

    @Query("SELECT COUNT(uqp) FROM UserQuizProgress uqp WHERE uqp.user = :user AND uqp.startedAt IS NOT NULL")
    long countStartedQuizzesByUser(@Param("user") User user);

    boolean existsByUserAndQuizAndCompletedTrue(User user, Quiz quiz);

    @Query("SELECT COUNT(uqp) FROM UserQuizProgress uqp WHERE uqp.user = :user AND uqp.completed = true AND uqp.score >= :minScore")
    long countCompletedQuizzesByUserWithMinScore(@Param("user") User user, @Param("minScore") int minScore);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserQuizProgress uqp WHERE uqp.user = :user")
    void deleteByUser(@Param("user") User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserQuizProgress uqp WHERE uqp.quiz = :quiz")
    void deleteByQuiz(@Param("quiz") Quiz quiz);
}