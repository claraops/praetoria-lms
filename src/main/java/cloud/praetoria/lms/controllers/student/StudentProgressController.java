package cloud.praetoria.lms.controllers.student;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import cloud.praetoria.lms.dtos.ApiResponse;
import cloud.praetoria.lms.dtos.ModuleProgressDTO;
import cloud.praetoria.lms.dtos.OverallProgressDTO;
import cloud.praetoria.lms.services.student.StudentProgressService;

@Slf4j
@RestController
@RequestMapping("/api/student/progress")
@RequiredArgsConstructor
@Tag(name = "Student - Progression", description = "Endpoints pour l'étudiant - Consultation de sa progression")
@PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
public class StudentProgressController {

    private final StudentProgressService studentProgressService;

    @GetMapping("/me")
    @Operation(summary = "Progression globale de l'étudiant connecté")
    public ResponseEntity<ApiResponse<OverallProgressDTO>> getMyOverallProgress() {
        OverallProgressDTO progress = studentProgressService.getMyOverallProgress();
        return ResponseEntity.ok(ApiResponse.success(progress));
    }

    @GetMapping("/me/modules/{moduleId}")
    @Operation(summary = "Progression détaillée sur un module spécifique")
    public ResponseEntity<ApiResponse<ModuleProgressDTO>> getMyModuleProgress(@PathVariable Long moduleId) {
        ModuleProgressDTO progress = studentProgressService.getMyModuleProgress(moduleId);
        return ResponseEntity.ok(ApiResponse.success(progress));
    }
}