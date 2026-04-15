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
import cloud.praetoria.lms.dtos.PromoRequest;
import cloud.praetoria.lms.dtos.PromoResponse;
import cloud.praetoria.lms.services.PromoService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/promos")
@RequiredArgsConstructor
@Tag(name = "Promotions", description = "Endpoints pour la gestion des promotions")
public class PromoController {

    private final PromoService promoService;

    @GetMapping
    @Operation(summary = "Lister toutes les promotions")
    public ResponseEntity<ApiResponse<List<PromoResponse>>> getAllPromos() {
        List<PromoResponse> promoResponses = promoService.getAllPromos();
        return ResponseEntity.ok(ApiResponse.success(promoResponses));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une promotion par son identifiant")
    public ResponseEntity<ApiResponse<PromoResponse>> getPromoById(@PathVariable Long id) {
        PromoResponse promoResponse = promoService.getPromoById(id);
        return ResponseEntity.ok(ApiResponse.success(promoResponse));
    }

    @PostMapping
    @Operation(summary = "Créer une promotion")
    public ResponseEntity<ApiResponse<PromoResponse>> createPromo(@Valid @RequestBody PromoRequest promoRequest) {
        PromoResponse promoResponse = promoService.createPromo(promoRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(promoResponse, "Promotion créée avec succès"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une promotion")
    public ResponseEntity<ApiResponse<PromoResponse>> updatePromo(
            @PathVariable Long id,
            @Valid @RequestBody PromoRequest promoRequest) {
        PromoResponse promoResponse = promoService.updatePromo(id, promoRequest);
        return ResponseEntity.ok(ApiResponse.success(promoResponse, "Promotion mise à jour avec succès"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une promotion")
    public ResponseEntity<ApiResponse<Void>> deletePromo(@PathVariable Long id) {
        promoService.deletePromo(id);
        return ResponseEntity.ok(ApiResponse.successVoid("Promotion supprimée avec succès"));
    }

    @PatchMapping("/{id}/active")
    @Operation(summary = "Activer ou désactiver une promotion")
    public ResponseEntity<ApiResponse<PromoResponse>> toggleActive(@PathVariable Long id) {
        PromoResponse promoResponse = promoService.toggleActive(id);
        return ResponseEntity.ok(ApiResponse.success(promoResponse, "État de la promotion mis à jour"));
    }
}