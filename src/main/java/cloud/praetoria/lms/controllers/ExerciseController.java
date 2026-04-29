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
import cloud.praetoria.lms.dtos.ExerciseRequest;
import cloud.praetoria.lms.dtos.ExerciseResponse;
import cloud.praetoria.lms.security.UserDetailsImpl;
import cloud.praetoria.lms.services.ExerciseService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
@Tag(name = "Exercices", description = "Endpoints pour la gestion des exercices")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping
    @Operation(summary = "Lister tous les exercices")
    public ResponseEntity<ApiResponse<List<ExerciseResponse>>> getAllExercises(
    		@AuthenticationPrincipal UserDetailsImpl userDetails) {
    	
    	 // Vérification de sécurité (sans commentaire bloquant)
        if (userDetails == null) {
            log.error("userDetails est null - Vérifiez la configuration Spring Security");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Utilisateur non authentifié"));
        }
        
        log.debug("✅ Récupération des cours pour: {}", userDetails.getEmail());
    	List<ExerciseResponse> exercises = exerciseService.getAllExercises(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(exercises));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un exercice par son identifiant")
    public ResponseEntity<ApiResponse<ExerciseResponse>> getExerciseById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ExerciseResponse exercise = exerciseService.getExerciseById(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(exercise));
    }

    @GetMapping("/module/{moduleId}")
    @Operation(summary = "Lister les exercices d'un module")
    public ResponseEntity<ApiResponse<List<ExerciseResponse>>> getExercisesByModuleId(
            @PathVariable Long moduleId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<ExerciseResponse> exercises = exerciseService.getExercisesByModuleId(moduleId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(exercises));
    }

    @PostMapping
    @Operation(summary = "Créer un exercice")
    public ResponseEntity<ApiResponse<ExerciseResponse>> createExercise(@Valid @RequestBody ExerciseRequest exerciseRequest) {
        ExerciseResponse exerciseResponse = exerciseService.createExercise(exerciseRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(exerciseResponse, "Exercice créé avec succès"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un exercice")
    public ResponseEntity<ApiResponse<ExerciseResponse>> updateExercise(
            @PathVariable Long id,
            @Valid @RequestBody ExerciseRequest exerciseRequest) {
        ExerciseResponse exerciseResponse = exerciseService.updateExercise(id, exerciseRequest);
        return ResponseEntity.ok(ApiResponse.success(exerciseResponse, "Exercice mis à jour avec succès"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un exercice")
    public ResponseEntity<ApiResponse<Void>> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.ok(ApiResponse.successVoid("Exercice supprimé avec succès"));
    }

   
}