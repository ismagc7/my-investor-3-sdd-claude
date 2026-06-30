export type BookingStatus = 'CONFIRMED' | 'CANCELLED' | 'PAYED';

export type Booking = {
  id: number;
  launchId: number;
  launchRocketName: string;
  launchDate: string;
  passengerName: string;
  passengerEmail: string;
  status: BookingStatus;
};

export type BookingRequest = Omit<Booking, 'id' | 'launchRocketName' | 'launchDate'>;
