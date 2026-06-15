import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { Briefcase, MapPin, DollarSign, Clock, ArrowLeft, Send, CheckCircle, Star, Loader2, Building, Users, Calendar, Award } from 'lucide-react';
import { useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import api from '../api/axios';

const JobDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useContext(AuthContext);

  const [job, setJob] = useState(null);
  const [loading, setLoading] = useState(true);
  const [applying, setApplying] = useState(false);
  const [applied, setApplied] = useState(false);
  const [matchResult, setMatchResult] = useState(null);
  const [showApplyForm, setShowApplyForm] = useState(false);
  const [error, setError] = useState('');

  // Apply form state
  const [coverLetter, setCoverLetter] = useState('');
  const [skills, setSkills] = useState('');

  useEffect(() => {
    const fetchJob = async () => {
      try {
        const response = await api.get(`/jobs/${id}`);
        setJob(response.data);
      } catch (err) {
        console.error('Failed to fetch job:', err);
        setError('Job not found');
      } finally {
        setLoading(false);
      }
    };
    fetchJob();
  }, [id]);

  const handleApply = async (e) => {
    e.preventDefault();
    setApplying(true);
    setError('');

    try {
      const skillsList = skills.split(',').map(s => s.trim()).filter(s => s.length > 0);
      
      const response = await api.post(`/applications/job/${id}`, {
        coverLetter,
        skillsMatch: skillsList,
        resumeFileName: 'resume.pdf',
      });

      setApplied(true);
      setMatchResult({
        score: response.data.matchScore,
        matchedSkills: response.data.skillsMatch || [],
      });
    } catch (err) {
      const errMsg = err.response?.data?.errorMessage || err.response?.data?.message || 'Failed to apply. You may have already applied to this job.';
      setError(errMsg);
    } finally {
      setApplying(false);
    }
  };

  const [drafting, setDrafting] = useState(false);
  const handleAutoDraft = async () => {
    if (!user) {
      toast.error('Please login first');
      return;
    }
    setDrafting(true);
    try {
      const res = await api.post('/ai/draft-cover-letter', {
        jobDescription: job.description,
        skills: skills || (user.skills ? user.skills.join(', ') : 'Software Development')
      });
      setCoverLetter(res.data.coverLetter);
      toast.success('Cover letter drafted!');
    } catch (err) {
      console.error(err);
      toast.error('Failed to draft cover letter');
    } finally {
      setDrafting(false);
    }
  };

  const formatSalary = (min, max) => {
    if (!min && !max) return 'Not disclosed';
    const fmt = (v) => `$${(v / 1000).toFixed(0)}k`;
    if (min && max) return `${fmt(min)} - ${fmt(max)} / year`;
    if (min) return `From ${fmt(min)} / year`;
    return `Up to ${fmt(max)} / year`;
  };

  if (loading) {
    return (
      <div className="page-container" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}>
        <Loader2 size={48} color="var(--primary)" style={{ animation: 'spin 1s linear infinite' }} />
      </div>
    );
  }

  if (error && !job) {
    return (
      <div className="page-container" style={{ textAlign: 'center', paddingTop: '4rem' }}>
        <h2>{error}</h2>
        <Link to="/jobs" className="btn-primary" style={{ marginTop: '1rem', display: 'inline-flex' }}>
          <ArrowLeft size={18} style={{ marginRight: '0.5rem' }} /> Back to Jobs
        </Link>
      </div>
    );
  }

  // Success screen after applying
  if (applied && matchResult) {
    const scoreColor = matchResult.score >= 70 ? '#10b981' : matchResult.score >= 40 ? '#f59e0b' : '#ef4444';
    return (
      <div className="page-container" style={{ maxWidth: '600px', margin: '0 auto' }}>
        <div className="glass-card animate-fade-in" style={{ textAlign: 'center', padding: '3rem' }}>
          <div style={{ background: 'rgba(16, 185, 129, 0.2)', width: '80px', height: '80px', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 1.5rem' }}>
            <CheckCircle size={40} color="#10b981" />
          </div>
          <h2 style={{ fontSize: '1.75rem', marginBottom: '0.5rem' }}>Application Submitted!</h2>
          <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem' }}>
            You've applied to <strong>{job.title}</strong> at <strong>{job.company}</strong>
          </p>

          {/* AI Match Score - Visible to the applicant */}
          <div className="glass-card" style={{ background: 'rgba(139, 92, 246, 0.1)', border: '1px solid rgba(139, 92, 246, 0.3)', marginBottom: '1.5rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
              <Star size={20} color="var(--primary)" />
              <h3>AI Match Score</h3>
            </div>
            <div style={{ fontSize: '3rem', fontWeight: '700', color: scoreColor, marginBottom: '0.5rem' }}>
              {Math.round(matchResult.score)}%
            </div>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginBottom: '1rem' }}>
              {matchResult.score >= 70 ? 'Excellent match! Your skills align well with this role.' :
               matchResult.score >= 40 ? 'Good match. Consider building more relevant skills.' :
               'Your skills partially match. Consider upskilling in required areas.'}
            </p>
            {matchResult.matchedSkills.length > 0 && (
              <div>
                <p style={{ color: 'var(--text-secondary)', fontSize: '0.8rem', marginBottom: '0.5rem' }}>Matched Skills:</p>
                <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', justifyContent: 'center' }}>
                  {matchResult.matchedSkills.map((skill, i) => (
                    <span key={i} style={{ fontSize: '0.8rem', background: 'rgba(16, 185, 129, 0.2)', color: '#10b981', padding: '0.25rem 0.6rem', borderRadius: '6px' }}>
                      {skill}
                    </span>
                  ))}
                </div>
              </div>
            )}
          </div>

          <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center' }}>
            <Link to="/dashboard" className="btn-primary" style={{ padding: '0.75rem 1.5rem' }}>
              Go to Dashboard
            </Link>
            <Link to="/jobs" style={{ padding: '0.75rem 1.5rem', border: '1px solid var(--border)', borderRadius: '10px', color: 'var(--text-primary)', textDecoration: 'none', display: 'flex', alignItems: 'center' }}>
              Browse More Jobs
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="page-container" style={{ maxWidth: '900px', margin: '0 auto' }}>
      {/* Back button */}
      <button onClick={() => navigate('/jobs')} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-secondary)', background: 'none', border: 'none', cursor: 'pointer', marginBottom: '1.5rem', fontSize: '0.9rem' }}>
        <ArrowLeft size={18} /> Back to Jobs
      </button>

      {/* Job Header */}
      <div className="glass-card" style={{ marginBottom: '1.5rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem' }}>
          <div style={{ display: 'flex', gap: '1.25rem', alignItems: 'center' }}>
            <div style={{ background: 'rgba(139, 92, 246, 0.2)', padding: '1.25rem', borderRadius: '16px' }}>
              <Building color="var(--primary)" size={32} />
            </div>
            <div>
              <h1 style={{ fontSize: '1.75rem', fontWeight: '700', marginBottom: '0.25rem' }}>{job.title}</h1>
              <p style={{ color: 'var(--text-secondary)', fontSize: '1.1rem' }}>{job.company}</p>
            </div>
          </div>
          {!showApplyForm && (
            <button onClick={() => setShowApplyForm(true)} className="btn-primary" style={{ padding: '0.75rem 2rem', fontSize: '1rem' }}>
              <Send size={18} style={{ marginRight: '0.5rem' }} /> Apply Now
            </button>
          )}
        </div>

        {/* Job Meta */}
        <div style={{ display: 'flex', gap: '2rem', flexWrap: 'wrap', marginTop: '1.5rem', paddingTop: '1.5rem', borderTop: '1px solid var(--border)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <MapPin size={18} color="var(--primary)" />
            <span>{job.location || 'Not specified'}</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <DollarSign size={18} color="#10b981" />
            <span>{formatSalary(job.salaryMin, job.salaryMax)}</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Clock size={18} color="#f59e0b" />
            <span style={{ textTransform: 'capitalize' }}>{job.jobType || 'Not specified'}</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Award size={18} color="#ec4899" />
            <span style={{ textTransform: 'capitalize' }}>{job.experienceLevel || 'Any'} level</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Users size={18} color="var(--text-secondary)" />
            <span>{job.applicationCount || 0} applicant{job.applicationCount !== 1 ? 's' : ''}</span>
          </div>
        </div>
      </div>

      {/* Apply Form */}
      {showApplyForm && !applied && (
        <div className="glass-card animate-fade-in" style={{ marginBottom: '1.5rem', border: '1px solid rgba(139, 92, 246, 0.3)' }}>
          <h2 style={{ fontSize: '1.25rem', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Send size={20} color="var(--primary)" /> Apply for this Position
          </h2>
          
          {error && (
            <div style={{ color: '#ef4444', background: 'rgba(239, 68, 68, 0.1)', padding: '0.75rem', borderRadius: '8px', marginBottom: '1rem' }}>
              {error}
            </div>
          )}

          <form onSubmit={handleApply} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                Your Skills (comma-separated) — used for AI Match Score
              </label>
              <input
                type="text"
                value={skills}
                onChange={(e) => setSkills(e.target.value)}
                placeholder="e.g., React, Java, Spring Boot, PostgreSQL, Docker"
                required
                className="input-field"
              />
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.75rem', marginTop: '0.25rem' }}>
                Required skills for this job: {job.skills ? job.skills.join(', ') : 'None listed'}
              </p>
            </div>

            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                Resume (PDF/Doc)
              </label>
              <input
                type="file"
                accept=".pdf,.doc,.docx"
                required
                className="input-field"
                style={{ padding: '0.5rem' }}
              />
            </div>
            
            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                <label style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', margin: 0 }}>
                  Cover Letter (optional)
                </label>
                <button 
                  type="button" 
                  onClick={handleAutoDraft} 
                  disabled={drafting}
                  style={{ background: 'rgba(139, 92, 246, 0.1)', color: 'var(--primary)', border: '1px solid var(--primary)', borderRadius: '6px', padding: '0.3rem 0.6rem', fontSize: '0.8rem', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '0.25rem' }}
                >
                  {drafting ? <Loader2 size={14} className="spin" /> : <Star size={14} />} 
                  {drafting ? 'Drafting...' : 'Auto-Draft with AI'}
                </button>
              </div>
              <textarea
                value={coverLetter}
                onChange={(e) => setCoverLetter(e.target.value)}
                placeholder="Tell the recruiter why you're a great fit for this role..."
                rows={5}
                className="input-field"
                style={{ resize: 'vertical' }}
              />
            </div>

            <div style={{ display: 'flex', gap: '1rem' }}>
              <button type="submit" className="btn-primary" style={{ padding: '0.75rem 2rem', flex: 1 }} disabled={applying}>
                {applying ? (
                  <><Loader2 size={18} style={{ marginRight: '0.5rem', animation: 'spin 1s linear infinite' }} /> Submitting...</>
                ) : (
                  <><Send size={18} style={{ marginRight: '0.5rem' }} /> Submit Application</>
                )}
              </button>
              <button type="button" onClick={() => setShowApplyForm(false)} style={{ padding: '0.75rem 1.5rem', border: '1px solid var(--border)', borderRadius: '10px', background: 'transparent', color: 'var(--text-primary)', cursor: 'pointer' }}>
                Cancel
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Job Description */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
        <div className="glass-card">
          <h2 style={{ fontSize: '1.25rem', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Briefcase size={20} color="var(--primary)" /> About this Role
          </h2>
          <p style={{ color: 'var(--text-secondary)', lineHeight: '1.7' }}>{job.description}</p>
        </div>

        {/* Skills */}
        {job.skills && job.skills.length > 0 && (
          <div className="glass-card">
            <h2 style={{ fontSize: '1.25rem', marginBottom: '1rem' }}>Required Skills</h2>
            <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
              {job.skills.map((skill, i) => (
                <span key={i} style={{ fontSize: '0.875rem', background: 'rgba(139, 92, 246, 0.15)', color: 'var(--primary)', padding: '0.4rem 0.8rem', borderRadius: '8px', border: '1px solid rgba(139, 92, 246, 0.3)' }}>
                  {skill}
                </span>
              ))}
            </div>
          </div>
        )}

        {/* Requirements */}
        {job.requirements && job.requirements.length > 0 && (
          <div className="glass-card">
            <h2 style={{ fontSize: '1.25rem', marginBottom: '1rem' }}>Requirements</h2>
            <ul style={{ color: 'var(--text-secondary)', paddingLeft: '1.25rem', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              {job.requirements.map((req, i) => (
                <li key={i} style={{ lineHeight: '1.6' }}>{req}</li>
              ))}
            </ul>
          </div>
        )}

        {/* Responsibilities */}
        {job.responsibilities && job.responsibilities.length > 0 && (
          <div className="glass-card">
            <h2 style={{ fontSize: '1.25rem', marginBottom: '1rem' }}>Responsibilities</h2>
            <ul style={{ color: 'var(--text-secondary)', paddingLeft: '1.25rem', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              {job.responsibilities.map((resp, i) => (
                <li key={i} style={{ lineHeight: '1.6' }}>{resp}</li>
              ))}
            </ul>
          </div>
        )}
      </div>
    </div>
  );
};

export default JobDetail;
