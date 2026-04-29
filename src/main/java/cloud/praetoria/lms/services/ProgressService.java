package cloud.praetoria.lms.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.lms.dtos.CourseProgressDTO;
import cloud.praetoria.lms.dtos.ModuleProgressDTO;
import cloud.praetoria.lms.dtos.OverallProgressDTO;
import cloud.praetoria.lms.entities.Block;
import cloud.praetoria.lms.entities.Course;
import cloud.praetoria.lms.entities.Exercise;
import cloud.praetoria.lms.entities.Module;
import cloud.praetoria.lms.entities.Quiz;
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.entities.UserCourseProgress;
import cloud.praetoria.lms.entities.UserExerciseProgress;
import cloud.praetoria.lms.entities.UserQuizProgress;
import cloud.praetoria.lms.repositories.BlockRepository;
import cloud.praetoria.lms.repositories.CourseRepository;
import cloud.praetoria.lms.repositories.ExerciseRepository;
import cloud.praetoria.lms.repositories.ModuleRepository;
import cloud.praetoria.lms.repositories.QuizRepository;
import cloud.praetoria.lms.repositories.UserCourseProgressRepository;
import cloud.praetoria.lms.repositories.UserExerciseProgressRepository;
import cloud.praetoria.lms.repositories.UserQuizProgressRepository;
import cloud.praetoria.lms.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProgressService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ExerciseRepository exerciseRepository;
    private final QuizRepository quizRepository;
    private final ModuleRepository moduleRepository;
    private final BlockRepository blockRepository;
    
    private final UserCourseProgressRepository userCourseProgressRepository;
    private final UserExerciseProgressRepository userExerciseProgressRepository;
    private final UserQuizProgressRepository userQuizProgressRepository;
    
    // Constantes XP
    private static final int XP_PER_COURSE_COMPLETION = 10;
    private static final int XP_PER_MODULE_COMPLETION = 50;
    
    // ==================== GESTION DES COURS ====================
    
    @Transactional
    public UserCourseProgress startCourse(Long userId, Long courseId) {
        User user = getUserById(userId);
        Course course = getCourseById(courseId);
        
        UserCourseProgress progress = userCourseProgressRepository
                .findByUserAndCourse(user, course)
                .orElse(UserCourseProgress.builder()
                    .user(user)
                    .course(course)
                    .completed(false)
                    .build());
        
        if (progress.getStartedAt() == null) {
            progress.setStartedAt(LocalDateTime.now());
            log.info("User {} started course {}", user.getEmail(), course.getName());
        }
        
        return userCourseProgressRepository.save(progress);
    }
    
    @Transactional
    public UserCourseProgress completeCourse(Long userId, Long courseId) {
        User user = getUserById(userId);
        Course course = getCourseById(courseId);
        
        UserCourseProgress progress = userCourseProgressRepository
                .findByUserAndCourse(user, course)
                .orElseThrow(() -> new RuntimeException("Course not started"));
        
        if (!progress.getCompleted()) {
            progress.setCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
            
            // Ajout XP
            user.setXp(user.getXp() + XP_PER_COURSE_COMPLETION);
            userRepository.save(user);
            
            log.info("User {} completed course {} (+{} XP)", 
                    user.getEmail(), course.getName(), XP_PER_COURSE_COMPLETION);
            
            // Vérifier complétion du module
            checkAndAwardModuleBonus(user, course.getModule());
        }
        
        return userCourseProgressRepository.save(progress);
    }
    
    // ==================== GESTION DES EXERCICES ====================
    
    @Transactional
    public UserExerciseProgress completeExercise(Long userId, Long exerciseId, Integer score) {
        User user = getUserById(userId);
        Exercise exercise = getExerciseById(exerciseId);
        
        UserExerciseProgress progress = userExerciseProgressRepository
                .findByUserAndExercise(user, exercise)
                .orElse(UserExerciseProgress.builder()
                    .user(user)
                    .exercise(exercise)
                    .completed(false)
                    .score(score)
                    .build());
        
        if (!progress.getCompleted()) {
            progress.setCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
            if (score != null) {
                progress.setScore(score);
            }
            if (progress.getStartedAt() == null) {
                progress.setStartedAt(LocalDateTime.now());
            }
            
            log.info("User {} completed exercise {} (score: {})", 
                    user.getEmail(), exercise.getName(), score);
            
            // Vérifier complétion du module (pas d'XP direct pour exercice)
            checkAndAwardModuleBonus(user, exercise.getModule());
        }
        
        return userExerciseProgressRepository.save(progress);
    }
    
    // ==================== GESTION DES QUIZ ====================
    
    @Transactional
    public UserQuizProgress completeQuiz(Long userId, Long quizId, Integer score) {
        User user = getUserById(userId);
        Quiz quiz = getQuizById(quizId);
        
        UserQuizProgress progress = userQuizProgressRepository
                .findByUserAndQuiz(user, quiz)
                .orElse(UserQuizProgress.builder()
                    .user(user)
                    .quiz(quiz)
                    .completed(false)
                    .attempts(0)
                    .build());
        
        progress.setAttempts(progress.getAttempts() + 1);
        
        if (!progress.getCompleted()) {
            progress.setCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
            progress.setScore(score);
            if (progress.getStartedAt() == null) {
                progress.setStartedAt(LocalDateTime.now());
            }
            
            log.info("User {} completed quiz {} (score: {}, attempts: {})", 
                    user.getEmail(), quiz.getName(), score, progress.getAttempts());
            
            // Vérifier complétion du module
            Module module = getModuleByQuiz(quiz);
            if (module != null) {
                checkAndAwardModuleBonus(user, module);
            }
        }
        
        return userQuizProgressRepository.save(progress);
    }
    
    // ==================== VÉRIFICATION DES BONUS ====================
    
    private void checkAndAwardModuleBonus(User user, Module module) {
        if (module == null) return;
        
        if (isModuleCompleted(user, module)) {
            // Vérifier si bonus déjà donné (via un flag ou vérification XP)
            // Pour simplifier, on vérifie si tous les cours/exos/quiz sont complétés
            // mais on évite le double bonus en vérifiant une condition spécifique
            
            // Ajout XP bonus module
            user.setXp(user.getXp() + XP_PER_MODULE_COMPLETION);
            userRepository.save(user);
            
            log.info("bravo !  User {} completed module {} (+{} XP bonus)", 
                    user.getEmail(), module.getName(), XP_PER_MODULE_COMPLETION);
        }
    }
    
    
    // ==================== MÉTHODES DE VÉRIFICATION ====================
    
    @Transactional(readOnly = true)
    public boolean isModuleCompleted(User user, Module module) {
        if (module == null) return false;
        
        // Vérifier tous les cours du module
        List<Course> courses = module.getCourses();
        for (Course course : courses) {
            boolean courseCompleted = userCourseProgressRepository
                    .existsByUserAndCourseAndCompletedTrue(user, course);
            if (!courseCompleted) return false;
        }
        
        // Vérifier tous les exercices du module
        List<Exercise> exercises = module.getExercises();
        for (Exercise exercise : exercises) {
            boolean exerciseCompleted = userExerciseProgressRepository
                    .findByUserAndExercise(user, exercise)
                    .map(UserExerciseProgress::getCompleted)
                    .orElse(false);
            if (!exerciseCompleted) return false;
        }
        
        // Vérifier le quiz si présent
        if (module.getQuiz() != null) {
            return userQuizProgressRepository
                    .existsByUserAndQuizAndCompletedTrue(user, module.getQuiz());
        }
        
        return true;
    }
    
    // ==================== RÉCUPÉRATION DES PROGRESSIONS ====================
    
    @Transactional(readOnly = true)
    public ModuleProgressDTO getUserProgressForModule(Long userId, Long moduleId) {
        User user = getUserById(userId);
        Module module = getModuleById(moduleId);
        
        // Statistiques cours
        List<Course> courses = module.getCourses();
        long completedCourses = courses.stream()
                .filter(course -> userCourseProgressRepository
                        .existsByUserAndCourseAndCompletedTrue(user, course))
                .count();
        
        // Statistiques exercices
        List<Exercise> exercises = module.getExercises();
        long completedExercises = exercises.stream()
                .filter(exercise -> userExerciseProgressRepository
                        .findByUserAndExercise(user, exercise)
                        .map(UserExerciseProgress::getCompleted)
                        .orElse(false))
                .count();
        
        // Statistiques quiz
        boolean hasQuiz = module.getQuiz() != null;
        boolean quizCompleted = false;
        Integer quizScore = null;
        Integer quizAttempts = null;
        
        if (hasQuiz) {
            UserQuizProgress quizProgress = userQuizProgressRepository
                    .findByUserAndQuiz(user, module.getQuiz())
                    .orElse(null);
            if (quizProgress != null) {
                quizCompleted = quizProgress.getCompleted();
                quizScore = quizProgress.getScore();
                quizAttempts = quizProgress.getAttempts();
            }
        }
        
        // Calcul progression module
        int totalItems = courses.size() + exercises.size() + (hasQuiz ? 1 : 0);
        int completedItems = (int)(completedCourses + completedExercises + (quizCompleted ? 1 : 0));
        double percentComplete = totalItems > 0 ? (completedItems * 100.0 / totalItems) : 0.0;
        
        return ModuleProgressDTO.builder()
                .moduleId(moduleId)
                .moduleName(module.getName())
                .blockId(module.getBlock() != null ? module.getBlock().getId() : null)
                .blockName(module.getBlock() != null ? module.getBlock().getName() : null)
                .totalCourses(courses.size())
                .completedCourses((int) completedCourses)
                .totalExercises(exercises.size())
                .completedExercises((int) completedExercises)
                .hasQuiz(hasQuiz)
                .quizId(hasQuiz ? module.getQuiz().getId() : null)
                .quizCompleted(quizCompleted)
                .quizScore(quizScore)
                .quizAttempts(quizAttempts)
                .percentComplete(Math.round(percentComplete * 100.0) / 100.0)
                .isCompleted(percentComplete == 100.0)
                .build();
    }
    
    @Transactional(readOnly = true)
    public OverallProgressDTO getUserOverallProgress(Long userId) {
        User user = getUserById(userId);
        
        // Statistiques globales
        long totalCoursesCompleted = userCourseProgressRepository.countCompletedCoursesByUser(user);
        long totalCoursesStarted = userCourseProgressRepository.countStartedCoursesByUser(user);
        
        long totalExercisesCompleted = userExerciseProgressRepository.countByUserAndCompletedTrue(user);
        long totalExercisesStarted = userExerciseProgressRepository.countStartedExercisesByUser(user);
        
        long totalQuizzesCompleted = userQuizProgressRepository.countByUserAndCompletedTrue(user);
        long totalQuizzesStarted = userQuizProgressRepository.countStartedQuizzesByUser(user);
        
        // Parcours des blocs pour compter les modules
        List<Block> userBlocks = user.getBlocks();
        long totalModules = 0;
        long completedModules = 0;
        
        for (Block block : userBlocks) {
            for (Module module : block.getModules()) {
                totalModules++;
                if (isModuleCompleted(user, module)) {
                    completedModules++;
                }
            }
        }
        
        // Calcul des taux et score moyen
        double coursesRate = totalCoursesStarted > 0 ? (totalCoursesCompleted * 100.0 / totalCoursesStarted) : 0;
        double exercisesRate = totalExercisesStarted > 0 ? (totalExercisesCompleted * 100.0 / totalExercisesStarted) : 0;
        double quizzesRate = totalQuizzesStarted > 0 ? (totalQuizzesCompleted * 100.0 / totalQuizzesStarted) : 0;
        double overallRate = totalModules > 0 ? (completedModules * 100.0 / totalModules) : 0;
        
        // Score moyen des quiz
        double avgQuizScore = calculateAverageQuizScore(user);
        
        // Niveau (basé sur XP)
        int currentLevel = calculateLevelFromXp(user.getXp());
        
        return OverallProgressDTO.builder()
                .userId(userId)
                .userName(user.getFullName())
                .totalXp(user.getXp())
                .currentLevel(currentLevel)
                .totalCoursesStarted(totalCoursesStarted)
                .totalCoursesCompleted(totalCoursesCompleted)
                .coursesCompletionRate(Math.round(coursesRate * 100.0) / 100.0)
                .totalExercisesCompleted(totalExercisesCompleted)
                .totalExercisesStarted(totalExercisesStarted)
                .exercisesCompletionRate(Math.round(exercisesRate * 100.0) / 100.0)
                .totalQuizzesCompleted(totalQuizzesCompleted)
                .totalQuizzesStarted(totalQuizzesStarted)
                .quizzesCompletionRate(Math.round(quizzesRate * 100.0) / 100.0)
                .averageQuizScore(Math.round(avgQuizScore * 100.0) / 100.0)
                .totalModules(totalModules)
                .completedModules(completedModules)
                .overallPercentComplete(Math.round(overallRate * 100.0) / 100.0)
                .nextRecommendedAction(determineNextAction(user, userBlocks))
                .build();
    }
    
    // ==================== MÉTHODES PRIVÉES UTILITAIRES ====================
    
    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }
    
    private Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found: " + id));
    }
    
    private Exercise getExerciseById(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise not found: " + id));
    }
    
    private Quiz getQuizById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found: " + id));
    }
    
    private Module getModuleById(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found: " + id));
    }
    
    private Module getModuleByQuiz(Quiz quiz) {
        return moduleRepository.findByQuiz(quiz)
                .orElse(null);
    }
    
    private double calculateAverageQuizScore(User user) {
        List<UserQuizProgress> completedQuizzes = userQuizProgressRepository.findByUser(user).stream()
                .filter(UserQuizProgress::getCompleted)
                .filter(q -> q.getScore() != null)
                .collect(Collectors.toList());
        
        if (completedQuizzes.isEmpty()) return 0.0;
        
        return completedQuizzes.stream()
                .mapToInt(UserQuizProgress::getScore)
                .average()
                .orElse(0.0);
    }
    
    private int calculateLevelFromXp(int xp) {
        // Formule simple : niveau = 1 + (XP / 100)
        return 1 + (xp / 100);
    }
    
    private String determineNextAction(User user, List<Block> blocks) {
        // Trouver le premier cours non complété
        for (Block block : blocks) {
            for (Module module : block.getModules()) {
                for (Course course : module.getCourses()) {
                    boolean completed = userCourseProgressRepository
                            .existsByUserAndCourseAndCompletedTrue(user, course);
                    if (!completed) {
                        return "Complete course: " + course.getName();
                    }
                }
                for (Exercise exercise : module.getExercises()) {
                    boolean completed = userExerciseProgressRepository
                            .findByUserAndExercise(user, exercise)
                            .map(UserExerciseProgress::getCompleted)
                            .orElse(false);
                    if (!completed) {
                        return "Complete exercise: " + exercise.getName();
                    }
                }
                if (module.getQuiz() != null) {
                    boolean quizCompleted = userQuizProgressRepository
                            .existsByUserAndQuizAndCompletedTrue(user, module.getQuiz());
                    if (!quizCompleted) {
                        return "Complete quiz: " + module.getQuiz().getName();
                    }
                }
            }
        }
        return "All completed! Great job! felications ...";
    }
}