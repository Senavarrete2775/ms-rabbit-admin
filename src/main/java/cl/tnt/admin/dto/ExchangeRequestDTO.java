package cl.tnt.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de exchanges con validación estricta de parámetros.
 */
public class ExchangeRequestDTO {

    @NotBlank(message = "El nombre del exchange es obligatorio y no puede estar vacío.")
    @Size(min = 3, max = 80, message = "El nombre del exchange debe tener entre 3 y 80 caracteres.")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El nombre del exchange solo puede contener caracteres alfanuméricos, puntos y guiones.")
    private String name;

    @NotBlank(message = "El tipo de exchange es obligatorio (direct, topic, fanout, headers).")
    @Pattern(regexp = "^(direct|topic|fanout|headers)$", message = "El tipo de exchange debe ser 'direct', 'topic', 'fanout' o 'headers'.")
    private String type = "topic";

    private boolean durable = true;
    private boolean autoDelete = false;

    public ExchangeRequestDTO() {
    }

    public ExchangeRequestDTO(String name, String type, boolean durable, boolean autoDelete) {
        this.name = name;
        this.type = type;
        this.durable = durable;
        this.autoDelete = autoDelete;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDurable() {
        return durable;
    }

    public void setDurable(boolean durable) {
        this.durable = durable;
    }

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public void setAutoDelete(boolean autoDelete) {
        this.autoDelete = autoDelete;
    }
}
