import { useEffect, useState } from 'react';
import { api } from '../api/client';

export default function Evaluations() {
  const [list, setList] = useState([]);
  const [subs, setSubs] = useState([]);
  const [interns, setInterns] = useState([]);
  const [form, setForm] = useState({
    internId: '',
    submissionId: '',
    score: '85',
    feedback: '',
  });
  const [error, setError] = useState('');

  const load = async () => {
    try {
      const [ev, sub, users] = await Promise.all([
        api.get('/api/evaluations'),
        api.get('/api/submissions'),
        api.get('/api/users/role/INTERN'),
      ]);
      setList(ev.data);
      setSubs(sub.data.filter((s) => s.status === 'SUBMITTED'));
      setInterns(users.data);
    } catch (e) {
      setError(e.response?.data?.message || 'Failed to load');
    }
  };

  useEffect(() => {
    load();
  }, []);

  const submit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await api.post('/api/evaluations', {
        internId: Number(form.internId),
        submissionId: form.submissionId ? Number(form.submissionId) : null,
        score: Number(form.score),
        feedback: form.feedback || null,
      });
      setForm({ internId: '', submissionId: '', score: '85', feedback: '' });
      await load();
    } catch (e) {
      setError(e.response?.data?.message || 'Could not save evaluation');
    }
  };

  return (
    <div className="container">
      <h1>Evaluations</h1>
      <p className="muted">
        Mentors and admins score interns. If a submission is linked, the submission is approved and a certificate PDF is
        generated.
      </p>

      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <h2 style={{ marginTop: 0 }}>New evaluation</h2>
        <form onSubmit={submit}>
          <div className="field">
            <label>Intern</label>
            <select
              value={form.internId}
              onChange={(e) => setForm({ ...form, internId: e.target.value })}
              required
            >
              <option value="">Select…</option>
              {interns.map((u) => (
                <option key={u.id} value={u.id}>
                  {u.name}
                </option>
              ))}
            </select>
          </div>
          <div className="field">
            <label>Submission (optional — links evaluation to task &amp; triggers certificate)</label>
            <select
              value={form.submissionId}
              onChange={(e) => setForm({ ...form, submissionId: e.target.value })}
            >
              <option value="">None</option>
              {subs.map((s) => (
                <option key={s.id} value={s.id}>
                  #{s.id} — {s.userName} / {s.taskTitle}
                </option>
              ))}
            </select>
          </div>
          <div className="field">
            <label>Score (0–100)</label>
            <input
              type="number"
              min="0"
              max="100"
              step="0.1"
              value={form.score}
              onChange={(e) => setForm({ ...form, score: e.target.value })}
              required
            />
          </div>
          <div className="field">
            <label>Feedback</label>
            <textarea rows={3} value={form.feedback} onChange={(e) => setForm({ ...form, feedback: e.target.value })} />
          </div>
          {error && <p style={{ color: 'var(--danger)' }}>{error}</p>}
          <button type="submit" className="btn btn-primary">
            Save evaluation
          </button>
        </form>
      </div>

      <div className="card">
        <h2 style={{ marginTop: 0 }}>History</h2>
        <div className="table-wrap">
          <table className="data">
            <thead>
              <tr>
                <th>Intern</th>
                <th>Evaluator</th>
                <th>Score</th>
                <th>Feedback</th>
                <th>Submission</th>
              </tr>
            </thead>
            <tbody>
              {list.map((ev) => (
                <tr key={ev.id}>
                  <td>{ev.internName}</td>
                  <td>{ev.evaluatorName || '—'}</td>
                  <td>{ev.score}</td>
                  <td>{ev.feedback || '—'}</td>
                  <td>{ev.submissionId || '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
