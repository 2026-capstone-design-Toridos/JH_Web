import React, { useEffect, useState } from 'react';
import { Link, useNavigate, Routes, Route } from 'react-router-dom';
import { getDashboard, getAllOrders, updateOrderStatus, deleteProduct } from '../../api/admin';
import { getProducts, getCategories } from '../../api/products';
import { useAuth } from '../../context/AuthContext';
import './AdminPage.css';

// ===== Dashboard =====
function Dashboard() {
  const [stats, setStats] = useState({});
  useEffect(() => { getDashboard().then((r) => setStats(r.data)).catch(() => {}); }, []);
  const cards = [
    { label: '총 회원수', value: stats.totalUsers, icon: '👥', color: '#3b82f6' },
    { label: '전체 상품', value: stats.totalProducts, icon: '👔', color: '#8b5cf6' },
    { label: '전체 주문', value: stats.totalOrders, icon: '📦', color: '#f59e0b' },
    { label: '결제완료 주문', value: stats.pendingOrders, icon: '💳', color: '#10b981' },
  ];
  return (
    <div>
      <h2 className="admin-section__title">대시보드</h2>
      <div className="admin-stats">
        {cards.map((c) => (
          <div key={c.label} className="admin-stat-card" style={{ borderTop: `4px solid ${c.color}` }}>
            <div className="admin-stat-card__icon">{c.icon}</div>
            <div className="admin-stat-card__value">{c.value ?? '-'}</div>
            <div className="admin-stat-card__label">{c.label}</div>
          </div>
        ))}
      </div>
    </div>
  );
}

// ===== Orders =====
const STATUS_OPTIONS = ['PENDING','PAID','PREPARING','SHIPPING','DELIVERED','CANCELLED','REFUNDED'];
const STATUS_LABELS = {
  PENDING:'주문접수', PAID:'결제완료', PREPARING:'상품준비중',
  SHIPPING:'배송중', DELIVERED:'배송완료', CANCELLED:'취소', REFUNDED:'환불완료'
};
function Orders() {
  const [orders, setOrders] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);

  const fetchOrders = async (p = 0) => {
    try {
      const r = await getAllOrders({ page: p, size: 20 });
      setOrders(r.data.content);
      setTotalPages(r.data.totalPages);
    } catch {}
  };

  useEffect(() => { fetchOrders(page); }, [page]);

  const handleStatusChange = async (orderId, status) => {
    try {
      await updateOrderStatus(orderId, status);
      fetchOrders(page);
    } catch (e) { alert(e.response?.data?.message || '오류 발생'); }
  };

  return (
    <div>
      <h2 className="admin-section__title">주문 관리</h2>
      <div className="admin-table-wrap">
        <table className="admin-table">
          <thead>
            <tr>
              <th>주문번호</th><th>날짜</th><th>상품</th>
              <th>금액</th><th>상태</th><th>처리</th>
            </tr>
          </thead>
          <tbody>
            {orders.map((o) => (
              <tr key={o.id}>
                <td><span className="admin-order-num">{o.orderNumber}</span></td>
                <td>{new Date(o.createdAt).toLocaleDateString('ko-KR')}</td>
                <td>{o.items?.[0]?.productName} {o.items?.length > 1 && `외 ${o.items.length - 1}건`}</td>
                <td>{o.totalAmount?.toLocaleString()}원</td>
                <td><span className={`badge badge-${statusColor(o.status)}`}>{STATUS_LABELS[o.status]}</span></td>
                <td>
                  <select
                    className="admin-select"
                    value={o.status}
                    onChange={(e) => handleStatusChange(o.id, e.target.value)}
                  >
                    {STATUS_OPTIONS.map((s) => <option key={s} value={s}>{STATUS_LABELS[s]}</option>)}
                  </select>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {totalPages > 1 && (
        <div className="pagination">
          {Array.from({ length: totalPages }, (_, i) => (
            <button key={i} className={page === i ? 'active' : ''} onClick={() => setPage(i)}>{i + 1}</button>
          ))}
        </div>
      )}
    </div>
  );
}

function statusColor(s) {
  const map = { PENDING:'gray', PAID:'info', PREPARING:'warning', SHIPPING:'warning', DELIVERED:'success', CANCELLED:'error', REFUNDED:'gray' };
  return map[s] || 'gray';
}

// ===== Products =====
function Products() {
  const navigate = useNavigate();
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const fetchProducts = async (p = 0) => {
    try {
      const r = await getProducts({ page: p, size: 20 });
      setProducts(r.data.content);
      setTotalPages(r.data.totalPages);
    } catch {}
  };

  useEffect(() => {
    fetchProducts(page);
    getCategories().then((r) => setCategories(r.data));
  }, [page]);

  const handleDelete = async (id) => {
    if (!window.confirm('이 상품을 비활성화하시겠습니까?')) return;
    try {
      await deleteProduct(id);
      fetchProducts(page);
    } catch (e) { alert(e.response?.data?.message || '오류 발생'); }
  };

  return (
    <div>
      <div className="admin-section__header">
        <h2 className="admin-section__title">상품 관리</h2>
        <Link to="/admin/products/new" className="btn btn-primary btn-sm">+ 상품 등록</Link>
      </div>
      <div className="admin-table-wrap">
        <table className="admin-table">
          <thead>
            <tr><th>이미지</th><th>상품명</th><th>카테고리</th><th>가격</th><th>재고</th><th>상태</th><th>관리</th></tr>
          </thead>
          <tbody>
            {products.map((p) => (
              <tr key={p.id}>
                <td>
                  <img src={p.mainImage || 'https://via.placeholder.com/48x60'} alt={p.name}
                    style={{ width: 48, height: 60, objectFit: 'cover', borderRadius: 4 }} />
                </td>
                <td>{p.name}</td>
                <td>{p.categoryName}</td>
                <td>
                  {p.discountPrice ? (
                    <span>{p.discountPrice.toLocaleString()}원</span>
                  ) : (
                    <span>{p.price?.toLocaleString()}원</span>
                  )}
                </td>
                <td>{p.stock}</td>
                <td><span className={`badge ${p.status === 'ACTIVE' ? 'badge-success' : 'badge-gray'}`}>{p.status}</span></td>
                <td>
                  <div style={{ display: 'flex', gap: 8 }}>
                    <Link to={`/admin/products/${p.id}/edit`} className="btn btn-sm btn-outline">수정</Link>
                    <button className="btn btn-sm" style={{ background:'var(--accent)', color:'#fff' }}
                      onClick={() => handleDelete(p.id)}>삭제</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {totalPages > 1 && (
        <div className="pagination">
          {Array.from({ length: totalPages }, (_, i) => (
            <button key={i} className={page === i ? 'active' : ''} onClick={() => setPage(i)}>{i + 1}</button>
          ))}
        </div>
      )}
    </div>
  );
}

// ===== Product Form =====
function ProductForm({ editId }) {
  const navigate = useNavigate();
  const [categories, setCategories] = useState([]);
  const [form, setForm] = useState({
    name: '', description: '', price: '', discountPrice: '', stock: '', brand: '',
    categoryId: '', status: 'ACTIVE',
  });
  const [mainImage, setMainImage] = useState(null);
  const [saving, setSaving] = useState(false);
  const [msg, setMsg] = useState('');

  useEffect(() => {
    getCategories().then((r) => setCategories(r.data));
    if (editId) {
      import('../../api/products').then(({ getProduct }) => {
        getProduct(editId).then((r) => {
          const p = r.data;
          setForm({
            name: p.name, description: p.description || '', price: p.price,
            discountPrice: p.discountPrice || '', stock: p.stock, brand: p.brand || '',
            categoryId: p.categoryId || '', status: p.status,
          });
        });
      });
    }
  }, [editId]);

  const set = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      const formData = new FormData();
      const dataBlob = new Blob([JSON.stringify(form)], { type: 'application/json' });
      formData.append('data', dataBlob);
      if (mainImage) formData.append('mainImage', mainImage);

      if (editId) {
        const { updateProduct } = await import('../../api/admin');
        await updateProduct(editId, formData);
      } else {
        const { createProduct } = await import('../../api/admin');
        await createProduct(formData);
      }
      setMsg('저장되었습니다.');
      setTimeout(() => navigate('/admin/products'), 1000);
    } catch (e) {
      setMsg(e.response?.data?.message || '오류 발생');
    }
    setSaving(false);
  };

  return (
    <div>
      <h2 className="admin-section__title">{editId ? '상품 수정' : '상품 등록'}</h2>
      <form className="admin-form card" onSubmit={handleSubmit}>
        <div className="admin-form__grid">
          <div className="form-group">
            <label className="form-label">상품명 *</label>
            <input className="form-input" value={form.name} onChange={set('name')} required />
          </div>
          <div className="form-group">
            <label className="form-label">브랜드</label>
            <input className="form-input" value={form.brand} onChange={set('brand')} />
          </div>
          <div className="form-group">
            <label className="form-label">카테고리 *</label>
            <select className="form-input" value={form.categoryId} onChange={set('categoryId')} required>
              <option value="">선택</option>
              {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
          </div>
          <div className="form-group">
            <label className="form-label">상태</label>
            <select className="form-input" value={form.status} onChange={set('status')}>
              <option value="ACTIVE">활성</option>
              <option value="INACTIVE">비활성</option>
              <option value="SOLD_OUT">품절</option>
            </select>
          </div>
          <div className="form-group">
            <label className="form-label">정가 *</label>
            <input className="form-input" type="number" value={form.price} onChange={set('price')} required />
          </div>
          <div className="form-group">
            <label className="form-label">할인가</label>
            <input className="form-input" type="number" value={form.discountPrice} onChange={set('discountPrice')} />
          </div>
          <div className="form-group">
            <label className="form-label">재고 *</label>
            <input className="form-input" type="number" value={form.stock} onChange={set('stock')} required />
          </div>
          <div className="form-group">
            <label className="form-label">메인 이미지</label>
            <input type="file" accept="image/*" onChange={(e) => setMainImage(e.target.files[0])} />
          </div>
        </div>
        <div className="form-group">
          <label className="form-label">상품 설명</label>
          <textarea className="form-input" rows={6} value={form.description} onChange={set('description')} />
        </div>
        {msg && <p style={{ marginBottom: 12, color: msg.includes('오류') ? 'var(--accent)' : 'green' }}>{msg}</p>}
        <div style={{ display: 'flex', gap: 12 }}>
          <button type="submit" className="btn btn-primary" disabled={saving}>
            {saving ? '저장 중...' : '저장'}
          </button>
          <button type="button" className="btn btn-outline" onClick={() => navigate('/admin/products')}>취소</button>
        </div>
      </form>
    </div>
  );
}

// ===== Main Admin Layout =====
export default function AdminPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [activeMenu, setActiveMenu] = useState('dashboard');

  useEffect(() => {
    if (user && user.role !== 'ADMIN') navigate('/');
  }, [user]);

  return (
    <div className="admin-layout">
      {/* Sidebar */}
      <aside className="admin-sidebar">
        <div className="admin-sidebar__logo">
          <Link to="/">← 쇼핑몰</Link>
          <h2>관리자</h2>
        </div>
        <nav className="admin-nav">
          {[
            { key: 'dashboard', icon: '📊', label: '대시보드', path: '/admin' },
            { key: 'products', icon: '👔', label: '상품 관리', path: '/admin/products' },
            { key: 'orders', icon: '📦', label: '주문 관리', path: '/admin/orders' },
          ].map((m) => (
            <Link key={m.key} to={m.path}
              className={`admin-nav__item ${activeMenu === m.key ? 'active' : ''}`}
              onClick={() => setActiveMenu(m.key)}>
              <span>{m.icon}</span> {m.label}
            </Link>
          ))}
        </nav>
      </aside>

      {/* Content */}
      <main className="admin-content">
        <Routes>
          <Route index element={<Dashboard />} />
          <Route path="products" element={<Products />} />
          <Route path="products/new" element={<ProductForm />} />
          <Route path="products/:id/edit" element={<ProductFormWrapper />} />
          <Route path="orders" element={<Orders />} />
        </Routes>
      </main>
    </div>
  );
}

function ProductFormWrapper() {
  const { id } = require('react-router-dom').useParams();
  return <ProductForm editId={id} />;
}
