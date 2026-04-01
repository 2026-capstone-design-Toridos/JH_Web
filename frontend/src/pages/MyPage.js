import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getMe, updateProfile } from '../api/auth';
import { getMyOrders, cancelOrder } from '../api/orders';
import { useAuth } from '../context/AuthContext';
import './MyPage.css';

const ORDER_STATUS_LABEL = {
  PENDING: { label: '주문접수', class: 'badge-gray' },
  PAID: { label: '결제완료', class: 'badge-info' },
  PREPARING: { label: '상품준비중', class: 'badge-warning' },
  SHIPPING: { label: '배송중', class: 'badge-warning' },
  DELIVERED: { label: '배송완료', class: 'badge-success' },
  CANCELLED: { label: '취소됨', class: 'badge-error' },
  REFUNDED: { label: '환불완료', class: 'badge-gray' },
};

export default function MyPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [tab, setTab] = useState('orders');
  const [orders, setOrders] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [profile, setProfile] = useState({ name: '', phone: '', address: '', addressDetail: '', zipCode: '' });
  const [profileMsg, setProfileMsg] = useState('');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!user) { navigate('/login'); return; }
    fetchOrders(0);
    getMe().then((r) => setProfile({
      name: r.data.name || '',
      phone: r.data.phone || '',
      address: r.data.address || '',
      addressDetail: r.data.addressDetail || '',
      zipCode: r.data.zipCode || '',
    }));
  }, [user]);

  const fetchOrders = async (page) => {
    try {
      const r = await getMyOrders({ page, size: 10 });
      setOrders(r.data.content);
      setTotalPages(r.data.totalPages);
      setCurrentPage(page);
    } catch {}
  };

  const handleCancelOrder = async (orderNumber) => {
    if (!window.confirm('주문을 취소하시겠습니까?')) return;
    try {
      await cancelOrder(orderNumber);
      fetchOrders(currentPage);
    } catch (e) {
      alert(e.response?.data?.message || '취소 실패');
    }
  };

  const handleSaveProfile = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      await updateProfile(profile);
      setProfileMsg('프로필이 업데이트되었습니다.');
      setTimeout(() => setProfileMsg(''), 3000);
    } catch {}
    setSaving(false);
  };

  const set = (k) => (e) => setProfile((p) => ({ ...p, [k]: e.target.value }));

  return (
    <div className="page-content">
      <div className="container">
        <div className="mypage__layout">
          {/* Sidebar */}
          <aside className="mypage__sidebar">
            <div className="mypage__user-info">
              <div className="mypage__avatar">{user?.name?.charAt(0)}</div>
              <div className="mypage__user-name">{user?.name}</div>
              <div className="mypage__user-email">{user?.email}</div>
            </div>
            <nav className="mypage__nav">
              <button className={tab === 'orders' ? 'active' : ''} onClick={() => setTab('orders')}>
                📦 주문 내역
              </button>
              <button className={tab === 'profile' ? 'active' : ''} onClick={() => setTab('profile')}>
                👤 회원 정보 수정
              </button>
              {user?.role === 'ADMIN' && (
                <Link to="/admin">⚙️ 관리자 페이지</Link>
              )}
            </nav>
          </aside>

          {/* Content */}
          <main className="mypage__content">
            {tab === 'orders' && (
              <div>
                <h2 className="mypage__section-title">주문 내역</h2>
                {orders.length === 0 ? (
                  <div className="empty-state">
                    <div className="empty-state__icon">📦</div>
                    <h3 className="empty-state__title">주문 내역이 없습니다</h3>
                    <Link to="/products" className="btn btn-primary">쇼핑하러 가기</Link>
                  </div>
                ) : (
                  <>
                    <div className="order-list">
                      {orders.map((order) => (
                        <div key={order.id} className="order-card">
                          <div className="order-card__header">
                            <div>
                              <span className="order-card__number">주문번호: {order.orderNumber}</span>
                              <span className="order-card__date">
                                {new Date(order.createdAt).toLocaleDateString('ko-KR')}
                              </span>
                            </div>
                            <div className="order-card__actions">
                              <span className={`badge ${ORDER_STATUS_LABEL[order.status]?.class}`}>
                                {ORDER_STATUS_LABEL[order.status]?.label}
                              </span>
                              {order.status === 'PAID' && (
                                <button className="btn btn-sm btn-outline"
                                  style={{ color: 'var(--accent)', borderColor: 'var(--accent)' }}
                                  onClick={() => handleCancelOrder(order.orderNumber)}>
                                  취소
                                </button>
                              )}
                            </div>
                          </div>
                          <div className="order-card__items">
                            {order.items?.slice(0, 2).map((item) => (
                              <div key={item.id} className="order-card__item">
                                <img src={item.productImage || 'https://via.placeholder.com/56x68'} alt={item.productName} />
                                <div>
                                  <div className="order-card__item-name">{item.productName}</div>
                                  <div className="order-card__item-info">
                                    {item.selectedSize && <span>{item.selectedSize}</span>}
                                    {item.selectedColor && <span>{item.selectedColor}</span>}
                                    <span>{item.quantity}개</span>
                                    <span>{item.price?.toLocaleString()}원</span>
                                  </div>
                                </div>
                              </div>
                            ))}
                            {order.items?.length > 2 && (
                              <p className="order-card__more">+ {order.items.length - 2}개 상품 더보기</p>
                            )}
                          </div>
                          <div className="order-card__footer">
                            <span className="order-card__total">
                              총 {order.totalAmount?.toLocaleString()}원
                            </span>
                          </div>
                        </div>
                      ))}
                    </div>
                    {totalPages > 1 && (
                      <div className="pagination">
                        {Array.from({ length: totalPages }, (_, i) => (
                          <button key={i} className={currentPage === i ? 'active' : ''}
                            onClick={() => fetchOrders(i)}>{i + 1}</button>
                        ))}
                      </div>
                    )}
                  </>
                )}
              </div>
            )}

            {tab === 'profile' && (
              <div>
                <h2 className="mypage__section-title">회원 정보 수정</h2>
                <form className="card" style={{ padding: 28 }} onSubmit={handleSaveProfile}>
                  <div className="form-group">
                    <label className="form-label">이름</label>
                    <input className="form-input" value={profile.name} onChange={set('name')} required />
                  </div>
                  <div className="form-group">
                    <label className="form-label">전화번호</label>
                    <input className="form-input" value={profile.phone} onChange={set('phone')} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">우편번호</label>
                    <input className="form-input" value={profile.zipCode} onChange={set('zipCode')} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">주소</label>
                    <input className="form-input" value={profile.address} onChange={set('address')} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">상세 주소</label>
                    <input className="form-input" value={profile.addressDetail} onChange={set('addressDetail')} />
                  </div>
                  {profileMsg && <p style={{ color: 'green', marginBottom: 12 }}>{profileMsg}</p>}
                  <button type="submit" className="btn btn-primary" disabled={saving}>
                    {saving ? '저장 중...' : '저장하기'}
                  </button>
                </form>
              </div>
            )}
          </main>
        </div>
      </div>
    </div>
  );
}
