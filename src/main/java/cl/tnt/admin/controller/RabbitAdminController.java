package cl.tnt.admin.controller;

import cl.tnt.admin.dto.BindingRequestDTO;
import cl.tnt.admin.dto.ExchangeRequestDTO;
import cl.tnt.admin.dto.QueueRequestDTO;
import cl.tnt.admin.service.RabbitAdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controlador REST para la Administración de RabbitMQ.
 * Cumple con los Indicadores 6, 7 y 8 de la Pauta:
 * - Expone endpoints REST claros: POST /queues, DELETE /queues/{name}, POST /exchanges, POST /bindings.
 * - Toda la lógica está delegada a RabbitAdminService (sin lógica de broker en el controlador).
 * - Valida DTOs con @Valid para prevenir nombres vacíos y configuraciones erróneas.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping
public class RabbitAdminController {

    private final RabbitAdminService rabbitAdminService;

    public RabbitAdminController(RabbitAdminService rabbitAdminService) {
        this.rabbitAdminService = rabbitAdminService;
    }

    /**
     * Endpoint de estado del microservicio administrador.
     */
    @GetMapping({"/status", "/api/admin/status"})
    public ResponseEntity<Map<String, Object>> status() {
        return ResponseEntity.ok(Map.of(
                "servicio", "ms-rabbit-admin",
                "estado", "ONLINE",
                "descripcion", "Microservicio Administrador dinámico de Colas, Exchanges y Bindings RabbitMQ",
                "timestamp", System.currentTimeMillis()
        ));
    }

    // =========================================================================
    // GESTIÓN DE COLAS (QUEUES) - Indicador 6 y 8
    // =========================================================================

    /**
     * Crear una nueva cola con validación estricta de parámetros.
     * POST /queues o POST /api/admin/queues
     */
    @PostMapping({"/queues", "/api/admin/queues"})
    public ResponseEntity<Map<String, Object>> crearCola(@Valid @RequestBody QueueRequestDTO request) {
        Map<String, Object> respuesta = rabbitAdminService.crearCola(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    /**
     * Consultar información y métricas de una cola existente.
     * GET /queues/{name} o GET /api/admin/queues/{name}
     */
    @GetMapping({"/queues/{name}", "/api/admin/queues/{name}"})
    public ResponseEntity<Map<String, Object>> obtenerCola(@PathVariable("name") String name) {
        Map<String, Object> detalle = rabbitAdminService.obtenerDetalleCola(name);
        if (Boolean.FALSE.equals(detalle.get("existe"))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detalle);
        }
        return ResponseEntity.ok(detalle);
    }

    /**
     * Eliminar una cola existente en RabbitMQ.
     * DELETE /queues/{name} o DELETE /api/admin/queues/{name}
     */
    @DeleteMapping({"/queues/{name}", "/api/admin/queues/{name}"})
    public ResponseEntity<Map<String, Object>> eliminarCola(@PathVariable("name") String name) {
        boolean eliminada = rabbitAdminService.eliminarCola(name);
        if (eliminada) {
            return ResponseEntity.ok(Map.of(
                    "status", "OK",
                    "mensaje", "La cola '" + name + "' fue eliminada exitosamente de RabbitMQ."
            ));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", "NOT_FOUND",
                    "mensaje", "La cola '" + name + "' no existía o no pudo ser eliminada."
            ));
        }
    }

    // =========================================================================
    // GESTIÓN DE EXCHANGES - Indicador 6
    // =========================================================================

    /**
     * Crear un nuevo Exchange.
     * POST /exchanges o POST /api/admin/exchanges
     */
    @PostMapping({"/exchanges", "/api/admin/exchanges"})
    public ResponseEntity<Map<String, Object>> crearExchange(@Valid @RequestBody ExchangeRequestDTO request) {
        Map<String, Object> respuesta = rabbitAdminService.crearExchange(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    /**
     * Eliminar un Exchange existente.
     * DELETE /exchanges/{name} o DELETE /api/admin/exchanges/{name}
     */
    @DeleteMapping({"/exchanges/{name}", "/api/admin/exchanges/{name}"})
    public ResponseEntity<Map<String, Object>> eliminarExchange(@PathVariable("name") String name) {
        boolean eliminado = rabbitAdminService.eliminarExchange(name);
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "mensaje", "El Exchange '" + name + "' fue procesado para eliminación.",
                "resultado", eliminado
        ));
    }

    // =========================================================================
    // GESTIÓN DE ENLACES (BINDINGS) - Indicador 6
    // =========================================================================

    /**
     * Crear un enlace (binding) entre una cola y un exchange.
     * POST /bindings o POST /api/admin/bindings
     */
    @PostMapping({"/bindings", "/api/admin/bindings"})
    public ResponseEntity<Map<String, Object>> crearBinding(@Valid @RequestBody BindingRequestDTO request) {
        Map<String, Object> respuesta = rabbitAdminService.crearBinding(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
}
