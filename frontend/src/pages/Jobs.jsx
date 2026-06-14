import { Search, Filter, Briefcase, MapPin, DollarSign, Clock } from 'lucide-react';

const Jobs = () => {
  const jobs = [
    { id: 1, title: 'Senior React Developer', company: 'TechNova', location: 'Remote', salary: '$120k - $150k', type: 'Full-time', time: '2 days ago' },
    { id: 2, title: 'UX Designer', company: 'CreativeSolutions', location: 'New York, NY', salary: '$90k - $110k', type: 'Contract', time: '5 hours ago' },
    { id: 3, title: 'Backend Engineer (Spring Boot)', company: 'FinTech Group', location: 'San Francisco, CA', salary: '$130k - $160k', type: 'Full-time', time: '1 week ago' },
    { id: 4, title: 'Product Manager', company: 'Innovate LLC', location: 'Austin, TX', salary: '$110k - $140k', type: 'Full-time', time: '3 days ago' },
  ];

  return (
    <div className="page-container" style={{ display: 'flex', gap: '2rem' }}>
      {/* Sidebar Filters */}
      <div style={{ flex: '0 0 250px' }}>
        <div className="glass-card" style={{ position: 'sticky', top: '100px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1.5rem', paddingBottom: '1rem', borderBottom: '1px solid var(--border)' }}>
            <Filter size={20} color="var(--primary)" />
            <h3 style={{ fontSize: '1.2rem' }}>Filters</h3>
          </div>
          
          <div style={{ marginBottom: '1.5rem' }}>
            <h4 style={{ marginBottom: '0.75rem', color: 'var(--text-secondary)' }}>Job Type</h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <label style={{ display: 'flex', gap: '0.5rem', cursor: 'pointer' }}>
                <input type="checkbox" /> Full-time
              </label>
              <label style={{ display: 'flex', gap: '0.5rem', cursor: 'pointer' }}>
                <input type="checkbox" /> Part-time
              </label>
              <label style={{ display: 'flex', gap: '0.5rem', cursor: 'pointer' }}>
                <input type="checkbox" /> Contract
              </label>
            </div>
          </div>

          <div>
            <h4 style={{ marginBottom: '0.75rem', color: 'var(--text-secondary)' }}>Work Mode</h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <label style={{ display: 'flex', gap: '0.5rem', cursor: 'pointer' }}>
                <input type="checkbox" /> Remote
              </label>
              <label style={{ display: 'flex', gap: '0.5rem', cursor: 'pointer' }}>
                <input type="checkbox" /> Hybrid
              </label>
              <label style={{ display: 'flex', gap: '0.5rem', cursor: 'pointer' }}>
                <input type="checkbox" /> On-site
              </label>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div style={{ flex: '1' }}>
        <div className="glass-card" style={{ display: 'flex', gap: '1rem', marginBottom: '2rem', padding: '1rem', borderRadius: '12px' }}>
          <div style={{ flex: 1, display: 'flex', alignItems: 'center', background: 'rgba(15, 23, 42, 0.6)', borderRadius: '8px', padding: '0 1rem' }}>
            <Search color="var(--text-secondary)" size={18} />
            <input type="text" placeholder="Search jobs..." style={{ border: 'none', background: 'transparent', color: 'white', padding: '0.75rem', width: '100%', outline: 'none' }} />
          </div>
          <button className="btn-primary" style={{ padding: '0.75rem 1.5rem', borderRadius: '8px' }}>Search</button>
        </div>

        <h2 style={{ fontSize: '1.5rem', marginBottom: '1.5rem' }}>4 Jobs Found</h2>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          {jobs.map(job => (
            <div key={job.id} className="glass-card" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
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
                <button className="btn-primary" style={{ background: 'transparent', border: '1px solid var(--primary)', color: 'var(--primary)', padding: '0.5rem 1rem' }}>Apply</button>
              </div>

              <div style={{ display: 'flex', gap: '1.5rem', flexWrap: 'wrap', borderTop: '1px solid var(--border)', paddingTop: '1rem', marginTop: '0.5rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
                  <MapPin size={16} /> {job.location}
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
                  <DollarSign size={16} /> {job.salary}
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-secondary)', fontSize: '0.875rem' }}>
                  <Clock size={16} /> {job.type} • {job.time}
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Jobs;
