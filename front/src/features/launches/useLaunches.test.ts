import { renderHook, waitFor } from '@testing-library/react';
import { useLaunches } from './useLaunches';
import { getLaunches } from './launchesApi';
import type { Launch } from '../../shared/types/launch';

vi.mock('./launchesApi', () => ({ getLaunches: vi.fn() }));

const apollo: Launch = {
  id: 1,
  rocketId: 2,
  rocketName: 'Falcon 9',
  date: '2027-06-15',
  pricePerSeat: 50000,
  status: 'CREATED',
};

beforeEach(() => {
  vi.mocked(getLaunches).mockReset();
});

test('exposes data when the request resolves', async () => {
  vi.mocked(getLaunches).mockResolvedValue([apollo]);

  const { result } = renderHook(() => useLaunches());

  expect(result.current.isLoading).toBe(true);
  await waitFor(() => expect(result.current.isLoading).toBe(false));
  expect(result.current.data).toEqual([apollo]);
  expect(result.current.error).toBeNull();
});

test('exposes an error when the request rejects', async () => {
  vi.mocked(getLaunches).mockRejectedValue(new Error('network down'));

  const { result } = renderHook(() => useLaunches());

  await waitFor(() => expect(result.current.isLoading).toBe(false));
  expect(result.current.data).toBeNull();
  expect(result.current.error).toBeInstanceOf(Error);
});

test('exposes empty array when no launches exist', async () => {
  vi.mocked(getLaunches).mockResolvedValue([]);

  const { result } = renderHook(() => useLaunches());

  await waitFor(() => expect(result.current.isLoading).toBe(false));
  expect(result.current.data).toEqual([]);
  expect(result.current.error).toBeNull();
});

test('refresh triggers a new fetch', async () => {
  vi.mocked(getLaunches).mockResolvedValue([apollo]);

  const { result } = renderHook(() => useLaunches());
  await waitFor(() => expect(result.current.isLoading).toBe(false));

  vi.mocked(getLaunches).mockResolvedValue([apollo, { ...apollo, id: 2 }]);
  result.current.refresh();

  await waitFor(() => expect(result.current.data).toHaveLength(2));
  expect(getLaunches).toHaveBeenCalledTimes(2);
});
