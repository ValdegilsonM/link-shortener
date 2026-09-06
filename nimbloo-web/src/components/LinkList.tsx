import { useState, useEffect } from 'react';
import axios from 'axios';

// 1. Tipagem: Definindo o formato dos dados que o backend nos envia
interface Link {
  code: string;
  originalUrl: string;
  clicks: number;
  active: boolean;
  createdAt: string;
  expiresAt: string | null;
}

export function LinkList() {
  const [links, setLinks] = useState<Link[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Função para buscar os dados na API (GET /api/v1/links)
  const fetchLinks = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get('http://localhost:8080/api/v1/links');
      setLinks(response.data);
    } catch (err) {
      setError('Ocorreu um erro ao buscar a lista de links.');
    } finally {
      setLoading(false);
    }
  };

  // O useEffect executa a busca automaticamente quando o componente aparece na tela
  useEffect(() => {
    fetchLinks();
  }, []);

  // Lógica para determinar o status dinamicamente (Ativo, Expirado ou Desativado)
  const getStatus = (link: Link) => {
    if (!link.active) return 'Desativado';
    if (link.expiresAt && new Date(link.expiresAt) < new Date()) return 'Expirado';
    return 'Ativo';
  };

  // Lógica para formatar a data do padrão ISO para o padrão brasileiro
  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('pt-BR', {
      day: '2-digit', month: '2-digit', year: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  };

  return (
    <div style={{ padding: '20px', border: '1px solid #ccc', borderRadius: '8px', maxWidth: '800px', margin: '20px auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>Links Cadastrados</h2>
        <button onClick={fetchLinks} style={{ padding: '5px 10px', cursor: 'pointer' }}>Atualizar Lista</button>
      </div>

      {/* Tratamento Explícito 1: Loading */}
      {loading && <p style={{ color: 'blue' }}>Carregando links...</p>}

      {/* Tratamento Explícito 2: Erro */}
      {error && <p style={{ color: 'red', backgroundColor: '#ffe6e6', padding: '10px' }}>{error}</p>}

      {/* Tratamento Explícito 3: Lista Vazia */}
      {!loading && !error && links.length === 0 && (
        <p style={{ fontStyle: 'italic', color: '#555' }}>Nenhum link encontrado. Crie o seu primeiro link acima!</p>
      )}

      {/* Exibição da Tabela com os dados */}
      {!loading && !error && links.length > 0 && (
        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
            <thead>
              <tr style={{ backgroundColor: '#f2f2f2' }}>
                <th style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>Código</th>
                <th style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>Destino</th>
                <th style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>Cliques</th>
                <th style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>Status</th>
                <th style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>Criado em</th>
              </tr>
            </thead>
            <tbody>
              {links.map((link) => (
                <tr key={link.code}>
                  <td style={{ padding: '10px', borderBottom: '1px solid #ddd', fontWeight: 'bold' }}>{link.code}</td>
                  <td style={{ padding: '10px', borderBottom: '1px solid #ddd', maxWidth: '200px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                    <a href={link.originalUrl} target="_blank" rel="noopener noreferrer" title={link.originalUrl}>
                      {link.originalUrl}
                    </a>
                  </td>
                  <td style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>{link.clicks}</td>
                  <td style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>
                    <span style={{ 
                      color: getStatus(link) === 'Ativo' ? 'green' : 'red',
                      fontWeight: 'bold'
                    }}>
                      {getStatus(link)}
                    </span>
                  </td>
                  <td style={{ padding: '10px', borderBottom: '1px solid #ddd' }}>{formatDate(link.createdAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}