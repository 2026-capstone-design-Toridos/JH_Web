import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { getProduct } from '../api/products';
import { getProductReviews, createReview, deleteReview } from '../api/reviews';
import { addCartItem } from '../api/cart';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import './ProductDetailPage.css';

const SIZES = ['XS', 'S', 'M', 'L', 'XL', 'XXL'];
const COLORS = ['블랙', '화이트', '네이비', '베이지', '그레이', '핑크'];

export default function ProductDetailPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const { fetchCart, addGuestItem } = useCart();
  const navigate = useNavigate();

  const [product, setProduct] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [reviewPage, setReviewPage] = useState({ totalPages: 0, totalElements: 0 });
  const [loading, setLoading] = useState(true);
  const [selectedImage, setSelectedImage] = useState(0);
  const [selectedSize, setSelectedSize] = useState('');
  const [selectedColor, setSelectedColor] = useState('');
  const [quantity, setQuantity] = useState(1);
  const [addingToCart, setAddingToCart] = useState(false);
  const [toast, setToast] = useState('');
  const [activeTab, setActiveTab] = useState('desc');

  // Review form
  const [rating, setRating] = useState(5);
  const [reviewContent, setReviewContent] = useState('');
  const [submittingReview, setSubmittingReview] = useState(false);

  useEffect(() => {
    setLoading(true);
    getProduct(id)
      .then((r) => setProduct(r.data))
      .catch(() => navigate('/products'))
      .finally(() => setLoading(false));
    loadReviews();
  }, [id]);

  const loadReviews = async () => {
    try {
      const r = await getProductReviews(id, { page: 0, size: 20 });
      setReviews(r.data.content);
      setReviewPage({ totalPages: r.data.totalPages, totalElements: r.data.totalElements });
    } catch {}
  };

  const showToast = (msg) => {
    setToast(msg);
    setTimeout(() => setToast(''), 2500);
  };

  const handleAddToCart = async () => {
    setAddingToCart(true);
    try {
      if (user) {
        await addCartItem({ productId: parseInt(id), quantity, selectedSize, selectedColor });
        await fetchCart();
      } else {
        addGuestItem(product, quantity, selectedSize, selectedColor);
      }
      showToast('장바구니에 담았습니다!');
    } catch (e) {
      showToast(e.response?.data?.message || '오류가 발생했습니다.');
    } finally {
      setAddingToCart(false);
    }
  };

  const handleBuyNow = async () => {
    try {
      if (user) {
        await addCartItem({ productId: parseInt(id), quantity, selectedSize, selectedColor });
        await fetchCart();
      } else {
        addGuestItem(product, quantity, selectedSize, selectedColor);
      }
      navigate('/cart');
    } catch (e) {
      showToast(e.response?.data?.message || '오류가 발생했습니다.');
    }
  };

  const handleReviewSubmit = async (e) => {
    e.preventDefault();
    if (!user) { navigate('/login'); return; }
    setSubmittingReview(true);
    try {
      const formData = new FormData();
      const blob = new Blob([JSON.stringify({ productId: parseInt(id), rating, content: reviewContent })],
        { type: 'application/json' });
      formData.append('data', blob);
      await createReview(formData);
      setReviewContent('');
      setRating(5);
      await loadReviews();
      showToast('리뷰가 등록되었습니다.');
    } catch (e) {
      showToast(e.response?.data?.message || '리뷰 등록 실패');
    } finally {
      setSubmittingReview(false);
    }
  };

  const handleDeleteReview = async (reviewId) => {
    if (!window.confirm('리뷰를 삭제하시겠습니까?')) return;
    try {
      await deleteReview(reviewId);
      await loadReviews();
      showToast('리뷰가 삭제되었습니다.');
    } catch {}
  };

  if (loading) return <div className="loading-center"><div className="spinner" /></div>;
  if (!product) return null;

  const hasDiscount = product.discountPrice && product.discountPrice < product.price;
  const discountRate = hasDiscount ? Math.round((1 - product.discountPrice / product.price) * 100) : 0;
  const allImages = [product.mainImage, ...(product.images || [])].filter(Boolean);

  return (
    <div className="page-content">
      <div className="container">
        {toast && <div className="toast-container"><div className="toast success">{toast}</div></div>}

        {/* Breadcrumb */}
        <div className="breadcrumb">
          <Link to="/">홈</Link>
          <span className="breadcrumb__sep">›</span>
          <Link to="/products">상품</Link>
          <span className="breadcrumb__sep">›</span>
          <span>{product.name}</span>
        </div>

        <div className="product-detail" data-section="product-detail" data-product-id={product.id}>
          {/* Images */}
          <div className="product-detail__images" data-subsection="product-images">
            <div className="product-detail__main-img">
              <img src={allImages[selectedImage] || 'https://via.placeholder.com/500x600?text=No+Image'} alt={product.name} />
              {hasDiscount && <span className="product-card__badge">-{discountRate}%</span>}
            </div>
            {allImages.length > 1 && (
              <div className="product-detail__thumbs">
                {allImages.map((img, i) => (
                  <div
                    key={i}
                    className={`product-detail__thumb ${selectedImage === i ? 'active' : ''}`}
                    onClick={() => setSelectedImage(i)}
                  >
                    <img src={img} alt={`${product.name} ${i + 1}`} />
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Info */}
          <div className="product-detail__info" data-subsection="product-info">
            <div className="product-detail__brand">{product.brand}</div>
            <h1 className="product-detail__name">{product.name}</h1>

            <div className="product-detail__rating">
              <div className="stars">
                {[1,2,3,4,5].map((s) => (
                  <span key={s} className={`star ${s <= Math.round(product.averageRating) ? 'filled' : ''}`}>★</span>
                ))}
              </div>
              <span>{product.averageRating?.toFixed(1)}</span>
              <span className="text-muted">({reviewPage.totalElements}개 리뷰)</span>
            </div>

            <div className="product-detail__price">
              {hasDiscount && (
                <div className="product-detail__price-original">{product.price?.toLocaleString()}원</div>
              )}
              <div className="product-detail__price-main">
                {(hasDiscount ? product.discountPrice : product.price)?.toLocaleString()}원
                {hasDiscount && <span className="product-detail__discount-badge">-{discountRate}%</span>}
              </div>
            </div>

            <div className="divider" />

            {/* Size */}
            <div className="product-detail__option">
              <div className="product-detail__option-label">사이즈</div>
              <div className="product-detail__option-btns">
                {SIZES.map((s) => (
                  <button
                    key={s}
                    className={`option-btn ${selectedSize === s ? 'active' : ''}`}
                    onClick={() => setSelectedSize(s)}
                  >{s}</button>
                ))}
              </div>
            </div>

            {/* Color */}
            <div className="product-detail__option">
              <div className="product-detail__option-label">색상</div>
              <div className="product-detail__option-btns">
                {COLORS.map((c) => (
                  <button
                    key={c}
                    className={`option-btn ${selectedColor === c ? 'active' : ''}`}
                    onClick={() => setSelectedColor(c)}
                  >{c}</button>
                ))}
              </div>
            </div>

            {/* Quantity */}
            <div className="product-detail__option">
              <div className="product-detail__option-label">수량</div>
              <div className="product-detail__qty">
                <button onClick={() => setQuantity(Math.max(1, quantity - 1))}>-</button>
                <span>{quantity}</span>
                <button onClick={() => setQuantity(Math.min(product.stock, quantity + 1))}>+</button>
              </div>
              <span className="product-detail__stock">재고 {product.stock}개</span>
            </div>

            {/* Total price */}
            <div className="product-detail__total">
              <span>합계</span>
              <span className="product-detail__total-price">
                {((hasDiscount ? product.discountPrice : product.price) * quantity)?.toLocaleString()}원
              </span>
            </div>

            {/* Actions */}
            <div className="product-detail__actions">
              <button
                className="btn btn-outline btn-full"
                data-ghost-role="add-to-cart"
                data-product-id={product.id}
                data-product-name={product.name}
                onClick={handleAddToCart}
                disabled={addingToCart || product.stock === 0}
              >
                {product.stock === 0 ? '품절' : addingToCart ? '담는 중...' : '장바구니 담기'}
              </button>
              <button
                className="btn btn-primary btn-full btn-lg"
                data-ghost-role="purchase-btn"
                data-product-id={product.id}
                onClick={handleBuyNow}
                disabled={product.stock === 0}
              >바로 구매</button>
            </div>

            {/* Shipping info */}
            <div className="product-detail__shipping">
              <div>🚚 <strong>5만원 이상 무료배송</strong> (미만 시 3,000원)</div>
              <div>📦 주문 후 1~3 영업일 내 출고</div>
              <div>🔄 30일 이내 무료 반품/교환</div>
            </div>
          </div>
        </div>

        {/* Tabs */}
        <div className="product-tabs" data-section="product-tabs">
          <div className="product-tabs__nav">
            {[
              { key: 'desc', label: '상품 설명' },
              { key: 'review', label: `리뷰 (${reviewPage.totalElements})` },
              { key: 'ship', label: '배송/교환/반품' },
            ].map((t) => (
              <button
                key={t.key}
                className={activeTab === t.key ? 'active' : ''}
                onClick={() => setActiveTab(t.key)}
              >{t.label}</button>
            ))}
          </div>

          <div className="product-tabs__content">
            {activeTab === 'desc' && (
              <div className="product-desc" data-subsection="product-desc">
                <p style={{ whiteSpace: 'pre-line', lineHeight: 1.8 }}>{product.description || '상품 설명이 없습니다.'}</p>
              </div>
            )}

            {activeTab === 'review' && (
              <div className="product-reviews" data-subsection="product-reviews">
                {/* Review form */}
                {user && (
                  <form className="review-form" onSubmit={handleReviewSubmit}>
                    <h3>리뷰 작성</h3>
                    <div className="review-form__stars">
                      {[1,2,3,4,5].map((s) => (
                        <span
                          key={s}
                          className={`review-star ${s <= rating ? 'active' : ''}`}
                          onClick={() => setRating(s)}
                        >★</span>
                      ))}
                    </div>
                    <textarea
                      className="form-input"
                      rows={4}
                      placeholder="구매하신 상품에 대한 리뷰를 작성해주세요."
                      value={reviewContent}
                      onChange={(e) => setReviewContent(e.target.value)}
                    />
                    <button className="btn btn-primary" type="submit" disabled={submittingReview}>
                      {submittingReview ? '등록 중...' : '리뷰 등록'}
                    </button>
                  </form>
                )}

                {reviews.length === 0 ? (
                  <div className="empty-state">
                    <div className="empty-state__icon">💬</div>
                    <p className="empty-state__title">아직 리뷰가 없습니다</p>
                    <p className="empty-state__desc">첫 번째 리뷰를 작성해보세요!</p>
                  </div>
                ) : (
                  <div className="review-list">
                    {reviews.map((r) => (
                      <div key={r.id} className="review-item">
                        <div className="review-item__header">
                          <div className="stars">
                            {[1,2,3,4,5].map((s) => (
                              <span key={s} className={`star ${s <= r.rating ? 'filled' : ''}`}>★</span>
                            ))}
                          </div>
                          <span className="review-item__author">{r.userName}</span>
                          <span className="review-item__date">
                            {new Date(r.createdAt).toLocaleDateString('ko-KR')}
                          </span>
                          {user?.email && r.userId === user.id && (
                            <button className="btn-ghost btn-sm" onClick={() => handleDeleteReview(r.id)}>삭제</button>
                          )}
                        </div>
                        <p className="review-item__content">{r.content}</p>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}

            {activeTab === 'ship' && (
              <div className="product-ship-info">
                <h3>배송 안내</h3>
                <table>
                  <tbody>
                    <tr><th>배송 방법</th><td>택배 (CJ대한통운)</td></tr>
                    <tr><th>배송비</th><td>5만원 이상 무료 / 미만 3,000원</td></tr>
                    <tr><th>배송 기간</th><td>주문 후 1~3 영업일 내 출고 (주말/공휴일 제외)</td></tr>
                  </tbody>
                </table>
                <h3>교환/반품 안내</h3>
                <table>
                  <tbody>
                    <tr><th>교환/반품 기간</th><td>수령 후 30일 이내</td></tr>
                    <tr><th>교환/반품 불가</th><td>착용/세탁/수선 후, 태그 제거 후, 고객 과실로 인한 손상</td></tr>
                    <tr><th>교환/반품 방법</th><td>고객센터 1588-0000 (평일 09:00~18:00)</td></tr>
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
