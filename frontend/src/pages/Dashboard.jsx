import { User, FileText, Bookmark, Settings, Bell } from 'lucide-react';

const Dashboard = () => {
  return (
    <div style={{ padding: '2rem 5%', maxWidth: '1200px', margin: '0 auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>Welcome, John Doe</h1>
          <p style={{ color: 'var(--text-secondary)' }}>Here is what's happening with your job applications today.</p>
        </div>
        <button className="btn-primary" style={{ borderRadius: '50%', width: '40px', height: '40px', padding: '0' }}>
          <Bell size={20} />
        </button>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '2rem', marginBottom: '3rem' }}>
        <div className="glass-card" style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
          <div style={{ background: 'rgba(139, 92, 246, 0.2)', padding: '1rem', borderRadius: '12px' }}>
            <FileText color="var(--primary)" size={24} />
          </div>
          <div>
            <h3 style={{ fontSize: '1.5rem', fontWeight: '700' }}>12</h3>
            <p style={{ color: 'var(--text-secondary)' }}>Applications</p>
          </div>
        </div>
        <div className="glass-card" style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
          <div style={{ background: 'rgba(236, 72, 153, 0.2)', padding: '1rem', borderRadius: '12px' }}>
            <Bookmark color="var(--secondary)" size={24} />
          </div>
          <div>
            <h3 style={{ fontSize: '1.5rem', fontWeight: '700' }}>5</h3>
            <p style={{ color: 'var(--text-secondary)' }}>Saved Jobs</p>
          </div>
        </div>
        <div className="glass-card" style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
          <div style={{ background: 'rgba(16, 185, 129, 0.2)', padding: '1rem', borderRadius: '12px' }}>
            <User color="#10b981" size={24} />
          </div>
          <div>
            <h3 style={{ fontSize: '1.5rem', fontWeight: '700' }}>85%</h3>
            <p style={{ color: 'var(--text-secondary)' }}>Profile Match</p>
          </div>
        </div>
      </div>

      <div className="glass-card">
        <h2 style={{ fontSize: '1.5rem', marginBottom: '1.5rem' }}>Recent Applications</h2>
        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid var(--border)', color: 'var(--text-secondary)' }}>
                <th style={{ padding: '1rem' }}>Job Title</th>
                <th style={{ padding: '1rem' }}>Company</th>
                <th style={{ padding: '1rem' }}>Date Applied</th>
                <th style={{ padding: '1rem' }}>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr style={{ borderBottom: '1px solid var(--border)' }}>
                <td style={{ padding: '1rem', fontWeight: '500' }}>Frontend Developer</td>
                <td style={{ padding: '1rem', color: 'var(--text-secondary)' }}>TechNova</td>
                <td style={{ padding: '1rem', color: 'var(--text-secondary)' }}>Oct 12, 2023</td>
                <td style={{ padding: '1rem' }}>
                  <span style={{ background: 'rgba(245, 158, 11, 0.2)', color: '#f59e0b', padding: '0.25rem 0.75rem', borderRadius: '20px', fontSize: '0.875rem' }}>Pending</span>
                </td>
              </tr>
              <tr style={{ borderBottom: '1px solid var(--border)' }}>
                <td style={{ padding: '1rem', fontWeight: '500' }}>UI/UX Designer</td>
                <td style={{ padding: '1rem', color: 'var(--text-secondary)' }}>CreativeSolutions</td>
                <td style={{ padding: '1rem', color: 'var(--text-secondary)' }}>Oct 10, 2023</td>
                <td style={{ padding: '1rem' }}>
                  <span style={{ background: 'rgba(16, 185, 129, 0.2)', color: '#10b981', padding: '0.25rem 0.75rem', borderRadius: '20px', fontSize: '0.875rem' }}>Interview</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
