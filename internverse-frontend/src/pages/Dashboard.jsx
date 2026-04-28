import { useEffect, useState } from 'react';
import { api } from '../api/client';
import { useAuth } from '../context/AuthContext';

export default function Dashboard() {
  const { role } = useAuth();
  const [data, setData] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const res = await api.get('/api/dashboard');
        if (!cancelled) setData(res.data);
      } catch (e) {
        if (!cancelled) setError(e.response?.data?.message || 'Failed to load dashboard');
      }
    })();
    return () => {
      cancelled = true;
    };
  }, []);

  if (error) {
    return (
      <div className="container">
        <p style={{ color: 'var(--danger)' }}>{error}</p>
      </div>
    );
  }

  if (!data) {
    return (
      <div className="container">
        <p className="muted">Loading dashboard…</p>
      </div>
    );
  }

  const entries = Object.entries(data).filter(([k]) => k !== 'role');

  return (
    <div className="container">
      <h1 style={{ marginBottom: '0.5rem' }}>Dashboard</h1>
      <p className="muted" style={{ marginBottom: '1.5rem' }}>
        Role:{' '}
        <span className={`badge badge-${(data.role || role || '').toLowerCase()}`}>
          {data.role || role}
        </span>
      </p>
      <div className="grid-stats">
        {entries.map(([key, val]) => (
          <div key={key} className="stat">
            <div className="val">{String(val)}</div>
            <div className="lbl">{formatLabel(key)}</div>
          </div>
        ))}
      </div>
    </div>
  );
}

function formatLabel(key) {
  return key
    .replace(/([A-Z])/g, ' $1')
    .replace(/^./, (s) => s.toUpperCase())
    .trim();
}
