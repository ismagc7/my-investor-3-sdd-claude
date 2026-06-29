package dev.aiddbot.abjavareact.launch;

import static org.assertj.core.api.Assertions.assertThat;

import dev.aiddbot.abjavareact.rocket.Rocket;
import dev.aiddbot.abjavareact.rocket.RocketRange;
import dev.aiddbot.abjavareact.rocket.RocketRepository;
import dev.aiddbot.abjavareact.rocket.RocketStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class LaunchRepositoryTest {

  @Autowired private LaunchRepository repository;
  @Autowired private RocketRepository rocketRepository;

  private Rocket savedRocket() {
    return rocketRepository.save(
        new Rocket("Falcon 9", 9, RocketRange.EARTH, RocketStatus.ACTIVE, null, null));
  }

  @Test
  void persistsAndReadsBackLaunch() {
    Rocket rocket = savedRocket();
    LocalDate date = LocalDate.now().plusMonths(1);
    Launch launch = new Launch(rocket, date, new BigDecimal("50000.00"), LaunchStatus.CREATED);

    Launch saved = repository.save(launch);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getRocket().getId()).isEqualTo(rocket.getId());
    assertThat(saved.getDate()).isEqualTo(date);
    assertThat(saved.getPricePerSeat()).isEqualByComparingTo("50000.00");
    assertThat(saved.getStatus()).isEqualTo(LaunchStatus.CREATED);
  }

  @Test
  void findsAllLaunches() {
    Rocket rocket = savedRocket();
    repository.save(
        new Launch(rocket, LocalDate.now().plusMonths(1), new BigDecimal("50000"), LaunchStatus.CREATED));
    repository.save(
        new Launch(rocket, LocalDate.now().plusMonths(2), new BigDecimal("60000"), LaunchStatus.CONFIRMED));

    assertThat(repository.findAll()).hasSize(2);
  }

  @Test
  void deletesLaunch() {
    Rocket rocket = savedRocket();
    Launch saved =
        repository.save(
            new Launch(rocket, LocalDate.now().plusMonths(1), new BigDecimal("50000"), LaunchStatus.CREATED));

    repository.deleteById(saved.getId());

    assertThat(repository.findById(saved.getId())).isEmpty();
  }
}
