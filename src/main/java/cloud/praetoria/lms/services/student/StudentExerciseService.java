package cloud.praetoria.lms.services.student;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.lms.dtos.ExerciseSummaryDTO;
import cloud.praetoria.lms.entities.Exercise;
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.entities.UserExerciseProgress;
import cloud.praetoria.lms.repositories.ExerciseRepository;
import cloud.praetoria.lms.repositories.UserExerciseProgressRepository;
import cloud.praetoria.lms.services.CurrentUserService;
import cloud.praetoria.lms.services.ProgressService;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentExerciseService {

    private final CurrentUserService currentUserService;
    private final ExerciseRepository exerciseRepository;
    private final ProgressService progressService;
    private final UserExerciseProgressRepository userExerciseProgressRepository;

    /**
     * Récupère le contenu d'un exercice avec son statut
     */
    @Transactional(readOnly = true)
    public ExerciseSummaryDTO getExerciseContent(Long exerciseId) {
        User user = currentUserService.getCurrentUser();
        
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercice non trouvé: " + exerciseId));
        
        UserExerciseProgress progress = userExerciseProgressRepository
                .findByUserAndExercise(user, exercise).orElse(null);
        
        return ExerciseSummaryDTO.builder()
                .exerciseId(exercise.getId())
                .name(exercise.getName())
                .content(exercise.getContent())
                .isCompleted(progress != null && progress.getCompleted())
                .score(progress != null ? progress.getScore() : null)
                .attempts(progress != null ? 
                        (progress.getCompleted() && progress.getScore() != null ? 1 : 0) : 0)
                .build();
    }

    /**
     * Soumet la réponse d'un exercice
     */
    @Transactional
    public void submitExercise(Long exerciseId, Integer score) {
        User user = currentUserService.getCurrentUser();
        progressService.completeExercise(user.getId(), exerciseId, score);
        log.info("Exercice {} soumis par {} avec score {}", exerciseId, user.getEmail(), score);
    }
}
