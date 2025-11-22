import React, { useEffect, useState } from 'react';
import { Table, Spin, message, Tag } from 'antd';
import { bettingService } from 'services/bettingService';
import { useUserStore } from 'store/userStore';

const MyBetsPage: React.FC = () => {
  const [bets, setBets] = useState([]);
  const [loading, setLoading] = useState(true);
  const { userId } = useUserStore();

  useEffect(() => {
    if (!userId) {
      message.error('Please log in to see your bets');
      setLoading(false);
      return;
    }

    const fetchBets = async () => {
      try {
        const data = await bettingService.getUserBets(userId);
        setBets(data);
      } catch (error) {
        message.error('Failed to fetch bets');
      } finally {
        setLoading(false);
      }
    };

    fetchBets();
  }, [userId]);

  const columns = [
    { title: 'Event ID', dataIndex: 'eventId', key: 'eventId' },
    { title: 'Type', dataIndex: 'type', key: 'type' },
    { title: 'Amount', dataIndex: 'amount', key: 'amount', render: (amount: number) => `$${amount.toFixed(2)}` },
    { title: 'Odds', dataIndex: 'odds', key: 'odds' },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => {
        let color = 'geekblue';
        if (status === 'WON') {
          color = 'green';
        } else if (status === 'LOST') {
          color = 'volcano';
        }
        return <Tag color={color}>{status.toUpperCase()}</Tag>;
      },
    },
  ];

  if (loading) {
    return <Spin />;
  }

  return (
    <div style={{ padding: '20px' }}>
      <h1>My Bets</h1>
      <Table dataSource={bets} columns={columns} rowKey="id" />
    </div>
  );
};

export default MyBetsPage;
