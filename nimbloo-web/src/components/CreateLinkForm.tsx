import { useState } from 'react';
import type { FormEvent } from 'react';
import axios from 'axios';

export function CreateLinkForm() {
  // 1. Estados dos campos do formulário
  const [url, setUrl] = useState('');
  const [alias, setAlias] = useState('');
  const [expiresAt, setExpiresAt] = useState('');

  // 2. Estados de controle exigidos no teste (Loading e Erro)
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [createdLink, setCreatedLink] = useState<string | null>(null);

  // Função disparada ao clicar no botão de submit
  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault(); // Evita que a página recarregue
    setLoading(true);
    setError(null);
    setCreatedLink(null);

    try {
      // Montamos o corpo (body) no formato que nossa API Java espera
      const body = {
        url: url,
        alias: alias.trim() !== '' ? alias : null,
        // Converte a data do HTML para o formato ISO que o Java (Instant) entende
        expiresAt: expiresAt ? new Date(expiresAt).toISOString() : null,
      };

      // Chamada POST usando o axios
      const response = await axios.post('http://localhost:8080/api/v1/links', body);
      
      // Monta o link curto final para exibição
      const shortCode = response.data.code;
      setCreatedLink(`${window.location.origin}/${shortCode}`);
      
      // Limpa os campos após sucesso
      setUrl('');
      setAlias('');
      setExpiresAt('');

    } catch (err: any) {
      // Tratamento de erro explícito
      if (err.response && err.response.data && err.response.data.detail) {
        setError(err.response.data.detail); // Pega a mensagem de erro bonita do nosso Java ProblemDetail
      } else {
        setError('Ocorreu um erro inesperado ao conectar com o servidor.');
      }
    } finally {
      setLoading(false); // Desativa o loading, dando sucesso ou erro
    }
  };

  // Função para copiar para a área de transferência
  const handleCopy = () => {
    if (createdLink) {
      navigator.clipboard.writeText(createdLink);
      alert('Link copiado!');
    }
  };

  return (
    <div style={{ padding: '20px', border: '1px solid #ccc', borderRadius: '8px', maxWidth: '400px', margin: '20px auto' }}>
      <h2>Encurtador Nimbloo</h2>
      
      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
        <div>
          <label>URL Original (obrigatório):</label><br/>
          <input 
            type="url" 
            value={url} 
            onChange={(e) => setUrl(e.target.value)} 
            required 
            placeholder="https://exemplo.com"
            style={{ width: '100%', padding: '8px' }}
          />
        </div>

        <div>
          <label>Alias (opcional):</label><br/>
          <input 
            type="text" 
            value={alias} 
            onChange={(e) => setAlias(e.target.value)} 
            placeholder="Ex: promo-black-friday"
            style={{ width: '100%', padding: '8px' }}
          />
        </div>

        <div>
          <label>Data de Expiração (opcional):</label><br/>
          <input 
            type="datetime-local" 
            value={expiresAt} 
            onChange={(e) => setExpiresAt(e.target.value)}
            style={{ width: '100%', padding: '8px' }}
          />
        </div>

        <button type="submit" disabled={loading} style={{ padding: '10px', cursor: 'pointer' }}>
          {loading ? 'Criando...' : 'Criar Link Curto'}
        </button>
      </form>

      {/* Exibição condicional do Erro */}
      {error && (
        <div style={{ color: 'red', marginTop: '15px', padding: '10px', backgroundColor: '#ffe6e6', borderRadius: '4px' }}>
          <strong>Erro:</strong> {error}
        </div>
      )}

      {/* Exibição condicional do Sucesso com botão de copiar */}
      {createdLink && (
        <div style={{ marginTop: '15px', padding: '10px', backgroundColor: '#e6ffe6', borderRadius: '4px' }}>
          <p>Link criado com sucesso!</p>
          <a href={createdLink} target="_blank" rel="noopener noreferrer">{createdLink}</a>
          <button onClick={handleCopy} style={{ marginLeft: '10px', cursor: 'pointer' }}>
            Copiar Link
          </button>
        </div>
      )}
    </div>
  );
}