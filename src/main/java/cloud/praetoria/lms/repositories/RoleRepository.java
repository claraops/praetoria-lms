package cloud.praetoria.lms.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cloud.praetoria.lms.entities.Role;
import cloud.praetoria.lms.enums.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByRoleName(RoleName roleName);
    
}
