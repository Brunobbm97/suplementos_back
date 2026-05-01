package com.suplementos.erp_suplementos.modules.sales.dto.request;

import com.suplementos.erp_suplementos.modules.sales.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class SaleRequestDTO {

    @NotNull(message = "O ID da loja/localização é obrigatório")
    private Long locationId;

    @NotEmpty(message = "A venda deve conter pelo menos um item")
    @Valid
    private List<SaleItemRequestDTO> items;

    // Se você adotar o Enum:
    @NotNull(message = "A forma de pagamento é obrigatória")
    private PaymentMethod paymentMethod;
}