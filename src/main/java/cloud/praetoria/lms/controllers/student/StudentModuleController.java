package cloud.praetoria.lms.controllers.student;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import cloud.praetoria.lms.dtos.ApiResponse;
import cloud.praetoria.lms.dtos.ModuleDetailDTO;
import cloud.praetoria.lms.dtos.ModuleProgressDTO;
import cloud.praetoria.lms.services.student.StudentModuleService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@Tag(name = "Student - Modules", description = "Endpoints pour l'étudiant - Consultation des modules")
@PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
public class StudentModuleController {

    private final StudentModuleService studentModuleService;

    @GetMapping("/blocks/{blockId}/modules")
    @Operation(summary = "Lister les modules d'un bloc avec progression")
    public ResponseEntity<ApiResponse<List<ModuleProgressDTO>>> getModulesByBlock(@PathVariable Long blockId) {
        List<ModuleProgressDTO> modules = studentModuleService.getModulesByBlockWithProgress(blockId);
        return ResponseEntity.ok(ApiResponse.success(modules));
    }

    @GetMapping("/modules/{moduleId}")
    @Operation(summary = "Détail complet d'un module (cours, exercices, quiz, statut)")
    public ResponseEntity<ApiResponse<ModuleDetailDTO>> getModuleDetail(@PathVariable Long moduleId) {
        ModuleDetailDTO moduleDetail = studentModuleService.getModuleDetail(moduleId);
        return ResponseEntity.ok(ApiResponse.success(moduleDetail));
    }
}


