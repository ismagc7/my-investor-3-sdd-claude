package dev.aiddbot.abjavareact.booking;

import dev.aiddbot.abjavareact.launch.Launch;
import dev.aiddbot.abjavareact.launch.LaunchRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BookingService {

  private final BookingRepository repository;
  private final LaunchRepository launchRepository;

  public BookingService(BookingRepository repository, LaunchRepository launchRepository) {
    this.repository = repository;
    this.launchRepository = launchRepository;
  }

  public List<BookingResponse> findAll() {
    return repository.findAll().stream().map(this::toResponse).toList();
  }

  public BookingResponse findById(Long id) {
    return repository
        .findById(id)
        .map(this::toResponse)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found: " + id));
  }

  public BookingResponse create(BookingRequest request) {
    validate(request);
    Launch launch = findLaunch(request.launchId());
    Booking booking =
        new Booking(launch, request.passengerName(), request.passengerEmail(), request.status());
    return toResponse(repository.save(booking));
  }

  public BookingResponse update(Long id, BookingRequest request) {
    validate(request);
    Booking booking =
        repository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found: " + id));
    Launch launch = findLaunch(request.launchId());
    booking.setLaunch(launch);
    booking.setPassengerName(request.passengerName());
    booking.setPassengerEmail(request.passengerEmail());
    booking.setStatus(request.status());
    return toResponse(repository.save(booking));
  }

  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found: " + id);
    }
    repository.deleteById(id);
  }

  private Launch findLaunch(Long launchId) {
    return launchRepository
        .findById(launchId)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Launch not found: " + launchId));
  }

  private void validate(BookingRequest request) {
    if (request.launchId() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Launch is required");
    }
    if (request.passengerName() == null || request.passengerName().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passenger name is required");
    }
    if (request.passengerEmail() == null || request.passengerEmail().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passenger email is required");
    }
    if (request.status() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
    }
  }

  private BookingResponse toResponse(Booking booking) {
    return new BookingResponse(
        booking.getId(),
        booking.getLaunch().getId(),
        booking.getLaunch().getRocket().getName(),
        booking.getLaunch().getDate(),
        booking.getPassengerName(),
        booking.getPassengerEmail(),
        booking.getStatus().name());
  }
}
