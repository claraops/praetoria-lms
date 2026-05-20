package cloud.praetoria.lms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.entities.UserGamification;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGamificationRepository extends JpaRepository<UserGamification, Long> {

    Optional<UserGamification> findByUser(User user);

    @Query("SELECT ug FROM UserGamification ug ORDER BY ug.totalXp DESC")
    List<UserGamification> findTop10ByOrderByTotalXpDesc();

    @Query("SELECT ug FROM UserGamification ug WHERE ug.user.organization.id = :organizationId ORDER BY ug.totalXp DESC")
    List<UserGamification> findByUserOrganizationOrderByTotalXpDesc(@Param("organizationId") Long organizationId);
}
