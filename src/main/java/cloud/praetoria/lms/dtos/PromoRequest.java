package cloud.praetoria.lms.dtos;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PromoRequest {

    @NotBlank(message = "Le nom de la promotion est obligatoire")
    private String name;

    @NotNull(message = "La date de début de la promotion est obligatoire")
    private LocalDate startDate;

    @NotNull(message = "La date de fin de la promotion est obligatoire")
    private LocalDate endDate;

    /**
     * Vérifie que la date de fin est strictement après la date de début.
     *
     * @return true si la contrainte est respectée
     */
    @AssertTrue(message = "La date de fin doit être strictement après la date de début")
    public boolean isEndDateAfterStartDate() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return endDate.isAfter(startDate);
    }
}
