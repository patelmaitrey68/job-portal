import { useState, useEffect, useContext } from 'react';
import { FileText, Users, CheckCircle, XCircle, Loader2, Star, Clock, AlertCircle } from 'lucide-react';
import { AuthContext } from '../context/AuthContext';
import { Link } from 'react-router-dom';
import api from '../api/axios';
import toast from 'react-hot-toast';

const EmployerDashboard = () => {
  const { user } = useContext(AuthContext);
  const [jobs, setJobs] = useState([]);
  const [loadingJobs, setLoadingJobs] = useState(true);
  
  const [selectedJob, setSelectedJob] = useState(null);
  const [applications, setApplications] = useState([]);
  const [loadingApps, setLoadingApps] = useState(false);

  // Rejection modal state
  const [showRejectModal, setShowRejectModal] = useState(false);
  const [rejectingAppId, setRejectingAppId] = useState(null);
  const [rejectionReason, setRejectionReason] = useState('');

  // Create Job modal state
  const [showCreateJobModal, setShowCreateJobModal] = useState(false);
  const [newJob, setNewJob] = useState({
    title: '', description: '', location: '', jobType: 'full-time', 
    experienceLevel: 'entry', salaryMin: '', salaryMax: '', 
    category: 'Technology', industry: 'Software Development', 
    skills: '', requirements: '', responsibilities: ''
  });

  useEffect(() => {
    fetchJobs();
  }, []);

  const fetchJobs = async () => {
    try {
      const res = await api.get('/jobs/my-jobs');
      setJobs(res.data.content || []);
    } catch (err) {
      console.error('Failed to fetch employer jobs', err);
    } finally {
      setLoadingJobs(false);
    }
  };

  const fetchApplications = async (jobId) => {
    setLoadingApps(true);
    try {
      const res = await api.get(`/applications/job/${jobId}`);
      setApplications(res.data.content || []);
    } catch (err) {
      console.error('Failed to fetch apps', err);
      toast.error('Failed to load applications');
    } finally {
      setLoadingApps(false);
    }
  };

  const handleSelectJob = (job) => {
    setSelectedJob(job);
    fetchApplications(job.id);
  };

  const updateAppStatus = async (appId, status, reason = '') => {
    try {
      await api.put(`/applications/${appId}/status`, null, {
        params: { status, rejectionReason: reason }
      });
      toast.success(`Applicant ${status}`);
      // refresh
      fetchApplications(selectedJob.id);
    } catch (err) {
      toast.error('Failed to update status');
    }
  };

  const openRejectModal = (appId) => {
    setRejectingAppId(appId);
    setRejectionReason('');
    setShowRejectModal(true);
  };

  const submitReject = () => {
    if (!rejectionReason.trim()) {
      toast.error('You must provide a reason for rejection.');
      return;
    }
    updateAppStatus(rejectingAppId, 'rejected', rejectionReason);
    setShowRejectModal(false);
  };

  const handleCreateJob = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        ...newJob,
        salaryMin: parseFloat(newJob.salaryMin),
        salaryMax: parseFloat(newJob.salaryMax),
        salaryCurrency: 'USD',
        skills: newJob.skills.split(',').map(s => s.trim()),
        requirements: newJob.requirements.split('\n').filter(r => r.trim() !== ''),
        responsibilities: newJob.responsibilities.split('\n').filter(r => r.trim() !== ''),
        status: 'active'
      };
      await api.post('/jobs', payload);
      toast.success('Job created successfully!');
      setShowCreateJobModal(false);
      fetchJobs();
    } catch (err) {
      console.error(err);
      toast.error('Failed to create job');
    }
  };

  const [enhancingJob, setEnhancingJob] = useState(false);
  const handleEnhanceJob = async () => {
    if (!newJob.description) {
      toast.error("Please enter a basic description first.");
      return;
    }
    setEnhancingJob(true);
    try {
      const res = await api.post('/ai/enhance-job', { description: newJob.description });
      const enhanced = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
      setNewJob({
        ...newJob,
        title: enhanced.title || newJob.title,
        description: enhanced.description || newJob.description,
        requirements: enhanced.requirements ? enhanced.requirements.join('\n') : newJob.requirements,
        responsibilities: enhanced.responsibilities ? enhanced.responsibilities.join('\n') : newJob.responsibilities
      });
      toast.success("Job enhanced with AI!");
    } catch (err) {
      console.error(err);
      toast.error("Failed to enhance job");
    } finally {
      setEnhancingJob(false);
    }
  };

  const [interviewQuestions, setInterviewQuestions] = useState({});
  const [generatingQuestions, setGeneratingQuestions] = useState(null);

  const generateQuestions = async (app) => {
    setGeneratingQuestions(app.id);
    try {
      const res = await api.post('/ai/generate-interview-questions', {
        jobDescription: selectedJob.description,
        applicantSkills: app.skillsMatch?.join(', ') || 'None provided'
      });
      const questions = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
      setInterviewQuestions({ ...interviewQuestions, [app.id]: questions });
      toast.success("Interview questions generated!");
    } catch (err) {
      console.error(err);
      toast.error("Failed to generate questions");
    } finally {
      setGeneratingQuestions(null);
    }
  };

  return (
    <div className="page-container">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>Employer Dashboard</h1>
          <p style={{ color: 'var(--text-secondary)' }}>{user.companyName || 'Your Company'} • Manage your job postings and applicants.</p>
        </div>
        <button onClick={() => setShowCreateJobModal(true)} className="btn-primary" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          + Create New Job
        </button>
      </div>

      <div style={{ display: 'flex', gap: '2rem', alignItems: 'flex-start' }}>
        {/* Left Column: Jobs Posted */}
        <div className="glass-card" style={{ flex: '1' }}>
          <h2 style={{ fontSize: '1.2rem', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <FileText size={20} color="var(--primary)" /> Your Postings
          </h2>
          
          {loadingJobs ? <Loader2 className="spin" /> : jobs.length === 0 ? (
            <p style={{ color: 'var(--text-secondary)' }}>You haven't posted any jobs yet.</p>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              {jobs.map(job => (
                <div 
                  key={job.id} 
                  onClick={() => handleSelectJob(job)}
                  style={{ 
                    padding: '1rem', 
                    borderRadius: '8px', 
                    cursor: 'pointer',
                    background: selectedJob?.id === job.id ? 'rgba(139, 92, 246, 0.15)' : 'rgba(255,255,255,0.03)',
                    border: selectedJob?.id === job.id ? '1px solid var(--primary)' : '1px solid transparent'
                  }}
                >
                  <h3 style={{ fontSize: '1rem' }}>{job.title}</h3>
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', color: 'var(--text-secondary)', marginTop: '0.5rem' }}>
                    <span>{job.applicationCount} Applicants</span>
                    <span>{job.status}</span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Right Column: Applicants for selected job */}
        <div className="glass-card" style={{ flex: '2', minHeight: '500px' }}>
          {selectedJob ? (
            <>
              <h2 style={{ fontSize: '1.2rem', marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Users size={20} color="var(--primary)" /> Applicants for {selectedJob.title}
              </h2>
              
              {loadingApps ? <Loader2 className="spin" /> : applications.length === 0 ? (
                <p style={{ color: 'var(--text-secondary)' }}>No applicants yet for this position.</p>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                  {applications.sort((a, b) => (b.matchScore || 0) - (a.matchScore || 0)).map(app => (
                    <div key={app.id} style={{ padding: '1.25rem', background: 'rgba(255,255,255,0.03)', borderRadius: '12px', border: '1px solid var(--border)' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                        <div style={{ flex: 1, paddingRight: '1rem' }}>
                          <h3 style={{ fontSize: '1.1rem', fontWeight: '600' }}>{app.applicantName}</h3>
                          <div style={{ marginTop: '0.5rem', color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                            <p><strong>Status:</strong> <span style={{ textTransform: 'capitalize', color: app.status === 'hired' ? '#10b981' : app.status === 'rejected' ? '#ef4444' : 'inherit' }}>{app.status}</span></p>
                            {app.coverLetter && <p style={{ marginTop: '0.5rem', fontStyle: 'italic' }}>"{app.coverLetter}"</p>}
                            {app.skillsMatch?.length > 0 && (
                              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem', marginTop: '0.5rem' }}>
                                {app.skillsMatch.map(s => (
                                  <span key={s} style={{ background: 'rgba(16, 185, 129, 0.1)', color: '#10b981', padding: '0.2rem 0.5rem', borderRadius: '4px', fontSize: '0.75rem' }}>
                                    {s}
                                  </span>
                                ))}
                              </div>
                            )}
                          </div>
                        </div>
                        <div style={{ textAlign: 'right', minWidth: '120px' }}>
                          <div style={{ background: 'rgba(139, 92, 246, 0.1)', padding: '0.5rem 1rem', borderRadius: '8px', display: 'inline-flex', flexDirection: 'column', alignItems: 'center', width: '100%' }}>
                            <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>AI Match</span>
                            <span style={{ fontSize: '1.2rem', fontWeight: 'bold', color: 'var(--primary)' }}>{Math.round(app.matchScore || 0)}%</span>
                          </div>
                        </div>
                      </div>

                      {/* AI Feedback Section */}
                      {app.aiFeedback && (
                        <div style={{ marginTop: '1rem', background: 'rgba(139, 92, 246, 0.05)', borderLeft: '3px solid var(--primary)', padding: '0.75rem', borderRadius: '4px' }}>
                          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                            <h4 style={{ fontSize: '0.85rem', color: 'var(--primary)', margin: 0, display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                              <Star size={12} /> AI Analysis
                            </h4>
                            <button 
                              onClick={() => generateQuestions(app)}
                              disabled={generatingQuestions === app.id}
                              style={{ background: 'none', color: 'var(--primary)', border: '1px solid var(--primary)', borderRadius: '4px', padding: '0.2rem 0.5rem', fontSize: '0.75rem', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '0.25rem' }}
                            >
                              {generatingQuestions === app.id ? <Loader2 size={12} className="spin" /> : null}
                              Generate Interview Questions
                            </button>
                          </div>
                          <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', margin: 0 }}>{app.aiFeedback}</p>
                          
                          {/* Interview Questions Output */}
                          {interviewQuestions[app.id] && (
                            <div style={{ marginTop: '1rem', padding: '0.75rem', background: 'rgba(0,0,0,0.2)', borderRadius: '6px' }}>
                              <h5 style={{ fontSize: '0.8rem', color: 'var(--text-primary)', marginBottom: '0.5rem' }}>Suggested Questions:</h5>
                              <ul style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', paddingLeft: '1.2rem', margin: 0 }}>
                                {interviewQuestions[app.id].map((q, idx) => (
                                  <li key={idx} style={{ marginBottom: '0.25rem' }}>{q}</li>
                                ))}
                              </ul>
                            </div>
                          )}
                        </div>
                      )}

                      {/* Action Buttons */}
                      {app.status === 'pending' || app.status === 'shortlisted' ? (
                        <div style={{ display: 'flex', gap: '1rem', marginTop: '1.5rem', borderTop: '1px solid rgba(255,255,255,0.05)', paddingTop: '1rem' }}>
                          <button onClick={() => updateAppStatus(app.id, 'hired')} style={{ background: '#10b981', color: 'white', padding: '0.5rem 1rem', borderRadius: '6px', border: 'none', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            <CheckCircle size={16} /> Select / Offer
                          </button>
                          <button onClick={() => openRejectModal(app.id)} style={{ background: 'rgba(239, 68, 68, 0.2)', color: '#ef4444', padding: '0.5rem 1rem', borderRadius: '6px', border: 'none', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                            <XCircle size={16} /> Reject
                          </button>
                        </div>
                      ) : app.status === 'hired' ? (
                        <div style={{ marginTop: '1rem', color: '#10b981', fontSize: '0.9rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                          <Clock size={16} /> Offer Extended (Waiting for candidate)
                        </div>
                      ) : app.status === 'accepted' ? (
                        <div style={{ marginTop: '1rem', color: '#10b981', fontSize: '0.9rem', display: 'flex', alignItems: 'center', gap: '0.5rem', fontWeight: 'bold' }}>
                          <CheckCircle size={16} /> Candidate Accepted Offer!
                        </div>
                      ) : app.status === 'rejected' && app.rejectionReason ? (
                        <div style={{ marginTop: '1rem', color: '#ef4444', fontSize: '0.9rem' }}>
                          <strong>Rejection Reason:</strong> {app.rejectionReason}
                        </div>
                      ) : null}
                    </div>
                  ))}
                </div>
              )}
            </>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100%', color: 'var(--text-secondary)' }}>
              <AlertCircle size={48} style={{ marginBottom: '1rem', opacity: 0.5 }} />
              <p>Select a job from the left to view applicants.</p>
            </div>
          )}
        </div>
      </div>

      {/* Reject Modal */}
      {showRejectModal && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.7)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 }}>
          <div className="glass-card" style={{ width: '100%', maxWidth: '500px' }}>
            <h2 style={{ marginBottom: '1rem', color: '#ef4444' }}>Reject Applicant</h2>
            <p style={{ color: 'var(--text-secondary)', marginBottom: '1rem', fontSize: '0.9rem' }}>
              Please provide a reason so the candidate knows what they should improve. This will be sent to them.
            </p>
            <textarea
              className="input-field"
              rows={4}
              placeholder="e.g., We are looking for someone with more React experience..."
              value={rejectionReason}
              onChange={(e) => setRejectionReason(e.target.value)}
              style={{ width: '100%', marginBottom: '1.5rem' }}
            />
            <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end' }}>
              <button onClick={() => setShowRejectModal(false)} className="btn-secondary">Cancel</button>
              <button onClick={submitReject} style={{ background: '#ef4444', color: 'white', padding: '0.75rem 1.5rem', borderRadius: '8px', border: 'none', cursor: 'pointer' }}>
                Confirm Rejection
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Create Job Modal */}
      {showCreateJobModal && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.7)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000, overflowY: 'auto', padding: '2rem' }}>
          <div className="glass-card" style={{ width: '100%', maxWidth: '700px', maxHeight: '90vh', overflowY: 'auto' }}>
            <h2 style={{ marginBottom: '1.5rem' }}>Create New Job Posting</h2>
            <form onSubmit={handleCreateJob} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <div style={{ display: 'flex', gap: '1rem' }}>
                <div style={{ flex: 1 }}>
                  <label className="input-label">Job Title</label>
                  <input type="text" className="input-field" required value={newJob.title} onChange={e => setNewJob({...newJob, title: e.target.value})} placeholder="e.g., Senior React Developer" />
                </div>
                <div style={{ flex: 1 }}>
                  <label className="input-label">Location</label>
                  <input type="text" className="input-field" required value={newJob.location} onChange={e => setNewJob({...newJob, location: e.target.value})} placeholder="e.g., Remote or New York, NY" />
                </div>
              </div>

              <div style={{ display: 'flex', gap: '1rem' }}>
                <div style={{ flex: 1 }}>
                  <label className="input-label">Job Type</label>
                  <select className="input-field" value={newJob.jobType} onChange={e => setNewJob({...newJob, jobType: e.target.value})}>
                    <option value="full-time">Full-Time</option>
                    <option value="part-time">Part-Time</option>
                    <option value="contract">Contract</option>
                  </select>
                </div>
                <div style={{ flex: 1 }}>
                  <label className="input-label">Experience Level</label>
                  <select className="input-field" value={newJob.experienceLevel} onChange={e => setNewJob({...newJob, experienceLevel: e.target.value})}>
                    <option value="entry">Entry Level</option>
                    <option value="mid">Mid Level</option>
                    <option value="senior">Senior Level</option>
                  </select>
                </div>
              </div>

              <div style={{ display: 'flex', gap: '1rem' }}>
                <div style={{ flex: 1 }}>
                  <label className="input-label">Min Salary (USD)</label>
                  <input type="number" className="input-field" required value={newJob.salaryMin} onChange={e => setNewJob({...newJob, salaryMin: e.target.value})} placeholder="e.g., 80000" />
                </div>
                <div style={{ flex: 1 }}>
                  <label className="input-label">Max Salary (USD)</label>
                  <input type="number" className="input-field" required value={newJob.salaryMax} onChange={e => setNewJob({...newJob, salaryMax: e.target.value})} placeholder="e.g., 120000" />
                </div>
              </div>

              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                  <label className="input-label" style={{ marginBottom: 0 }}>Job Description</label>
                  <button 
                    type="button" 
                    onClick={handleEnhanceJob} 
                    disabled={enhancingJob}
                    style={{ background: 'rgba(139, 92, 246, 0.1)', color: 'var(--primary)', border: '1px solid var(--primary)', borderRadius: '6px', padding: '0.3rem 0.6rem', fontSize: '0.8rem', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '0.25rem' }}
                  >
                    {enhancingJob ? <Loader2 size={14} className="spin" /> : <Star size={14} />} 
                    {enhancingJob ? 'Enhancing...' : 'Enhance with AI'}
                  </button>
                </div>
                <textarea className="input-field" rows={3} required value={newJob.description} onChange={e => setNewJob({...newJob, description: e.target.value})} placeholder="Describe the role..." />
              </div>

              <div>
                <label className="input-label">Required Skills (comma separated)</label>
                <input type="text" className="input-field" required value={newJob.skills} onChange={e => setNewJob({...newJob, skills: e.target.value})} placeholder="e.g., React, TypeScript, Node.js" />
              </div>

              <div>
                <label className="input-label">Requirements (one per line)</label>
                <textarea className="input-field" rows={3} required value={newJob.requirements} onChange={e => setNewJob({...newJob, requirements: e.target.value})} placeholder="3+ years experience..." />
              </div>

              <div>
                <label className="input-label">Responsibilities (one per line)</label>
                <textarea className="input-field" rows={3} required value={newJob.responsibilities} onChange={e => setNewJob({...newJob, responsibilities: e.target.value})} placeholder="Build frontend features..." />
              </div>

              <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end', marginTop: '1rem' }}>
                <button type="button" onClick={() => setShowCreateJobModal(false)} className="btn-secondary">Cancel</button>
                <button type="submit" className="btn-primary">Create Job</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default EmployerDashboard;
