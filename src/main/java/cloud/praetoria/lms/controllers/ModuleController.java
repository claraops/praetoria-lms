package cloud.praetoria.lms.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cloud.praetoria.lms.dtos.ApiResponse;
import cloud.praetoria.lms.dtos.ModuleRequest;
import cloud.praetoria.lms.dtos.ModuleResponse;
import cloud.praetoria.lms.services.ModuleService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
@Tag(name = "Modules", description = "Endpoints pour la gestion des modules")
public class ModuleController {

    private final ModuleService moduleService;

    @GetMapping
    @Operation(summary = "Lister tous les modules")
    public ResponseEntity<ApiResponse<List<ModuleResponse>>> getAllModules() {
        List<ModuleResponse> modules = moduleService.getAllModules();
        return ResponseEntity.ok(ApiResponse.success(modules));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un module par son identifiant")
    public ResponseEntity<ApiResponse<ModuleResponse>> getModuleById(@PathVariable Long id) {
        ModuleResponse module = moduleService.getModuleById(id);
        return ResponseEntity.ok(ApiResponse.success(module));
    }

    @GetMapping("/block/{blockId}")
    @Operation(summary = "Lister les modules d'un bloc")
    public ResponseEntity<ApiResponse<List<ModuleResponse>>> getModulesByBlockId(@PathVariable Long blockId) {
        List<ModuleResponse> modules = moduleService.getModulesByBlockId(blockId);
        return ResponseEntity.ok(ApiResponse.success(modules));
    }

    @PostMapping
    @Operation(summary = "Créer un module")
    public ResponseEntity<ApiResponse<ModuleResponse>> createModule(@Valid @RequestBody ModuleRequest moduleRequest) {
        ModuleResponse moduleResponse = moduleService.createModule(moduleRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(moduleResponse, "Module créé avec succès"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un module")
    public ResponseEntity<ApiResponse<ModuleResponse>> updateModule(
            @PathVariable Long id,
            @Valid @RequestBody ModuleRequest moduleRequest) {
        ModuleResponse moduleResponse = moduleService.updateModule(id, moduleRequest);
        return ResponseEntity.ok(ApiResponse.success(moduleResponse, "Module mis à jour avec succès"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un module")
    public ResponseEntity<ApiResponse<Void>> deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.ok(ApiResponse.successVoid("Module supprimé avec succès"));
    }
}