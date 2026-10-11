import { useState, useEffect } from 'react';
import { fetchTasks } from '../api';

export function useTasks(query, status, page, pageSize) {
  const [tasks, setTasks] = useState([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    // FIX #9: Guard against race conditions. If the user types a new query
    //         before the old fetch resolves, the old fetch's .then() sees
    //         isCurrent=false and skips state updates — preventing stale
    //         data from overwriting newer results.
    let isCurrent = true;

    // FIX #11: Clear previous error when starting a new request
    setError(null);
    setLoading(true);

    fetchTasks({ query, status, page, pageSize })
      .then((data) => {
        if (!isCurrent) return;
        setTasks(data.items);
        setTotal(data.total);
        setLoading(false);
      })
      .catch((err) => {
        if (!isCurrent) return;
        // FIX #10: Reset loading on error — otherwise spinner spins forever
        setError(err.message);
        setLoading(false);
      });

    // Cleanup: mark this fetch as stale when the effect re-runs
    return () => {
      isCurrent = false;
    };
  }, [query, status, page, pageSize]);

  return { tasks, total, loading, error };
}
