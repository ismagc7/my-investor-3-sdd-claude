import { useEffect, useState } from 'react';
import { getBookings } from './bookingsApi';
import type { Booking } from '../../shared/types/booking';

export function useBookings() {
  const [data, setData] = useState<Booking[] | null>(null);
  const [error, setError] = useState<Error | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [refreshKey, setRefreshKey] = useState(0);

  useEffect(() => {
    let active = true;
    setIsLoading(true);
    setError(null);

    getBookings()
      .then((bookings) => {
        if (active) setData(bookings);
      })
      .catch((cause: unknown) => {
        if (active)
          setError(cause instanceof Error ? cause : new Error(String(cause)));
      })
      .finally(() => {
        if (active) setIsLoading(false);
      });

    return () => {
      active = false;
    };
  }, [refreshKey]);

  const refresh = () => setRefreshKey((k) => k + 1);

  return { data, error, isLoading, refresh } as const;
}
