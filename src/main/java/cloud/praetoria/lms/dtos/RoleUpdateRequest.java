package cloud.praetoria.lms.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import cloud.praetoria.lms.enums.RoleName;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleUpdateRequest {

    @NotNull(message = "Le rôle est obligatoire")
    private RoleName roleName;
}