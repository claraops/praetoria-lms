package cloud.praetoria.lms.services.student;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.lms.dtos.CourseSummaryDTO;
import cloud.praetoria.lms.entities.Course;
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.entities.UserCourseProgress;
import cloud.praetoria.lms.repositories.CourseRepository;
import cloud.praetoria.lms.repositories.UserCourseProgressRepository;
import cloud.praetoria.lms.services.CurrentUserService;
import cloud.praetoria.lms.services.ProgressService;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentCourseService {

    private final CurrentUserService currentUserService;
    private final CourseRepository courseRepository;
    private final ProgressService progressService;
    private final UserCourseProgressRepository userCourseProgressRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Récupère le contenu complet d'un cours
     * Appelle startCourse() si premier accès (startedAt == null)
     */
    @Transactional
    public CourseSummaryDTO getCourseContent(Long courseId) {
        User user = currentUserService.getCurrentUser();
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé: " + courseId));
        
        // Vérifier si c'est le premier accès
        UserCourseProgress progress = userCourseProgressRepository
                .findByUserAndCourse(user, course).orElse(null);
        
        boolean isFirstAccess = (progress == null || progress.getStartedAt() == null);
        
        if (isFirstAccess) {
            // Démarrer le cours automatiquement
            progressService.startCourse(user.getId(), courseId);
            log.info("Premier accès au cours {} pour l'utilisateur {}, démarrage automatique", 
                    course.getName(), user.getEmail());
            
            // Recharger la progression
            progress = userCourseProgressRepository
                    .findByUserAndCourse(user, course).orElse(null);
        }
        
        return CourseSummaryDTO.builder()
                .courseId(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .content(course.getContent())
                .videoUrl(course.getVideoUrl())
                .isCompleted(progress != null && progress.getCompleted())
                .startedAt(progress != null && progress.getStartedAt() != null ? 
                        progress.getStartedAt().format(DATE_FORMATTER) : null)
                .completedAt(progress != null && progress.getCompletedAt() != null ? 
                        progress.getCompletedAt().format(DATE_FORMATTER) : null)
                .build();
    }

    /**
     * Marque un cours comme complété
     */
    @Transactional
    public void completeCourse(Long courseId) {
        User user = currentUserService.getCurrentUser();
        progressService.completeCourse(user.getId(), courseId);
    }
}