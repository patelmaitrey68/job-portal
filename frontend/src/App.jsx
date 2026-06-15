import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Jobs from './pages/Jobs';
import JobDetail from './pages/JobDetail';
import ProtectedRoute from './components/ProtectedRoute';
import { WebSocketProvider } from './context/WebSocketContext';

function App() {
  return (
    <WebSocketProvider>
      <Router>
          <Navbar />
          <Toaster position="top-right" />
          <main className="animate-fade-in">
            <Routes>
              <Route path="/" element={<ProtectedRoute><Home /></ProtectedRoute>} />
              <Route path="/login" element={<Login />} />
              <Route path="/jobs" element={<ProtectedRoute><Jobs /></ProtectedRoute>} />
              <Route path="/jobs/:id" element={<ProtectedRoute><JobDetail /></ProtectedRoute>} />
              <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
              {/* Catch all route - redirect to home */}
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </main>
        </Router>
      </WebSocketProvider>
  );
}

export default App;
