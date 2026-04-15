package cloud.praetoria.lms.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogoutRequest {

    @NotBlank(message = "Le refresh token est obligatoire")
    private String refreshToken;
}

