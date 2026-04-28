import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Home() {
  const { isAuthenticated } = useAuth();

  return (
    <div className="container" style={{ paddingTop: '3rem' }}>
      <div className="card" style={{ textAlign: 'center', maxWidth: 640, margin: '0 auto' }}>
        <h1 style={{ marginTop: 0, fontSize: '2rem' }}>InternVerse</h1>
        <p className="muted" style={{ fontSize: '1.1rem', lineHeight: 1.6 }}>
          Onboard interns, assign tasks, review submissions, evaluate performance, and issue verifiable PDF
          certificates — all in one place.
        </p>
        <div style={{ marginTop: '2rem', display: 'flex', gap: '1rem', justifyContent: 'center' }}>
          {isAuthenticated ? (
            <Link to="/dashboard" className="btn btn-primary">
              Go to dashboard
            </Link>
          ) : (
            <>
              <Link to="/register" className="btn btn-primary">
                Create account
              </Link>
              <Link to="/login" className="btn btn-ghost">
                Sign in
              </Link>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
