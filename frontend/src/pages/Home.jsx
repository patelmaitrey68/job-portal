import { useState } from 'react';
import { Search, MapPin, Briefcase, TrendingUp, Users, Building } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import api from '../api/axios';

const Home = () => {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');
  const [locationQuery, setLocationQuery] = useState('');
  const [featuredJobs, setFeaturedJobs] = useState([]);
  const [loadingFeatured, setLoadingFeatured] = useState(true);

  useEffect(() => {
    const fetchFeatured = async () => {
      try {
        const response = await api.get('/jobs/featured');
        setFeaturedJobs(response.data || []);
      } catch (err) {
        console.error('Failed to fetch featured jobs:', err);
        // Fallback: fetch regular jobs
        try {
          const fallback = await api.get('/jobs', { params: { page: 0, size: 3 } });
          setFeaturedJobs(fallback.data.content || []);
        } catch (e) { /* ignore */ }
      } finally {
        setLoadingFeatured(false);
      }
    };
    fetchFeatured();
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    const params = new URLSearchParams();
    if (searchQuery.trim()) params.set('q', searchQuery.trim());
    if (locationQuery.trim()) params.set('location', locationQuery.trim());
    navigate(`/jobs?${params.toString()}`);
  };

  const formatSalary = (min, max) => {
    if (!min && !max) return 'Competitive';
    const fmt = (v) => `$${(v / 1000).toFixed(0)}k`;
    if (min && max) return `${fmt(min)} - ${fmt(max)}`;
    if (min) return `From ${fmt(min)}`;
    return `Up to ${fmt(max)}`;
  };

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

        {/* Working Search Bar */}
        <form onSubmit={handleSearch} className="glass-card" style={{ display: 'flex', gap: '1rem', width: '100%', maxWidth: '800px', padding: '1.5rem', borderRadius: '50px' }}>
          <div style={{ flex: 1, display: 'flex', alignItems: 'center', background: 'rgba(15, 23, 42, 0.6)', borderRadius: '30px', padding: '0 1rem' }}>
            <Search color="var(--text-secondary)" size={20} />
            <input 
              type="text" 
              placeholder="Job title, keyword, or company" 
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              style={{ border: 'none', background: 'transparent', color: 'white', padding: '1rem', width: '100%', outline: 'none' }}
            />
          </div>
          <div style={{ flex: 1, display: 'flex', alignItems: 'center', background: 'rgba(15, 23, 42, 0.6)', borderRadius: '30px', padding: '0 1rem' }}>
            <MapPin color="var(--text-secondary)" size={20} />
            <input 
              type="text" 
              placeholder="City, state, or remote" 
              value={locationQuery}
              onChange={(e) => setLocationQuery(e.target.value)}
              style={{ border: 'none', background: 'transparent', color: 'white', padding: '1rem', width: '100%', outline: 'none' }}
            />
          </div>
          <button type="submit" className="btn-primary" style={{ borderRadius: '30px', padding: '0 2rem' }}>
            Search
          </button>
        </form>
      </section>

      {/* Stats */}
      <section style={{ display: 'flex', justifyContent: 'center', gap: '3rem', marginBottom: '4rem', flexWrap: 'wrap' }}>
        <div style={{ textAlign: 'center' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
            <Briefcase size={24} color="var(--primary)" />
            <span style={{ fontSize: '2rem', fontWeight: '700' }}>{featuredJobs.length > 0 ? '100+' : '...'}</span>
          </div>
          <p style={{ color: 'var(--text-secondary)' }}>Active Jobs</p>
        </div>
        <div style={{ textAlign: 'center' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
            <Building size={24} color="#10b981" />
            <span style={{ fontSize: '2rem', fontWeight: '700' }}>50+</span>
          </div>
          <p style={{ color: 'var(--text-secondary)' }}>Companies</p>
        </div>
        <div style={{ textAlign: 'center' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
            <TrendingUp size={24} color="#f59e0b" />
            <span style={{ fontSize: '2rem', fontWeight: '700' }}>AI</span>
          </div>
          <p style={{ color: 'var(--text-secondary)' }}>Smart Matching</p>
        </div>
      </section>

      {/* Featured Jobs — Real data from API */}
      <section style={{ margin: '4rem 0' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
          <h2 style={{ fontSize: '2rem' }}>Featured Opportunities</h2>
          <Link to="/jobs" style={{ color: 'var(--primary)', textDecoration: 'none', fontSize: '0.95rem', display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
            View all jobs →
          </Link>
        </div>
        
        {loadingFeatured ? (
          <div style={{ textAlign: 'center', padding: '3rem' }}>
            <p style={{ color: 'var(--text-secondary)' }}>Loading featured jobs...</p>
          </div>
        ) : featuredJobs.length === 0 ? (
          <div className="glass-card" style={{ textAlign: 'center', padding: '3rem' }}>
            <p style={{ color: 'var(--text-secondary)' }}>No featured jobs yet. Check back soon!</p>
          </div>
        ) : (
          <div className="grid-auto-fit">
            {featuredJobs.slice(0, 3).map((job) => (
              <div key={job.id} className="glass-card">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
                  <div style={{ background: 'rgba(139, 92, 246, 0.2)', padding: '1rem', borderRadius: '12px' }}>
                    <Briefcase color="var(--primary)" size={24} />
                  </div>
                  <span style={{ background: 'rgba(236, 72, 153, 0.2)', color: 'var(--secondary)', padding: '0.25rem 0.75rem', borderRadius: '20px', fontSize: '0.875rem', fontWeight: '500', textTransform: 'capitalize' }}>
                    {job.jobType || 'Full Time'}
                  </span>
                </div>
                <h3 style={{ fontSize: '1.25rem', marginBottom: '0.5rem' }}>{job.title}</h3>
                <p style={{ color: 'var(--text-secondary)', marginBottom: '0.5rem' }}>{job.company} • {job.location}</p>
                <p style={{ color: '#10b981', fontSize: '0.9rem', marginBottom: '1rem' }}>{formatSalary(job.salaryMin, job.salaryMax)}</p>
                {job.skills && job.skills.length > 0 && (
                  <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.5rem', flexWrap: 'wrap' }}>
                    {job.skills.slice(0, 3).map((skill, i) => (
                      <span key={i} style={{ fontSize: '0.8rem', color: '#cbd5e1', background: 'rgba(255,255,255,0.05)', padding: '0.25rem 0.5rem', borderRadius: '4px' }}>{skill}</span>
                    ))}
                  </div>
                )}
                <Link to={`/jobs/${job.id}`} className="btn-primary" style={{ width: '100%' }}>View & Apply</Link>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
};

export default Home;
