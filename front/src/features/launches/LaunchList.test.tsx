import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { LaunchList } from './LaunchList';
import { getLaunches, createLaunch, updateLaunch, deleteLaunch } from './launchesApi';
import { getRockets } from '../rockets/rocketsApi';
import type { Launch } from '../../shared/types/launch';
import type { Rocket } from '../../shared/types/rocket';

vi.mock('./launchesApi', () => ({
  getLaunches: vi.fn(),
  createLaunch: vi.fn(),
  updateLaunch: vi.fn(),
  deleteLaunch: vi.fn(),
}));

vi.mock('../rockets/rocketsApi', () => ({
  getRockets: vi.fn(),
}));

const falcon: Rocket = {
  id: 2,
  name: 'Falcon 9',
  capacity: 9,
  range: 'EARTH',
  status: 'ACTIVE',
  lastMaintenanceDate: null,
  nextMaintenanceDate: null,
};

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
  vi.mocked(createLaunch).mockReset();
  vi.mocked(updateLaunch).mockReset();
  vi.mocked(deleteLaunch).mockReset();
  vi.mocked(getRockets).mockResolvedValue([falcon]);
});

test('shows a loading indicator while fetching', () => {
  vi.mocked(getLaunches).mockReturnValue(new Promise(() => {}));

  render(<LaunchList />);

  expect(screen.getByTestId('launches-loading')).toBeInTheDocument();
});

test('renders the launches table when data loads', async () => {
  vi.mocked(getLaunches).mockResolvedValue([apollo]);

  render(<LaunchList />);

  expect(await screen.findByTestId('launches-table')).toBeInTheDocument();
  expect(screen.getByTestId('launch-row-1')).toBeInTheDocument();
  expect(screen.getByText('Falcon 9')).toBeInTheDocument();
  expect(screen.getByText('CREATED')).toBeInTheDocument();
});

test('shows empty state when no launches exist', async () => {
  vi.mocked(getLaunches).mockResolvedValue([]);

  render(<LaunchList />);

  expect(await screen.findByTestId('launches-empty')).toBeInTheDocument();
});

test('shows error state when fetch fails', async () => {
  vi.mocked(getLaunches).mockRejectedValue(new Error('network down'));

  render(<LaunchList />);

  const error = await screen.findByTestId('launches-error');
  expect(error).toHaveAttribute('role', 'alert');
  expect(error).toHaveTextContent(/network down/i);
});

test('opens the form when add button is clicked', async () => {
  vi.mocked(getLaunches).mockResolvedValue([]);
  const user = userEvent.setup();

  render(<LaunchList />);
  await screen.findByTestId('launches-empty');

  await user.click(screen.getByTestId('add-launch-btn'));

  expect(screen.getByTestId('launch-form')).toBeInTheDocument();
  expect(screen.getByTestId('field-rocket')).toBeInTheDocument();
  expect(screen.getByTestId('field-date')).toBeInTheDocument();
  expect(screen.getByTestId('field-price')).toBeInTheDocument();
});

test('creates a launch when form is submitted', async () => {
  vi.mocked(getLaunches).mockResolvedValueOnce([]).mockResolvedValueOnce([apollo]);
  vi.mocked(createLaunch).mockResolvedValue(apollo);
  const user = userEvent.setup();

  render(<LaunchList />);
  await screen.findByTestId('launches-empty');

  await user.click(screen.getByTestId('add-launch-btn'));
  await user.selectOptions(screen.getByTestId('field-rocket'), String(falcon.id));
  await user.type(screen.getByTestId('field-date'), '2027-06-15');
  await user.type(screen.getByTestId('field-price'), '50000');
  await user.click(screen.getByTestId('submit-btn'));

  await waitFor(() => expect(createLaunch).toHaveBeenCalled());
  expect(createLaunch).toHaveBeenCalledWith(
    expect.objectContaining({ rocketId: 2, date: '2027-06-15', pricePerSeat: 50000 }),
  );
});

test('opens edit form pre-filled with launch data', async () => {
  vi.mocked(getLaunches).mockResolvedValue([apollo]);
  const user = userEvent.setup();

  render(<LaunchList />);
  await screen.findByTestId('launches-table');

  await user.click(screen.getByTestId('edit-btn-1'));

  expect(screen.getByTestId('launch-form')).toBeInTheDocument();
  expect(screen.getByTestId('field-date')).toHaveValue('2027-06-15');
  expect(screen.getByTestId('field-status')).toHaveValue('CREATED');
});

test('shows confirmation before deleting a launch', async () => {
  vi.mocked(getLaunches).mockResolvedValueOnce([apollo]).mockResolvedValueOnce([]);
  vi.mocked(deleteLaunch).mockResolvedValue(undefined);
  const user = userEvent.setup();

  render(<LaunchList />);
  await screen.findByTestId('launches-table');

  await user.click(screen.getByTestId('delete-btn-1'));
  expect(screen.getByTestId('confirm-delete-1')).toBeInTheDocument();

  await user.click(screen.getByTestId('confirm-delete-1'));
  await waitFor(() => expect(deleteLaunch).toHaveBeenCalledWith(1));
});
