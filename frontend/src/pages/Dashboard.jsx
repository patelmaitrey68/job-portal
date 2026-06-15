import { useState, useEffect, useContext } from 'react';
import { User, FileText, Bookmark, Bell, Loader2, ExternalLink, Clock, CheckCircle, XCircle, AlertCircle, Star } from 'lucide-react';
import { AuthContext } from '../context/AuthContext';
import { Navigate, Link } from 'react-router-dom';
import api from '../api/axios';
import EmployerDashboard from './EmployerDashboard';
import toast from 'react-hot-toast';

const Dashboard = () => {
  const { user } = useContext(AuthContext);
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({ total: 0, pending: 0, shortlisted: 0, rejected: 0 });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await api.get('/applications', { params: { page: 0, size: 20 } });
        const apps = response.data.content || [];
        setApplications(apps);
        
        // Calculate stats from real data
        setStats({
          total: response.data.totalElements || apps.length,
          pending: apps.filter(a => a.status === 'pending').length,
          shortlisted: apps.filter(a => a.status === 'shortlisted').length,
          rejected: apps.filter(a => a.status === 'rejected').length,
        });
      } catch (err) {
        console.error('Failed to fetch applications:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  if (!user) {
    return <Navigate to="/login" />;
  }

  if (user.accountType === 'EMPLOYER') {
    return <EmployerDashboard />;
  }

  const getStatusBadge = (status) => {
    const styles = {
      pending: { bg: 'rgba(245, 158, 11, 0.2)', color: '#f59e0b', icon: <Clock size={14} /> },
      shortlisted: { bg: 'rgba(16, 185, 129, 0.2)', color: '#10b981', icon: <CheckCircle size={14} /> },
      rejected: { bg: 'rgba(239, 68, 68, 0.2)', color: '#ef4444', icon: <XCircle size={14} /> },
      withdrawn: { bg: 'rgba(107, 114, 128, 0.2)', color: '#6b7280', icon: <AlertCircle size={14} /> },
    };
    const s = styles[status] || styles.pending;
    return (
      <span style={{ background: s.bg, color: s.color, padding: '0.3rem 0.75rem', borderRadius: '20px', fontSize: '0.8rem', fontWeight: '500', display: 'inline-flex', alignItems: 'center', gap: '0.35rem', textTransform: 'capitalize' }}>
        {s.icon} {status}
      </span>
    );
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return 'N/A';
    return new Date(dateStr).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  };

  // Application status timeline
  const StatusTimeline = ({ status }) => {
    const steps = ['pending', 'shortlisted', 'interview', 'hired'];
    const currentIndex = status === 'rejected' ? -1 : steps.indexOf(status);
    
    return (
      <div style={{ display: 'flex', alignItems: 'center', gap: '0', width: '100%', marginTop: '0.5rem' }}>
        {steps.map((step, i) => (
          <div key={step} style={{ display: 'flex', alignItems: 'center', flex: 1 }}>
            <div style={{
              width: '24px', height: '24px', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '0.65rem', fontWeight: '600',
              background: i <= currentIndex ? 'var(--primary)' : status === 'rejected' && i === 0 ? '#ef4444' : 'rgba(255,255,255,0.1)',
              color: i <= currentIndex || (status === 'rejected' && i === 0) ? 'white' : 'var(--text-secondary)',
              flexShrink: 0
            }}>
              {i <= currentIndex ? '✓' : i + 1}
            </div>
            {i < steps.length - 1 && (
              <div style={{ flex: 1, height: '2px', background: i < currentIndex ? 'var(--primary)' : 'rgba(255,255,255,0.1)' }} />
            )}
          </div>
        ))}
      </div>
    );
  };

  const acceptOffer = async (appId) => {
    try {
      await api.post(`/applications/${appId}/accept-offer`);
      toast.success('Offer Accepted! Other active applications have been withdrawn.');
      // Refresh applications
      const response = await api.get('/applications', { params: { page: 0, size: 20 } });
      setApplications(response.data.content || []);
    } catch (err) {
      toast.error('Failed to accept offer');
    }
  };

  const [activeTab, setActiveTab] = useState('all');

  const filteredApplications = applications.filter(app => {
    if (activeTab === 'all') return true;
    if (activeTab === 'pending') return app.status === 'pending' || app.status === 'shortlisted' || app.status === 'interview';
    if (activeTab === 'accepted') return app.status === 'hired' || app.status === 'accepted';
    if (activeTab === 'rejected') return app.status === 'rejected' || app.status === 'withdrawn';
    return true;
  });

  return (
    <div className="page-container">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>Welcome, {user.name || user.email}</h1>
          <p style={{ color: 'var(--text-secondary)' }}>Here's what's happening with your job applications.</p>
        </div>
        <Link to="/jobs" className="btn-primary" style={{ padding: '0.6rem 1.2rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <ExternalLink size={18} /> Find Jobs
        </Link>
      </div>

      {/* Stats Cards — Real Data */}
      <div className="grid-auto-fit" style={{ marginBottom: '3rem' }}>
        <div className="glass-card" style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
          <div style={{ background: 'rgba(139, 92, 246, 0.2)', padding: '1rem', borderRadius: '12px' }}>
            <FileText color="var(--primary)" size={24} />
          </div>
          <div>
            <h3 style={{ fontSize: '1.5rem', fontWeight: '700' }}>{loading ? '...' : stats.total}</h3>
            <p style={{ color: 'var(--text-secondary)' }}>Total Applications</p>
          </div>
        </div>
        <div className="glass-card" style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
          <div style={{ background: 'rgba(245, 158, 11, 0.2)', padding: '1rem', borderRadius: '12px' }}>
            <Clock color="#f59e0b" size={24} />
          </div>
          <div>
            <h3 style={{ fontSize: '1.5rem', fontWeight: '700' }}>{loading ? '...' : stats.pending}</h3>
            <p style={{ color: 'var(--text-secondary)' }}>Pending Review</p>
          </div>
        </div>
        <div className="glass-card" style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
          <div style={{ background: 'rgba(16, 185, 129, 0.2)', padding: '1rem', borderRadius: '12px' }}>
            <CheckCircle color="#10b981" size={24} />
          </div>
          <div>
            <h3 style={{ fontSize: '1.5rem', fontWeight: '700' }}>{loading ? '...' : applications.filter(a => a.status === 'hired' || a.status === 'accepted').length}</h3>
            <p style={{ color: 'var(--text-secondary)' }}>Accepted / Hired</p>
          </div>
        </div>
        <div className="glass-card" style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
          <div style={{ background: 'rgba(239, 68, 68, 0.2)', padding: '1rem', borderRadius: '12px' }}>
            <XCircle color="#ef4444" size={24} />
          </div>
          <div>
            <h3 style={{ fontSize: '1.5rem', fontWeight: '700' }}>{loading ? '...' : stats.rejected}</h3>
            <p style={{ color: 'var(--text-secondary)' }}>Rejected</p>
          </div>
        </div>
      </div>

      {/* Recent Applications — Real Data */}
      <div className="glass-card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '1rem' }}>
          <h2 style={{ fontSize: '1.5rem', margin: 0 }}>Your Applications</h2>
          
          {/* Tabs */}
          <div style={{ display: 'flex', gap: '0.5rem', background: 'rgba(255,255,255,0.05)', padding: '0.3rem', borderRadius: '8px' }}>
            {['all', 'pending', 'accepted', 'rejected'].map(tab => (
              <button
                key={tab}
                onClick={() => setActiveTab(tab)}
                style={{
                  padding: '0.4rem 0.8rem', borderRadius: '6px', border: 'none', cursor: 'pointer',
                  background: activeTab === tab ? 'var(--primary)' : 'transparent',
                  color: activeTab === tab ? 'white' : 'var(--text-secondary)',
                  fontWeight: activeTab === tab ? '600' : '400',
                  transition: 'all 0.2s', textTransform: 'capitalize'
                }}
              >
                {tab}
              </button>
            ))}
          </div>
        </div>

        {loading ? (
          <div style={{ display: 'flex', justifyContent: 'center', padding: '3rem' }}>
            <Loader2 size={32} color="var(--primary)" style={{ animation: 'spin 1s linear infinite' }} />
          </div>
        ) : applications.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '3rem' }}>
            <FileText size={48} color="var(--text-secondary)" style={{ marginBottom: '1rem' }} />
            <h3 style={{ marginBottom: '0.5rem' }}>No applications yet</h3>
            <p style={{ color: 'var(--text-secondary)', marginBottom: '1.5rem' }}>Start browsing jobs and apply to your first position!</p>
            <Link to="/jobs" className="btn-primary" style={{ display: 'inline-flex' }}>Find Jobs</Link>
          </div>
        ) : filteredApplications.length === 0 ? (
           <div style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-secondary)' }}>
             <p>No applications found in the '{activeTab}' category.</p>
           </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {filteredApplications.map(app => (
              <div key={app.id} style={{ padding: '1.25rem', background: 'rgba(255,255,255,0.03)', borderRadius: '12px', border: '1px solid var(--border)' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.75rem' }}>
                  <div>
                    <h3 style={{ fontSize: '1.1rem', fontWeight: '600', marginBottom: '0.25rem' }}>{app.jobTitle || 'Job Title'}</h3>
                    <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>{app.companyName || 'Company'} • Applied {formatDate(app.appliedAt)}</p>
                  </div>
                  <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: '0.5rem' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                      {app.matchScore != null && (
                        <span style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', fontSize: '0.85rem', color: app.matchScore >= 70 ? '#10b981' : app.matchScore >= 40 ? '#f59e0b' : '#ef4444' }}>
                          <Star size={14} /> {Math.round(app.matchScore)}%
                        </span>
                      )}
                      {getStatusBadge(app.status)}
                    </div>
                    {app.status === 'hired' && (
                      <button onClick={() => acceptOffer(app.id)} style={{ background: '#10b981', color: 'white', border: 'none', padding: '0.4rem 0.8rem', borderRadius: '6px', cursor: 'pointer', fontSize: '0.85rem', fontWeight: 'bold' }}>
                        Accept Offer
                      </button>
                    )}
                  </div>
                </div>
                {app.rejectionReason && app.status === 'rejected' && (
                  <div style={{ background: 'rgba(239, 68, 68, 0.1)', color: '#ef4444', padding: '0.75rem', borderRadius: '8px', marginBottom: '1rem', fontSize: '0.9rem' }}>
                    <strong>Employer Feedback:</strong> {app.rejectionReason}
                  </div>
                )}
                <StatusTimeline status={app.status} />
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
