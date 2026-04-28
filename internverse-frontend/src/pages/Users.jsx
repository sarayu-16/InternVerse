import { useEffect, useState } from 'react';
import { api } from '../api/client';

const empty = {
  name: '',
  email: '',
  password: '',
  role: 'MENTOR',
  college: '',
  skills: '',
};

export default function Users() {
  const [users, setUsers] = useState([]);
  const [form, setForm] = useState(empty);
  const [error, setError] = useState('');

  const load = async () => {
    try {
      const res = await api.get('/api/users');
      setUsers(res.data);
    } catch (e) {
      setError(e.response?.data?.message || 'Failed to load users');
    }
  };

  useEffect(() => {
    load();
  }, []);

  const create = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const skills = form.skills
        .split(',')
        .map((s) => s.trim())
        .filter(Boolean);
      await api.post('/api/users', {
        name: form.name,
        email: form.email,
        password: form.password,
        role: form.role,
        college: form.college || undefined,
        skills: skills.length ? skills : undefined,
      });
      setForm(empty);
      await load();
    } catch (e) {
      setError(e.response?.data?.message || 'Create failed');
    }
  };

  const remove = async (id) => {
    if (!window.confirm('Delete this user?')) return;
    try {
      await api.delete(`/api/users/${id}`);
      await load();
    } catch (e) {
      setError(e.response?.data?.message || 'Delete failed');
    }
  };

  return (
    <div className="container">
      <h1>Users</h1>
      <p className="muted">Admins can create mentors or additional admins and remove accounts.</p>

      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <h2 style={{ marginTop: 0 }}>Create user</h2>
        <form onSubmit={create}>
          <div className="field">
            <label>Name</label>
            <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
          </div>
          <div className="field">
            <label>Email</label>
            <input
              type="email"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
              required
            />
          </div>
          <div className="field">
            <label>Password</label>
            <input
              type="password"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              minLength={8}
              required
            />
          </div>
          <div className="field">
            <label>Role</label>
            <select value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}>
              <option value="ADMIN">ADMIN</option>
              <option value="MENTOR">MENTOR</option>
              <option value="INTERN">INTERN</option>
            </select>
          </div>
          <div className="field">
            <label>College</label>
            <input value={form.college} onChange={(e) => setForm({ ...form, college: e.target.value })} />
          </div>
          <div className="field">
            <label>Skills (comma-separated)</label>
            <input value={form.skills} onChange={(e) => setForm({ ...form, skills: e.target.value })} />
          </div>
          {error && <p style={{ color: 'var(--danger)' }}>{error}</p>}
          <button type="submit" className="btn btn-primary">
            Create
          </button>
        </form>
      </div>

      <div className="card">
        <h2 style={{ marginTop: 0 }}>Directory</h2>
        <div className="table-wrap">
          <table className="data">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Role</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id}>
                  <td>{u.name}</td>
                  <td>{u.email}</td>
                  <td>
                    <span className={`badge badge-${u.role.toLowerCase()}`}>{u.role}</span>
                  </td>
                  <td>
                    <button type="button" className="btn btn-danger" onClick={() => remove(u.id)}>
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
