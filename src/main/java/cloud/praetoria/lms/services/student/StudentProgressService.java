package cloud.praetoria.lms.services.student;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.lms.dtos.ModuleProgressDTO;
import cloud.praetoria.lms.dtos.OverallProgressDTO;
import cloud.praetoria.lms.services.CurrentUserService;
import cloud.praetoria.lms.services.ProgressService;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudentProgressService {

    private final CurrentUserService currentUserService;
    private final ProgressService progressService;

    /**
     * Récupère la progression globale de l'utilisateur connecté
     */
    public OverallProgressDTO getMyOverallProgress() {
        Long userId = currentUserService.getCurrentUserId();
        return progressService.getUserOverallProgress(userId);
    }

    /**
     * Récupère la progression détaillée d'un module spécifique
     */
    public ModuleProgressDTO getMyModuleProgress(Long moduleId) {
        Long userId = currentUserService.getCurrentUserId();
        return progressService.getUserProgressForModule(userId, moduleId);
    }
}
