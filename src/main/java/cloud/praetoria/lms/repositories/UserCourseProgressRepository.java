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

import cloud.praetoria.lms.entities.Course;
import cloud.praetoria.lms.entities.Module;
import cloud.praetoria.lms.entities.Organization;
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.entities.UserCourseProgress;

@Repository
public interface UserCourseProgressRepository extends JpaRepository<UserCourseProgress, Long> {
    
    /**
     * Trouver la progression d'un utilisateur pour un cours spécifique
     */
    Optional<UserCourseProgress> findByUserAndCourse(User user, Course course);
    
   
    List<UserCourseProgress> findByUser(User user);
    
    /**
     * Trouver la progression d'un utilisateur pour un module spécifique d'un cours
     */
    @Query("SELECT ucp FROM UserCourseProgress ucp WHERE ucp.user = :user AND ucp.course = :course AND ucp.module = :module")
    Optional<UserCourseProgress> findByUserAndCourseAndModule(@Param("user") User user, 
                                                               @Param("course") Course course, 
                                                               @Param("module") Module module);
    
   /* @Query("SELECT ucp FROM UserCourseProgress ucp " +
    	       "WHERE ucp.user = :user AND ucp.course.module = :module")
    	Optional<UserCourseProgress> findByUserAndModule(User user, Module module);*/
    
    /**
     * Compter le nombre de cours terminés par un utilisateur
     */
    long countByUserAndCompletedTrue(User user);
    
    /**
     * Compter le nombre de modules terminés par un utilisateur pour un cours spécifique
     */
    @Query("SELECT COUNT(ucp) FROM UserCourseProgress ucp WHERE ucp.user = :user AND ucp.course = :course AND ucp.module = :module AND ucp.completed = true")
    long countByUserAndCourseAndModuleAndCompletedTrue(@Param("user") User user, 
                                                        @Param("course") Course course, 
                                                        @Param("module") Module module);
    
    /**
     * Vérifier si un utilisateur a terminé un cours
     */
    boolean existsByUserAndCourseAndCompletedTrue(User user, Course course);
    
    /**
     * Vérifier si un utilisateur a terminé un module spécifique d'un cours
     */
    @Query("SELECT COUNT(ucp) > 0 FROM UserCourseProgress ucp WHERE ucp.user = :user AND ucp.course = :course AND ucp.module = :module AND ucp.completed = true")
    boolean existsByUserAndCourseAndModuleAndCompletedTrue(@Param("user") User user, 
                                                            @Param("course") Course course, 
                                                            @Param("module") Module module);
    
    /**
     * Récupérer tous les cours démarrés par un utilisateur
     */
    List<UserCourseProgress> findByUserAndStartedAtIsNotNull(User user);
    
    /**
     * Récupérer tous les cours terminés par un utilisateur avec des pagination au cas ou ...
     */
    Page<UserCourseProgress> findByUserAndCompletedTrue(User user, Pageable pageable);
    
    /**
     * Compter le nombre de cours terminés par un utilisateur (version avec Query)
     */
    @Query("SELECT COUNT(ucp) FROM UserCourseProgress ucp WHERE ucp.user = :user AND ucp.completed = true")
    long countCompletedCoursesByUser(@Param("user") User user);
    
    /**
     * Compter le nombre de cours démarrés par un utilisateur
     */
    @Query("SELECT COUNT(ucp) FROM UserCourseProgress ucp WHERE ucp.user = :user AND ucp.startedAt IS NOT NULL")
    long countStartedCoursesByUser(@Param("user") User user);
    
    /**
     * Compter le nombre de modules terminés par un utilisateur pour un cours
     */
    @Query("SELECT COUNT(ucp) FROM UserCourseProgress ucp WHERE ucp.user = :user AND ucp.course = :course AND ucp.completed = true")
    long countCompletedModulesByUserAndCourse(@Param("user") User user, @Param("course") Course course);
    
    /**
     * Supprimer toutes les progressions d'un utilisateur (quand le compte est supprimé)
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM UserCourseProgress ucp WHERE ucp.user = :user")
    void deleteByUser(@Param("user") User user);
    
    /**
     * Supprimer toutes les progressions pour un cours (quand le cours est supprimé)
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM UserCourseProgress ucp WHERE ucp.course = :course")
    void deleteByCourse(@Param("course") Course course);
     
    
}