import { useEffect, useState } from 'react';
import { getLaunches } from './launchesApi';
import type { Launch } from '../../shared/types/launch';

export function useLaunches() {
  const [data, setData] = useState<Launch[] | null>(null);
  const [error, setError] = useState<Error | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [refreshKey, setRefreshKey] = useState(0);

  useEffect(() => {
    let active = true;
    setIsLoading(true);
    setError(null);

    getLaunches()
      .then((launches) => {
        if (active) setData(launches);
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
