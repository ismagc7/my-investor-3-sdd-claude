package dev.aiddbot.abjavareact.launch;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LaunchRequest(Long rocketId, LocalDate date, BigDecimal pricePerSeat, LaunchStatus status) {}
