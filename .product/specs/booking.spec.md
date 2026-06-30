---
slug: booking
status: pending
---
# Specification — Passenger ticket booking

## Problem definition

Visitors browsing AstroBookings have no way to reserve a seat on a rocket launch with full contact details. The current `Booking` model only stores a passenger name and email and uses a payment-oriented status (`CONFIRMED`, `CANCELLED`, `PAYED`) that mixes booking and payment concerns. This feature replaces it with a simple booking lifecycle: a visitor books a ticket for a launch with name, email and phone number, and can cancel it. The status only tracks whether the booking is active (`CREATED`) or `CANCELLED`.

### User Stories

- As a visitor, I want to **book a passenger ticket for a rocket launch with my name, email and phone number** so that _I can reserve a seat on that launch_.
- As a visitor, I want to **see the status of my booking** so that _I know whether it is still active or has been cancelled_.
- As a visitor, I want to **cancel an existing booking** so that _I can free my seat if I change my plans_.

## Solution overview

> Expected results only — outcomes, not implementation.
> Functional containers only — no `e2e` or `db` sections here.

### Data Model

- **Booking**: a passenger's reservation for a seat on a rocket launch
    - passengerName: text
    - passengerEmail: text
    - passengerPhone: text
    - status: enum [CREATED, CANCELLED]
    - launch: Launch [1,1]
    - Rules:
      - A booking must reference an existing Launch.
      - A new booking always starts as `CREATED`.
      - A booking may transition from `CREATED` to `CANCELLED`, never the other way.

### Backend API

- Creates a booking for a launch from passenger name, email and phone, starting in `CREATED` status.
- Rejects booking creation when name, email or phone is missing.
- Lists bookings, including passenger details and status.
- Cancels an existing booking by setting its status to `CANCELLED`.
- Rejects cancellation of a booking that does not exist.

### Frontend

- Offers a booking form on a launch to capture passenger name, email and phone number.
- Shows the list of bookings with passenger details and status.
- Lets a visitor cancel a `CREATED` booking from the list.
- Surfaces a validation error when required passenger fields are missing.

## Acceptance and Release

- [ ] WHEN a visitor submits the booking form with name, email and phone for a launch, THE backend SHALL create a booking with status `CREATED`.
- [ ] IF the booking request is missing passenger name, email or phone, THEN THE backend SHALL reject it with a `400` response.
- [ ] WHEN a visitor cancels an existing `CREATED` booking, THE backend SHALL set its status to `CANCELLED`.
- [ ] IF a cancel request targets a booking that does not exist, THEN THE backend SHALL respond with a `404`.
- [ ] THE frontend SHALL list bookings showing passenger name, email, phone and status.
- [ ] WHILE a booking's status is `CANCELLED`, THE frontend SHALL show it as cancelled and not offer cancellation again.
- [ ] WHEN a user completes the booking flow end-to-end (select launch, fill form, submit), THE Playwright suite SHALL verify the booking appears with status `CREATED`.
- [ ] WHEN a user cancels a booking end-to-end, THE Playwright suite SHALL verify its status changes to `CANCELLED`.
