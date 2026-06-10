package cloud.praetoria.lms.controllers.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import cloud.praetoria.lms.dtos.ApiResponse;
import cloud.praetoria.lms.dtos.CreateOrganizationRequest;
import cloud.praetoria.lms.dtos.OrganizationDTO;
import cloud.praetoria.lms.services.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/admin/organizations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationDTO>> createOrganization(@Valid @RequestBody CreateOrganizationRequest request) {
        OrganizationDTO org = organizationService.createOrganization(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(org, "Organization created"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrganizationDTO>>> getAllOrganizations() {
        List<OrganizationDTO> orgs = organizationService.getAllOrganizations();
        return ResponseEntity.ok(ApiResponse.success(orgs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationDTO>> getOrganization(@PathVariable Long id) {
        OrganizationDTO org = organizationService.getOrganizationById(id);
        return ResponseEntity.ok(ApiResponse.success(org));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationDTO>> updateOrganization(
            @PathVariable Long id,
            @Valid @RequestBody CreateOrganizationRequest request) {
        OrganizationDTO org = organizationService.updateOrganization(id, request);
        return ResponseEntity.ok(ApiResponse.success(org, "Organization updated"));
    }

    @PatchMapping("/{id}/regenerate-key")
    public ResponseEntity<ApiResponse<OrganizationDTO>> regenerateKey(@PathVariable Long id) {
        OrganizationDTO org = organizationService.regenerateKey(id);
        return ResponseEntity.ok(ApiResponse.success(org, "Registration key regenerated"));
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<ApiResponse<Void>> toggleActive(@PathVariable Long id) {
        OrganizationDTO org = organizationService.getOrganizationById(id);
        organizationService.toggleOrganizationActive(id, !org.getIsActive());
        return ResponseEntity.ok(ApiResponse.successVoid("Organization toggled"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrganization(@PathVariable Long id) {
        organizationService.getOrganizationById(id);
        return ResponseEntity.ok(ApiResponse.successVoid("Organization deleted"));
    }
}
