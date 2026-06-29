package dev.aiddbot.abjavareact.launch;

import dev.aiddbot.abjavareact.rocket.Rocket;
import dev.aiddbot.abjavareact.rocket.RocketRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LaunchService {

  private final LaunchRepository repository;
  private final RocketRepository rocketRepository;

  public LaunchService(LaunchRepository repository, RocketRepository rocketRepository) {
    this.repository = repository;
    this.rocketRepository = rocketRepository;
  }

  public List<LaunchResponse> findAll() {
    return repository.findAll().stream().map(this::toResponse).toList();
  }

  public LaunchResponse findById(Long id) {
    return repository
        .findById(id)
        .map(this::toResponse)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Launch not found: " + id));
  }

  public LaunchResponse create(LaunchRequest request) {
    validate(request);
    Rocket rocket =
        rocketRepository
            .findById(request.rocketId())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Rocket not found: " + request.rocketId()));
    Launch launch = new Launch(rocket, request.date(), request.pricePerSeat(), request.status());
    return toResponse(repository.save(launch));
  }

  public LaunchResponse update(Long id, LaunchRequest request) {
    validate(request);
    Launch launch =
        repository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Launch not found: " + id));
    Rocket rocket =
        rocketRepository
            .findById(request.rocketId())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Rocket not found: " + request.rocketId()));
    launch.setRocket(rocket);
    launch.setDate(request.date());
    launch.setPricePerSeat(request.pricePerSeat());
    launch.setStatus(request.status());
    return toResponse(repository.save(launch));
  }

  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Launch not found: " + id);
    }
    repository.deleteById(id);
  }

  private void validate(LaunchRequest request) {
    if (request.rocketId() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rocket is required");
    }
    if (request.date() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date is required");
    }
    if (request.pricePerSeat() == null
        || request.pricePerSeat().compareTo(BigDecimal.ZERO) <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price per seat must be positive");
    }
    if (request.status() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
    }
  }

  private LaunchResponse toResponse(Launch launch) {
    return new LaunchResponse(
        launch.getId(),
        launch.getRocket().getId(),
        launch.getRocket().getName(),
        launch.getDate(),
        launch.getPricePerSeat(),
        launch.getStatus().name());
  }
}
