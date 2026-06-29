package dev.aiddbot.abjavareact.launch;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LaunchResponse(
    Long id,
    Long rocketId,
    String rocketName,
    LocalDate date,
    BigDecimal pricePerSeat,
    String status) {}
