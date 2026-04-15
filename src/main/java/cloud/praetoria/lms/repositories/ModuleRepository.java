package cloud.praetoria.lms.repositories;

import cloud.praetoria.lms.entities.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    List<Module> findByBlockId(Long blockId);
}