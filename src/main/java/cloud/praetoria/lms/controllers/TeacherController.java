package cloud.praetoria.lms.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cloud.praetoria.lms.dtos.ApiResponse;
import cloud.praetoria.lms.dtos.ModuleProgressDTO;
import cloud.praetoria.lms.dtos.ModuleResponse;  // ← AJOUTER CET IMPORT
import cloud.praetoria.lms.dtos.OverallProgressDTO;
import cloud.praetoria.lms.entities.Module;
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.repositories.ModuleRepository;
import cloud.praetoria.lms.repositories.UserRepository;
import cloud.praetoria.lms.services.CurrentUserService;
import cloud.praetoria.lms.services.ProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
@Tag(name = "Teacher", description = "Endpoints pour les enseignants - Gestion des étudiants et progression")
@PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
public class TeacherController {

    private final CurrentUserService currentUserService;
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;
    private final ProgressService progressService;

    /**
     * Récupère tous les modules gérés par l'enseignant connecté
     * Note: Adaptez selon votre logique d'association Teacher-Module
     */
    @GetMapping("/modules")
    @Operation(summary = "Modules gérés par l'enseignant connecté")
    public ResponseEntity<ApiResponse<List<ModuleResponse>>> getTeacherModules() {  // ← ModuleResponse au lieu de Module
        User teacher = currentUserService.getCurrentUser();
        
        // TODO: Filtrer par enseignant selon votre modèle
        List<Module> modules = moduleRepository.findAll();
        
        // Convertir Module → ModuleResponse
        List<ModuleResponse> moduleResponses = modules.stream()
                .map(this::convertToModuleResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(moduleResponses));
    }

    /**
     * Liste les étudiants d'un module avec leur progression
     */
    @GetMapping("/modules/{moduleId}/students")
    @Operation(summary = "Liste des étudiants d'un module avec progression")
    public ResponseEntity<ApiResponse<List<StudentProgressSummaryDTO>>> getModuleStudents(@PathVariable Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module non trouvé: " + moduleId));
        
        // Récupérer les étudiants via l'organisation (alternative)
        List<User> students = userRepository.findByBlocksContaining(module.getBlock());
        
        List<StudentProgressSummaryDTO> result = students.stream()
                .map(student -> {
                    ModuleProgressDTO progress = progressService.getUserProgressForModule(student.getId(), moduleId);
                    return StudentProgressSummaryDTO.builder()
                            .studentId(student.getId())
                            .studentName(student.getFullName())
                            .studentEmail(student.getEmail())
                            .moduleProgress(progress)
                            .build();
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Progression détaillée d'un étudiant spécifique
     */
    @GetMapping("/students/{studentId}/progress")
    @Operation(summary = "Progression globale d'un étudiant")
    public ResponseEntity<ApiResponse<OverallProgressDTO>> getStudentProgress(@PathVariable Long studentId) {
        OverallProgressDTO progress = progressService.getUserOverallProgress(studentId);
        return ResponseEntity.ok(ApiResponse.success(progress));
    }

    /**
     * Convertit un Module en ModuleResponse
     */
    private ModuleResponse convertToModuleResponse(Module module) {
    	 return ModuleResponse.fromEntity(module);
        /*return ModuleResponse.builder()
                .id(module.getId())
                .name(module.getName())
                .description(module.getDescription())
                .blockId(module.getBlock() != null ? module.getBlock().getId() : null)
                .blockName(module.getBlock() != null ? module.getBlock().getName() : null)
                .quizId(module.getQuiz() != null ? module.getQuiz().getId() : null)
                .quizName(module.getQuiz() != null ? module.getQuiz().getName() : null)
                .createdAt(module.getCreatedAt())
                .updatedAt(module.getUpdatedAt())
                .build();*/
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class StudentProgressSummaryDTO {
        private Long studentId;
        private String studentName;
        private String studentEmail;
        private ModuleProgressDTO moduleProgress;
    }
}