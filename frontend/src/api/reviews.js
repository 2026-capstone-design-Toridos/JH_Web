import api from './axios';

export const getProductReviews = (productId, params) =>
  api.get(`/reviews/product/${productId}`, { params });

export const createReview = (formData) =>
  api.post('/reviews', formData, { headers: { 'Content-Type': 'multipart/form-data' } });

export const deleteReview = (reviewId) => api.delete(`/reviews/${reviewId}`);
