package dev.aiddbot.abjavareact.launch;

import dev.aiddbot.abjavareact.rocket.Rocket;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "launch")
public class Launch {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "rocket_id", nullable = false)
  private Rocket rocket;

  @Column(nullable = false)
  private LocalDate date;

  @Column(name = "price_per_seat", nullable = false)
  private BigDecimal pricePerSeat;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LaunchStatus status;

  protected Launch() {}

  public Launch(Rocket rocket, LocalDate date, BigDecimal pricePerSeat, LaunchStatus status) {
    this.rocket = rocket;
    this.date = date;
    this.pricePerSeat = pricePerSeat;
    this.status = status;
  }

  public Long getId() { return id; }
  public Rocket getRocket() { return rocket; }
  public LocalDate getDate() { return date; }
  public BigDecimal getPricePerSeat() { return pricePerSeat; }
  public LaunchStatus getStatus() { return status; }

  public void setRocket(Rocket rocket) { this.rocket = rocket; }
  public void setDate(LocalDate date) { this.date = date; }
  public void setPricePerSeat(BigDecimal pricePerSeat) { this.pricePerSeat = pricePerSeat; }
  public void setStatus(LaunchStatus status) { this.status = status; }
}
