import React from 'react';
import { Link } from 'react-router-dom';
import './Footer.css';

export default function Footer() {
  return (
    <footer className="footer">
      <div className="container">
        <div className="footer__grid">
          <div className="footer__brand">
            <div className="footer__logo">MOMO</div>
            <p>트렌디한 패션, 합리적인 가격.<br />매일 새로운 컬렉션을 만나보세요.</p>
          </div>
          <div className="footer__col">
            <h4>카테고리</h4>
            <ul>
              <li><Link to="/products?categoryId=1">상의</Link></li>
              <li><Link to="/products?categoryId=2">하의</Link></li>
              <li><Link to="/products?categoryId=3">아우터</Link></li>
              <li><Link to="/products?categoryId=4">원피스</Link></li>
            </ul>
          </div>
          <div className="footer__col">
            <h4>고객 지원</h4>
            <ul>
              <li><Link to="/mypage/orders">주문 조회</Link></li>
              <li><a href="#">배송 안내</a></li>
              <li><a href="#">교환/반품</a></li>
              <li><a href="#">자주 묻는 질문</a></li>
            </ul>
          </div>
          <div className="footer__col">
            <h4>고객센터</h4>
            <div className="footer__cs">
              <p className="footer__tel">1588-0000</p>
              <p>평일 09:00 ~ 18:00</p>
              <p>점심시간 12:00 ~ 13:00</p>
              <p>토/일/공휴일 휴무</p>
            </div>
          </div>
        </div>
        <div className="footer__bottom">
          <p>상호명: (주)모모패션 | 대표: 김다민 | 사업자등록번호: 000-00-00000</p>
          <p>통신판매업신고: 2025-서울강남-0000 | 주소: 서울특별시 강남구</p>
          <p className="footer__copy">&copy; 2025 MOMO FASHION. All rights reserved.</p>
        </div>
      </div>
    </footer>
  );
}
