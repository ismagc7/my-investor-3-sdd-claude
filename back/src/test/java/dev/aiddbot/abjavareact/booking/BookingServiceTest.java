package dev.aiddbot.abjavareact.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import dev.aiddbot.abjavareact.launch.Launch;
import dev.aiddbot.abjavareact.launch.LaunchRepository;
import dev.aiddbot.abjavareact.launch.LaunchStatus;
import dev.aiddbot.abjavareact.rocket.Rocket;
import dev.aiddbot.abjavareact.rocket.RocketRange;
import dev.aiddbot.abjavareact.rocket.RocketStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

  @Mock private BookingRepository repository;
  @Mock private LaunchRepository launchRepository;
  @InjectMocks private BookingService service;

  private static final Rocket ROCKET =
      new Rocket("Falcon 9", 9, RocketRange.EARTH, RocketStatus.ACTIVE, null, null);
  private static final Launch LAUNCH =
      new Launch(ROCKET, LocalDate.now().plusMonths(3), new BigDecimal("50000"), LaunchStatus.CREATED);
  private static final BookingRequest REQUEST =
      new BookingRequest(1L, "Ada Lovelace", "ada@example.com", BookingStatus.CONFIRMED);

  private Booking newBooking() {
    return new Booking(LAUNCH, "Ada Lovelace", "ada@example.com", BookingStatus.CONFIRMED);
  }

  @Test
  void findAllReturnsMappedResponses() {
    given(repository.findAll()).willReturn(List.of(newBooking()));

    List<BookingResponse> result = service.findAll();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).status()).isEqualTo("CONFIRMED");
    assertThat(result.get(0).launchRocketName()).isEqualTo("Falcon 9");
  }

  @Test
  void findByIdReturnsResponseWhenFound() {
    given(repository.findById(1L)).willReturn(Optional.of(newBooking()));

    BookingResponse result = service.findById(1L);

    assertThat(result.passengerName()).isEqualTo("Ada Lovelace");
    assertThat(result.status()).isEqualTo("CONFIRMED");
  }

  @Test
  void findByIdThrows404WhenNotFound() {
    given(repository.findById(99L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> service.findById(99L))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("404");
  }

  @Test
  void createPersistsAndReturnsMappedResponse() {
    given(launchRepository.findById(1L)).willReturn(Optional.of(LAUNCH));
    given(repository.save(any())).willAnswer(inv -> inv.getArgument(0));

    BookingResponse result = service.create(REQUEST);

    assertThat(result.status()).isEqualTo("CONFIRMED");
    assertThat(result.passengerName()).isEqualTo("Ada Lovelace");
    assertThat(result.passengerEmail()).isEqualTo("ada@example.com");
  }

  @Test
  void createThrows400WhenLaunchIdIsNull() {
    BookingRequest bad = new BookingRequest(null, "Ada Lovelace", "ada@example.com", BookingStatus.CONFIRMED);

    assertThatThrownBy(() -> service.create(bad))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("400");
  }

  @Test
  void createThrows400WhenPassengerNameIsBlank() {
    BookingRequest bad = new BookingRequest(1L, "  ", "ada@example.com", BookingStatus.CONFIRMED);

    assertThatThrownBy(() -> service.create(bad))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("400");
  }

  @Test
  void createThrows400WhenPassengerEmailIsBlank() {
    BookingRequest bad = new BookingRequest(1L, "Ada Lovelace", "", BookingStatus.CONFIRMED);

    assertThatThrownBy(() -> service.create(bad))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("400");
  }

  @Test
  void createThrows400WhenStatusIsNull() {
    BookingRequest bad = new BookingRequest(1L, "Ada Lovelace", "ada@example.com", null);

    assertThatThrownBy(() -> service.create(bad))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("400");
  }

  @Test
  void createThrows400WhenLaunchNotFound() {
    given(launchRepository.findById(1L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> service.create(REQUEST))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("400");
  }

  @Test
  void updateAppliesAllFieldsAndPersists() {
    given(repository.findById(1L)).willReturn(Optional.of(newBooking()));
    given(launchRepository.findById(1L)).willReturn(Optional.of(LAUNCH));
    given(repository.save(any())).willAnswer(inv -> inv.getArgument(0));
    BookingRequest updated =
        new BookingRequest(1L, "Grace Hopper", "grace@example.com", BookingStatus.PAYED);

    BookingResponse result = service.update(1L, updated);

    assertThat(result.passengerName()).isEqualTo("Grace Hopper");
    assertThat(result.status()).isEqualTo("PAYED");
  }

  @Test
  void updateThrows404WhenNotFound() {
    given(repository.findById(99L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> service.update(99L, REQUEST))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("404");
  }

  @Test
  void deleteRemovesExistingBooking() {
    given(repository.existsById(1L)).willReturn(true);

    service.delete(1L);

    then(repository).should().deleteById(1L);
  }

  @Test
  void deleteThrows404WhenNotFound() {
    given(repository.existsById(99L)).willReturn(false);

    assertThatThrownBy(() -> service.delete(99L))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("404");
  }
}
