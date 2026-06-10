package cloud.praetoria.lms.services.student;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.lms.dtos.*;
import cloud.praetoria.lms.entities.*;
import cloud.praetoria.lms.entities.Module;
import cloud.praetoria.lms.repositories.ModuleRepository;
import cloud.praetoria.lms.repositories.UserCourseProgressRepository;
import cloud.praetoria.lms.repositories.UserExerciseProgressRepository;
import cloud.praetoria.lms.repositories.UserQuizProgressRepository;
import cloud.praetoria.lms.services.CurrentUserService;
import cloud.praetoria.lms.services.ProgressService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudentModuleService {

    private final CurrentUserService currentUserService;
    private final ModuleRepository moduleRepository;
    private final ProgressService progressService;
    private final UserCourseProgressRepository userCourseProgressRepository;
    private final UserExerciseProgressRepository userExerciseProgressRepository;
    private final UserQuizProgressRepository userQuizProgressRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Liste les modules d'un bloc avec leur progression
     */
    public List<ModuleProgressDTO> getModulesByBlockWithProgress(Long blockId) {
        User user = currentUserService.getCurrentUser();
        
        List<Module> modules = moduleRepository.findByBlockId(blockId);
        List<ModuleProgressDTO> result = new ArrayList<>();
        
        for (Module module : modules) {
            ModuleProgressDTO progress = progressService.getUserProgressForModule(user.getId(), module.getId());
            result.add(progress);
        }
        
        return result;
    }

    /**
     * Récupère le détail complet d'un module (cours, exercices, quiz avec statuts)
     */
    public ModuleDetailDTO getModuleDetail(Long moduleId) {
        User user = currentUserService.getCurrentUser();
        
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module non trouvé: " + moduleId));
        
        boolean isModuleCompleted = progressService.isModuleCompleted(user, module);
        ModuleProgressDTO progress = progressService.getUserProgressForModule(user.getId(), moduleId);
        
        List<CourseSummaryDTO> courseDTOs = new ArrayList<>();
        for (Course course : module.getCourses()) {
            UserCourseProgress courseProgress = userCourseProgressRepository
                    .findByUserAndCourse(user, course).orElse(null);
            
            courseDTOs.add(CourseSummaryDTO.builder()
                    .courseId(course.getId())
                    .name(course.getName())
                    .description(course.getDescription())
                    .content(course.getContent())
                    .videoUrl(course.getVideoUrl())
                    .isCompleted(courseProgress != null && courseProgress.getCompleted())
                    .startedAt(courseProgress != null && courseProgress.getStartedAt() != null ? 
                            courseProgress.getStartedAt().format(DATE_FORMATTER) : null)
                    .completedAt(courseProgress != null && courseProgress.getCompletedAt() != null ? 
                            courseProgress.getCompletedAt().format(DATE_FORMATTER) : null)
                    .build());
        }
        
        List<ExerciseSummaryDTO> exerciseDTOs = new ArrayList<>();
        for (Exercise exercise : module.getExercises()) {
            UserExerciseProgress exerciseProgress = userExerciseProgressRepository
                    .findByUserAndExercise(user, exercise).orElse(null);
            
            exerciseDTOs.add(ExerciseSummaryDTO.builder()
                    .exerciseId(exercise.getId())
                    .name(exercise.getName())
                    .content(exercise.getContent())
                    .isCompleted(exerciseProgress != null && exerciseProgress.getCompleted())
                    .score(exerciseProgress != null ? exerciseProgress.getScore() : null)
                    .attempts(exerciseProgress != null ? 
                            (exerciseProgress.getCompleted() ? 1 : 0) : 0)
                    .build());
        }
        
        QuizSummaryDTO quizDTO = null;
        if (module.getQuiz() != null) {
            Quiz quiz = module.getQuiz();
            UserQuizProgress quizProgress = userQuizProgressRepository
                    .findByUserAndQuiz(user, quiz).orElse(null);
            
            quizDTO = QuizSummaryDTO.builder()
                    .quizId(quiz.getId())
                    .name(quiz.getName())
                    .content(quiz.getContent())
                    .isCompleted(quizProgress != null && quizProgress.getCompleted())
                    .score(quizProgress != null ? quizProgress.getScore() : null)
                    .attempts(quizProgress != null ? quizProgress.getAttempts() : 0)
                    .build();
        }
        
        return ModuleDetailDTO.builder()
                .moduleId(module.getId())
                .moduleName(module.getName())
                .description(module.getDescription())
                .blockId(module.getBlock() != null ? module.getBlock().getId() : null)
                .blockName(module.getBlock() != null ? module.getBlock().getName() : null)
                .isCompleted(isModuleCompleted)
                .percentComplete(progress.getPercentComplete())
                .courses(courseDTOs)
                .exercises(exerciseDTOs)
                .quiz(quizDTO)
                .build();
    }
}
