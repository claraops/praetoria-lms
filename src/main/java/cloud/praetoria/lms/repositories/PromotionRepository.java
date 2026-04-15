package cloud.praetoria.lms.repositories;

import cloud.praetoria.lms.entities.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    @Query("SELECT p FROM Promotion p WHERE p.active = true AND p.startDate <= :date AND p.endDate >= :date")
    Optional<Promotion> findActiveByDate(@Param("date") LocalDate date);
}