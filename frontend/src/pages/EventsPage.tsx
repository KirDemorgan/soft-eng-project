import React, { useEffect, useState } from 'react';
import { Card, Col, Row, Spin, message, Button, Modal, InputNumber } from 'antd';
import { eventService } from '../services/eventService';
import { bettingService } from '../services/bettingService';
import { useUserStore } from '../store/userStore';

const EventsPage: React.FC = () => {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [selectedEvent, setSelectedEvent] = useState<any>(null);
  const [betType, setBetType] = useState('');
  const [amount, setAmount] = useState(10);
  const { userId } = useUserStore();

  useEffect(() => {
    const fetchEvents = async () => {
      try {
        const data = await eventService.getActiveEvents();
        setEvents(data);
      } catch (error) {
        message.error('Failed to fetch events');
      } finally {
        setLoading(false);
      }
    };

    fetchEvents();
  }, []);

  const showModal = (event: any, type: string) => {
    setSelectedEvent(event);
    setBetType(type);
    setIsModalVisible(true);
  };

  const handleOk = async () => {
    if (!userId) {
      message.error('Please log in to place a bet');
      return;
    }
    try {
      await bettingService.placeBet({
        userId,
        eventId: selectedEvent.id,
        type: betType,
        amount,
        odds: selectedEvent[`${betType.toLowerCase()}Odds`],
      });
      message.success('Bet placed successfully');
    } catch (error) {
      message.error('Failed to place bet');
    } finally {
      setIsModalVisible(false);
    }
  };

  const handleCancel = () => {
    setIsModalVisible(false);
  };

  if (loading) {
    return <Spin />;
  }

  return (
    <div style={{ padding: '20px' }}>
      <Row gutter={[16, 16]}>
        {events.map((event: any) => (
          <Col key={event.id} xs={24} sm={12} md={8} lg={6}>
            <Card title={`${event.homeTeam} vs ${event.awayTeam}`}>
              <p>Start Time: {new Date(event.startTime).toLocaleString()}</p>
              <Button onClick={() => showModal(event, 'HOME_WIN')}>
                Home Win: {event.homeWinOdds}
              </Button>
              <Button onClick={() => showModal(event, 'AWAY_WIN')}>
                Away Win: {event.awayWinOdds}
              </Button>
              <Button onClick={() => showModal(event, 'DRAW')}>
                Draw: {event.drawOdds}
              </Button>
            </Card>
          </Col>
        ))}
      </Row>
      <Modal title="Place Bet" visible={isModalVisible} onOk={handleOk} onCancel={handleCancel}>
        <p>Event: {selectedEvent?.homeTeam} vs {selectedEvent?.awayTeam}</p>
        <p>Bet Type: {betType}</p>
        <InputNumber min={1} defaultValue={10} onChange={(value) => setAmount(value)} />
      </Modal>
    </div>
  );
};

export default EventsPage;
