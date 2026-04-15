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
import cloud.praetoria.lms.dtos.BlockRequest;
import cloud.praetoria.lms.dtos.BlockResponse;
import cloud.praetoria.lms.services.BlockService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
@Tag(name = "Blocs", description = "Endpoints pour la gestion des blocs")
public class BlockController {

    private final BlockService blockService;

    @GetMapping
    @Operation(summary = "Récupérer tous les blocs")
    public ResponseEntity<ApiResponse<List<BlockResponse>>> getAllBlocks() {
        List<BlockResponse> blocks = blockService.getAllBlocks();
        return ResponseEntity.ok(ApiResponse.success(blocks));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un bloc par son identifiant")
    public ResponseEntity<ApiResponse<BlockResponse>> getBlockById(@PathVariable Long id) {
        BlockResponse block = blockService.getBlockById(id);
        return ResponseEntity.ok(ApiResponse.success(block));
    }

    @PostMapping
    @Operation(summary = "Créer un bloc")
    public ResponseEntity<ApiResponse<BlockResponse>> createBlock(@Valid @RequestBody BlockRequest blockRequest) {
        BlockResponse blockResponse = blockService.createBlock(blockRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(blockResponse, "Bloc créé avec succès"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un bloc")
    public ResponseEntity<ApiResponse<BlockResponse>> updateBlock(
            @PathVariable Long id,
            @Valid @RequestBody BlockRequest blockRequest) {
        BlockResponse blockResponse = blockService.updateBlock(id, blockRequest);
        return ResponseEntity.ok(ApiResponse.success(blockResponse, "Bloc mis à jour avec succès"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un bloc")
    public ResponseEntity<ApiResponse<Void>> deleteBlock(@PathVariable Long id) {
        blockService.deleteBlock(id);
        return ResponseEntity.ok(ApiResponse.successVoid("Bloc supprimé avec succès"));
    }
}