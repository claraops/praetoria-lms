package cloud.praetoria.lms.services.impl;

import cloud.praetoria.lms.dtos.QuizRequest;
import cloud.praetoria.lms.dtos.QuizResponse;
import cloud.praetoria.lms.entities.Module;
import cloud.praetoria.lms.entities.Quiz;
import cloud.praetoria.lms.exceptions.DuplicateResourceException;
import cloud.praetoria.lms.exceptions.ResourceNotFoundException;
import cloud.praetoria.lms.repositories.ModuleRepository;
import cloud.praetoria.lms.repositories.QuizRepository;
import cloud.praetoria.lms.services.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final ModuleRepository moduleRepository;

    @Override
    public List<QuizResponse> getAllQuizzes(Long userId) {
        return quizRepository.findAll().stream()
                .map(QuizResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public QuizResponse getQuizById(Long id, Long userId) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz non trouvé"));
        return QuizResponse.fromEntity(quiz);
    }

    @Override
    @Transactional
    public QuizResponse createQuiz(QuizRequest quizRequest) {
        Module module = moduleRepository.findById(quizRequest.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module non trouvé"));
        
        if (module.getQuiz() != null) {
            throw new DuplicateResourceException("Ce module a déjà un quiz");
        }
        
        Quiz quiz = Quiz.builder()
                .name(quizRequest.getName())
                .content(quizRequest.getContent())
                .build();
        
        Quiz savedQuiz = quizRepository.save(quiz);
        module.setQuiz(savedQuiz);
        moduleRepository.save(module);
        
        return QuizResponse.fromEntity(savedQuiz);
    }

    @Override
    @Transactional
    public QuizResponse updateQuiz(Long id, QuizRequest quizRequest) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz non trouvé"));
        
        quiz.setName(quizRequest.getName());
        quiz.setContent(quizRequest.getContent());
        
        return QuizResponse.fromEntity(quizRepository.save(quiz));
    }

    @Override
    @Transactional
    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz non trouvé"));
        
        if (quiz.getModule() != null) {
            Module module = quiz.getModule();
            module.setQuiz(null);
            moduleRepository.save(module);
        }
        
        quizRepository.deleteById(id);
    }

    @Override
    @Transactional
    public QuizResponse toggleCompleted(Long quizId, Long userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz non trouvé"));
        return QuizResponse.fromEntity(quiz);
    }
}