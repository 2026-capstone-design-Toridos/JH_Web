import api from './axios';

export const getProducts = (params) => api.get('/products', { params });
export const getProduct = (id) => api.get(`/products/${id}`);
export const getNewArrivals = () => api.get('/products/new-arrivals');
export const getBestSellers = () => api.get('/products/best-sellers');
export const getCategories = () => api.get('/categories');
