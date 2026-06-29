export type LaunchStatus = 'CREATED' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';

export type Launch = {
  id: number;
  rocketId: number;
  rocketName: string;
  date: string;
  pricePerSeat: number;
  status: LaunchStatus;
};

export type LaunchRequest = Omit<Launch, 'id' | 'rocketName'>;
