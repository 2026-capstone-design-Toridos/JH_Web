import React from 'react';
import { Link, useParams, useLocation } from 'react-router-dom';
import './OrderCompletePage.css';

export default function OrderCompletePage() {
  const { orderNumber: paramOrderNumber } = useParams();
  const location = useLocation();
  const success = location.state?.success;
  const isGuest = location.state?.isGuest;
  const guestEmail = location.state?.guestEmail;
  const orderNumber = paramOrderNumber || location.state?.orderNumber;

  return (
    <div className="order-complete">
      <div className="order-complete__card">
        <div className="order-complete__icon">{(success || isGuest) ? '✅' : '📋'}</div>
        <h1>{(success || isGuest) ? '주문이 완료되었습니다!' : '주문 상세'}</h1>
        <p className="order-complete__number">주문번호: <strong>{orderNumber}</strong></p>
        {isGuest ? (
          <div className="order-complete__desc">
            <p>
              비회원 주문이 성공적으로 접수되었습니다.<br />
              주문 조회 시 아래 이메일이 필요합니다.
            </p>
            <p style={{ marginTop: 12, padding: '12px 16px', background: 'var(--bg-secondary, #f5f5f5)', borderRadius: 8, fontWeight: 600 }}>
              📧 {guestEmail}
            </p>
          </div>
        ) : success ? (
          <p className="order-complete__desc">
            주문이 성공적으로 접수되었습니다.<br />
            마이페이지에서 주문 상태를 확인하실 수 있습니다.
          </p>
        ) : null}
        <div className="order-complete__actions">
          {!isGuest && (
            <Link to="/mypage" className="btn btn-primary btn-lg">주문 내역 보기</Link>
          )}
          <Link to="/products" className={`btn ${isGuest ? 'btn-primary' : 'btn-outline'} btn-lg`}>쇼핑 계속하기</Link>
        </div>
      </div>
    </div>
  );
}
