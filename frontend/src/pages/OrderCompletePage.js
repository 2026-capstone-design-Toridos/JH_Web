import React from 'react';
import { Link, useParams, useLocation } from 'react-router-dom';
import './OrderCompletePage.css';

export default function OrderCompletePage() {
  const { orderNumber } = useParams();
  const location = useLocation();
  const success = location.state?.success;

  return (
    <div className="order-complete">
      <div className="order-complete__card">
        <div className="order-complete__icon">{success ? '✅' : '📋'}</div>
        <h1>{success ? '주문이 완료되었습니다!' : '주문 상세'}</h1>
        <p className="order-complete__number">주문번호: <strong>{orderNumber}</strong></p>
        {success && (
          <p className="order-complete__desc">
            주문이 성공적으로 접수되었습니다.<br />
            마이페이지에서 주문 상태를 확인하실 수 있습니다.
          </p>
        )}
        <div className="order-complete__actions">
          <Link to="/mypage" className="btn btn-primary btn-lg">주문 내역 보기</Link>
          <Link to="/products" className="btn btn-outline btn-lg">쇼핑 계속하기</Link>
        </div>
      </div>
    </div>
  );
}
