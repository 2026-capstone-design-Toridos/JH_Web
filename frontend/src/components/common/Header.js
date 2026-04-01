import React, { useState, useRef, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useCart } from '../../context/CartContext';
import './Header.css';

export default function Header() {
  const { user, logout } = useAuth();
  const { cart } = useCart();
  const navigate = useNavigate();
  const location = useLocation();
  const [search, setSearch] = useState('');
  const [menuOpen, setMenuOpen] = useState(false);
  const [userMenuOpen, setUserMenuOpen] = useState(false);
  const userMenuRef = useRef(null);

  const cartCount = cart?.totalCount || 0;

  useEffect(() => {
    const handler = (e) => {
      if (userMenuRef.current && !userMenuRef.current.contains(e.target)) {
        setUserMenuOpen(false);
      }
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    if (search.trim()) {
      navigate(`/products?keyword=${encodeURIComponent(search.trim())}`);
      setSearch('');
      setMenuOpen(false);
    }
  };

  const handleLogout = () => {
    logout();
    setUserMenuOpen(false);
    navigate('/');
  };

  const navLinks = [
    { to: '/products?categoryId=1', label: '상의' },
    { to: '/products?categoryId=2', label: '하의' },
    { to: '/products?categoryId=3', label: '아우터' },
    { to: '/products?categoryId=4', label: '원피스' },
    { to: '/products?categoryId=5', label: '가방' },
    { to: '/products?categoryId=6', label: '신발' },
    { to: '/products?categoryId=7', label: '액세서리' },
  ];

  return (
    <header className="header">
      <div className="header__top container">
        {/* Logo */}
        <Link to="/" className="header__logo">MOMO</Link>

        {/* Desktop Nav */}
        <nav className="header__nav">
          {navLinks.map((l) => (
            <Link key={l.to} to={l.to} className="header__nav-link">{l.label}</Link>
          ))}
        </nav>

        {/* Right Actions */}
        <div className="header__actions">
          {/* Search */}
          <form onSubmit={handleSearch} className="header__search">
            <input
              type="text"
              placeholder="검색..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="header__search-input"
            />
            <button type="submit" className="header__search-btn">🔍</button>
          </form>

          {/* Cart */}
          <Link to="/cart" className="header__icon-btn">
            <span>🛒</span>
            {cartCount > 0 && <span className="header__badge">{cartCount}</span>}
          </Link>

          {/* User */}
          {user ? (
            <div className="header__user" ref={userMenuRef}>
              <button className="header__icon-btn" onClick={() => setUserMenuOpen(!userMenuOpen)}>
                <span>👤</span>
              </button>
              {userMenuOpen && (
                <div className="header__user-menu">
                  <div className="header__user-name">{user.name}님</div>
                  <Link to="/mypage" onClick={() => setUserMenuOpen(false)}>마이페이지</Link>
                  <Link to="/mypage/orders" onClick={() => setUserMenuOpen(false)}>주문내역</Link>
                  {user.role === 'ADMIN' && (
                    <Link to="/admin" onClick={() => setUserMenuOpen(false)}>관리자</Link>
                  )}
                  <button onClick={handleLogout} className="header__logout">로그아웃</button>
                </div>
              )}
            </div>
          ) : (
            <Link to="/login" className="btn btn-sm btn-primary">로그인</Link>
          )}

          {/* Mobile menu toggle */}
          <button className="header__hamburger" onClick={() => setMenuOpen(!menuOpen)}>
            <span /><span /><span />
          </button>
        </div>
      </div>

      {/* Mobile menu */}
      {menuOpen && (
        <div className="header__mobile-menu">
          <form onSubmit={handleSearch} className="header__mobile-search">
            <input
              type="text"
              placeholder="검색..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
            <button type="submit">검색</button>
          </form>
          {navLinks.map((l) => (
            <Link key={l.to} to={l.to} onClick={() => setMenuOpen(false)}>{l.label}</Link>
          ))}
          {user ? (
            <>
              <Link to="/mypage" onClick={() => setMenuOpen(false)}>마이페이지</Link>
              <Link to="/mypage/orders" onClick={() => setMenuOpen(false)}>주문내역</Link>
              {user.role === 'ADMIN' && <Link to="/admin" onClick={() => setMenuOpen(false)}>관리자</Link>}
              <button onClick={handleLogout}>로그아웃</button>
            </>
          ) : (
            <Link to="/login" onClick={() => setMenuOpen(false)}>로그인 / 회원가입</Link>
          )}
        </div>
      )}
    </header>
  );
}
