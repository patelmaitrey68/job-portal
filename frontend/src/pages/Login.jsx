import { useState, useContext, useEffect } from 'react';
import { Mail, Lock, LogIn, Building, User as UserIcon } from 'lucide-react';
import { useNavigate, useLocation } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const Login = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [accountType, setAccountType] = useState('APPLICANT');
  const [companyName, setCompanyName] = useState('');
  const [error, setError] = useState('');
  
  const navigate = useNavigate();
  const location = useLocation();
  const { login, register, user } = useContext(AuthContext);

  // Auto-redirect if already logged in (e.g. after register sets token and user)
  useEffect(() => {
    if (user) {
      const origin = location.state?.from?.pathname || '/dashboard';
      navigate(origin, { replace: true });
    }
  }, [user, navigate, location]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    let result;
    if (isLogin) {
      result = await login(email, password);
    } else {
      result = await register(name, email, password, accountType, companyName);
    }

    if (!result.success) {
      setError(result.error);
    }
    // If success, the useEffect above will handle redirection once the user context is populated
  };

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '80vh', padding: '2rem' }}>
      <div className="glass-card animate-fade-in" style={{ width: '100%', maxWidth: '400px' }}>
        <h2 style={{ textAlign: 'center', fontSize: '2rem', marginBottom: '0.5rem' }}>
          {isLogin ? 'Welcome Back' : 'Create Account'}
        </h2>
        <p style={{ textAlign: 'center', color: 'var(--text-secondary)', marginBottom: '2rem' }}>
          {isLogin ? 'Enter your details to access your account' : 'Join Elevate to find or post jobs'}
        </p>

        {error && <div style={{ color: '#ef4444', background: 'rgba(239, 68, 68, 0.1)', padding: '0.75rem', borderRadius: '8px', marginBottom: '1.5rem', textAlign: 'center' }}>{error}</div>}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          
          {!isLogin && (
            <>
              {/* Role Toggle */}
              <div style={{ display: 'flex', gap: '0.5rem', background: 'rgba(255,255,255,0.05)', padding: '0.3rem', borderRadius: '8px' }}>
                <button
                  type="button"
                  onClick={() => setAccountType('APPLICANT')}
                  style={{
                    flex: 1, padding: '0.5rem', borderRadius: '6px', border: 'none', cursor: 'pointer',
                    background: accountType === 'APPLICANT' ? 'var(--primary)' : 'transparent',
                    color: accountType === 'APPLICANT' ? 'white' : 'var(--text-secondary)',
                    fontWeight: accountType === 'APPLICANT' ? '600' : '400',
                    transition: 'all 0.2s'
                  }}
                >
                  Job Seeker
                </button>
                <button
                  type="button"
                  onClick={() => setAccountType('EMPLOYER')}
                  style={{
                    flex: 1, padding: '0.5rem', borderRadius: '6px', border: 'none', cursor: 'pointer',
                    background: accountType === 'EMPLOYER' ? 'var(--primary)' : 'transparent',
                    color: accountType === 'EMPLOYER' ? 'white' : 'var(--text-secondary)',
                    fontWeight: accountType === 'EMPLOYER' ? '600' : '400',
                    transition: 'all 0.2s'
                  }}
                >
                  Employer
                </button>
              </div>

              <div>
                <label className="input-label">Full Name</label>
                <div style={{ position: 'relative' }}>
                  <div style={{ position: 'absolute', top: '50%', left: '1rem', transform: 'translateY(-50%)' }}>
                    <UserIcon size={18} color="var(--text-secondary)" />
                  </div>
                  <input 
                    type="text" 
                    className="input-field" 
                    placeholder="John Doe" 
                    style={{ paddingLeft: '2.5rem' }} 
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    required 
                  />
                </div>
              </div>

              {accountType === 'EMPLOYER' && (
                <div>
                  <label className="input-label">Company Name</label>
                  <div style={{ position: 'relative' }}>
                    <div style={{ position: 'absolute', top: '50%', left: '1rem', transform: 'translateY(-50%)' }}>
                      <Building size={18} color="var(--text-secondary)" />
                    </div>
                    <input 
                      type="text" 
                      className="input-field" 
                      placeholder="TechNova Solutions" 
                      style={{ paddingLeft: '2.5rem' }} 
                      value={companyName}
                      onChange={(e) => setCompanyName(e.target.value)}
                      required 
                    />
                  </div>
                </div>
              )}
            </>
          )}
          
          <div>
            <label className="input-label">Email Address</label>
            <div style={{ position: 'relative' }}>
              <div style={{ position: 'absolute', top: '50%', left: '1rem', transform: 'translateY(-50%)' }}>
                <Mail size={18} color="var(--text-secondary)" />
              </div>
              <input 
                type="email" 
                className="input-field" 
                placeholder="you@example.com" 
                style={{ paddingLeft: '2.5rem' }} 
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required 
              />
            </div>
          </div>

          <div>
            <label className="input-label">Password</label>
            <div style={{ position: 'relative' }}>
              <div style={{ position: 'absolute', top: '50%', left: '1rem', transform: 'translateY(-50%)' }}>
                <Lock size={18} color="var(--text-secondary)" />
              </div>
              <input 
                type="password" 
                className="input-field" 
                placeholder="••••••••" 
                style={{ paddingLeft: '2.5rem' }} 
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required 
              />
            </div>
          </div>

          <button type="submit" className="btn-primary" style={{ width: '100%', marginTop: '1rem' }}>
            {isLogin ? (
              <><LogIn size={18} style={{ marginRight: '0.5rem' }} /> Sign In</>
            ) : 'Sign Up'}
          </button>
        </form>

        <p style={{ textAlign: 'center', marginTop: '2rem', color: 'var(--text-secondary)' }}>
          {isLogin ? "Don't have an account? " : "Already have an account? "}
          <span 
            onClick={() => {setIsLogin(!isLogin); setError('');}} 
            style={{ color: 'var(--primary)', cursor: 'pointer', fontWeight: '500' }}
          >
            {isLogin ? 'Sign up' : 'Sign in'}
          </span>
        </p>
      </div>
    </div>
  );
};

export default Login;
