import { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, BrainCircuit, MessageSquare, Loader2, Send, Zap, AlertTriangle, Lightbulb, CheckCircle2 } from 'lucide-react';
import { AuthContext } from '../context/AuthContext';
import api from '../api/axios';
import toast from 'react-hot-toast';

const InterviewPrep = () => {
  const { id } = useParams(); // application ID
  const navigate = useNavigate();
  const { user } = useContext(AuthContext);

  const [application, setApplication] = useState(null);
  const [job, setJob] = useState(null);
  const [loading, setLoading] = useState(true);
  
  const [analysis, setAnalysis] = useState(null);
  const [analyzing, setAnalyzing] = useState(false);

  const [messages, setMessages] = useState([]);
  const [inputText, setInputText] = useState('');
  const [interviewing, setInterviewing] = useState(false);
  const [interviewComplete, setInterviewComplete] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Fetch application
        const appRes = await api.get(`/applications/${id}`);
        setApplication(appRes.data);
        
        // Fetch full job description
        if (appRes.data.jobId) {
          const jobRes = await api.get(`/jobs/${appRes.data.jobId}`);
          setJob(jobRes.data);
        }
      } catch (err) {
        console.error('Failed to fetch data:', err);
        toast.error('Could not load application data');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [id]);

  const handleAnalyzeProfile = async () => {
    if (!job || !application) return;
    setAnalyzing(true);
    try {
      const payload = {
        jobDescription: job.description,
        applicantSkills: user.skills ? user.skills.join(', ') : 'Software Development, Problem Solving'
      };
      const res = await api.post('/ai/analyze-profile', payload);
      const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
      setAnalysis(data);
      toast.success('Profile analysis complete!');
    } catch (err) {
      console.error(err);
      toast.error('Failed to analyze profile');
    } finally {
      setAnalyzing(false);
    }
  };

  const handleStartInterview = async () => {
    if (!job || !application) return;
    setInterviewing(true);
    try {
      const payload = {
        jobDescription: job.description,
        applicantSkills: user.skills ? user.skills.join(', ') : 'Software Development',
        transcript: JSON.stringify([])
      };
      const res = await api.post('/ai/mock-interview', payload);
      const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
      
      setMessages([{
        role: 'ai',
        content: data.nextQuestion,
        evaluation: null,
        suggestedAnswer: null
      }]);
    } catch (err) {
      console.error(err);
      toast.error('Failed to start interview');
    } finally {
      setInterviewing(false);
    }
  };

  const handleSendMessage = async (e) => {
    e.preventDefault();
    if (!inputText.trim()) return;

    const newMessages = [...messages, { role: 'user', content: inputText }];
    setMessages(newMessages);
    setInputText('');
    setInterviewing(true);

    try {
      const payload = {
        jobDescription: job.description,
        applicantSkills: user.skills ? user.skills.join(', ') : 'Software Development',
        transcript: JSON.stringify(newMessages.map(m => ({ role: m.role, content: m.content })))
      };
      
      const res = await api.post('/ai/mock-interview', payload);
      const data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
      
      const aiResponse = {
        role: 'ai',
        content: data.nextQuestion || "Interview Complete. Thank you!",
        evaluation: data.evaluation,
        suggestedAnswer: data.suggestedAnswer
      };
      
      setMessages(prev => [...prev, aiResponse]);
      if (data.isComplete) {
        setInterviewComplete(true);
      }
    } catch (err) {
      console.error(err);
      toast.error('Failed to get AI response');
    } finally {
      setInterviewing(false);
    }
  };

  if (loading) {
    return (
      <div className="page-container" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}>
        <Loader2 size={48} color="var(--primary)" style={{ animation: 'spin 1s linear infinite' }} />
      </div>
    );
  }

  return (
    <div className="page-container" style={{ maxWidth: '1200px', margin: '0 auto' }}>
      <button onClick={() => navigate('/dashboard')} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-secondary)', background: 'none', border: 'none', cursor: 'pointer', marginBottom: '1.5rem', fontSize: '0.9rem' }}>
        <ArrowLeft size={18} /> Back to Dashboard
      </button>

      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '2rem', marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <BrainCircuit color="var(--primary)" size={32} /> Interview Prep Center
        </h1>
        <p style={{ color: 'var(--text-secondary)' }}>
          Preparing for <strong>{job?.title || 'Job'}</strong> at <strong>{job?.company || 'Company'}</strong>
        </p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem', alignItems: 'start' }}>
        
        {/* LEFT PANEL: PROFILE ANALYSIS */}
        <div className="glass-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
            <h2 style={{ fontSize: '1.25rem', margin: 0, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Zap size={20} color="#f59e0b" /> Profile Analysis
            </h2>
            {!analysis && (
              <button onClick={handleAnalyzeProfile} disabled={analyzing} className="btn-primary" style={{ padding: '0.5rem 1rem', fontSize: '0.9rem' }}>
                {analyzing ? <Loader2 size={16} className="spin" /> : 'Analyze Profile'}
              </button>
            )}
          </div>

          {!analysis ? (
             <div style={{ padding: '3rem 1rem', textAlign: 'center', color: 'var(--text-secondary)' }}>
               <BrainCircuit size={48} style={{ opacity: 0.2, margin: '0 auto 1rem' }} />
               <p>Click "Analyze Profile" to see your strengths, weaknesses, and missing skills based on your resume and this job description.</p>
             </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem', animation: 'fadeIn 0.5s ease-out' }}>
              
              <div style={{ background: 'rgba(16, 185, 129, 0.1)', padding: '1rem', borderRadius: '8px', borderLeft: '4px solid #10b981' }}>
                <h3 style={{ color: '#10b981', fontSize: '1rem', marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}><CheckCircle2 size={16} /> Strengths</h3>
                <ul style={{ margin: 0, paddingLeft: '1.5rem', color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                  {analysis.strengths?.map((item, i) => <li key={i} style={{ marginBottom: '0.25rem' }}>{item}</li>)}
                </ul>
              </div>

              <div style={{ background: 'rgba(245, 158, 11, 0.1)', padding: '1rem', borderRadius: '8px', borderLeft: '4px solid #f59e0b' }}>
                <h3 style={{ color: '#f59e0b', fontSize: '1rem', marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}><AlertTriangle size={16} /> Weaknesses</h3>
                <ul style={{ margin: 0, paddingLeft: '1.5rem', color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                  {analysis.weaknesses?.map((item, i) => <li key={i} style={{ marginBottom: '0.25rem' }}>{item}</li>)}
                </ul>
              </div>

              <div style={{ background: 'rgba(239, 68, 68, 0.1)', padding: '1rem', borderRadius: '8px', borderLeft: '4px solid #ef4444' }}>
                <h3 style={{ color: '#ef4444', fontSize: '1rem', marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}><AlertTriangle size={16} /> Missing Keywords</h3>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                  {analysis.missingSkills?.map((skill, i) => (
                    <span key={i} style={{ background: 'rgba(239, 68, 68, 0.2)', color: '#ef4444', padding: '0.2rem 0.6rem', borderRadius: '4px', fontSize: '0.8rem' }}>{skill}</span>
                  ))}
                </div>
              </div>

              <div style={{ background: 'rgba(139, 92, 246, 0.1)', padding: '1rem', borderRadius: '8px', borderLeft: '4px solid var(--primary)' }}>
                <h3 style={{ color: 'var(--primary)', fontSize: '1rem', marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}><Lightbulb size={16} /> Improvement Suggestions</h3>
                <ul style={{ margin: 0, paddingLeft: '1.5rem', color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                  {analysis.improvementSuggestions?.map((item, i) => <li key={i} style={{ marginBottom: '0.25rem' }}>{item}</li>)}
                </ul>
              </div>

            </div>
          )}
        </div>

        {/* RIGHT PANEL: MOCK INTERVIEW CHAT */}
        <div className="glass-card" style={{ display: 'flex', flexDirection: 'column', height: '600px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', paddingBottom: '1rem', borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
            <h2 style={{ fontSize: '1.25rem', margin: 0, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <MessageSquare size={20} color="var(--primary)" /> Mock Interview
            </h2>
            {messages.length === 0 && (
              <button onClick={handleStartInterview} disabled={interviewing} style={{ background: 'var(--primary)', color: 'white', border: 'none', padding: '0.5rem 1rem', borderRadius: '6px', cursor: 'pointer', fontSize: '0.9rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                {interviewing ? <Loader2 size={16} className="spin" /> : 'Start Interview'}
              </button>
            )}
          </div>

          <div style={{ flex: 1, overflowY: 'auto', paddingRight: '0.5rem', display: 'flex', flexDirection: 'column', gap: '1rem', marginBottom: '1rem' }}>
            {messages.length === 0 ? (
               <div style={{ height: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', color: 'var(--text-secondary)', textAlign: 'center', padding: '2rem' }}>
                 <MessageSquare size={48} style={{ opacity: 0.2, marginBottom: '1rem' }} />
                 <p>Start a mock interview to practice answering technical questions tailored to this role and your resume.</p>
               </div>
            ) : (
              messages.map((msg, idx) => (
                <div key={idx} style={{ display: 'flex', flexDirection: 'column', alignItems: msg.role === 'user' ? 'flex-end' : 'flex-start' }}>
                  
                  {/* The Message Bubble */}
                  <div style={{ 
                    maxWidth: '85%', 
                    padding: '0.8rem 1rem', 
                    borderRadius: '12px',
                    borderBottomRightRadius: msg.role === 'user' ? '0' : '12px',
                    borderBottomLeftRadius: msg.role === 'ai' ? '0' : '12px',
                    background: msg.role === 'user' ? 'var(--primary)' : 'rgba(255,255,255,0.05)',
                    color: msg.role === 'user' ? 'white' : 'var(--text-primary)',
                    fontSize: '0.95rem',
                    lineHeight: '1.5'
                  }}>
                    {msg.content}
                  </div>

                  {/* The Evaluation & Suggested Answer (Only on AI messages after user answered) */}
                  {msg.role === 'ai' && (msg.evaluation || msg.suggestedAnswer) && (
                    <div style={{ marginTop: '0.5rem', maxWidth: '85%', background: 'rgba(139, 92, 246, 0.05)', padding: '0.75rem', borderRadius: '8px', borderLeft: '2px solid var(--primary)', fontSize: '0.85rem' }}>
                      {msg.evaluation && (
                        <div style={{ marginBottom: '0.5rem' }}>
                          <strong style={{ color: 'var(--primary)' }}>Evaluation:</strong> <span style={{ color: 'var(--text-secondary)' }}>{msg.evaluation}</span>
                        </div>
                      )}
                      {msg.suggestedAnswer && (
                        <div>
                          <strong style={{ color: '#10b981' }}>Suggested Answer:</strong> <span style={{ color: 'var(--text-secondary)' }}>{msg.suggestedAnswer}</span>
                        </div>
                      )}
                    </div>
                  )}
                </div>
              ))
            )}
            {interviewing && messages.length > 0 && (
              <div style={{ alignSelf: 'flex-start', color: 'var(--text-secondary)' }}>
                <Loader2 size={16} className="spin" /> <span style={{ fontSize: '0.8rem' }}>AI is typing...</span>
              </div>
            )}
          </div>

          <form onSubmit={handleSendMessage} style={{ display: 'flex', gap: '0.5rem' }}>
            <input 
              type="text" 
              value={inputText}
              onChange={(e) => setInputText(e.target.value)}
              placeholder={interviewComplete ? "Interview Complete" : messages.length === 0 ? "Start the interview first..." : "Type your answer..."}
              disabled={messages.length === 0 || interviewing || interviewComplete}
              className="input-field"
              style={{ flex: 1, marginBottom: 0 }}
            />
            <button 
              type="submit" 
              disabled={!inputText.trim() || interviewing || interviewComplete}
              style={{ background: 'var(--primary)', color: 'white', border: 'none', padding: '0 1.2rem', borderRadius: '8px', cursor: inputText.trim() && !interviewing ? 'pointer' : 'not-allowed', opacity: inputText.trim() && !interviewing ? 1 : 0.6 }}
            >
              <Send size={18} />
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default InterviewPrep;
