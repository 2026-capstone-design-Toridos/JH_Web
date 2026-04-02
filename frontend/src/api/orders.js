import api from './axios';

export const createOrder = (data) => api.post('/orders', data);
export const createGuestOrder = (data) => api.post('/orders/guest', data);
export const getMyOrders = (params) => api.get('/orders', { params });
export const getOrderDetail = (orderNumber) => api.get(`/orders/${orderNumber}`);
export const getGuestOrderDetail = (orderNumber, email) =>
  api.get(`/orders/guest/${orderNumber}`, { params: { email } });
export const cancelOrder = (orderNumber) => api.post(`/orders/${orderNumber}/cancel`);
