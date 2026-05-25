package cloud.praetoria.lms.controllers.student;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import cloud.praetoria.lms.dtos.ApiResponse;
import cloud.praetoria.lms.dtos.QuizSummaryDTO;
import cloud.praetoria.lms.dtos.StudentSubmitRequest;
import cloud.praetoria.lms.services.student.StudentQuizService;

@Slf4j
@RestController
@RequestMapping("/api/student/quizzes")
@RequiredArgsConstructor
@Tag(name = "Student - Quiz", description = "Endpoints pour l'étudiant - Consultation et soumission des quiz")
@PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
public class StudentQuizController {

    private final StudentQuizService studentQuizService;

    @GetMapping("/{quizId}")
    @Operation(summary = "Récupérer le contenu d'un quiz")
    public ResponseEntity<ApiResponse<QuizSummaryDTO>> getQuizContent(@PathVariable Long quizId) {
        QuizSummaryDTO quiz = studentQuizService.getQuizContent(quizId);
        return ResponseEntity.ok(ApiResponse.success(quiz));
    }

    @PostMapping("/{quizId}/submit")
    @Operation(summary = "Soumettre les réponses d'un quiz")
    public ResponseEntity<ApiResponse<Void>> submitQuiz(
            @PathVariable Long quizId,
            @Valid @RequestBody StudentSubmitRequest request) {
        studentQuizService.submitQuiz(quizId, request.getScore());
        return ResponseEntity.ok(ApiResponse.successVoid("Quiz soumis avec succès"));
    }
}