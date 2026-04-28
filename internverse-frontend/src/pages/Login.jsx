import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { api } from '../api/client';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from?.pathname || '/dashboard';

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const { data } = await api.post('/api/auth/login', { email, password });
      login({
        token: data.token,
        userId: data.userId,
        email: data.email,
        name: data.name,
        role: data.role,
      });
      navigate(from, { replace: true });
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container" style={{ maxWidth: 420, paddingTop: '3rem' }}>
      <div className="card">
        <h1 style={{ marginTop: 0 }}>Sign in</h1>
        <p className="muted">Use your InternVerse account.</p>
        <form onSubmit={handleSubmit}>
          <div className="field">
            <label htmlFor="email">Email</label>
            <input
              id="email"
              type="email"
              autoComplete="username"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <div className="field">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              autoComplete="current-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          {error && (
            <p style={{ color: 'var(--danger)', fontSize: '0.9rem', marginBottom: '1rem' }}>{error}</p>
          )}
          <button type="submit" className="btn btn-primary" disabled={loading} style={{ width: '100%' }}>
            {loading ? 'Signing in…' : 'Sign in'}
          </button>
        </form>
        <p className="muted" style={{ marginTop: '1.25rem' }}>
          No account? <Link to="/register">Register as intern</Link>
        </p>
        <p className="muted" style={{ fontSize: '0.85rem', marginTop: '1rem' }}>
          Demo admin (after DB seed): <code>admin@internverse.local</code> / <code>Admin@123</code>
        </p>
      </div>
    </div>
  );
}
