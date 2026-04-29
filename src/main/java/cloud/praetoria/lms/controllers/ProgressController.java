package cloud.praetoria.lms.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import cloud.praetoria.lms.dtos.ApiResponse;
import cloud.praetoria.lms.dtos.ModuleProgressDTO;
import cloud.praetoria.lms.dtos.OverallProgressDTO;
import cloud.praetoria.lms.security.UserDetailsImpl;
import cloud.praetoria.lms.services.ProgressService;

@Slf4j
@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@Tag(name = "Progression", description = "Endpoints pour le suivi de progression")
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping("/module/{moduleId}")
    @Operation(summary = "Obtenir la progression d'un utilisateur pour un module")
    public ResponseEntity<ApiResponse<ModuleProgressDTO>> getModuleProgress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long moduleId) {
        ModuleProgressDTO progress = progressService.getUserProgressForModule(userDetails.getId(), moduleId);
        return ResponseEntity.ok(ApiResponse.success(progress));
    }

    @GetMapping("/overall")
    @Operation(summary = "Obtenir la progression globale d'un utilisateur")
    public ResponseEntity<ApiResponse<OverallProgressDTO>> getOverallProgress(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        OverallProgressDTO progress = progressService.getUserOverallProgress(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(progress));
    }

    @PostMapping("/course/{courseId}/start")
    @Operation(summary = "Démarrer un cours")
    public ResponseEntity<ApiResponse<Void>> startCourse(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long courseId) {
        progressService.startCourse(userDetails.getId(), courseId);
        return ResponseEntity.ok(ApiResponse.successVoid("Course started"));
    }

    @PostMapping("/course/{courseId}/complete")
    @Operation(summary = "Compléter un cours (+10 XP)")
    public ResponseEntity<ApiResponse<Void>> completeCourse(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long courseId) {
        progressService.completeCourse(userDetails.getId(), courseId);
        return ResponseEntity.ok(ApiResponse.successVoid("Course completed! +10 XP"));
    }

    @PostMapping("/exercise/{exerciseId}/complete")
    @Operation(summary = "Compléter un exercice")
    public ResponseEntity<ApiResponse<Void>> completeExercise(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long exerciseId,
            @RequestParam(required = false) Integer score) {
        progressService.completeExercise(userDetails.getId(), exerciseId, score);
        return ResponseEntity.ok(ApiResponse.successVoid("Exercise completed"));
    }

    @PostMapping("/quiz/{quizId}/complete")
    @Operation(summary = "Compléter un quiz")
    public ResponseEntity<ApiResponse<Void>> completeQuiz(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long quizId,
            @RequestParam Integer score) {
        progressService.completeQuiz(userDetails.getId(), quizId, score);
        return ResponseEntity.ok(ApiResponse.successVoid("Quiz completed"));
    }
}