import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createOrder, createGuestOrder } from '../api/orders';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import './CheckoutPage.css';

export default function CheckoutPage() {
  const { cart, fetchCart, isGuest, clearGuestCart } = useCart();
  const { user } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    // Guest info (only shown for non-logged-in users)
    guestEmail: '',
    guestName: '',
    guestPhone: '',
    // Shipping info
    receiverName: user?.name || '',
    receiverPhone: user?.phone || '',
    zipCode: user?.zipCode || '',
    shippingAddress: user?.address || '',
    shippingAddressDetail: user?.addressDetail || '',
    deliveryRequest: '',
    paymentMethod: 'MOCK',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const set = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));

  const shippingFee = (cart.totalAmount || 0) >= 50000 ? 0 : 3000;
  const finalTotal = (cart.totalAmount || 0) + shippingFee;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    // Validate guest info
    if (isGuest) {
      if (!form.guestEmail || !form.guestName || !form.guestPhone) {
        setError('주문자 정보를 모두 입력해주세요.');
        return;
      }
    }
    if (!form.receiverName || !form.receiverPhone || !form.shippingAddress || !form.zipCode) {
      setError('배송지 정보를 모두 입력해주세요.');
      return;
    }

    setLoading(true);
    try {
      let res;
      if (isGuest) {
        // Guest order: send items from localStorage cart
        const guestData = {
          guestEmail: form.guestEmail,
          guestName: form.guestName,
          guestPhone: form.guestPhone,
          receiverName: form.receiverName,
          receiverPhone: form.receiverPhone,
          zipCode: form.zipCode,
          shippingAddress: form.shippingAddress,
          shippingAddressDetail: form.shippingAddressDetail,
          deliveryRequest: form.deliveryRequest,
          paymentMethod: form.paymentMethod,
          items: cart.items.map((item) => ({
            productId: item.productId,
            quantity: item.quantity,
            selectedSize: item.selectedSize || '',
            selectedColor: item.selectedColor || '',
          })),
        };
        res = await createGuestOrder(guestData);
        clearGuestCart();
        navigate(`/orders/complete`, {
          state: { orderNumber: res.data.orderNumber, guestEmail: form.guestEmail, isGuest: true }
        });
      } else {
        res = await createOrder(form);
        await fetchCart();
        navigate(`/orders/${res.data.orderNumber}`, { state: { success: true } });
      }
    } catch (e) {
      setError(e.response?.data?.message || '주문 처리 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-content">
      <div className="container">
        <h1 className="checkout__title">주문하기</h1>
        <form className="checkout__layout" onSubmit={handleSubmit}>
          <div className="checkout__left">
            {/* Guest Info (only for non-logged-in) */}
            {isGuest && (
              <div className="checkout__section">
                <h2>주문자 정보</h2>
                <p style={{ color: 'var(--text-secondary)', fontSize: 14, marginBottom: 16 }}>
                  비회원 주문입니다. 주문 조회 시 이메일이 필요합니다.
                </p>
                <div className="form-group">
                  <label className="form-label">이메일 *</label>
                  <input className="form-input" type="email" value={form.guestEmail}
                    onChange={set('guestEmail')} placeholder="order@example.com" required />
                </div>
                <div className="form-group">
                  <label className="form-label">이름 *</label>
                  <input className="form-input" value={form.guestName}
                    onChange={set('guestName')} required />
                </div>
                <div className="form-group">
                  <label className="form-label">연락처 *</label>
                  <input className="form-input" value={form.guestPhone}
                    onChange={set('guestPhone')} placeholder="010-0000-0000" required />
                </div>
              </div>
            )}

            {/* Shipping */}
            <div className="checkout__section">
              <h2>배송지 정보</h2>
              <div className="form-group">
                <label className="form-label">받는 분 이름 *</label>
                <input className="form-input" value={form.receiverName} onChange={set('receiverName')} required />
              </div>
              <div className="form-group">
                <label className="form-label">연락처 *</label>
                <input className="form-input" value={form.receiverPhone} onChange={set('receiverPhone')}
                  placeholder="010-0000-0000" required />
              </div>
              <div className="form-group">
                <label className="form-label">우편번호 *</label>
                <input className="form-input" value={form.zipCode} onChange={set('zipCode')} required />
              </div>
              <div className="form-group">
                <label className="form-label">주소 *</label>
                <input className="form-input" value={form.shippingAddress} onChange={set('shippingAddress')} required />
              </div>
              <div className="form-group">
                <label className="form-label">상세 주소</label>
                <input className="form-input" value={form.shippingAddressDetail} onChange={set('shippingAddressDetail')} />
              </div>
              <div className="form-group">
                <label className="form-label">배송 요청사항</label>
                <select className="form-input" value={form.deliveryRequest} onChange={set('deliveryRequest')}>
                  <option value="">선택하세요</option>
                  <option value="문 앞에 놓아주세요">문 앞에 놓아주세요</option>
                  <option value="경비실에 맡겨주세요">경비실에 맡겨주세요</option>
                  <option value="택배함에 넣어주세요">택배함에 넣어주세요</option>
                  <option value="직접 수령할게요">직접 수령할게요</option>
                </select>
              </div>
            </div>

            {/* Order items */}
            <div className="checkout__section">
              <h2>주문 상품</h2>
              <div className="checkout__order-items">
                {cart.items?.map((item) => {
                  const unitPrice = item.discountPrice || item.price;
                  return (
                    <div key={item.id} className="checkout__order-item">
                      <img src={item.productImage || 'https://via.placeholder.com/60x72'} alt={item.productName} />
                      <div className="checkout__order-item-info">
                        <div className="checkout__order-item-name">{item.productName}</div>
                        <div className="checkout__order-item-opts">
                          {item.selectedSize && <span>{item.selectedSize}</span>}
                          {item.selectedColor && <span>{item.selectedColor}</span>}
                          <span>수량 {item.quantity}개</span>
                        </div>
                      </div>
                      <div className="checkout__order-item-price">
                        {(unitPrice * item.quantity).toLocaleString()}원
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>

            {/* Payment */}
            <div className="checkout__section">
              <h2>결제 방법</h2>
              <div className="checkout__payment-methods">
                {[
                  { value: 'MOCK', label: '모의 결제 (테스트)', icon: '🧪' },
                  { value: 'CARD', label: '신용/체크카드', icon: '💳' },
                  { value: 'VIRTUAL_ACCOUNT', label: '가상계좌', icon: '🏦' },
                  { value: 'TRANSFER', label: '계좌이체', icon: '💰' },
                ].map((m) => (
                  <label key={m.value} className={`payment-method ${form.paymentMethod === m.value ? 'active' : ''}`}>
                    <input type="radio" name="paymentMethod" value={m.value}
                      checked={form.paymentMethod === m.value} onChange={set('paymentMethod')} />
                    <span>{m.icon}</span>
                    <span>{m.label}</span>
                  </label>
                ))}
              </div>
              {form.paymentMethod === 'MOCK' && (
                <div className="checkout__mock-notice">
                  🧪 테스트 모드입니다. 실제 결제가 이루어지지 않습니다.
                </div>
              )}
            </div>
          </div>

          {/* Right: Summary */}
          <div className="checkout__right">
            <div className="checkout__summary">
              <h3>결제 금액</h3>
              <div className="checkout__summary-row">
                <span>상품 금액</span>
                <span>{cart.totalAmount?.toLocaleString()}원</span>
              </div>
              <div className="checkout__summary-row">
                <span>배송비</span>
                <span>{shippingFee === 0 ? '무료' : `${shippingFee.toLocaleString()}원`}</span>
              </div>
              <div className="checkout__summary-row">
                <span>할인 금액</span>
                <span>0원</span>
              </div>
              <div className="divider" />
              <div className="checkout__summary-total">
                <span>최종 결제 금액</span>
                <span>{finalTotal.toLocaleString()}원</span>
              </div>

              {error && <div className="form-error" style={{ marginTop: 12 }}>{error}</div>}

              <button type="submit" className="btn btn-primary btn-full btn-lg" disabled={loading}
                style={{ marginTop: 20 }}>
                {loading ? '처리 중...' : `${finalTotal.toLocaleString()}원 결제하기`}
              </button>
              <p className="checkout__agree">
                주문하시면 이용약관 및 개인정보처리방침에 동의한 것으로 간주됩니다.
              </p>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
}
