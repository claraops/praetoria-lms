package cloud.praetoria.lms.services.impl;

import cloud.praetoria.lms.dtos.ModuleRequest;
import cloud.praetoria.lms.dtos.ModuleResponse;
import cloud.praetoria.lms.entities.Block;
import cloud.praetoria.lms.entities.Module;
import cloud.praetoria.lms.exceptions.DuplicateResourceException;
import cloud.praetoria.lms.exceptions.ResourceNotFoundException;
import cloud.praetoria.lms.repositories.BlockRepository;
import cloud.praetoria.lms.repositories.ModuleRepository;
import cloud.praetoria.lms.services.ModuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;
    private final BlockRepository blockRepository;

    @Override
    public List<ModuleResponse> getAllModules() {
        log.debug("Récupération de tous les modules");
        return moduleRepository.findAll().stream()
                .map(ModuleResponse::fromEntity)
                .toList();
    }

    @Override
    public ModuleResponse getModuleById(Long id) {
        log.debug("Récupération du module avec l'id: {}", id);

        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module non trouvé avec l'id: " + id));

        return ModuleResponse.fromEntityWithDetails(module);
    }

    @Override
    public List<ModuleResponse> getModulesByBlockId(Long blockId) {
        log.debug("Récupération des modules du bloc: {}", blockId);

        if (!blockRepository.existsById(blockId)) {
            throw new ResourceNotFoundException("Bloc non trouvé avec l'id: " + blockId);
        }

        return moduleRepository.findByBlockId(blockId).stream()
                .map(ModuleResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public ModuleResponse createModule(ModuleRequest moduleRequest) {
        log.info("Création d'un nouveau module: {}", moduleRequest.getName());

        if (moduleRepository.existsByNameIgnoreCase(moduleRequest.getName())) {
            throw new DuplicateResourceException("Un module avec ce nom existe déjà: " + moduleRequest.getName());
        }

        Block block = blockRepository.findById(moduleRequest.getBlockId())
                .orElseThrow(() -> new ResourceNotFoundException("Bloc non trouvé avec l'id: " + moduleRequest.getBlockId()));

        Module module = Module.builder()
                .name(moduleRequest.getName())
                .description(moduleRequest.getDescription())
                .block(block)
                .build();

        Module savedModule = moduleRepository.save(module);
        log.info("Module créé avec succès: {}", savedModule.getName());

        return ModuleResponse.fromEntity(savedModule);
    }

    @Override
    @Transactional
    public ModuleResponse updateModule(Long id, ModuleRequest moduleRequest) {
        log.info("Mise à jour du module avec l'id: {}", id);

        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module non trouvé avec l'id: " + id));

        if (moduleRepository.existsByNameIgnoreCaseAndIdNot(moduleRequest.getName(), id)) {
            throw new DuplicateResourceException("Un module avec ce nom existe déjà: " + moduleRequest.getName());
        }

        module.setName(moduleRequest.getName());
        module.setDescription(moduleRequest.getDescription());

        if (!module.getBlock().getId().equals(moduleRequest.getBlockId())) {
            Block newBlock = blockRepository.findById(moduleRequest.getBlockId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bloc non trouvé avec l'id: " + moduleRequest.getBlockId()));
            module.setBlock(newBlock);
        }

        Module updatedModule = moduleRepository.save(module);
        log.info("Module mis à jour avec succès: {}", updatedModule.getName());

        return ModuleResponse.fromEntity(updatedModule);
    }

    @Override
    @Transactional
    public void deleteModule(Long id) {
        log.info("Suppression du module avec l'id: {}", id);

        if (!moduleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Module non trouvé avec l'id: " + id);
        }

        moduleRepository.deleteById(id);
        log.info("Module supprimé avec succès: {}", id);
    }
}