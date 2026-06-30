import { renderHook, waitFor } from '@testing-library/react';
import { useBookings } from './useBookings';
import { getBookings } from './bookingsApi';
import type { Booking } from '../../shared/types/booking';

vi.mock('./bookingsApi', () => ({ getBookings: vi.fn() }));

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
  vi.mocked(getBookings).mockReset();
});

test('exposes data when the request resolves', async () => {
  vi.mocked(getBookings).mockResolvedValue([booking]);

  const { result } = renderHook(() => useBookings());

  expect(result.current.isLoading).toBe(true);
  await waitFor(() => expect(result.current.isLoading).toBe(false));
  expect(result.current.data).toEqual([booking]);
  expect(result.current.error).toBeNull();
});

test('exposes an error when the request rejects', async () => {
  vi.mocked(getBookings).mockRejectedValue(new Error('network down'));

  const { result } = renderHook(() => useBookings());

  await waitFor(() => expect(result.current.isLoading).toBe(false));
  expect(result.current.data).toBeNull();
  expect(result.current.error).toBeInstanceOf(Error);
});

test('exposes empty array when no bookings exist', async () => {
  vi.mocked(getBookings).mockResolvedValue([]);

  const { result } = renderHook(() => useBookings());

  await waitFor(() => expect(result.current.isLoading).toBe(false));
  expect(result.current.data).toEqual([]);
  expect(result.current.error).toBeNull();
});

test('refresh triggers a new fetch', async () => {
  vi.mocked(getBookings).mockResolvedValue([booking]);

  const { result } = renderHook(() => useBookings());
  await waitFor(() => expect(result.current.isLoading).toBe(false));

  vi.mocked(getBookings).mockResolvedValue([booking, { ...booking, id: 2 }]);
  result.current.refresh();

  await waitFor(() => expect(result.current.data).toHaveLength(2));
  expect(getBookings).toHaveBeenCalledTimes(2);
});
