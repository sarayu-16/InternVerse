import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Layout.css';

export default function Layout({ children }) {
  const { user, logout, isAuthenticated, role } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="layout">
      <header className="topbar">
        <div className="topbar-inner container">
          <Link to="/" className="brand">
            <span className="brand-mark">IV</span>
            InternVerse
          </Link>
          {isAuthenticated && (
            <nav className="nav">
              <NavLink to="/dashboard" className={({ isActive }) => (isActive ? 'active' : '')}>
                Dashboard
              </NavLink>
              <NavLink to="/tasks">Tasks</NavLink>
              <NavLink to="/submissions">Submissions</NavLink>
              {(role === 'ADMIN' || role === 'MENTOR') && (
                <NavLink to="/evaluations">Evaluations</NavLink>
              )}
              <NavLink to="/certificates">Certificates</NavLink>
              {role === 'ADMIN' && <NavLink to="/users">Users</NavLink>}
            </nav>
          )}
          <div className="topbar-right">
            {isAuthenticated ? (
              <>
                <span className="user-pill">
                  {user?.name}
                  <span className={`mini-badge mini-${(role || '').toLowerCase()}`}>{role}</span>
                </span>
                <button type="button" className="btn btn-ghost" onClick={handleLogout}>
                  Log out
                </button>
              </>
            ) : (
              <Link to="/login" className="btn btn-primary">
                Sign in
              </Link>
            )}
          </div>
        </div>
      </header>
      <main className="main-content">{children}</main>
      <footer className="footer container muted">
        InternVerse — Internship Management System
      </footer>
    </div>
  );
}
