# Bookings specifications

## Problem definition

- As a user I want to create a booking for a launch in order to fligth in a rocket

### Out of scope

- No security nor user ids
- No payment procedure

## Solution overview

### Data

- A booking is for a launch
- Has a passenger name and email
- Has a status: confirmed, cancelled, payed

### API

- A rest endpoiunt `api/bookings`

### UI

- A form to fille with passenger data for a launch

## Verification criteria

- [ ] THE system SHALL accept a new booking
- [ ] WHEN no passenger data is filles THEN should respond wit error 400