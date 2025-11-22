import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { Layout, Menu, Button, Dropdown } from 'antd';
import { UserOutlined } from '@ant-design/icons';
import LoginPage from 'pages/LoginPage';
import RegistrationPage from 'pages/RegistrationPage';
import EventsPage from 'pages/EventsPage';
import MyBetsPage from 'pages/MyBetsPage';
import { useUserStore } from 'store/userStore';

const { Header, Content } = Layout;

const App: React.FC = () => {
  const { userId, setUserId } = useUserStore();
  const isAuthenticated = userId !== null;

  const handleLogout = () => {
    setUserId(null);
  };

  const menu = (
    <Menu>
      {isAuthenticated ? (
        <>
          <Menu.Item key="1">
            <Link to="/my-bets">My Bets</Link>
          </Menu.Item>
          <Menu.Item key="2" onClick={handleLogout}>
            Logout
          </Menu.Item>
        </>
      ) : (
        <>
          <Menu.Item key="1">
            <Link to="/login">Login</Link>
          </Menu.Item>
          <Menu.Item key="2">
            <Link to="/register">Register</Link>
          </Menu.Item>
        </>
      )}
    </Menu>
  );

  return (
    <Router>
      <Layout>
        <Header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div style={{ color: 'white', fontSize: '24px' }}>
            <Link to="/events" style={{ color: 'white' }}>Bookmaker</Link>
          </div>
          <div>
            <Link to="/events" style={{ color: 'white', marginRight: '20px' }}>Events</Link>
            <Dropdown overlay={menu} placement="bottomRight">
              <Button type="primary" shape="circle" icon={<UserOutlined />} />
            </Dropdown>
          </div>
        </Header>
        <Content style={{ padding: '0 50px', marginTop: 64 }}>
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
