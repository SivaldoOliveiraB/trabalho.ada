package trabalho.ada.resource.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "E-mail é obrigatório")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        String senha

) {
}

