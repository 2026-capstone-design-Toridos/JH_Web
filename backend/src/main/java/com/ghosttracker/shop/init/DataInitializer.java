package com.ghosttracker.shop.init;

import com.ghosttracker.shop.entity.*;
import com.ghosttracker.shop.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CouponRepository couponRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initCategories();
        initProducts();
        initUsers();
        initCoupons();
    }

    private void initCategories() {
        if (categoryRepository.count() > 0) return;
        List<Category> categories = List.of(
            Category.builder().name("상의").slug("tops").description("티셔츠, 블라우스, 니트").displayOrder(1).build(),
            Category.builder().name("하의").slug("bottoms").description("팬츠, 스커트, 청바지").displayOrder(2).build(),
            Category.builder().name("아우터").slug("outerwear").description("코트, 재킷, 패딩").displayOrder(3).build(),
            Category.builder().name("원피스").slug("dresses").description("캐주얼, 포멀 원피스").displayOrder(4).build(),
            Category.builder().name("가방").slug("bags").description("숄더백, 백팩, 클러치").displayOrder(5).build(),
            Category.builder().name("신발").slug("shoes").description("스니커즈, 힐, 로퍼").displayOrder(6).build(),
            Category.builder().name("액세서리").slug("accessories").description("목걸이, 귀걸이, 반지").displayOrder(7).build()
        );
        categoryRepository.saveAll(categories);
        log.info("Categories initialized.");
    }

    private void initProducts() {
        if (productRepository.count() >= 20) return;

        Category tops       = categoryRepository.findBySlug("tops").orElseThrow();
        Category bottoms    = categoryRepository.findBySlug("bottoms").orElseThrow();
        Category outerwear  = categoryRepository.findBySlug("outerwear").orElseThrow();
        Category dresses    = categoryRepository.findBySlug("dresses").orElseThrow();
        Category bags       = categoryRepository.findBySlug("bags").orElseThrow();
        Category shoes      = categoryRepository.findBySlug("shoes").orElseThrow();
        Category accessories= categoryRepository.findBySlug("accessories").orElseThrow();

        List<Product> products = new ArrayList<>();

        // ===== 상의 (7) =====
        products.add(Product.builder()
            .name("오버핏 스트라이프 셔츠")
            .description("편안한 오버핏 스트라이프 패턴의 기본 셔츠입니다.\n면 100% 소재로 세탁이 편리하고 어느 시즌에나 활용 가능합니다.\n데님 팬츠, 슬랙스 어디에나 잘 어울립니다.")
            .price(BigDecimal.valueOf(39000)).discountPrice(BigDecimal.valueOf(29000))
            .stock(50).brand("MOMO").category(tops)
            .mainImage("https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=500").build());

        products.add(Product.builder()
            .name("크롭 니트 스웨터")
            .description("부드러운 아크릴 혼방 소재의 크롭 니트 스웨터입니다.\n하이웨이스트 하의와 완벽 매칭되는 길이감.\n다양한 컬러로 구비되어 있어 취향에 맞게 선택하세요.")
            .price(BigDecimal.valueOf(55000)).discountPrice(BigDecimal.valueOf(44000))
            .stock(40).brand("KNIT CO").category(tops)
            .mainImage("https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=500").build());

        products.add(Product.builder()
            .name("베이직 라운드넥 티셔츠")
            .description("언제 어디서나 편하게 입을 수 있는 기본 라운드넥 티셔츠.\n부드러운 면 소재로 피부 자극 없이 착용 가능합니다.\n7가지 색상 보유.")
            .price(BigDecimal.valueOf(19000))
            .stock(100).brand("BASIC LAB").category(tops)
            .mainImage("https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=500").build());

        products.add(Product.builder()
            .name("플로럴 프린트 블라우스")
            .description("화사한 플로럴 패턴의 여성스러운 블라우스.\n시폰 소재로 가볍고 통기성이 좋아 봄/여름에 특히 좋습니다.\n포멀과 캐주얼 모두 소화 가능.")
            .price(BigDecimal.valueOf(45000)).discountPrice(BigDecimal.valueOf(36000))
            .stock(35).brand("BLOOM").category(tops)
            .mainImage("https://images.unsplash.com/photo-1434389677669-e08b4cac3105?w=500").build());

        products.add(Product.builder()
            .name("스트라이프 크롭 맨투맨")
            .description("포인트 스트라이프 디자인의 크롭 맨투맨.\n기모 안감으로 포근한 착용감.\n운동이나 캐주얼 데이트 모두 활용 가능.")
            .price(BigDecimal.valueOf(42000)).discountPrice(BigDecimal.valueOf(33000))
            .stock(45).brand("COZY FIT").category(tops)
            .mainImage("https://images.unsplash.com/photo-1556821840-3a63f15732ce?w=500").build());

        products.add(Product.builder()
            .name("린넨 오버사이즈 셔츠")
            .description("천연 린넨 소재로 시원하고 통기성이 뛰어난 오버사이즈 셔츠.\n여름철 레이어드 또는 원피스처럼 활용 가능.\n세련된 루즈핏 실루엣.")
            .price(BigDecimal.valueOf(52000))
            .stock(28).brand("LINEN HOUSE").category(tops)
            .mainImage("https://images.unsplash.com/photo-1598554747436-c9293d6a588f?w=500").build());

        products.add(Product.builder()
            .name("터틀넥 슬림핏 니트")
            .description("클래식한 터틀넥 디자인의 슬림핏 니트.\n고급스러운 울혼방 소재로 따뜻하고 부드럽습니다.\n코트나 재킷 안에 레이어드하기 좋은 아이템.")
            .price(BigDecimal.valueOf(62000)).discountPrice(BigDecimal.valueOf(49000))
            .stock(22).brand("KNIT CO").category(tops)
            .mainImage("https://images.unsplash.com/photo-1620799140408-edc6dcb6d633?w=500").build());

        // ===== 하의 (5) =====
        products.add(Product.builder()
            .name("슬림핏 데님 팬츠")
            .description("고신축성 스트레치 데님 소재로 편안한 착용감.\n슬림하게 다리 라인을 살려주는 핏.\n블루/블랙/화이트 3가지 색상 운영.")
            .price(BigDecimal.valueOf(59000)).discountPrice(BigDecimal.valueOf(49000))
            .stock(30).brand("DENIM LAB").category(bottoms)
            .mainImage("https://images.unsplash.com/photo-1542272604-787c3835535d?w=500").build());

        products.add(Product.builder()
            .name("와이드 슬랙스")
            .description("편안한 와이드핏의 고급스러운 슬랙스.\n폴리에스터 혼방 소재로 구김이 적고 관리가 편합니다.\n오피스룩부터 캐주얼룩까지 다양하게 활용.")
            .price(BigDecimal.valueOf(65000)).discountPrice(BigDecimal.valueOf(52000))
            .stock(35).brand("OFFICE FIT").category(bottoms)
            .mainImage("https://images.unsplash.com/photo-1509631179647-0177331693ae?w=500").build());

        products.add(Product.builder()
            .name("미디 플리츠 스커트")
            .description("우아한 플리츠 디테일의 미디 기장 스커트.\n가볍고 드레이프감이 좋은 소재.\n블라우스, 니트, 어디에나 잘 어울리는 베이직 아이템.")
            .price(BigDecimal.valueOf(48000)).discountPrice(BigDecimal.valueOf(38000))
            .stock(32).brand("BLOOM").category(bottoms)
            .mainImage("https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa?w=500").build());

        products.add(Product.builder()
            .name("하이웨이스트 숏 데님 스커트")
            .description("트렌디한 하이웨이스트 데님 미니스커트.\n레트로한 무드의 빈티지 워싱 데님.\n스니커즈부터 힐까지 다양하게 매칭 가능.")
            .price(BigDecimal.valueOf(39000))
            .stock(40).brand("DENIM LAB").category(bottoms)
            .mainImage("https://images.unsplash.com/photo-1572201426051-b97ce4f0a98c?w=500").build());

        products.add(Product.builder()
            .name("조거 트레이닝 팬츠")
            .description("편안한 조거핏의 트레이닝 팬츠.\n부드러운 기모 소재로 집에서도, 밖에서도 활용 가능.\n허리 밴딩으로 사이즈 조절이 편리.")
            .price(BigDecimal.valueOf(35000)).discountPrice(BigDecimal.valueOf(28000))
            .stock(55).brand("COZY FIT").category(bottoms)
            .mainImage("https://images.unsplash.com/photo-1552902865-b72c031ac5ea?w=500").build());

        // ===== 아우터 (5) =====
        products.add(Product.builder()
            .name("울혼방 롱코트")
            .description("고급 울 60% 혼방 소재의 클래식한 롱코트.\n포멀한 자리부터 캐주얼 데이트까지 완벽한 아우터.\n안감이 있어 보온성이 뛰어납니다.")
            .price(BigDecimal.valueOf(189000)).discountPrice(BigDecimal.valueOf(149000))
            .stock(20).brand("CLASSIC").category(outerwear)
            .mainImage("https://images.unsplash.com/photo-1539533018447-63fcce2678e3?w=500").build());

        products.add(Product.builder()
            .name("린넨 블렌드 재킷")
            .description("시원한 린넨 혼방 소재의 오버핏 재킷.\n봄/여름 레이어드에 최적화된 아이템.\n단독 착용 또는 레이어드 모두 스타일리시.")
            .price(BigDecimal.valueOf(89000))
            .stock(15).brand("LINEN HOUSE").category(outerwear)
            .mainImage("https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=500").build());

        products.add(Product.builder()
            .name("퀼팅 패딩 점퍼")
            .description("보온성이 뛰어난 퀼팅 디자인의 숏 패딩 점퍼.\n가볍고 따뜻한 다운 충전재 사용.\n데일리 겨울 필수 아이템.")
            .price(BigDecimal.valueOf(129000)).discountPrice(BigDecimal.valueOf(99000))
            .stock(25).brand("WINTER LAB").category(outerwear)
            .mainImage("https://images.unsplash.com/photo-1544022613-e87ca75a784a?w=500").build());

        products.add(Product.builder()
            .name("데님 트러커 재킷")
            .description("클래식한 데님 소재의 트러커 재킷.\n어느 계절이나 레이어드하기 좋은 만능 아우터.\n워싱 데님으로 빈티지한 무드 연출.")
            .price(BigDecimal.valueOf(75000)).discountPrice(BigDecimal.valueOf(62000))
            .stock(30).brand("DENIM LAB").category(outerwear)
            .mainImage("https://images.unsplash.com/photo-1576871337622-98d48d1cf531?w=500").build());

        products.add(Product.builder()
            .name("체크 울 블레이저")
            .description("세련된 체크 패턴의 오버핏 블레이저.\n울 혼방 소재로 고급스러운 질감.\n오피스룩부터 데이트룩까지 다양하게 활용 가능.")
            .price(BigDecimal.valueOf(115000)).discountPrice(BigDecimal.valueOf(89000))
            .stock(18).brand("CLASSIC").category(outerwear)
            .mainImage("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500").build());

        // ===== 원피스 (5) =====
        products.add(Product.builder()
            .name("플로럴 미디 원피스")
            .description("사랑스러운 플로럴 패턴의 미디 원피스.\n가벼운 시폰 소재로 봄/여름에 딱입니다.\n허리 벨트 포함으로 다양한 스타일링 가능.")
            .price(BigDecimal.valueOf(69000))
            .stock(25).brand("BLOOM").category(dresses)
            .mainImage("https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=500").build());

        products.add(Product.builder()
            .name("새틴 슬립 드레스")
            .description("고급 새틴 소재의 미니 슬립 드레스.\n파티룩으로도 데일리룩으로도 완벽.\n블랙/아이보리/슬레이트 블루 3가지 색상.")
            .price(BigDecimal.valueOf(75000)).discountPrice(BigDecimal.valueOf(59000))
            .stock(18).brand("SILK & MORE").category(dresses)
            .mainImage("https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?w=500").build());

        products.add(Product.builder()
            .name("니트 미니 원피스")
            .description("보들보들한 니트 소재의 심플한 미니 원피스.\n단독 착용 또는 레이어드 모두 가능.\n부츠와 코디하면 완벽한 가을/겨울 룩.")
            .price(BigDecimal.valueOf(58000)).discountPrice(BigDecimal.valueOf(46000))
            .stock(28).brand("KNIT CO").category(dresses)
            .mainImage("https://images.unsplash.com/photo-1509631179647-0177331693ae?w=500").build());

        products.add(Product.builder()
            .name("스트라이프 셔츠 원피스")
            .description("클래식한 스트라이프 패턴의 셔츠 원피스.\n면 소재로 편안하고 활동적.\n허리 타이로 실루엣 조절 가능.")
            .price(BigDecimal.valueOf(62000))
            .stock(22).brand("MOMO").category(dresses)
            .mainImage("https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=500").build());

        products.add(Product.builder()
            .name("블랙 홀터넥 미디 드레스")
            .description("섹시하고 우아한 홀터넥 미디 드레스.\n저지 소재로 적당한 신축성.\n특별한 날의 파티룩으로 완벽.")
            .price(BigDecimal.valueOf(88000)).discountPrice(BigDecimal.valueOf(72000))
            .stock(15).brand("SILK & MORE").category(dresses)
            .mainImage("https://images.unsplash.com/photo-1563178406-4cdc2923acbc?w=500").build());

        // ===== 가방 (4) =====
        products.add(Product.builder()
            .name("미니 크로스백")
            .description("데일리로 활용하기 좋은 컴팩트한 미니 크로스백.\n부드러운 PU 가죽 소재로 고급스러운 외관.\n탈착 가능한 체인 스트랩 포함.")
            .price(BigDecimal.valueOf(49000)).discountPrice(BigDecimal.valueOf(39000))
            .stock(40).brand("BAG STUDIO").category(bags)
            .mainImage("https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=500").build());

        products.add(Product.builder()
            .name("캔버스 토트백")
            .description("실용적인 캔버스 소재의 대용량 토트백.\n A4 서류, 노트북도 쏙 들어가는 넉넉한 크기.\n분리형 내부 파우치 포함.")
            .price(BigDecimal.valueOf(35000))
            .stock(60).brand("ECO BAG").category(bags)
            .mainImage("https://images.unsplash.com/photo-1544816155-12df9643f363?w=500").build());

        products.add(Product.builder()
            .name("버킷백")
            .description("트렌디한 버킷 실루엣의 숄더백.\n부드러운 천연 가죽 소재.\n드로우스트링으로 개폐 가능.")
            .price(BigDecimal.valueOf(125000)).discountPrice(BigDecimal.valueOf(98000))
            .stock(12).brand("LEATHER CO").category(bags)
            .mainImage("https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=500").build());

        products.add(Product.builder()
            .name("백팩")
            .description("세련된 디자인의 데일리 백팩.\n방수 소재로 비 오는 날도 걱정 없음.\n노트북 전용 수납 공간 포함.")
            .price(BigDecimal.valueOf(79000)).discountPrice(BigDecimal.valueOf(65000))
            .stock(35).brand("BAG STUDIO").category(bags)
            .mainImage("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=500").build());

        // ===== 신발 (4) =====
        products.add(Product.builder()
            .name("클래식 화이트 스니커즈")
            .description("어디에나 잘 어울리는 클래식 화이트 스니커즈.\n가벼운 EVA 밑창으로 편안한 착화감.\n청바지, 원피스 모두 완벽 매칭.")
            .price(BigDecimal.valueOf(79000)).discountPrice(BigDecimal.valueOf(65000))
            .stock(45).brand("STRIDE").category(shoes)
            .mainImage("https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500").build());

        products.add(Product.builder()
            .name("블록힐 앵클 부츠")
            .description("안정감 있는 블록힐의 앵클 부츠.\n부드러운 합성 가죽 소재.\n지퍼로 쉽게 착탈 가능.")
            .price(BigDecimal.valueOf(95000)).discountPrice(BigDecimal.valueOf(78000))
            .stock(20).brand("HEEL STUDIO").category(shoes)
            .mainImage("https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=500").build());

        products.add(Product.builder()
            .name("스트랩 샌들")
            .description("여름에 딱인 스트랩 디자인의 플랫 샌들.\n발이 편한 아나토믹 인솔.\n다양한 컬러 운영.")
            .price(BigDecimal.valueOf(42000))
            .stock(50).brand("STRIDE").category(shoes)
            .mainImage("https://images.unsplash.com/photo-1562273138-f46be4ebdf33?w=500").build());

        products.add(Product.builder()
            .name("로퍼")
            .description("클래식하고 세련된 슬립온 로퍼.\n천연 가죽 소재로 오래 신어도 편안.\n오피스룩, 데이트룩 모두 잘 어울림.")
            .price(BigDecimal.valueOf(88000)).discountPrice(BigDecimal.valueOf(72000))
            .stock(25).brand("LEATHER CO").category(shoes)
            .mainImage("https://images.unsplash.com/photo-1533867617858-e7b97e060509?w=500").build());

        // ===== 액세서리 (3) =====
        products.add(Product.builder()
            .name("레이어드 목걸이 세트")
            .description("트렌디한 레이어드 목걸이 3종 세트.\n14k 골드 도금으로 변색 걱정 없음.\n커플/선물용으로도 인기 있는 아이템.")
            .price(BigDecimal.valueOf(28000)).discountPrice(BigDecimal.valueOf(22000))
            .stock(80).brand("GOLD TOUCH").category(accessories)
            .mainImage("https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500").build());

        products.add(Product.builder()
            .name("진주 귀걸이")
            .description("클래식한 담수 진주 귀걸이.\n925 실버 소재로 피부 자극이 없음.\n포멀한 자리부터 일상까지 잘 어울리는 아이템.")
            .price(BigDecimal.valueOf(32000))
            .stock(60).brand("PEARL CO").category(accessories)
            .mainImage("https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=500").build());

        products.add(Product.builder()
            .name("스카프")
            .description("부드러운 실크 혼방 소재의 멀티 스카프.\n목에 두르거나 가방에 달아도 세련된 포인트.\n다양한 패턴 운영.")
            .price(BigDecimal.valueOf(25000)).discountPrice(BigDecimal.valueOf(19000))
            .stock(70).brand("STYLE ADD").category(accessories)
            .mainImage("https://images.unsplash.com/photo-1601924994987-69e26d50dc26?w=500").build());

        productRepository.saveAll(products);
        log.info("Products initialized: {} items", products.size());
    }

    private void initUsers() {
        if (!userRepository.existsByEmail("admin@ghost.com")) {
            User admin = User.builder()
                    .email("admin@ghost.com")
                    .password(passwordEncoder.encode("admin1234!"))
                    .name("관리자").role(User.Role.ADMIN).build();
            userRepository.save(admin);
            cartRepository.save(Cart.builder().user(admin).build());
            log.info("Admin created: admin@ghost.com / admin1234!");
        }
        if (!userRepository.existsByEmail("test@test.com")) {
            User testUser = User.builder()
                    .email("test@test.com")
                    .password(passwordEncoder.encode("test1234!"))
                    .name("테스트유저").role(User.Role.USER).build();
            userRepository.save(testUser);
            cartRepository.save(Cart.builder().user(testUser).build());
            log.info("Test user created: test@test.com / test1234!");
        }
    }

    private void initCoupons() {
        if (couponRepository.count() > 0) return;
        List<Coupon> coupons = List.of(
            Coupon.builder().code("WELCOME10").name("신규가입 10% 할인")
                .discountType(Coupon.DiscountType.PERCENT).discountValue(BigDecimal.valueOf(10))
                .minOrderAmount(BigDecimal.valueOf(30000)).maxUses(9999).build(),
            Coupon.builder().code("SAVE5000").name("5,000원 할인쿠폰")
                .discountType(Coupon.DiscountType.FIXED).discountValue(BigDecimal.valueOf(5000))
                .minOrderAmount(BigDecimal.valueOf(50000)).maxUses(9999).build(),
            Coupon.builder().code("SUMMER20").name("여름맞이 20% 할인")
                .discountType(Coupon.DiscountType.PERCENT).discountValue(BigDecimal.valueOf(20))
                .minOrderAmount(BigDecimal.valueOf(80000)).maxUses(500).build()
        );
        couponRepository.saveAll(coupons);
        log.info("Coupons initialized. Codes: WELCOME10, SAVE5000, SUMMER20");
    }
}
