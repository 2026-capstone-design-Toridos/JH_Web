import api from './axios';

export const getDashboard = () => api.get('/admin/dashboard');
export const getAllOrders = (params) => api.get('/admin/orders', { params });
export const updateOrderStatus = (orderId, status) =>
  api.patch(`/admin/orders/${orderId}/status`, { status });

export const createProduct = (formData) =>
  api.post('/admin/products', formData, { headers: { 'Content-Type': 'multipart/form-data' } });

export const updateProduct = (id, formData) =>
  api.put(`/admin/products/${id}`, formData, { headers: { 'Content-Type': 'multipart/form-data' } });

export const deleteProduct = (id) => api.delete(`/admin/products/${id}`);

export const createCategory = (data) => api.post('/admin/categories', data);
export const updateCategory = (id, data) => api.put(`/admin/categories/${id}`, data);
export const deleteCategory = (id) => api.delete(`/admin/categories/${id}`);
