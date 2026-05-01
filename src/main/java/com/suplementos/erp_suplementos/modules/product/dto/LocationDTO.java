package com.suplementos.erp_suplementos.modules.product.dto;

import com.suplementos.erp_suplementos.modules.product.enums.LocationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LocationDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String name;

    @NotNull(message = "O tipo de local é obrigatório")
    private LocationType type;

    @NotBlank(message = "A rua é obrigatória")
    private String street;

    @NotBlank(message = "O número é obrigatório")
    private String number;

    @NotBlank(message = "O bairro é obrigatório")
    private String neighborhood;

    private String city;
    private String state;
    private String zipCode;

    @NotBlank(message = "O telefone é obrigatório")
    private String phone;

    private String email;
}