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

import cloud.praetoria.lms.entities.Exercise;
import cloud.praetoria.lms.entities.Module;
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.entities.UserExerciseProgress;

@Repository
public interface UserExerciseProgressRepository extends JpaRepository<UserExerciseProgress, Long> {

    // Recherches de base
    Optional<UserExerciseProgress> findByUserAndExercise(User user, Exercise exercise);
    List<UserExerciseProgress> findByUser(User user);

    // Parcours via relation Exercise -> Module
    @Query("SELECT uep FROM UserExerciseProgress uep WHERE uep.user = :user AND uep.exercise.module = :module")
    List<UserExerciseProgress> findByUserAndExerciseModule(@Param("user") User user, 
                                                             @Param("module") Module module);

    // Comptages
    long countByUserAndCompletedTrue(User user);

    @Query("SELECT COUNT(uep) FROM UserExerciseProgress uep WHERE uep.user = :user AND uep.exercise.module = :module AND uep.completed = true")
    long countByUserAndExerciseModuleAndCompletedTrue(@Param("user") User user, 
                                                        @Param("module") Module module);

    // Progression par utilisateur
    List<UserExerciseProgress> findByUserAndStartedAtIsNotNull(User user);
    Page<UserExerciseProgress> findByUserAndCompletedTrue(User user, Pageable pageable);

    // Compteurs supplémentaires
    @Query("SELECT COUNT(uep) FROM UserExerciseProgress uep WHERE uep.user = :user AND uep.startedAt IS NOT NULL")
    long countStartedExercisesByUser(@Param("user") User user);

    // Suppressions
    @Modifying
    @Transactional
    @Query("DELETE FROM UserExerciseProgress uep WHERE uep.user = :user")
    void deleteByUser(@Param("user") User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserExerciseProgress uep WHERE uep.exercise = :exercise")
    void deleteByExercise(@Param("exercise") Exercise exercise);
}