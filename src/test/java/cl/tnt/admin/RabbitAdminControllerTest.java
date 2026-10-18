package cl.tnt.admin;

import cl.tnt.admin.controller.RabbitAdminController;
import cl.tnt.admin.dto.QueueRequestDTO;
import cl.tnt.admin.service.RabbitAdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RabbitAdminController.class)
class RabbitAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RabbitAdminService rabbitAdminService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void statusEndpointDeberiaRetornar200() throws Exception {
        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servicio").value("ms-rabbit-admin"))
                .andExpect(jsonPath("$.estado").value("ONLINE"));
    }

    @Test
    void crearColaConParametrosValidosDeberiaRetornar201() throws Exception {
        QueueRequestDTO request = new QueueRequestDTO("cola.pruebas", true, false, false);

        Mockito.when(rabbitAdminService.crearCola(any())).thenReturn(Map.of(
                "mensaje", "Cola creada o verificada exitosamente en RabbitMQ",
                "nombre", "cola.pruebas"
        ));

        mockMvc.perform(post("/queues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("cola.pruebas"));
    }

    @Test
    void crearColaConNombreVacioDeberiaRetornar400YMensajeClaro() throws Exception {
        QueueRequestDTO requestInvalido = new QueueRequestDTO("", true, false, false);

        mockMvc.perform(post("/queues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detalles.name").exists());
    }

    @Test
    void eliminarColaExistenteDeberiaRetornar200() throws Exception {
        Mockito.when(rabbitAdminService.eliminarCola("cola.pruebas")).thenReturn(true);

        mockMvc.perform(delete("/queues/cola.pruebas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));
    }
}
