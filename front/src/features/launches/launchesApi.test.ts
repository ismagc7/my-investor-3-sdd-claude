import { getLaunches, getLaunchById, createLaunch, updateLaunch, deleteLaunch } from './launchesApi';
import { httpClient } from '../../shared/api/httpClient';
import type { Launch } from '../../shared/types/launch';

vi.mock('../../shared/api/httpClient', () => ({
  httpClient: { get: vi.fn(), post: vi.fn(), put: vi.fn(), del: vi.fn() },
}));

const apollo: Launch = {
  id: 1,
  rocketId: 2,
  rocketName: 'Falcon 9',
  date: '2027-06-15',
  pricePerSeat: 50000,
  status: 'CREATED',
};

beforeEach(() => {
  vi.mocked(httpClient.get).mockReset();
  vi.mocked(httpClient.post).mockReset();
  vi.mocked(httpClient.put).mockReset();
  vi.mocked(httpClient.del).mockReset();
});

test('getLaunches calls GET /api/launches and returns launch list', async () => {
  vi.mocked(httpClient.get).mockResolvedValue([apollo]);

  const result = await getLaunches();

  expect(httpClient.get).toHaveBeenCalledWith('/api/launches');
  expect(result).toEqual([apollo]);
});

test('getLaunchById calls GET /api/launches/:id', async () => {
  vi.mocked(httpClient.get).mockResolvedValue(apollo);

  const result = await getLaunchById(1);

  expect(httpClient.get).toHaveBeenCalledWith('/api/launches/1');
  expect(result).toEqual(apollo);
});

test('createLaunch calls POST /api/launches with payload', async () => {
  const { id: _id, rocketName: _rn, ...request } = apollo;
  vi.mocked(httpClient.post).mockResolvedValue(apollo);

  const result = await createLaunch(request);

  expect(httpClient.post).toHaveBeenCalledWith('/api/launches', request);
  expect(result).toEqual(apollo);
});

test('updateLaunch calls PUT /api/launches/:id with payload', async () => {
  const { id: _id, rocketName: _rn, ...request } = apollo;
  vi.mocked(httpClient.put).mockResolvedValue(apollo);

  const result = await updateLaunch(1, request);

  expect(httpClient.put).toHaveBeenCalledWith('/api/launches/1', request);
  expect(result).toEqual(apollo);
});

test('deleteLaunch calls DELETE /api/launches/:id', async () => {
  vi.mocked(httpClient.del).mockResolvedValue(undefined);

  await deleteLaunch(1);

  expect(httpClient.del).toHaveBeenCalledWith('/api/launches/1');
});

test('getLaunches propagates client errors', async () => {
  vi.mocked(httpClient.get).mockRejectedValue(new Error('network error'));

  await expect(getLaunches()).rejects.toThrow('network error');
});
