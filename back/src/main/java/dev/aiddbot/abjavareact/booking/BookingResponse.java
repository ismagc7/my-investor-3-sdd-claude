package dev.aiddbot.abjavareact.booking;

import java.time.LocalDate;

public record BookingResponse(
    Long id,
    Long launchId,
    String launchRocketName,
    LocalDate launchDate,
    String passengerName,
    String passengerEmail,
    String status) {}
