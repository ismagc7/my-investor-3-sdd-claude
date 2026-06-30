import { HealthStatus } from './features/health/HealthStatus';
import { RocketList } from './features/rockets/RocketList';
import { LaunchList } from './features/launches/LaunchList';
import { BookingList } from './features/bookings/BookingList';
import './App.css';

function App() {
  return (
    <main className="app-shell">
      <h1 className="app-hero">AstroBookings</h1>
      <HealthStatus />
      <RocketList />
      <LaunchList />
      <BookingList />
    </main>
  );
}

export default App;
