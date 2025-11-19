import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { Layout, Menu } from 'antd';
import LoginPage from './pages/LoginPage';
import RegistrationPage from './pages/RegistrationPage';
import EventsPage from './pages/EventsPage';
import MyBetsPage from './pages/MyBetsPage';

const { Header, Content } = Layout;

const App: React.FC = () => {
  return (
    <Router>
      <Layout>
        <Header>
          <Menu theme="dark" mode="horizontal">
            <Menu.Item key="1">
              <Link to="/events">Events</Link>
            </Menu.Item>
            <Menu.Item key="2">
              <Link to="/my-bets">My Bets</Link>
            </Menu.Item>
            <Menu.Item key="3">
              <Link to="/login">Login</Link>
            </Menu.Item>
            <Menu.Item key="4">
              <Link to="/register">Register</Link>
            </Menu.Item>
          </Menu>
        </Header>
        <Content style={{ padding: '0 50px' }}>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegistrationPage />} />
            <Route path="/events" element={<EventsPage />} />
            <Route path="/my-bets" element={<MyBetsPage />} />
          </Routes>
        </Content>
      </Layout>
    </Router>
  );
};

export default App;
