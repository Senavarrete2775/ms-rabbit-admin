package cl.tnt.admin.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para la creación de enlaces (bindings) entre exchanges y colas.
 */
public class BindingRequestDTO {

    @NotBlank(message = "El nombre de la cola destino es obligatorio.")
    private String queueName;

    @NotBlank(message = "El nombre del exchange origen es obligatorio.")
    private String exchangeName;

    @NotBlank(message = "El routing key de enlace es obligatorio.")
    private String routingKey;

    public BindingRequestDTO() {
    }

    public BindingRequestDTO(String queueName, String exchangeName, String routingKey) {
        this.queueName = queueName;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }
}
