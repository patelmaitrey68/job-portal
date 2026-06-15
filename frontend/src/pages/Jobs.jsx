import { useState, useEffect } from 'react';
import { Search, Filter, Briefcase, MapPin, DollarSign, Clock, ChevronRight, Loader2, X } from 'lucide-react';
import { Link, useSearchParams } from 'react-router-dom';
import api from '../api/axios';

const Jobs = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState(searchParams.get('q') || '');
  const [locationFilter, setLocationFilter] = useState(searchParams.get('location') || '');
  const [totalElements, setTotalElements] = useState(0);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  // Filter states
  const [jobTypeFilters, setJobTypeFilters] = useState({
    'full-time': false,
    'part-time': false,
    'contract': false,
    'internship': false,
  });
  const [workModeFilters, setWorkModeFilters] = useState({
    'Remote': false,
    'Hybrid': false,
    'On-site': false,
  });

  const fetchJobs = async (pageNum = 0) => {
    setLoading(true);
    try {
      let response;
      const activeJobTypes = Object.entries(jobTypeFilters).filter(([, v]) => v).map(([k]) => k);
      const activeLocations = Object.entries(workModeFilters).filter(([, v]) => v).map(([k]) => k);

      if (searchQuery.trim()) {
        response = await api.get('/jobs/search', {
          params: { q: searchQuery.trim(), page: pageNum, size: 10 }
        });
      } else if (activeJobTypes.length > 0 || activeLocations.length > 0 || locationFilter.trim()) {
        response = await api.get('/jobs', {
          params: {
            page: pageNum,
            size: 10,
            jobType: activeJobTypes[0] || undefined,
            location: locationFilter.trim() || activeLocations[0] || undefined,
          }
        });
      } else {
        response = await api.get('/jobs', { params: { page: pageNum, size: 10 } });
      }

      const data = response.data;
      setJobs(data.content || []);
      setTotalElements(data.totalElements || 0);
      setTotalPages(data.totalPages || 0);
      setPage(pageNum);
    } catch (err) {
      console.error('Failed to fetch jobs:', err);
      setJobs([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchJobs(0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [jobTypeFilters, workModeFilters]);

  // Fetch on initial load if query params exist
  useEffect(() => {
    const q = searchParams.get('q');
    const loc = searchParams.get('location');
    if (q) setSearchQuery(q);
    if (loc) setLocationFilter(loc);
    // Trigger search after setting state
    fetchJobs(0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    setSearchParams(searchQuery ? { q: searchQuery } : {});
    fetchJobs(0);
  };

  const handleJobTypeChange = (type) => {
    setJobTypeFilters(prev => ({ ...prev, [type]: !prev[type] }));
  };

  const handleWorkModeChange = (mode) => {
    setWorkModeFilters(prev => ({ ...prev, [mode]: !prev[mode] }));
  };

  const clearFilters = () => {
    setSearchQuery('');
    setLocationFilter('');
    setJobTypeFilters({ 'full-time': false, 'part-time': false, 'contract': false, 'internship': false });
    setWorkModeFilters({ 'Remote': false, 'Hybrid': false, 'On-site': false });
    setSearchParams({});
    fetchJobs(0);
  };

  const hasActiveFilters = searchQuery || locationFilter || Object.values(jobTypeFilters).some(v => v) || Object.values(workModeFilters).some(v => v);

  const formatSalary = (min, max, currency = 'USD') => {
    if (!min && !max) return 'Not specified';
    const fmt = (v) => `$${(v / 1000).toFixed(0)}k`;
    if (min && max) return `${fmt(min)} - ${fmt(max)}`;
    if (min) return `From ${fmt(min)}`;
    return `Up to ${fmt(max)}`;
  };

  const timeAgo = (dateStr) => {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const now = new Date();
    const diffMs = now - date;
    const diffMins = Math.floor(diffMs / 60000);
    if (diffMins < 60) return `${diffMins}m ago`;
    const diffHours = Math.floor(diffMins / 60);
    if (diffHours < 24) return `${diffHours}h ago`;
    const diffDays = Math.floor(diffHours / 24);
    if (diffDays < 7) return `${diffDays}d ago`;
    return `${Math.floor(diffDays / 7)}w ago`;
  };

  return (
    <div className="page-container" style={{ display: 'flex', gap: '2rem' }}>
      {/* Sidebar Filters */}
      <div style={{ flex: '0 0 250px' }}>
        <div className="glass-card" style={{ position: 'sticky', top: '100px' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1.5rem', paddingBottom: '1rem', borderBottom: '1px solid var(--border)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Filter size={20} color="var(--primary)" />
              <h3 style={{ fontSize: '1.2rem' }}>Filters</h3>
            </div>
            {hasActiveFilters && (
              <button onClick={clearFilters} style={{ background: 'none', border: 'none', color: 'var(--secondary)', cursor: 'pointer', fontSize: '0.8rem', display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                <X size={14} /> Clear
              </button>
            )}
          </div>
          
          <div style={{ marginBottom: '1.5rem' }}>
            <h4 style={{ marginBottom: '0.75rem', color: 'var(--text-secondary)' }}>Job Type</h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              {Object.entries(jobTypeFilters).map(([type, checked]) => (
                <label key={type} style={{ display: 'flex', gap: '0.5rem', cursor: 'pointer', textTransform: 'capitalize' }}>
                  <input type="checkbox" checked={checked} onChange={() => handleJobTypeChange(type)} /> {type}
                </label>
              ))}
            </div>
          </div>

          <div>
            <h4 style={{ marginBottom: '0.75rem', color: 'var(--text-secondary)' }}>Work Mode</h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              {Object.entries(workModeFilters).map(([mode, checked]) => (
                <label key={mode} style={{ display: 'flex', gap: '0.5rem', cursor: 'pointer' }}>
                  <input type="checkbox" checked={checked} onChange={() => handleWorkModeChange(mode)} /> {mode}
                </label>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div style={{ flex: '1' }}>
        <form onSubmit={handleSearch} className="glass-card" style={{ display: 'flex', gap: '1rem', marginBottom: '2rem', padding: '1rem', borderRadius: '12px' }}>
          <div style={{ flex: 1, display: 'flex', alignItems: 'center', background: 'rgba(15, 23, 42, 0.6)', borderRadius: '8px', padding: '0 1rem' }}>
            <Search color="var(--text-secondary)" size={18} />
            <input
              type="text"
              placeholder="Search jobs by title, skill, or company..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              style={{ border: 'none', background: 'transparent', color: 'white', padding: '0.75rem', width: '100%', outline: 'none' }}
            />
          </div>
          <button type="submit" className="btn-primary" style={{ padding: '0.75rem 1.5rem', borderRadius: '8px' }}>Search</button>
        </form>

        <h2 style={{ fontSize: '1.5rem', marginBottom: '1.5rem' }}>
          {loading ? 'Searching...' : `${totalElements} Job${totalElements !== 1 ? 's' : ''} Found`}
        </h2>

        {loading ? (
          <div style={{ display: 'flex', justifyContent: 'center', padding: '4rem' }}>
            <Loader2 size={40} color="var(--primary)" className="spin" style={{ animation: 'spin 1s linear infinite' }} />
          </div>
        ) : jobs.length === 0 ? (
          <div className="glass-card" style={{ textAlign: 'center', padding: '3rem' }}>
            <Briefcase size={48} color="var(--text-secondary)" style={{ marginBottom: '1rem' }} />
            <h3 style={{ marginBottom: '0.5rem' }}>No jobs found</h3>
            <p style={{ color: 'var(--text-secondary)' }}>Try adjusting your search or filters</p>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
            {jobs.map(job => (
              <Link to={`/jobs/${job.id}`} key={job.id} style={{ textDecoration: 'none', color: 'inherit' }}>
                <div className="glass-card" style={{ display: 'flex', flexDirection: 'column', gap: '1rem', cursor: 'pointer', transition: 'transform 0.2s ease, border-color 0.2s ease', border: '1px solid transparent' }}
                  onMouseEnter={e => { e.currentTarget.style.transform = 'translateY(-2px)'; e.currentTarget.style.borderColor = 'var(--primary)'; }}
                  onMouseLeave={e => { e.currentTarget.style.transform = 'translateY(0)'; e.currentTarget.style.borderColor = 'transparent'; }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                    <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
                      <div style={{ background: 'rgba(139, 92, 246, 0.2)', padding: '1rem', borderRadius: '12px' }}>
                        <Briefcase color="var(--primary)" size={24} />
                      </div>
                      <div>
                        <h3 style={{ fontSize: '1.25rem', fontWeight: '600', marginBottom: '0.25rem' }}>{job.title}</h3>
                        <p style={{ color: 'var(--text-secondary)' }}>{job.company}</p>
                      </div>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--primary)' }}>
                      <span style={{ fontSize: '0.9rem' }}>View Details</span>
                      <ChevronRight size={18} />
                    </div>
                  </div>

                  {/* Skills */}
                  {job.skills && job.skills.length > 0 && (
                    <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
                      {job.skills.slice(0, 5).map((skill, i) => (
                        <span key={i} style={{ fontSize: '0.8rem', color: '#cbd5e1', background: 'rgba(139, 92, 246, 0.1)', padding: '0.25rem 0.6rem', borderRadius: '6px', border: '1px solid rgba(139, 92, 246, 0.2)' }}>
                          {skill}
                        </span>
                      ))}
                    </div>
                  )}

                  <div style={{ display: 'flex', gap: '1.5rem', flexWrap: 'wrap', borderTop: '1px solid var(--border)', paddingTop: '1rem', marginTop: '0.5rem' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
                      <MapPin size={16} /> {job.location || 'Not specified'}
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
                      <DollarSign size={16} /> {formatSalary(job.salaryMin, job.salaryMax, job.salaryCurrency)}
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
                      <Clock size={16} /> {job.jobType || 'Not specified'} • {timeAgo(job.createdAt)}
                    </div>
                  </div>
                </div>
              </Link>
            ))}

            {/* Pagination */}
            {totalPages > 1 && (
              <div style={{ display: 'flex', justifyContent: 'center', gap: '0.5rem', marginTop: '1rem' }}>
                <button
                  disabled={page === 0}
                  onClick={() => fetchJobs(page - 1)}
                  className="btn-primary"
                  style={{ padding: '0.5rem 1rem', opacity: page === 0 ? 0.5 : 1, background: 'transparent', border: '1px solid var(--border)', color: 'var(--text-primary)' }}
                >
                  Previous
                </button>
                <span style={{ display: 'flex', alignItems: 'center', color: 'var(--text-secondary)', padding: '0 1rem' }}>
                  Page {page + 1} of {totalPages}
                </span>
                <button
                  disabled={page >= totalPages - 1}
                  onClick={() => fetchJobs(page + 1)}
                  className="btn-primary"
                  style={{ padding: '0.5rem 1rem', opacity: page >= totalPages - 1 ? 0.5 : 1, background: 'transparent', border: '1px solid var(--border)', color: 'var(--text-primary)' }}
                >
                  Next
                </button>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default Jobs;
