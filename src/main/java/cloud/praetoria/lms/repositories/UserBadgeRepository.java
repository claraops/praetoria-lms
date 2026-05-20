package cloud.praetoria.lms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cloud.praetoria.lms.entities.Badge;
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.entities.UserBadge;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    List<UserBadge> findByUser(User user);

    boolean existsByUserAndBadge(User user, Badge badge);

    long countByUser(User user);

    @Query("SELECT COUNT(ub) FROM UserBadge ub WHERE ub.user = :user")
    long countBadgesByUser(@Param("user") User user);
}


