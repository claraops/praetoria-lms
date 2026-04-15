package cloud.praetoria.lms.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cloud.praetoria.lms.entities.Organization;
import cloud.praetoria.lms.entities.Promotion;
import cloud.praetoria.lms.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isActive = true")
    Optional<User> findActiveByEmail(@Param("email") String email);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true ORDER BY u.createdAt DESC")
    Iterable<User> findAllActive();
    
    @Query("SELECT u FROM User u WHERE u.organization = :org AND u.isActive = true ORDER BY u.createdAt DESC")
    Iterable<User> findAllActiveByOrganization(@Param("org") Organization org);


    /*@Query("SELECT c.id FROM User u JOIN u.completedCourses c WHERE u.id = :userId")
    Set<Long> findCompletedCourseIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT e.id FROM User u JOIN u.completedExercises e WHERE u.id = :userId")
    Set<Long> findCompletedExerciseIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT q.id FROM User u JOIN u.completedQuizzes q WHERE u.id = :userId")
    Set<Long> findCompletedQuizIdsByUserId(@Param("userId") Long userId);*/

    List<User> findByPromotionId(Long promotionId);
    List<User> findByPromotion(Promotion promotion);
    
    
    boolean existsByEmailAndOrganization(String email, Organization organization);
    
}
