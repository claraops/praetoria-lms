package cloud.praetoria.lms.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import cloud.praetoria.lms.dtos.ApiResponse;
import cloud.praetoria.lms.dtos.QuizRequest;
import cloud.praetoria.lms.dtos.QuizResponse;
import cloud.praetoria.lms.security.UserDetailsImpl;
import cloud.praetoria.lms.services.QuizService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
@Tag(name = "Quiz", description = "Endpoints pour la gestion des quiz")
public class QuizController {

    private final QuizService quizService;

    @GetMapping
    @Operation(summary = "Lister tous les quiz")
    public ResponseEntity<ApiResponse<List<QuizResponse>>> getAllQuizzes(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<QuizResponse> quizzes = quizService.getAllQuizzes(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(quizzes));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un quiz par son identifiant")
    public ResponseEntity<ApiResponse<QuizResponse>> getQuizById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        QuizResponse quiz = quizService.getQuizById(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(quiz));
    }

    @PostMapping
    @Operation(summary = "Créer un quiz")
    public ResponseEntity<ApiResponse<QuizResponse>> createQuiz(@Valid @RequestBody QuizRequest quizRequest) {
        QuizResponse quizResponse = quizService.createQuiz(quizRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(quizResponse, "Quiz créé avec succès"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un quiz")
    public ResponseEntity<ApiResponse<QuizResponse>> updateQuiz(
            @PathVariable Long id,
            @Valid @RequestBody QuizRequest quizRequest) {
        QuizResponse quizResponse = quizService.updateQuiz(id, quizRequest);
        return ResponseEntity.ok(ApiResponse.success(quizResponse, "Quiz mis à jour avec succès"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un quiz")
    public ResponseEntity<ApiResponse<Void>> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.ok(ApiResponse.successVoid("Quiz supprimé avec succès"));
    }

    @PatchMapping("/{id}/completed")
    @Operation(summary = "Marquer un quiz comme complété ou non complété")
    public ResponseEntity<ApiResponse<QuizResponse>> toggleCompleted(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        QuizResponse quizResponse = quizService.toggleCompleted(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(quizResponse, "État du quiz mis à jour"));
    }
}