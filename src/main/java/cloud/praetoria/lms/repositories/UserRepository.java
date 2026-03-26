package cloud.praetoria.lms.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cloud.praetoria.lms.entities.Organization;
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
    
    boolean existsByEmailAndOrganization(String email, Organization organization);
    
}
