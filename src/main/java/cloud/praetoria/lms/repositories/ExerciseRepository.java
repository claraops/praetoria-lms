package cloud.praetoria.lms.repositories;

import cloud.praetoria.lms.entities.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    List<Exercise> findByModuleId(Long moduleId);
}