package com.lms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleChangeRequest {
    @NotBlank
    @Pattern(regexp = "^(STUDENT|EDUCATOR|ADMIN)$", message = "Role must be STUDENT, EDUCATOR or ADMIN")
    private String role;
}
