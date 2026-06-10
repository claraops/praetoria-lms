package cloud.praetoria.lms.services.student;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.lms.dtos.BlockProgressDTO;
import cloud.praetoria.lms.entities.Block;
import cloud.praetoria.lms.entities.Module;
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.services.ProgressService;
import cloud.praetoria.lms.services.CurrentUserService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudentBlockService {

    private final CurrentUserService currentUserService;
    private final ProgressService progressService;

    /**
     * Récupère tous les blocs assignés à l'utilisateur avec leur progression
     */
    public List<BlockProgressDTO> getUserBlocksWithProgress() {
        User user = currentUserService.getCurrentUser();
        List<Block> userBlocks = user.getBlocks();
        
        List<BlockProgressDTO> result = new ArrayList<>();
        
        for (Block block : userBlocks) {
            BlockProgressDTO blockProgress = calculateBlockProgress(user, block);
            result.add(blockProgress);
        }
        
        return result;
    }

    /**
     * Calcule la progression d'un bloc pour un utilisateur
     * La progression du bloc = moyenne des progressions de ses modules
     */
    private BlockProgressDTO calculateBlockProgress(User user, Block block) {
        List<Module> modules = block.getModules();
        int totalModules = modules.size();
        int completedModules = 0;
        double totalPercent = 0.0;
        
        for (Module module : modules) {
            boolean isModuleCompleted = progressService.isModuleCompleted(user, module);
            if (isModuleCompleted) {
                completedModules++;
                totalPercent += 100.0;
            } else {
                var moduleProgress = progressService.getUserProgressForModule(user.getId(), module.getId());
                totalPercent += moduleProgress.getPercentComplete();
            }
        }
        
        double percentComplete = totalModules > 0 ? totalPercent / totalModules : 0.0;
        boolean isCompleted = totalModules > 0 && completedModules == totalModules;
        
        return BlockProgressDTO.builder()
                .blockId(block.getId())
                .blockName(block.getName())
                .description(block.getDescription())
                .cover(block.getCover())
                .totalModules(totalModules)
                .completedModules(completedModules)
                .percentComplete(Math.round(percentComplete * 100.0) / 100.0)
                .isCompleted(isCompleted)
                .build();
    }
}
