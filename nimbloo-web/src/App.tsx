import { useEffect, useState } from 'react';
import axios from 'axios';
import { CreateLinkForm } from './components/CreateLinkForm';
import { LinkList } from './components/LinkList';

function App() {
  const [redirecting, setRedirecting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // Pega o texto da URL logo após a barra (ex: pega "busca" em localhost:5173/busca)
    const pathCode = window.location.pathname.substring(1);
    
    // Se existir um código na URL, tentamos fazer o redirecionamento
    if (pathCode) {
      setRedirecting(true);
      
      // Busca os detalhes desse link no nosso backend
      axios.get(`http://localhost:8080/api/v1/links/${pathCode}`)
        .then(response => {
          // O backend devolveu a originalUrl. O React joga o usuário para lá!
          window.location.href = response.data.originalUrl;
        })
        .catch(() => {
          // Se o backend retornar erro 404 (não encontrado) ou 400 (expirado)
          setError('Ops! Este link não existe, está desativado ou expirou.');
          setRedirecting(false);
        });
    }
  }, []);

  // Se estiver no processo de redirecionar, mostramos uma tela simples
  if (redirecting) {
    return <h2 style={{ textAlign: 'center', marginTop: '50px', fontFamily: 'sans-serif' }}>Redirecionando...</h2>;
  }

  // Se der erro no redirecionamento, mostramos a mensagem
  if (error) {
    return (
      <div style={{ textAlign: 'center', marginTop: '50px', fontFamily: 'sans-serif' }}>
        <h2 style={{ color: 'red' }}>{error}</h2>
        <a href="/">Voltar para o Encurtador</a>
      </div>
    );
  }

  // Comportamento normal: mostra a nossa interface
  return (
    <div style={{ paddingBottom: '50px' }}>
      <h1 style={{ textAlign: 'center', fontFamily: 'sans-serif' }}>Desafio Nimbloo</h1>
      <CreateLinkForm />
      <LinkList />
    </div>
  );
}

export default App;