import { useEffect, useState } from 'react';
import { api } from '../api/client';
import { useAuth } from '../context/AuthContext';

export default function Submissions() {
  const { role } = useAuth();
  const [items, setItems] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [interns, setInterns] = useState([]);
  const [error, setError] = useState('');
  const [assign, setAssign] = useState({ taskId: '', internId: '' });
  const [linkInput, setLinkInput] = useState({});

  const load = async () => {
    try {
      const [sRes, tRes] = await Promise.all([api.get('/api/submissions'), api.get('/api/tasks')]);
      setItems(sRes.data);
      setTasks(tRes.data);
      if (role === 'ADMIN') {
        const uRes = await api.get('/api/users/role/INTERN');
        setInterns(uRes.data);
      }
    } catch (e) {
      setError(e.response?.data?.message || 'Failed to load');
    }
  };

  useEffect(() => {
    load();
  }, [role]);

  const assignTask = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await api.post('/api/submissions/assign', {
        taskId: Number(assign.taskId),
        internId: Number(assign.internId),
      });
      setAssign({ taskId: '', internId: '' });
      await load();
    } catch (e) {
      setError(e.response?.data?.message || 'Assignment failed');
    }
  };

  const submitWork = async (id) => {
    const submissionLink = linkInput[id];
    if (!submissionLink) {
      setError('Enter a submission URL');
      return;
    }
    setError('');
    try {
      await api.patch(`/api/submissions/${id}/submit`, { submissionLink });
      setLinkInput((prev) => ({ ...prev, [id]: '' }));
      await load();
    } catch (e) {
      setError(e.response?.data?.message || 'Submit failed');
    }
  };

  return (
    <div className="container">
      <h1>Submissions</h1>
      <p className="muted">
        Admins assign tasks to interns. Interns submit links to their work. Admins receive email notifications when
        configured.
      </p>

      {role === 'ADMIN' && (
        <div className="card" style={{ marginBottom: '1.5rem' }}>
          <h2 style={{ marginTop: 0 }}>Assign task to intern</h2>
          <form onSubmit={assignTask} style={{ display: 'flex', flexWrap: 'wrap', gap: '1rem', alignItems: 'flex-end' }}>
            <div className="field" style={{ marginBottom: 0, minWidth: 200 }}>
              <label>Task</label>
              <select
                value={assign.taskId}
                onChange={(e) => setAssign({ ...assign, taskId: e.target.value })}
                required
              >
                <option value="">Select…</option>
                {tasks.map((t) => (
                  <option key={t.id} value={t.id}>
                    {t.title}
                  </option>
                ))}
              </select>
            </div>
            <div className="field" style={{ marginBottom: 0, minWidth: 200 }}>
              <label>Intern</label>
              <select
                value={assign.internId}
                onChange={(e) => setAssign({ ...assign, internId: e.target.value })}
                required
              >
                <option value="">Select…</option>
                {interns.map((u) => (
                  <option key={u.id} value={u.id}>
                    {u.name} ({u.email})
                  </option>
                ))}
              </select>
            </div>
            <button type="submit" className="btn btn-primary">
              Assign
            </button>
          </form>
        </div>
      )}

      {error && <p style={{ color: 'var(--danger)', marginBottom: '1rem' }}>{error}</p>}

      <div className="card">
        <h2 style={{ marginTop: 0 }}>Records</h2>
        <div className="table-wrap">
          <table className="data">
            <thead>
              <tr>
                <th>Intern</th>
                <th>Task</th>
                <th>Status</th>
                <th>Link</th>
                {role === 'INTERN' && <th>Submit</th>}
              </tr>
            </thead>
            <tbody>
              {items.map((s) => (
                <tr key={s.id}>
                  <td>{s.userName}</td>
                  <td>{s.taskTitle}</td>
                  <td>{s.status}</td>
                  <td>
                    {s.submissionLink ? (
                      <a href={s.submissionLink} target="_blank" rel="noreferrer">
                        Open
                      </a>
                    ) : (
                      '—'
                    )}
                  </td>
                  {role === 'INTERN' && (
                    <td>
                      {s.status === 'ASSIGNED' || s.status === 'SUBMITTED' ? (
                        <div style={{ display: 'flex', gap: '0.35rem' }}>
                          <input
                            placeholder="https://…"
                            value={linkInput[s.id] || ''}
                            onChange={(e) => setLinkInput({ ...linkInput, [s.id]: e.target.value })}
                            style={{ minWidth: 140 }}
                          />
                          <button type="button" className="btn btn-primary" onClick={() => submitWork(s.id)}>
                            Save
                          </button>
                        </div>
                      ) : (
                        '—'
                      )}
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
