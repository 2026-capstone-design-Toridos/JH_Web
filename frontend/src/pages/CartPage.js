import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { updateCartItem, removeCartItem } from '../api/cart';
import { useCart } from '../context/CartContext';
import './CartPage.css';

export default function CartPage() {
  const { cart, fetchCart, isGuest, updateGuestItem, removeGuestItem } = useCart();
  const navigate = useNavigate();
  const [updating, setUpdating] = useState(null);

  const handleQtyChange = async (itemId, quantity) => {
    setUpdating(itemId);
    try {
      if (isGuest) {
        updateGuestItem(itemId, quantity);
      } else {
        await updateCartItem(itemId, quantity);
        await fetchCart();
      }
    } catch {}
    setUpdating(null);
  };

  const handleRemove = async (itemId) => {
    setUpdating(itemId);
    try {
      if (isGuest) {
        removeGuestItem(itemId);
      } else {
        await removeCartItem(itemId);
        await fetchCart();
      }
    } catch {}
    setUpdating(null);
  };

  const shippingFee = cart.totalAmount >= 50000 ? 0 : 3000;
  const finalTotal = (cart.totalAmount || 0) + shippingFee;

  if (!cart.items || cart.items.length === 0) {
    return (
      <div className="page-content container">
        <div className="empty-state">
          <div className="empty-state__icon">🛒</div>
          <h2 className="empty-state__title">장바구니가 비어 있습니다</h2>
          <p className="empty-state__desc">마음에 드는 상품을 장바구니에 담아보세요!</p>
          <Link to="/products" className="btn btn-primary btn-lg">쇼핑하러 가기</Link>
        </div>
      </div>
    );
  }

  return (
    <div className="page-content" data-section="cart">
      <div className="container">
        <h1 className="cart__title">장바구니</h1>
        <div className="cart__layout">
          {/* Items */}
          <div className="cart__items">
            {cart.items.map((item) => {
              const unitPrice = item.discountPrice || item.price;
              return (
                <div key={item.id} className={`cart-item ${updating === item.id ? 'loading' : ''}`}>
                  <div
                    className="cart-item__img"
                    onClick={() => navigate(`/products/${item.productId}`)}
                  >
                    <img src={item.productImage || 'https://via.placeholder.com/100x120'} alt={item.productName} />
                  </div>
                  <div className="cart-item__info">
                    <div
                      className="cart-item__name"
                      onClick={() => navigate(`/products/${item.productId}`)}
                    >{item.productName}</div>
                    <div className="cart-item__options">
                      {item.selectedSize && <span>사이즈: {item.selectedSize}</span>}
                      {item.selectedColor && <span>색상: {item.selectedColor}</span>}
                    </div>
                    <div className="cart-item__price-row">
                      <div className="cart-item__qty">
                        <button onClick={() => handleQtyChange(item.id, item.quantity - 1)}
                          disabled={item.quantity <= 1 || updating === item.id}>-</button>
                        <span>{item.quantity}</span>
                        <button onClick={() => handleQtyChange(item.id, item.quantity + 1)}
                          disabled={updating === item.id}>+</button>
                      </div>
                      <div className="cart-item__price">
                        {(unitPrice * item.quantity).toLocaleString()}원
                      </div>
                      <button
                        className="cart-item__remove"
                        data-ghost-role="remove-from-cart"
                        data-product-id={item.productId}
                        onClick={() => handleRemove(item.id)}
                      >✕</button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>

          {/* Summary */}
          <div className="cart__summary">
            <h3>주문 요약</h3>
            <div className="cart__summary-row">
              <span>상품 금액</span>
              <span>{cart.totalAmount?.toLocaleString()}원</span>
            </div>
            <div className="cart__summary-row">
              <span>배송비</span>
              <span>{shippingFee === 0 ? '무료' : `${shippingFee.toLocaleString()}원`}</span>
            </div>
            {shippingFee > 0 && (
              <div className="cart__shipping-notice">
                {(50000 - cart.totalAmount).toLocaleString()}원 더 담으면 무료배송!
              </div>
            )}
            <div className="divider" />
            <div className="cart__summary-row cart__total">
              <span>최종 결제 금액</span>
              <span>{finalTotal.toLocaleString()}원</span>
            </div>
            <button
              className="btn btn-primary btn-full btn-lg"
              data-ghost-role="purchase-btn"
              onClick={() => navigate('/checkout')}
            >
              주문하기 ({cart.totalCount}개)
            </button>
            <Link to="/products" className="btn btn-outline btn-full" style={{ marginTop: 8 }}>
              쇼핑 계속하기
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
