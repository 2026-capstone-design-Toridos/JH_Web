package com.ghosttracker.shop.service;

import com.ghosttracker.shop.entity.Coupon;
import com.ghosttracker.shop.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private final CouponRepository couponRepository;

    public Map<String, Object> validate(String code, BigDecimal orderAmount) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 코드입니다."));

        if (!coupon.getActive()) {
            throw new IllegalStateException("사용할 수 없는 쿠폰입니다.");
        }
        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("만료된 쿠폰입니다.");
        }
        if (coupon.getMaxUses() != null && coupon.getUsedCount() >= coupon.getMaxUses()) {
            throw new IllegalStateException("사용 한도가 초과된 쿠폰입니다.");
        }
        if (coupon.getMinOrderAmount() != null && orderAmount.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new IllegalStateException(
                    coupon.getMinOrderAmount().toPlainString() + "원 이상 구매 시 사용 가능한 쿠폰입니다.");
        }

        BigDecimal discountAmount = calculateDiscount(coupon, orderAmount);

        return Map.of(
                "code", coupon.getCode(),
                "name", coupon.getName(),
                "discountType", coupon.getDiscountType().name(),
                "discountValue", coupon.getDiscountValue(),
                "discountAmount", discountAmount
        );
    }

    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal orderAmount) {
        if (coupon.getDiscountType() == Coupon.DiscountType.PERCENT) {
            return orderAmount.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.FLOOR);
        } else {
            return coupon.getDiscountValue().min(orderAmount);
        }
    }

    @Transactional
    public Coupon findAndValidate(String code, BigDecimal orderAmount) {
        if (code == null || code.isBlank()) return null;
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰 코드입니다."));

        if (!coupon.getActive()) throw new IllegalStateException("사용할 수 없는 쿠폰입니다.");
        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new IllegalStateException("만료된 쿠폰입니다.");
        if (coupon.getMaxUses() != null && coupon.getUsedCount() >= coupon.getMaxUses())
            throw new IllegalStateException("사용 한도가 초과된 쿠폰입니다.");
        if (coupon.getMinOrderAmount() != null && orderAmount.compareTo(coupon.getMinOrderAmount()) < 0)
            throw new IllegalStateException(coupon.getMinOrderAmount().toPlainString() + "원 이상 구매 시 사용 가능합니다.");

        coupon.setUsedCount(coupon.getUsedCount() + 1);
        return couponRepository.save(coupon);
    }
}
