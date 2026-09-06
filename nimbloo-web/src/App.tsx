import { CreateLinkForm } from './components/CreateLinkForm';
import { LinkList } from './components/LinkList';

function App() {
  return (
    <div style={{ paddingBottom: '50px' }}>
      <h1 style={{ textAlign: 'center', fontFamily: 'sans-serif' }}>Desafio Nimbloo</h1>
      <CreateLinkForm />
      <LinkList />
    </div>
  );
}

export default App;