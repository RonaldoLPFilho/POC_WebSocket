import React, { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';

function App() {
  const [jobs, setJobs] = useState([]);
  const [status, setStatus] = useState('Desconectado');
  const [error, setError] = useState(null);

  useEffect(() => {
    console.log('Iniciando conexão WebSocket...');
    
    const client = new Client({
      brokerURL: 'ws://localhost:8080/ws',  
      debug: function (str) {
        console.log('STOMP: ' + str);
      },
      onConnect: () => {
        console.log('WebSocket conectado com sucesso!');
        setStatus('Conectado');
        setError(null);
        

        client.subscribe('/topic/jobs', (message) => {
          console.log('Mensagem recebida:', message.body);
          const receivedJobs = JSON.parse(message.body);
          setJobs(receivedJobs);
        });


        console.log('Solicitando estado inicial...');
        client.publish({
          destination: '/app/get-jobs',
          body: JSON.stringify({ request: 'initialState' })  
        });
      },
      onDisconnect: () => {
        console.log('WebSocket desconectado');
        setStatus('Desconectado');
      },
      onStompError: (frame) => {
        console.error('Erro STOMP:', frame);
        setStatus('Erro na conexão');
        setError(frame?.headers?.message || frame?.body || 'Erro desconhecido');
      },
      onWebSocketError: (event) => {
        console.error('Erro WebSocket:', event);
        setStatus('Erro WebSocket');
        setError('Não foi possível conectar ao servidor');
      },
      reconnectDelay: 5000, 
      heartbeatIncoming: 4000, 
      heartbeatOutgoing: 4000, 
      onWebSocketClose: (event) => {
        console.warn('WebSocket fechado. Tentando reconectar...');
        setStatus('Tentando reconectar...');
        client.activate();
      },
    });

    try {
      client.activate(); 
    } catch (err) {
      console.error('Erro ao ativar cliente:', err);
      setError(err.message);
    }

    return () => {
      console.log('Desativando conexão...');
      client.deactivate();
    };
  }, []);

  return (
    <div>
      <h1>Monitoramento de Jobs</h1>
      <p>Status: {status}</p>
      {error && <p style={{color: 'red'}}>Erro: {error}</p>}
      
      <div>
        <h2>Jobs:</h2>
        {jobs.length === 0 ? (
          <p>Nenhum job encontrado</p>
        ) : (
          jobs.map(job => (
            <div key={job.id} style={{margin: '10px 0', border: '1px solid #ccc', padding: '10px'}}>
              <div>ID: {job.id}</div>
              <div>Título: {job.title}</div>
              <div>Status: {job.status}</div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default App;
