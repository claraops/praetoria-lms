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
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.entities.UserCourseProgress;

@Repository
public interface UserCourseProgressRepository extends JpaRepository<UserCourseProgress, Long> {

    Optional<UserCourseProgress> findByUserAndCourse(User user, Course course);
    List<UserCourseProgress> findByUser(User user);

    @Query("SELECT ucp FROM UserCourseProgress ucp WHERE ucp.user = :user AND ucp.course.module = :module")
    List<UserCourseProgress> findByUserAndCourseModule(@Param("user") User user, 
                                                         @Param("module") Module module);

    long countByUserAndCompletedTrue(User user);

    @Query("SELECT COUNT(ucp) FROM UserCourseProgress ucp WHERE ucp.user = :user AND ucp.course.module = :module AND ucp.completed = true")
    long countByUserAndCourseModuleAndCompletedTrue(@Param("user") User user, 
                                                      @Param("module") Module module);
    
    
    boolean existsByUserAndCourseAndCompletedTrue(User user, Course course);

    List<UserCourseProgress> findByUserAndStartedAtIsNotNull(User user);

    Page<UserCourseProgress> findByUserAndCompletedTrue(User user, Pageable pageable);

    @Query("SELECT COUNT(ucp) FROM UserCourseProgress ucp WHERE ucp.user = :user")
    long countByUser(@Param("user") User user);

    @Query("SELECT COUNT(ucp) FROM UserCourseProgress ucp WHERE ucp.user = :user AND ucp.startedAt IS NOT NULL")
    long countStartedCoursesByUser(@Param("user") User user);

    @Query("SELECT COUNT(ucp) FROM UserCourseProgress ucp WHERE ucp.user = :user AND ucp.completed = true")
    long countCompletedCoursesByUser(@Param("user") User user);
    
    
    @Modifying
    @Transactional
    @Query("DELETE FROM UserCourseProgress ucp WHERE ucp.user = :user")
    void deleteByUser(@Param("user") User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserCourseProgress ucp WHERE ucp.course = :course")
    void deleteByCourse(@Param("course") Course course);
}