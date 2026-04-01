import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { login as loginApi, register as registerApi } from '../api/auth';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import './AuthPage.css';

export default function LoginPage() {
  const [mode, setMode] = useState('login'); // 'login' | 'register'
  const [form, setForm] = useState({ email: '', password: '', name: '', phone: '', confirmPassword: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const { fetchCart } = useCart();
  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from || '/';

  const set = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (mode === 'register' && form.password !== form.confirmPassword) {
      setError('비밀번호가 일치하지 않습니다.');
      return;
    }

    setLoading(true);
    try {
      let res;
      if (mode === 'login') {
        res = await loginApi({ email: form.email, password: form.password });
      } else {
        res = await registerApi({ email: form.email, password: form.password, name: form.name, phone: form.phone });
      }
      login(res.data.token, res.data);
      await fetchCart();
      navigate(from, { replace: true });
    } catch (e) {
      setError(e.response?.data?.message || '오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <Link to="/" className="auth-logo">MOMO</Link>
        <h1 className="auth-title">{mode === 'login' ? '로그인' : '회원가입'}</h1>

        {/* Tab */}
        <div className="auth-tabs">
          <button className={mode === 'login' ? 'active' : ''} onClick={() => { setMode('login'); setError(''); }}>
            로그인
          </button>
          <button className={mode === 'register' ? 'active' : ''} onClick={() => { setMode('register'); setError(''); }}>
            회원가입
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          {mode === 'register' && (
            <>
              <div className="form-group">
                <label className="form-label">이름</label>
                <input className="form-input" type="text" value={form.name} onChange={set('name')}
                  placeholder="홍길동" required />
              </div>
              <div className="form-group">
                <label className="form-label">전화번호</label>
                <input className="form-input" type="tel" value={form.phone} onChange={set('phone')}
                  placeholder="010-0000-0000" />
              </div>
            </>
          )}
          <div className="form-group">
            <label className="form-label">이메일</label>
            <input className="form-input" type="email" value={form.email} onChange={set('email')}
              placeholder="example@email.com" required />
          </div>
          <div className="form-group">
            <label className="form-label">비밀번호</label>
            <input className="form-input" type="password" value={form.password} onChange={set('password')}
              placeholder={mode === 'register' ? '8자 이상 입력' : '비밀번호 입력'} required />
          </div>
          {mode === 'register' && (
            <div className="form-group">
              <label className="form-label">비밀번호 확인</label>
              <input className="form-input" type="password" value={form.confirmPassword}
                onChange={set('confirmPassword')} placeholder="비밀번호 재입력" required />
            </div>
          )}

          {error && <div className="form-error" style={{ marginBottom: 12 }}>{error}</div>}

          <button type="submit" className="btn btn-primary btn-full btn-lg" disabled={loading}>
            {loading ? '처리 중...' : mode === 'login' ? '로그인' : '회원가입'}
          </button>
        </form>

        {mode === 'login' && (
          <div className="auth-hint">
            <p>테스트 계정: test@test.com / test1234!</p>
            <p>관리자 계정: admin@ghost.com / admin1234!</p>
          </div>
        )}
      </div>
    </div>
  );
}
