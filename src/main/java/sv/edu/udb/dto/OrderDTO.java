package sv.edu.udb.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import sv.edu.udb.model.Order;

import java.util.List;

@Data
public class OrderDTO {
    
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long customerId;
    
    @NotEmpty(message = "Debe incluir al menos un detalle de pedido")
    @Valid
    private List<OrderDetailDTO> orderDetails;
    
    @NotNull(message = "El método de pago es obligatorio")
    private Order.PaymentMethod paymentMethod;
}
