import { createContext, useState, useEffect } from 'react';
import api from '../api/axios';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Fetch user profile from backend
  useEffect(() => {
    const fetchUser = async () => {
      const token = localStorage.getItem('token');
      if (token) {
        try {
          const response = await api.get('/users/profile');
          setUser(response.data);
        } catch (err) {
          console.error("Failed to fetch profile", err);
          localStorage.removeItem('token');
        }
      }
      setLoading(false);
    };
    fetchUser();
  }, []);

  const login = async (email, password) => {
    try {
      const response = await api.post('/auth/login', { email, password });
      const { token } = response.data;
      localStorage.setItem('token', token);
      
      // Fetch user profile immediately after login
      const profileRes = await api.get('/users/profile');
      setUser(profileRes.data);
      
      return { success: true };
    } catch (error) {
      const errData = error.response?.data;
      const errorMsg = errData?.errorMessage || errData?.message || 'Login failed';
      return { success: false, error: errorMsg };
    }
  };

  const register = async (name, email, password, accountType = 'APPLICANT', companyName = '') => {
    try {
      const payload = { name, email, password, accountType };
      if (accountType === 'EMPLOYER' && companyName) {
        payload.companyName = companyName;
      }
      const response = await api.post('/auth/register', payload);
      const { token } = response.data;
      if (token) {
        localStorage.setItem('token', token);
        
        // Fetch user profile immediately after register
        const profileRes = await api.get('/users/profile');
        setUser(profileRes.data);
      }
      return { success: true };
    } catch (error) {
      const errData = error.response?.data;
      const errorMsg = errData?.errorMessage || errData?.message || 'Registration failed';
      return { success: false, error: errorMsg };
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout, loading }}>
      {!loading && children}
    </AuthContext.Provider>
  );
};
