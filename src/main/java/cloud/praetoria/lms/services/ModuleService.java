package cloud.praetoria.lms.services;

import cloud.praetoria.lms.dtos.ModuleRequest;
import cloud.praetoria.lms.dtos.ModuleResponse;
import java.util.List;

public interface ModuleService {

    List<ModuleResponse> getAllModules();

    ModuleResponse getModuleById(Long id);

    List<ModuleResponse> getModulesByBlockId(Long blockId);

    ModuleResponse createModule(ModuleRequest moduleRequest);

    ModuleResponse updateModule(Long id, ModuleRequest moduleRequest);

    void deleteModule(Long id);
}