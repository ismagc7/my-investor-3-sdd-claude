import { getBookings, getBookingById, createBooking, updateBooking, deleteBooking } from './bookingsApi';
import { httpClient } from '../../shared/api/httpClient';
import type { Booking } from '../../shared/types/booking';

vi.mock('../../shared/api/httpClient', () => ({
  httpClient: { get: vi.fn(), post: vi.fn(), put: vi.fn(), del: vi.fn() },
}));

const booking: Booking = {
  id: 1,
  launchId: 2,
  launchRocketName: 'Falcon 9',
  launchDate: '2027-06-15',
  passengerName: 'Ada Lovelace',
  passengerEmail: 'ada@example.com',
  status: 'CONFIRMED',
};

beforeEach(() => {
  vi.mocked(httpClient.get).mockReset();
  vi.mocked(httpClient.post).mockReset();
  vi.mocked(httpClient.put).mockReset();
  vi.mocked(httpClient.del).mockReset();
});

test('getBookings calls GET /api/bookings and returns booking list', async () => {
  vi.mocked(httpClient.get).mockResolvedValue([booking]);

  const result = await getBookings();

  expect(httpClient.get).toHaveBeenCalledWith('/api/bookings');
  expect(result).toEqual([booking]);
});

test('getBookingById calls GET /api/bookings/:id', async () => {
  vi.mocked(httpClient.get).mockResolvedValue(booking);

  const result = await getBookingById(1);

  expect(httpClient.get).toHaveBeenCalledWith('/api/bookings/1');
  expect(result).toEqual(booking);
});

test('createBooking calls POST /api/bookings with payload', async () => {
  const { id: _id, launchRocketName: _rn, launchDate: _ld, ...request } = booking;
  vi.mocked(httpClient.post).mockResolvedValue(booking);

  const result = await createBooking(request);

  expect(httpClient.post).toHaveBeenCalledWith('/api/bookings', request);
  expect(result).toEqual(booking);
});

test('updateBooking calls PUT /api/bookings/:id with payload', async () => {
  const { id: _id, launchRocketName: _rn, launchDate: _ld, ...request } = booking;
  vi.mocked(httpClient.put).mockResolvedValue(booking);

  const result = await updateBooking(1, request);

  expect(httpClient.put).toHaveBeenCalledWith('/api/bookings/1', request);
  expect(result).toEqual(booking);
});

test('deleteBooking calls DELETE /api/bookings/:id', async () => {
  vi.mocked(httpClient.del).mockResolvedValue(undefined);

  await deleteBooking(1);

  expect(httpClient.del).toHaveBeenCalledWith('/api/bookings/1');
});

test('getBookings propagates client errors', async () => {
  vi.mocked(httpClient.get).mockRejectedValue(new Error('network error'));

  await expect(getBookings()).rejects.toThrow('network error');
});
