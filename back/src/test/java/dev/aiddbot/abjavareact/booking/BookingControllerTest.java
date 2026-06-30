package dev.aiddbot.abjavareact.booking;

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

@WebMvcTest(BookingController.class)
class BookingControllerTest {

  @Autowired private MockMvc mvc;
  @Autowired private ObjectMapper mapper;
  @MockitoBean private BookingService service;

  private static final LocalDate DATE = LocalDate.of(2027, 6, 15);

  private static final BookingResponse APOLLO =
      new BookingResponse(1L, 2L, "Falcon 9", DATE, "Ada Lovelace", "ada@example.com", "CONFIRMED");

  private static final BookingRequest APOLLO_REQUEST =
      new BookingRequest(2L, "Ada Lovelace", "ada@example.com", BookingStatus.CONFIRMED);

  @Test
  void findAllReturnsBookingList() throws Exception {
    given(service.findAll()).willReturn(List.of(APOLLO));

    mvc.perform(get("/api/bookings"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].passengerName").value("Ada Lovelace"))
        .andExpect(jsonPath("$[0].status").value("CONFIRMED"));
  }

  @Test
  void findByIdReturnsBooking() throws Exception {
    given(service.findById(1L)).willReturn(APOLLO);

    mvc.perform(get("/api/bookings/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.passengerName").value("Ada Lovelace"))
        .andExpect(jsonPath("$.status").value("CONFIRMED"));
  }

  @Test
  void findByIdReturns404WhenNotFound() throws Exception {
    given(service.findById(99L)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mvc.perform(get("/api/bookings/99")).andExpect(status().isNotFound());
  }

  @Test
  void createReturns201WithLocationHeader() throws Exception {
    given(service.create(any(BookingRequest.class))).willReturn(APOLLO);

    mvc.perform(
            post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(APOLLO_REQUEST)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", endsWith("/api/bookings/1")))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.passengerName").value("Ada Lovelace"));
  }

  @Test
  void createReturns400WhenMissingPassengerData() throws Exception {
    given(service.create(any(BookingRequest.class)))
        .willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passenger name is required"));

    mvc.perform(
            post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(
                    new BookingRequest(2L, "", "", BookingStatus.CONFIRMED))))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateReturnsUpdatedBooking() throws Exception {
    BookingResponse updated =
        new BookingResponse(1L, 2L, "Falcon 9", DATE, "Grace Hopper", "grace@example.com", "PAYED");
    given(service.update(eq(1L), any(BookingRequest.class))).willReturn(updated);

    mvc.perform(
            put("/api/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(APOLLO_REQUEST)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("PAYED"));
  }

  @Test
  void updateReturns404WhenNotFound() throws Exception {
    given(service.update(eq(99L), any(BookingRequest.class)))
        .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mvc.perform(
            put("/api/bookings/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(APOLLO_REQUEST)))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteReturns204() throws Exception {
    willDoNothing().given(service).delete(1L);

    mvc.perform(delete("/api/bookings/1")).andExpect(status().isNoContent());
  }

  @Test
  void deleteReturns404WhenNotFound() throws Exception {
    willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).given(service).delete(99L);

    mvc.perform(delete("/api/bookings/99")).andExpect(status().isNotFound());
  }
}
