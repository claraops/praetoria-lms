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
import cloud.praetoria.lms.dtos.ExerciseSummaryDTO;
import cloud.praetoria.lms.dtos.StudentSubmitRequest;
import cloud.praetoria.lms.services.student.StudentExerciseService;

@Slf4j
@RestController
@RequestMapping("/api/student/exercises")
@RequiredArgsConstructor
@Tag(name = "Student - Exercices", description = "Endpoints pour l'étudiant - Consultation et soumission des exercices")
@PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
public class StudentExerciseController {

    private final StudentExerciseService studentExerciseService;

    @GetMapping("/{exerciseId}")
    @Operation(summary = "Récupérer le contenu d'un exercice")
    public ResponseEntity<ApiResponse<ExerciseSummaryDTO>> getExerciseContent(@PathVariable Long exerciseId) {
        ExerciseSummaryDTO exercise = studentExerciseService.getExerciseContent(exerciseId);
        return ResponseEntity.ok(ApiResponse.success(exercise));
    }

    @PostMapping("/{exerciseId}/submit")
    @Operation(summary = "Soumettre la réponse d'un exercice")
    public ResponseEntity<ApiResponse<Void>> submitExercise(
            @PathVariable Long exerciseId,
            @Valid @RequestBody StudentSubmitRequest request) {
        studentExerciseService.submitExercise(exerciseId, request.getScore());
        return ResponseEntity.ok(ApiResponse.successVoid("Exercice soumis avec succès"));
    }
}
