import api from './axios';

export const getCart = () => api.get('/cart');
export const addCartItem = (data) => api.post('/cart/items', data);
export const updateCartItem = (itemId, quantity) => api.put(`/cart/items/${itemId}`, { quantity });
export const removeCartItem = (itemId) => api.delete(`/cart/items/${itemId}`);
export const clearCart = () => api.delete('/cart');
