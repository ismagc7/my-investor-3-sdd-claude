package dev.aiddbot.abjavareact.launch;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(LaunchController.class)
class LaunchControllerTest {

  @Autowired private MockMvc mvc;
  @Autowired private ObjectMapper mapper;
  @MockitoBean private LaunchService service;

  private static final LocalDate DATE = LocalDate.of(2027, 6, 15);

  private static final LaunchResponse APOLLO =
      new LaunchResponse(1L, 2L, "Falcon 9", DATE, new BigDecimal("50000.00"), "CREATED");

  private static final LaunchRequest APOLLO_REQUEST =
      new LaunchRequest(2L, DATE, new BigDecimal("50000.00"), LaunchStatus.CREATED);

  @Test
  void findAllReturnsLaunchList() throws Exception {
    given(service.findAll()).willReturn(List.of(APOLLO));

    mvc.perform(get("/api/launches"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].rocketName").value("Falcon 9"))
        .andExpect(jsonPath("$[0].status").value("CREATED"));
  }

  @Test
  void findByIdReturnsLaunch() throws Exception {
    given(service.findById(1L)).willReturn(APOLLO);

    mvc.perform(get("/api/launches/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.rocketName").value("Falcon 9"))
        .andExpect(jsonPath("$.status").value("CREATED"));
  }

  @Test
  void findByIdReturns404WhenNotFound() throws Exception {
    given(service.findById(99L)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mvc.perform(get("/api/launches/99"))
        .andExpect(status().isNotFound());
  }

  @Test
  void createReturns201WithLocationHeader() throws Exception {
    given(service.create(any(LaunchRequest.class))).willReturn(APOLLO);

    mvc.perform(
            post("/api/launches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(APOLLO_REQUEST)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", endsWith("/api/launches/1")))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.rocketName").value("Falcon 9"));
  }

  @Test
  void updateReturnsUpdatedLaunch() throws Exception {
    LaunchResponse updated =
        new LaunchResponse(1L, 2L, "Falcon 9", DATE, new BigDecimal("60000.00"), "CONFIRMED");
    given(service.update(eq(1L), any(LaunchRequest.class))).willReturn(updated);

    mvc.perform(
            put("/api/launches/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(APOLLO_REQUEST)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CONFIRMED"));
  }

  @Test
  void updateReturns404WhenNotFound() throws Exception {
    given(service.update(eq(99L), any(LaunchRequest.class)))
        .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mvc.perform(
            put("/api/launches/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(APOLLO_REQUEST)))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteReturns204() throws Exception {
    willDoNothing().given(service).delete(1L);

    mvc.perform(delete("/api/launches/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteReturns404WhenNotFound() throws Exception {
    willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).given(service).delete(99L);

    mvc.perform(delete("/api/launches/99"))
        .andExpect(status().isNotFound());
  }
}
