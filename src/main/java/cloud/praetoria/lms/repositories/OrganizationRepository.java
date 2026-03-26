package cloud.praetoria.lms.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cloud.praetoria.lms.entities.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    
    Optional<Organization> findByRegistrationKey(String registrationKey);
    
    boolean existsByRegistrationKey(String registrationKey);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.organization = :org AND u.isActive = true")
    long countActiveStudents(@Param("org") Organization org);
    
}
