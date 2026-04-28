import { useEffect, useState } from 'react';
import { api } from '../api/client';

export default function Certificates() {
  const [list, setList] = useState([]);
  const [code, setCode] = useState('');
  const [verifyResult, setVerifyResult] = useState(null);
  const [error, setError] = useState('');

  const load = async () => {
    try {
      const res = await api.get('/api/certificates');
      setList(res.data);
    } catch (e) {
      setError(e.response?.data?.message || 'Failed to load certificates');
    }
  };

  useEffect(() => {
    load();
  }, []);

  const verify = async (e) => {
    e.preventDefault();
    setError('');
    setVerifyResult(null);
    try {
      const res = await api.get(`/api/certificates/verify/${encodeURIComponent(code.trim())}`);
      setVerifyResult(res.data);
    } catch (e) {
      setError(e.response?.data?.message || 'Invalid code');
    }
  };

  return (
    <div className="container">
      <h1>Certificates</h1>
      <p className="muted">PDFs are stored on the server; links are shareable. Verification is public (no login).</p>

      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <h2 style={{ marginTop: 0 }}>Verify certificate</h2>
        <form onSubmit={verify} style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap', alignItems: 'flex-end' }}>
          <div className="field" style={{ marginBottom: 0, flex: 1, minWidth: 220 }}>
            <label>Verification code</label>
            <input value={code} onChange={(e) => setCode(e.target.value)} placeholder="Paste UUID-style code" />
          </div>
          <button type="submit" className="btn btn-primary">
            Verify
          </button>
        </form>
        {verifyResult && (
          <div style={{ marginTop: '1rem' }}>
            <p>
              <strong>{verifyResult.studentName}</strong> — issued {verifyResult.issueDate}
            </p>
            {verifyResult.certificateLink && (
              <a href={verifyResult.certificateLink} target="_blank" rel="noreferrer">
                Download PDF
              </a>
            )}
          </div>
        )}
        {error && <p style={{ color: 'var(--danger)', marginTop: '0.75rem' }}>{error}</p>}
      </div>

      <div className="card">
        <h2 style={{ marginTop: 0 }}>Your certificates</h2>
        <div className="table-wrap">
          <table className="data">
            <thead>
              <tr>
                <th>Student</th>
                <th>Issued</th>
                <th>Code</th>
                <th>PDF</th>
              </tr>
            </thead>
            <tbody>
              {list.map((c) => (
                <tr key={c.id}>
                  <td>{c.studentName}</td>
                  <td>{c.issueDate}</td>
                  <td style={{ fontFamily: 'monospace', fontSize: '0.8rem' }}>{c.verificationCode}</td>
                  <td>
                    {c.certificateLink ? (
                      <a href={c.certificateLink} target="_blank" rel="noreferrer">
                        Open
                      </a>
                    ) : (
                      '—'
                    )}
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
