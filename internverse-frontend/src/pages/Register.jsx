import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api } from '../api/client';
import { useAuth } from '../context/AuthContext';

export default function Register() {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [college, setCollege] = useState('');
  const [skills, setSkills] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const skillList = skills
        .split(',')
        .map((s) => s.trim())
        .filter(Boolean);
      const { data } = await api.post('/api/auth/register', {
        name,
        email,
        password,
        college: college || undefined,
        skills: skillList.length ? skillList : undefined,
      });
      login({
        token: data.token,
        userId: data.userId,
        email: data.email,
        name: data.name,
        role: data.role,
      });
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container" style={{ maxWidth: 480, paddingTop: '2rem' }}>
      <div className="card">
        <h1 style={{ marginTop: 0 }}>Create intern account</h1>
        <p className="muted">Public registration creates an <strong>INTERN</strong> profile.</p>
        <form onSubmit={handleSubmit}>
          <div className="field">
            <label htmlFor="name">Full name</label>
            <input id="name" value={name} onChange={(e) => setName(e.target.value)} required />
          </div>
          <div className="field">
            <label htmlFor="email">Email</label>
            <input
              id="email"
              type="email"
              autoComplete="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <div className="field">
            <label htmlFor="password">Password (min 8 characters)</label>
            <input
              id="password"
              type="password"
              autoComplete="new-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              minLength={8}
              required
            />
          </div>
          <div className="field">
            <label htmlFor="college">College / institution</label>
            <input id="college" value={college} onChange={(e) => setCollege(e.target.value)} />
          </div>
          <div className="field">
            <label htmlFor="skills">Skills (comma-separated)</label>
            <input
              id="skills"
              placeholder="Java, React, SQL"
              value={skills}
              onChange={(e) => setSkills(e.target.value)}
            />
          </div>
          {error && (
            <p style={{ color: 'var(--danger)', fontSize: '0.9rem', marginBottom: '1rem' }}>{error}</p>
          )}
          <button type="submit" className="btn btn-primary" disabled={loading} style={{ width: '100%' }}>
            {loading ? 'Creating…' : 'Register'}
          </button>
        </form>
        <p className="muted" style={{ marginTop: '1.25rem' }}>
          Already have an account? <Link to="/login">Sign in</Link>
        </p>
      </div>
    </div>
  );
}
