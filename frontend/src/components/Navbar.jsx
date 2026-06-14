import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Briefcase, User, Search, LogOut } from 'lucide-react';
import { useContext } from 'react';
import { AuthContext } from '../context/AuthContext';

const Navbar = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { user, logout } = useContext(AuthContext);

  const isActive = (path) => {
    return location.pathname === path ? 'active' : '';
  };

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="navbar">
      <Link to="/" className="nav-brand" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
        <Briefcase size={28} color="var(--primary)" />
        Elevate
      </Link>
      
      <div className="nav-links">
        <Link to="/jobs" className={`nav-link ${isActive('/jobs')}`}>
          Find Jobs
        </Link>
        {user ? (
          <>
            <Link to="/dashboard" className={`nav-link ${isActive('/dashboard')}`}>
              Dashboard
            </Link>
            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
              <span style={{ color: 'var(--text-secondary)' }}>{user.name || user.email}</span>
              <button onClick={handleLogout} className="btn-primary" style={{ padding: '0.5rem 1rem', background: 'transparent', border: '1px solid var(--border)', color: 'var(--text-primary)' }}>
                <LogOut size={18} />
              </button>
            </div>
          </>
        ) : (
          <Link to="/login" className="btn-primary" style={{ gap: '0.5rem' }}>
            <User size={18} />
            Sign In
          </Link>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
