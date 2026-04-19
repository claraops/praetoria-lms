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
    
    /**
     * Trouver la progression d'un utilisateur pour un exercice spécifique
     */
    Optional<UserExerciseProgress> findByUserAndExercise(User user, Exercise exercise);
    
    /**
     * Trouver toutes les progressions d'un utilisateur
     */
    List<UserExerciseProgress> findByUser(User user);
    
    /**
     * Trouver la progression d'un utilisateur pour les exercices d'un module spécifique
     * (via la relation Exercise -> Module)
     */
    @Query("SELECT uep FROM UserExerciseProgress uep WHERE uep.user = :user AND uep.exercise.module = :module")
    List<UserExerciseProgress> findByUserAndExerciseModule(@Param("user") User user, 
                                                             @Param("module") Module module);
    
    /**
     * Compter le nombre d'exercices terminés par un utilisateur
     */
    long countByUserAndCompletedTrue(User user);
    
    /**
     * Compter le nombre d'exercices terminés par un utilisateur pour un module spécifique
     */
    @Query("SELECT COUNT(uep) FROM UserExerciseProgress uep WHERE uep.user = :user AND uep.exercise.module = :module AND uep.completed = true")
    long countByUserAndExerciseModuleAndCompletedTrue(@Param("user") User user, 
                                                       @Param("module") Module module);
    
    /**
     * Vérifier si un utilisateur a terminé un exercice spécifique
     */
    boolean existsByUserAndExerciseAndCompletedTrue(User user, Exercise exercise);
    
    /**
     * Trouver tous les exercices démarrés par un utilisateur
     */
    List<UserExerciseProgress> findByUserAndStartedAtIsNotNull(User user);
    
    /**
     * Trouver tous les exercices terminés par un utilisateur avec pagination
     */
    Page<UserExerciseProgress> findByUserAndCompletedTrue(User user, Pageable pageable);
    
    /**
     * Compter le nombre d'exercices démarrés par un utilisateur
     */
    @Query("SELECT COUNT(uep) FROM UserExerciseProgress uep WHERE uep.user = :user AND uep.startedAt IS NOT NULL")
    long countStartedExercisesByUser(@Param("user") User user);
    
    /**
     * Supprimer toutes les progressions d'un utilisateur
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM UserExerciseProgress uep WHERE uep.user = :user")
    void deleteByUser(@Param("user") User user);
    
    /**
     * Supprimer toutes les progressions pour un exercice
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM UserExerciseProgress uep WHERE uep.exercise = :exercise")
    void deleteByExercise(@Param("exercise") Exercise exercise);
}