import React, { useEffect, useState, useCallback } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { getProducts, getCategories } from '../api/products';
import './ProductListPage.css';

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
          <div className="product-card__rating">★ {product.averageRating?.toFixed(1)} ({product.reviewCount})</div>
        )}
      </div>
    </div>
  );
}

export default function ProductListPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const searchParams = new URLSearchParams(location.search);

  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false);

  const categoryId = searchParams.get('categoryId') || '';
  const keyword = searchParams.get('keyword') || '';
  const sort = searchParams.get('sort') || 'latest';
  const page = parseInt(searchParams.get('page') || '0', 10);

  const updateParams = (updates) => {
    const p = new URLSearchParams(location.search);
    Object.entries(updates).forEach(([k, v]) => {
      if (v !== null && v !== '' && v !== undefined) p.set(k, v);
      else p.delete(k);
    });
    if ('page' in updates) p.set('page', updates.page);
    else p.set('page', '0');
    navigate(`/products?${p.toString()}`);
  };

  const fetchProducts = useCallback(async () => {
    setLoading(true);
    try {
      const params = { page, size: 12, sort };
      if (categoryId) params.categoryId = categoryId;
      if (keyword) params.keyword = keyword;
      const res = await getProducts(params);
      setProducts(res.data.content);
      setTotalPages(res.data.totalPages);
      setTotalElements(res.data.totalElements);
    } catch {
      setProducts([]);
    } finally {
      setLoading(false);
    }
  }, [categoryId, keyword, sort, page]);

  useEffect(() => {
    getCategories().then((r) => setCategories(r.data)).catch(() => {});
  }, []);

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  const currentCategory = categories.find((c) => String(c.id) === String(categoryId));

  return (
    <div className="page-content">
      <div className="container">
        {/* Breadcrumb */}
        <div className="breadcrumb">
          <Link to="/">홈</Link>
          <span className="breadcrumb__sep">›</span>
          <span>{currentCategory ? currentCategory.name : keyword ? `"${keyword}" 검색결과` : '전체 상품'}</span>
        </div>

        <div className="product-list-layout">
          {/* Sidebar */}
          <aside className="product-list__sidebar">
            <div className="sidebar__section">
              <h3>카테고리</h3>
              <ul>
                <li>
                  <button
                    className={!categoryId ? 'active' : ''}
                    onClick={() => updateParams({ categoryId: null })}
                  >전체</button>
                </li>
                {categories.map((c) => (
                  <li key={c.id}>
                    <button
                      className={String(categoryId) === String(c.id) ? 'active' : ''}
                      onClick={() => updateParams({ categoryId: c.id })}
                    >{c.name}</button>
                  </li>
                ))}
              </ul>
            </div>
          </aside>

          {/* Main */}
          <main className="product-list__main">
            {/* Header bar */}
            <div className="product-list__bar">
              <span className="product-list__count">총 <strong>{totalElements}</strong>개</span>
              <div className="product-list__sort">
                {[
                  { value: 'latest', label: '최신순' },
                  { value: 'price_asc', label: '낮은가격순' },
                  { value: 'price_desc', label: '높은가격순' },
                ].map((s) => (
                  <button
                    key={s.value}
                    className={sort === s.value ? 'active' : ''}
                    onClick={() => updateParams({ sort: s.value })}
                  >{s.label}</button>
                ))}
              </div>
            </div>

            {loading ? (
              <div className="loading-center"><div className="spinner" /></div>
            ) : products.length === 0 ? (
              <div className="empty-state">
                <div className="empty-state__icon">🔍</div>
                <h3 className="empty-state__title">상품이 없습니다</h3>
                <p className="empty-state__desc">다른 조건으로 검색해보세요.</p>
              </div>
            ) : (
              <div className="product-grid">
                {products.map((p) => <ProductCard key={p.id} product={p} />)}
              </div>
            )}

            {/* Pagination */}
            {totalPages > 1 && (
              <div className="pagination">
                {Array.from({ length: totalPages }, (_, i) => (
                  <button
                    key={i}
                    className={page === i ? 'active' : ''}
                    onClick={() => updateParams({ page: i })}
                  >{i + 1}</button>
                ))}
              </div>
            )}
          </main>
        </div>
      </div>
    </div>
  );
}
