import { Link, useLocation } from 'react-router-dom';
import { Briefcase, User, Search } from 'lucide-react';

const Navbar = () => {
  const location = useLocation();

  const isActive = (path) => {
    return location.pathname === path ? 'active' : '';
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
        <Link to="/dashboard" className={`nav-link ${isActive('/dashboard')}`}>
          Dashboard
        </Link>
        <Link to="/login" className="btn-primary" style={{ gap: '0.5rem' }}>
          <User size={18} />
          Sign In
        </Link>
      </div>
    </nav>
  );
};

export default Navbar;
