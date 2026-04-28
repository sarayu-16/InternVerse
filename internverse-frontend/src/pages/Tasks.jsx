import { useEffect, useState } from 'react';
import { api } from '../api/client';
import { useAuth } from '../context/AuthContext';

const emptyTask = {
  title: '',
  description: '',
  category: '',
  deadline: '',
  status: 'ACTIVE',
};

export default function Tasks() {
  const { role } = useAuth();
  const [tasks, setTasks] = useState([]);
  const [error, setError] = useState('');
  const [form, setForm] = useState(emptyTask);
  const [loading, setLoading] = useState(false);

  const load = async () => {
    try {
      const res = await api.get('/api/tasks');
      setTasks(res.data);
    } catch (e) {
      setError(e.response?.data?.message || 'Failed to load tasks');
    }
  };

  useEffect(() => {
    load();
  }, []);

  const createTask = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      await api.post('/api/tasks', {
        title: form.title,
        description: form.description,
        category: form.category,
        deadline: form.deadline ? new Date(form.deadline).toISOString() : null,
        status: form.status,
      });
      setForm(emptyTask);
      await load();
    } catch (e) {
      setError(e.response?.data?.message || 'Could not create task');
    } finally {
      setLoading(false);
    }
  };

  const remove = async (id) => {
    if (!window.confirm('Delete this task?')) return;
    try {
      await api.delete(`/api/tasks/${id}`);
      await load();
    } catch (e) {
      setError(e.response?.data?.message || 'Delete failed');
    }
  };

  return (
    <div className="container">
      <h1>Tasks</h1>
      <p className="muted">Admins create and manage tasks; interns see all tasks and receive assignments via Submissions.</p>

      {role === 'ADMIN' && (
        <div className="card" style={{ marginBottom: '1.5rem' }}>
          <h2 style={{ marginTop: 0 }}>New task</h2>
          <form onSubmit={createTask}>
            <div className="field">
              <label>Title</label>
              <input
                value={form.title}
                onChange={(e) => setForm({ ...form, title: e.target.value })}
                required
              />
            </div>
            <div className="field">
              <label>Description</label>
              <textarea
                rows={3}
                value={form.description}
                onChange={(e) => setForm({ ...form, description: e.target.value })}
              />
            </div>
            <div className="field">
              <label>Category</label>
              <input
                value={form.category}
                onChange={(e) => setForm({ ...form, category: e.target.value })}
                placeholder="AI, Web, Java…"
              />
            </div>
            <div className="field">
              <label>Deadline</label>
              <input
                type="datetime-local"
                value={form.deadline}
                onChange={(e) => setForm({ ...form, deadline: e.target.value })}
              />
            </div>
            <div className="field">
              <label>Status</label>
              <select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
                <option value="ACTIVE">ACTIVE</option>
                <option value="ARCHIVED">ARCHIVED</option>
              </select>
            </div>
            {error && <p style={{ color: 'var(--danger)' }}>{error}</p>}
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Saving…' : 'Create task'}
            </button>
          </form>
        </div>
      )}

      <div className="card">
        <h2 style={{ marginTop: 0 }}>All tasks</h2>
        <div className="table-wrap">
          <table className="data">
            <thead>
              <tr>
                <th>Title</th>
                <th>Category</th>
                <th>Deadline</th>
                <th>Status</th>
                <th>Posted by</th>
                {role === 'ADMIN' && <th />}
              </tr>
            </thead>
            <tbody>
              {tasks.map((t) => (
                <tr key={t.id}>
                  <td>{t.title}</td>
                  <td>{t.category || '—'}</td>
                  <td>{t.deadline ? new Date(t.deadline).toLocaleString() : '—'}</td>
                  <td>{t.status}</td>
                  <td>{t.postedByName || t.postedById}</td>
                  {role === 'ADMIN' && (
                    <td>
                      <button type="button" className="btn btn-danger" onClick={() => remove(t.id)}>
                        Delete
                      </button>
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
