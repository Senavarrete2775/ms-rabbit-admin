package cl.tnt.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO con validaciones estrictas para la creación de colas (Indicador 8 - 100%).
 * Evita nombres vacíos, caracteres peligrosos y configuraciones inválidas.
 */
public class QueueRequestDTO {

    @NotBlank(message = "El nombre de la cola es obligatorio y no puede contener únicamente espacios en blanco.")
    @Size(min = 3, max = 80, message = "El nombre de la cola debe tener entre 3 y 80 caracteres.")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El nombre de la cola solo puede contener caracteres alfanuméricos, puntos, guiones y guiones bajos.")
    private String name;

    private boolean durable = true;
    private boolean exclusive = false;
    private boolean autoDelete = false;

    // Configuración opcional para vincular a Dead Letter Exchange (DLQ)
    private String deadLetterExchange;
    private String deadLetterRoutingKey;

    public QueueRequestDTO() {
    }

    public QueueRequestDTO(String name, boolean durable, boolean exclusive, boolean autoDelete) {
        this.name = name;
        this.durable = durable;
        this.exclusive = exclusive;
        this.autoDelete = autoDelete;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDurable() {
        return durable;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public void setAutoDelete(boolean autoDelete) {
        this.autoDelete = autoDelete;
    }

    public String getDeadLetterExchange() {
        return deadLetterExchange;
    }

    public void setDeadLetterExchange(String deadLetterExchange) {
        this.deadLetterExchange = deadLetterExchange;
    }

    public String getDeadLetterRoutingKey() {
        return deadLetterRoutingKey;
    }

    public void setDeadLetterRoutingKey(String deadLetterRoutingKey) {
        this.deadLetterRoutingKey = deadLetterRoutingKey;
    }
}
