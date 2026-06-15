import { createContext, useContext, useEffect, useState } from 'react';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import toast from 'react-hot-toast';
import { AuthContext } from './AuthContext';

export const WebSocketContext = createContext();

export const WebSocketProvider = ({ children }) => {
  const [stompClient, setStompClient] = useState(null);
  const { user } = useContext(AuthContext);

  useEffect(() => {
    if (!user) {
      if (stompClient) {
        stompClient.disconnect();
        setStompClient(null);
      }
      return;
    }

    const token = localStorage.getItem('token');
    const socket = new SockJS('http://localhost:8080/ws');
    const client = Stomp.over(socket);
    
    // Disable debug logs in production
    client.debug = () => {};

    client.connect(
      { Authorization: `Bearer ${token}` },
      () => {
        setStompClient(client);
        
        // Subscribe to user-specific notifications
        client.subscribe(`/user/${user.id}/queue/notifications`, (message) => {
          if (message.body) {
            const notification = JSON.parse(message.body);
            
            // Show toast notification
            if (notification.type === 'STATUS_UPDATE') {
              toast(notification.message, {
                icon: '🔔',
                style: {
                  borderRadius: '10px',
                  background: 'var(--surface)',
                  color: 'var(--text-primary)',
                  border: '1px solid rgba(255,255,255,0.1)',
                },
              });
            } else if (notification.type === 'OFFER_ACCEPTED') {
              toast.success(notification.message, {
                style: {
                  borderRadius: '10px',
                  background: 'var(--surface)',
                  color: 'var(--text-primary)',
                  border: '1px solid rgba(255,255,255,0.1)',
                },
              });
            }
          }
        });
      },
      (error) => {
        console.error("STOMP connection error:", error);
      }
    );

    return () => {
      if (client) {
        client.disconnect();
      }
    };
  }, [user]);

  return (
    <WebSocketContext.Provider value={{ stompClient }}>
      {children}
    </WebSocketContext.Provider>
  );
};
