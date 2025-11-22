import React, { useEffect, useState } from 'react';
import { Card, Col, Row, Spin, message, Button, Modal, InputNumber, Input, Select } from 'antd';
import { eventService } from 'services/eventService';
import { bettingService } from 'services/bettingService';
import { useUserStore } from 'store/userStore';
import { SearchOutlined } from '@ant-design/icons';

const { Option } = Select;

const EventsPage: React.FC = () => {
  const [events, setEvents] = useState([]);
  const [filteredEvents, setFilteredEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [selectedEvent, setSelectedEvent] = useState<any>(null);
  const [betType, setBetType] = useState('');
  const [amount, setAmount] = useState(10);
  const { userId } = useUserStore();
  const [searchTerm, setSearchTerm] = useState('');
  const [sportFilter, setSportFilter] = useState('All');

  useEffect(() => {
    const fetchEvents = async () => {
      try {
        const data = await eventService.getActiveEvents();
        setEvents(data);
        setFilteredEvents(data);
      } catch (error) {
        message.error('Failed to fetch events');
      } finally {
        setLoading(false);
      }
    };

    fetchEvents();
  }, []);

  useEffect(() => {
    let filtered = events;
    if (searchTerm) {
      filtered = filtered.filter((event: any) =>
        event.homeTeam.toLowerCase().includes(searchTerm.toLowerCase()) ||
        event.awayTeam.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }
    if (sportFilter !== 'All') {
      filtered = filtered.filter((event: any) => event.sport === sportFilter);
    }
    setFilteredEvents(filtered);
  }, [searchTerm, sportFilter, events]);

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

  const sports = ['All', ...Array.from(new Set(events.map((event: any) => event.sport)))];

  return (
    <div style={{ padding: '20px' }}>
      <Row gutter={[16, 16]} style={{ marginBottom: '20px' }}>
        <Col xs={24} sm={12}>
          <Input
            placeholder="Search by team"
            prefix={<SearchOutlined />}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </Col>
        <Col xs={24} sm={12}>
          <Select defaultValue="All" style={{ width: '100%' }} onChange={(value) => setSportFilter(value)}>
            {sports.map((sport) => (
              <Option key={sport} value={sport}>{sport}</Option>
            ))}
          </Select>
        </Col>
      </Row>
      <Row gutter={[16, 16]}>
        {filteredEvents.map((event: any) => (
          <Col key={event.id} xs={24} sm={12} md={8} lg={6}>
            <Card
              title={`${event.homeTeam} vs ${event.awayTeam}`}
              extra={<span style={{ fontSize: '12px', color: '#888' }}>{event.sport}</span>}
              style={{ borderRadius: '8px' }}
            >
              <p>Start Time: {new Date(event.startTime).toLocaleString()}</p>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <Button onClick={() => showModal(event, 'HOME_WIN')}>
                  Home Win: {event.homeWinOdds}
                </Button>
                <Button onClick={() => showModal(event, 'AWAY_WIN')}>
                  Away Win: {event.awayWinOdds}
                </Button>
                <Button onClick={() => showModal(event, 'DRAW')}>
                  Draw: {event.drawOdds}
                </Button>
              </div>
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
