import { Search, MapPin, Briefcase } from 'lucide-react';
import { Link } from 'react-router-dom';

const Home = () => {
  return (
    <div className="page-container">
      {/* Hero Section */}
      <section style={{ textAlign: 'center', margin: '4rem 0', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <h1 style={{ 
          fontSize: '3.5rem', 
          fontWeight: '700', 
          marginBottom: '1rem',
          background: 'linear-gradient(to right, #f8fafc, #94a3b8)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent'
        }}>
          Find Your Dream Job Today
        </h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: '1.2rem', maxWidth: '600px', marginBottom: '3rem' }}>
          Connect with top employers and discover opportunities that match your skills, passion, and career goals.
        </p>

        {/* Search Bar */}
        <div className="glass-card" style={{ display: 'flex', gap: '1rem', width: '100%', maxWidth: '800px', padding: '1.5rem', borderRadius: '50px' }}>
          <div style={{ flex: 1, display: 'flex', alignItems: 'center', background: 'rgba(15, 23, 42, 0.6)', borderRadius: '30px', padding: '0 1rem' }}>
            <Search color="var(--text-secondary)" size={20} />
            <input 
              type="text" 
              placeholder="Job title, keyword, or company" 
              style={{ border: 'none', background: 'transparent', color: 'white', padding: '1rem', width: '100%', outline: 'none' }}
            />
          </div>
          <div style={{ flex: 1, display: 'flex', alignItems: 'center', background: 'rgba(15, 23, 42, 0.6)', borderRadius: '30px', padding: '0 1rem' }}>
            <MapPin color="var(--text-secondary)" size={20} />
            <input 
              type="text" 
              placeholder="City, state, or remote" 
              style={{ border: 'none', background: 'transparent', color: 'white', padding: '1rem', width: '100%', outline: 'none' }}
            />
          </div>
          <button className="btn-primary" style={{ borderRadius: '30px', padding: '0 2rem' }}>
            Search
          </button>
        </div>
      </section>

      {/* Featured Jobs */}
      <section style={{ margin: '6rem 0' }}>
        <h2 style={{ fontSize: '2rem', marginBottom: '2rem' }}>Featured Opportunities</h2>
        <div className="grid-auto-fit">
          {[1, 2, 3].map((i) => (
            <div key={i} className="glass-card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
                <div style={{ background: 'rgba(139, 92, 246, 0.2)', padding: '1rem', borderRadius: '12px' }}>
                  <Briefcase color="var(--primary)" size={24} />
                </div>
                <span style={{ background: 'rgba(236, 72, 153, 0.2)', color: 'var(--secondary)', padding: '0.25rem 0.75rem', borderRadius: '20px', fontSize: '0.875rem', fontWeight: '500' }}>
                  Full Time
                </span>
              </div>
              <h3 style={{ fontSize: '1.25rem', marginBottom: '0.5rem' }}>Senior Software Engineer</h3>
              <p style={{ color: 'var(--text-secondary)', marginBottom: '1rem' }}>TechCorp Inc. • San Francisco, CA (Remote)</p>
              <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.5rem' }}>
                <span style={{ fontSize: '0.875rem', color: '#cbd5e1', background: 'rgba(255,255,255,0.05)', padding: '0.25rem 0.5rem', borderRadius: '4px' }}>React</span>
                <span style={{ fontSize: '0.875rem', color: '#cbd5e1', background: 'rgba(255,255,255,0.05)', padding: '0.25rem 0.5rem', borderRadius: '4px' }}>Spring Boot</span>
                <span style={{ fontSize: '0.875rem', color: '#cbd5e1', background: 'rgba(255,255,255,0.05)', padding: '0.25rem 0.5rem', borderRadius: '4px' }}>MongoDB</span>
              </div>
              <Link to="/jobs" className="btn-primary" style={{ width: '100%' }}>Apply Now</Link>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
};

export default Home;
