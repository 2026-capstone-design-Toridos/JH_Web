import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getNewArrivals, getBestSellers, getCategories } from '../api/products';
import './HomePage.css';

function ProductCard({ product }) {
  const navigate = useNavigate();
  const hasDiscount = product.discountPrice && product.discountPrice < product.price;
  const discountRate = hasDiscount
    ? Math.round((1 - product.discountPrice / product.price) * 100) : 0;

  return (
    <div className="product-card" onClick={() => navigate(`/products/${product.id}`)}>
      <div className="product-card__img">
        <img src={product.mainImage || 'https://via.placeholder.com/300x400?text=No+Image'} alt={product.name} />
        {hasDiscount && <span className="product-card__badge">-{discountRate}%</span>}
      </div>
      <div className="product-card__body">
        <div className="product-card__brand">{product.brand}</div>
        <div className="product-card__name">{product.name}</div>
        <div className="product-card__price">
          {hasDiscount ? (
            <>
              <span className="product-card__price-original">{product.price?.toLocaleString()}원</span>
              <span className="product-card__price-sale">{product.discountPrice?.toLocaleString()}원</span>
            </>
          ) : (
            <span className="product-card__price-normal">{product.price?.toLocaleString()}원</span>
          )}
        </div>
        {product.reviewCount > 0 && (
          <div className="product-card__rating">
            ★ {product.averageRating?.toFixed(1)} ({product.reviewCount})
          </div>
        )}
      </div>
    </div>
  );
}

export default function HomePage() {
  const [newArrivals, setNewArrivals] = useState([]);
  const [bestSellers, setBestSellers] = useState([]);
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    getNewArrivals().then((r) => setNewArrivals(r.data)).catch(() => {});
    getBestSellers().then((r) => setBestSellers(r.data)).catch(() => {});
    getCategories().then((r) => setCategories(r.data)).catch(() => {});
  }, []);

  return (
    <div className="home">
      {/* Hero */}
      <section className="hero">
        <div className="hero__content">
          <p className="hero__sub">NEW COLLECTION 2025</p>
          <h1 className="hero__title">트렌드를<br />입다</h1>
          <p className="hero__desc">매 시즌 엄선된 패션 아이템을<br />합리적인 가격에 만나보세요.</p>
          <div className="hero__btns">
            <Link to="/products" className="btn btn-primary btn-lg">지금 쇼핑하기</Link>
            <Link to="/products?categoryId=4" className="btn btn-outline btn-lg" style={{color:'#fff', borderColor:'rgba(255,255,255,0.6)'}}>원피스 보기</Link>
          </div>
        </div>
        <div className="hero__overlay" />
      </section>

      {/* Category shortcuts */}
      <section className="section container">
        <div className="home__categories">
          {categories.slice(0, 6).map((cat) => (
            <Link key={cat.id} to={`/products?categoryId=${cat.id}`} className="home__cat-item">
              <div className="home__cat-icon">{getCatEmoji(cat.slug)}</div>
              <span>{cat.name}</span>
            </Link>
          ))}
        </div>
      </section>

      {/* New Arrivals */}
      <section className="section container">
        <div className="home__section-header">
          <div>
            <h2 className="section-title">신상품</h2>
            <p className="section-subtitle">이번 시즌 새로 들어온 아이템</p>
          </div>
          <Link to="/products?sort=latest" className="btn btn-outline btn-sm">전체보기</Link>
        </div>
        <div className="product-grid">
          {newArrivals.map((p) => <ProductCard key={p.id} product={p} />)}
        </div>
      </section>

      {/* Banner */}
      <section className="home__banner">
        <div className="container home__banner-content">
          <div>
            <p className="home__banner-sub">LIMITED OFFER</p>
            <h2 className="home__banner-title">5만원 이상 구매 시<br />무료배송</h2>
            <p className="home__banner-desc">지금 바로 쇼핑하고 배송비를 아끼세요!</p>
          </div>
          <Link to="/products" className="btn btn-primary btn-lg">쇼핑하러 가기</Link>
        </div>
      </section>

      {/* Best Sellers */}
      <section className="section container">
        <div className="home__section-header">
          <div>
            <h2 className="section-title">베스트셀러</h2>
            <p className="section-subtitle">가장 많은 사랑을 받은 아이템</p>
          </div>
          <Link to="/products?sort=rating" className="btn btn-outline btn-sm">전체보기</Link>
        </div>
        <div className="product-grid">
          {bestSellers.map((p) => <ProductCard key={p.id} product={p} />)}
        </div>
      </section>

      {/* Features */}
      <section className="section home__features">
        <div className="container home__features-grid">
          {[
            { icon: '🚚', title: '5만원 이상 무료배송', desc: '5만원 이상 구매 시 무료배송' },
            { icon: '🔄', title: '30일 무료 반품', desc: '구매 후 30일 이내 무료 반품' },
            { icon: '🔒', title: '안전한 결제', desc: '모든 결제 정보는 암호화됩니다' },
            { icon: '💬', title: '실시간 고객센터', desc: '평일 09:00-18:00 운영' },
          ].map((f) => (
            <div key={f.title} className="home__feature-item">
              <div className="home__feature-icon">{f.icon}</div>
              <div>
                <h4>{f.title}</h4>
                <p>{f.desc}</p>
              </div>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}

function getCatEmoji(slug) {
  const map = { tops: '👕', bottoms: '👖', outerwear: '🧥', dresses: '👗', bags: '👜', shoes: '👟', accessories: '💍' };
  return map[slug] || '🛍️';
}
