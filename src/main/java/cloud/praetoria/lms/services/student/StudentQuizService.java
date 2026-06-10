package cloud.praetoria.lms.services.student;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.lms.dtos.QuizSummaryDTO;
import cloud.praetoria.lms.entities.Quiz;
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.entities.UserQuizProgress;
import cloud.praetoria.lms.repositories.QuizRepository;
import cloud.praetoria.lms.repositories.UserQuizProgressRepository;
import cloud.praetoria.lms.services.CurrentUserService;
import cloud.praetoria.lms.services.ProgressService;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentQuizService {

    private final CurrentUserService currentUserService;
    private final QuizRepository quizRepository;
    private final ProgressService progressService;
    private final UserQuizProgressRepository userQuizProgressRepository;

    /**
     * Récupère le contenu d'un quiz avec son statut
     */
    @Transactional(readOnly = true)
    public QuizSummaryDTO getQuizContent(Long quizId) {
        User user = currentUserService.getCurrentUser();
        
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé: " + quizId));
        
        UserQuizProgress progress = userQuizProgressRepository
                .findByUserAndQuiz(user, quiz).orElse(null);
        
        return QuizSummaryDTO.builder()
                .quizId(quiz.getId())
                .name(quiz.getName())
                .content(quiz.getContent())
                .isCompleted(progress != null && progress.getCompleted())
                .score(progress != null ? progress.getScore() : null)
                .attempts(progress != null ? progress.getAttempts() : 0)
                .build();
    }

    /**
     * Soumet les réponses d'un quiz
     */
    @Transactional
    public void submitQuiz(Long quizId, Integer score) {
        User user = currentUserService.getCurrentUser();
        progressService.completeQuiz(user.getId(), quizId, score);
        log.info("Quiz {} soumis par {} avec score {}", quizId, user.getEmail(), score);
    }
    
    @Transactional
    public int calculateAndSubmitQuiz(Long quizId, Map<Integer, String> answers) {
        User user = currentUserService.getCurrentUser();
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé"));
        
        int score = calculateScore(quiz, answers);
        
        progressService.completeQuiz(user.getId(), quizId, score);
        return score;
    }

    private int calculateScore(Quiz quiz, Map<Integer, String> answers) {
        // À implémenter selon votre format de quiz
        // Exemple: parsing du contenu JSON du quiz
        return 85; // Score calculé
    }
}
