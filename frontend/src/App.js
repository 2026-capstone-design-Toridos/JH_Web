import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { CartProvider } from './context/CartContext';
import Header from './components/common/Header';
import Footer from './components/common/Footer';
import HomePage from './pages/HomePage';
import ProductListPage from './pages/ProductListPage';
import ProductDetailPage from './pages/ProductDetailPage';
import CartPage from './pages/CartPage';
import CheckoutPage from './pages/CheckoutPage';
import OrderCompletePage from './pages/OrderCompletePage';
import LoginPage from './pages/LoginPage';
import MyPage from './pages/MyPage';
import AdminPage from './pages/admin/AdminPage';
import './styles/global.css';

function PrivateRoute({ children }) {
  const { user, loading } = useAuth();
  if (loading) return <div className="loading-center"><div className="spinner" /></div>;
  return user ? children : <Navigate to="/login" />;
}

function AdminRoute({ children }) {
  const { user, loading } = useAuth();
  if (loading) return <div className="loading-center"><div className="spinner" /></div>;
  if (!user) return <Navigate to="/login" />;
  if (user.role !== 'ADMIN') return <Navigate to="/" />;
  return children;
}

function AppLayout({ children }) {
  return (
    <>
      <Header />
      <main>{children}</main>
      <Footer />
    </>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <CartProvider>
          <Routes>
            {/* Admin (no header/footer) */}
            <Route path="/admin/*" element={
              <AdminRoute>
                <AdminPage />
              </AdminRoute>
            } />

            {/* Main layout */}
            <Route path="/*" element={
              <AppLayout>
                <Routes>
                  <Route path="/" element={<HomePage />} />
                  <Route path="/products" element={<ProductListPage />} />
                  <Route path="/products/:id" element={<ProductDetailPage />} />
                  <Route path="/cart" element={<PrivateRoute><CartPage /></PrivateRoute>} />
                  <Route path="/checkout" element={<PrivateRoute><CheckoutPage /></PrivateRoute>} />
                  <Route path="/orders/:orderNumber" element={<PrivateRoute><OrderCompletePage /></PrivateRoute>} />
                  <Route path="/login" element={<LoginPage />} />
                  <Route path="/mypage" element={<PrivateRoute><MyPage /></PrivateRoute>} />
                  <Route path="/mypage/orders" element={<PrivateRoute><MyPage /></PrivateRoute>} />
                  <Route path="*" element={
                    <div className="empty-state" style={{ marginTop: 80 }}>
                      <div className="empty-state__icon">😢</div>
                      <h2 className="empty-state__title">페이지를 찾을 수 없습니다</h2>
                      <a href="/" className="btn btn-primary">홈으로 가기</a>
                    </div>
                  } />
                </Routes>
              </AppLayout>
            } />
          </Routes>
        </CartProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}
