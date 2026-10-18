package cl.tnt.admin.service;

import cl.tnt.admin.dto.BindingRequestDTO;
import cl.tnt.admin.dto.ExchangeRequestDTO;
import cl.tnt.admin.dto.QueueRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Servicio dedicado para encapsular toda la lógica administrativa de RabbitMQ (Indicador 7 - 100%).
 * Los controladores llaman exclusivamente a estos métodos de alto nivel sin manipular
 * objetos ni detalles internos de conexión con el broker.
 */
@Service
public class RabbitAdminService {

    private static final Logger log = LoggerFactory.getLogger(RabbitAdminService.class);

    private final RabbitAdmin rabbitAdmin;

    public RabbitAdminService(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    /**
     * Declara dinámicamente una nueva cola en RabbitMQ con soporte para DLQ.
     */
    public Map<String, Object> crearCola(QueueRequestDTO dto) {
        log.info("Creando cola '{}' en RabbitMQ (durable={}, exclusive={}, autoDelete={})",
                dto.getName(), dto.isDurable(), dto.isExclusive(), dto.isAutoDelete());

        QueueBuilder builder = dto.isDurable() ? QueueBuilder.durable(dto.getName()) : QueueBuilder.nonDurable(dto.getName());
        
        if (dto.isExclusive()) {
            builder.exclusive();
        }
        if (dto.isAutoDelete()) {
            builder.autoDelete();
        }

        // Si se especificó Dead Letter Exchange para la cola
        if (dto.getDeadLetterExchange() != null && !dto.getDeadLetterExchange().isBlank()) {
            builder.withArgument("x-dead-letter-exchange", dto.getDeadLetterExchange().trim());
            if (dto.getDeadLetterRoutingKey() != null && !dto.getDeadLetterRoutingKey().isBlank()) {
                builder.withArgument("x-dead-letter-routing-key", dto.getDeadLetterRoutingKey().trim());
            }
        }

        Queue queue = builder.build();
        String declaredName = rabbitAdmin.declareQueue(queue);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Cola creada o verificada exitosamente en RabbitMQ");
        response.put("nombre", declaredName);
        response.put("durable", dto.isDurable());
        response.put("dlx", dto.getDeadLetterExchange());
        response.put("dlqRoutingKey", dto.getDeadLetterRoutingKey());
        return response;
    }

    /**
     * Elimina una cola existente por su nombre.
     */
    public boolean eliminarCola(String queueName) {
        log.info("Eliminando cola '{}' de RabbitMQ", queueName);
        return rabbitAdmin.deleteQueue(queueName);
    }

    /**
     * Obtiene propiedades y estadísticas básicas de una cola.
     */
    public Map<String, Object> obtenerDetalleCola(String queueName) {
        Properties properties = rabbitAdmin.getQueueProperties(queueName);
        Map<String, Object> resultado = new HashMap<>();
        if (properties == null) {
            resultado.put("existe", false);
            resultado.put("mensaje", "La cola '" + queueName + "' no existe en RabbitMQ");
            return resultado;
        }

        resultado.put("existe", true);
        resultado.put("nombre", queueName);
        resultado.put("mensajesPendientes", properties.get(RabbitAdmin.QUEUE_MESSAGE_COUNT));
        resultado.put("consumidoresActivos", properties.get(RabbitAdmin.QUEUE_CONSUMER_COUNT));
        return resultado;
    }

    /**
     * Declara un nuevo Exchange (topic, direct, fanout o headers).
     */
    public Map<String, Object> crearExchange(ExchangeRequestDTO dto) {
        log.info("Creando Exchange '{}' de tipo '{}'", dto.getName(), dto.getType());

        Exchange exchange;
        String tipo = dto.getType().toLowerCase().trim();
        switch (tipo) {
            case "direct" -> exchange = new DirectExchange(dto.getName(), dto.isDurable(), dto.isAutoDelete());
            case "fanout" -> exchange = new FanoutExchange(dto.getName(), dto.isDurable(), dto.isAutoDelete());
            case "headers" -> exchange = new HeadersExchange(dto.getName(), dto.isDurable(), dto.isAutoDelete());
            case "topic" -> exchange = new TopicExchange(dto.getName(), dto.isDurable(), dto.isAutoDelete());
            default -> exchange = new CustomExchange(dto.getName(), tipo, dto.isDurable(), dto.isAutoDelete());
        }

        rabbitAdmin.declareExchange(exchange);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Exchange creado o verificado exitosamente");
        response.put("nombre", dto.getName());
        response.put("tipo", tipo);
        response.put("durable", dto.isDurable());
        return response;
    }

    /**
     * Elimina un exchange existente.
     */
    public boolean eliminarExchange(String exchangeName) {
        log.info("Eliminando Exchange '{}' de RabbitMQ", exchangeName);
        return rabbitAdmin.deleteExchange(exchangeName);
    }

    /**
     * Enlaza una cola con un exchange usando un routing key.
     */
    public Map<String, Object> crearBinding(BindingRequestDTO dto) {
        log.info("Creando binding: Cola '{}' <--- [{}] ---> Exchange '{}'",
                dto.getQueueName(), dto.getRoutingKey(), dto.getExchangeName());

        Binding binding = new Binding(
                dto.getQueueName(),
                Binding.DestinationType.QUEUE,
                dto.getExchangeName(),
                dto.getRoutingKey(),
                null
        );

        rabbitAdmin.declareBinding(binding);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Enlace (binding) creado exitosamente");
        response.put("cola", dto.getQueueName());
        response.put("exchange", dto.getExchangeName());
        response.put("routingKey", dto.getRoutingKey());
        return response;
    }
}
