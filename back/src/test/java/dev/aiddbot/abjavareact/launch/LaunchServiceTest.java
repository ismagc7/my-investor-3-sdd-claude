package dev.aiddbot.abjavareact.launch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import dev.aiddbot.abjavareact.rocket.Rocket;
import dev.aiddbot.abjavareact.rocket.RocketRange;
import dev.aiddbot.abjavareact.rocket.RocketRepository;
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
class LaunchServiceTest {

  @Mock private LaunchRepository repository;
  @Mock private RocketRepository rocketRepository;
  @InjectMocks private LaunchService service;

  private static final LocalDate FUTURE = LocalDate.now().plusMonths(3);
  private static final Rocket ROCKET =
      new Rocket("Falcon 9", 9, RocketRange.EARTH, RocketStatus.ACTIVE, null, null);
  private static final LaunchRequest REQUEST =
      new LaunchRequest(1L, FUTURE, new BigDecimal("50000"), LaunchStatus.CREATED);

  private Launch newLaunch() {
    return new Launch(ROCKET, FUTURE, new BigDecimal("50000"), LaunchStatus.CREATED);
  }

  @Test
  void findAllReturnsMappedResponses() {
    given(repository.findAll()).willReturn(List.of(newLaunch()));

    List<LaunchResponse> result = service.findAll();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).status()).isEqualTo("CREATED");
    assertThat(result.get(0).rocketName()).isEqualTo("Falcon 9");
  }

  @Test
  void findByIdReturnsResponseWhenFound() {
    given(repository.findById(1L)).willReturn(Optional.of(newLaunch()));

    LaunchResponse result = service.findById(1L);

    assertThat(result.rocketName()).isEqualTo("Falcon 9");
    assertThat(result.status()).isEqualTo("CREATED");
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
    given(rocketRepository.findById(1L)).willReturn(Optional.of(ROCKET));
    given(repository.save(any())).willAnswer(inv -> inv.getArgument(0));

    LaunchResponse result = service.create(REQUEST);

    assertThat(result.status()).isEqualTo("CREATED");
    assertThat(result.rocketName()).isEqualTo("Falcon 9");
    assertThat(result.pricePerSeat()).isEqualByComparingTo("50000");
  }

  @Test
  void createThrows400WhenRocketIdIsNull() {
    LaunchRequest bad = new LaunchRequest(null, FUTURE, new BigDecimal("50000"), LaunchStatus.CREATED);

    assertThatThrownBy(() -> service.create(bad))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("400");
  }

  @Test
  void createThrows400WhenDateIsNull() {
    LaunchRequest bad = new LaunchRequest(1L, null, new BigDecimal("50000"), LaunchStatus.CREATED);

    assertThatThrownBy(() -> service.create(bad))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("400");
  }

  @Test
  void createThrows400WhenPriceIsZero() {
    LaunchRequest bad = new LaunchRequest(1L, FUTURE, BigDecimal.ZERO, LaunchStatus.CREATED);

    assertThatThrownBy(() -> service.create(bad))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("400");
  }

  @Test
  void createThrows400WhenStatusIsNull() {
    LaunchRequest bad = new LaunchRequest(1L, FUTURE, new BigDecimal("50000"), null);

    assertThatThrownBy(() -> service.create(bad))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("400");
  }

  @Test
  void updateAppliesAllFieldsAndPersists() {
    given(repository.findById(1L)).willReturn(Optional.of(newLaunch()));
    given(rocketRepository.findById(1L)).willReturn(Optional.of(ROCKET));
    given(repository.save(any())).willAnswer(inv -> inv.getArgument(0));
    LocalDate newDate = FUTURE.plusMonths(1);
    LaunchRequest updated =
        new LaunchRequest(1L, newDate, new BigDecimal("60000"), LaunchStatus.CONFIRMED);

    LaunchResponse result = service.update(1L, updated);

    assertThat(result.date()).isEqualTo(newDate);
    assertThat(result.status()).isEqualTo("CONFIRMED");
    assertThat(result.pricePerSeat()).isEqualByComparingTo("60000");
  }

  @Test
  void updateThrows404WhenNotFound() {
    given(repository.findById(99L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> service.update(99L, REQUEST))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("404");
  }

  @Test
  void deleteRemovesExistingLaunch() {
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
