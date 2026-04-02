package com.ghosttracker.shop.init;

import com.ghosttracker.shop.entity.*;
import com.ghosttracker.shop.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    // 상품 데이터 버전: 이 숫자를 올리면 재시드
    private static final int DATA_VERSION = 2;

    @Override
    @Transactional
    public void run(String... args) {
        initCategories();
        initProducts();
        initUsers();
        initCoupons();
    }

    private void initCategories() {
        if (categoryRepository.count() > 0) return;
        List<Category> categories = List.of(
            Category.builder().name("상의").slug("tops").description("티셔츠, 블라우스, 니트, 맨투맨").displayOrder(1).build(),
            Category.builder().name("하의").slug("bottoms").description("팬츠, 스커트, 청바지, 슬랙스").displayOrder(2).build(),
            Category.builder().name("아우터").slug("outerwear").description("코트, 재킷, 패딩, 블레이저").displayOrder(3).build(),
            Category.builder().name("원피스").slug("dresses").description("캐주얼, 포멀, 미디, 맥시 원피스").displayOrder(4).build(),
            Category.builder().name("가방").slug("bags").description("숄더백, 백팩, 크로스백, 토트백").displayOrder(5).build(),
            Category.builder().name("신발").slug("shoes").description("스니커즈, 부츠, 로퍼, 샌들").displayOrder(6).build(),
            Category.builder().name("액세서리").slug("accessories").description("목걸이, 귀걸이, 반지, 스카프").displayOrder(7).build()
        );
        categoryRepository.saveAll(categories);
        log.info("Categories initialized.");
    }

    private void initProducts() {
        // DATA_VERSION 기반으로 재시드 판단
        long count = productRepository.count();
        if (count > 0 && count >= 30) {
            // 이미 최신 데이터 존재 — 첫 번째 상품의 설명 길이로 판단
            Product first = productRepository.findAll().get(0);
            if (first.getDescription() != null && first.getDescription().length() > 200) {
                log.info("Products already seeded with v{} data ({} items). Skipping.", DATA_VERSION, count);
                return;
            }
            // 구버전 데이터 → 삭제 후 재시드
            log.info("Re-seeding products with v{} data...", DATA_VERSION);
            productRepository.deleteAll();
        }

        Category tops       = categoryRepository.findBySlug("tops").orElseThrow();
        Category bottoms    = categoryRepository.findBySlug("bottoms").orElseThrow();
        Category outerwear  = categoryRepository.findBySlug("outerwear").orElseThrow();
        Category dresses    = categoryRepository.findBySlug("dresses").orElseThrow();
        Category bags       = categoryRepository.findBySlug("bags").orElseThrow();
        Category shoes      = categoryRepository.findBySlug("shoes").orElseThrow();
        Category accessories= categoryRepository.findBySlug("accessories").orElseThrow();

        List<Product> products = new ArrayList<>();

        // ===========================
        //    상의 (7)
        // ===========================
        products.add(Product.builder()
            .name("오버핏 스트라이프 셔츠")
            .description("깔끔한 스트라이프 패턴이 돋보이는 오버핏 셔츠입니다. 면 100% 소재로 부드러운 촉감과 뛰어난 통기성을 자랑하며, 사계절 내내 편안하게 착용할 수 있습니다.\n\n넉넉한 오버핏 실루엣으로 체형 커버가 용이하고, 슬랙스나 데님 팬츠 위에 프론트 인 스타일링을 하면 세련된 캐주얼 룩을 완성할 수 있습니다. 단독 착용은 물론 가디건이나 니트 안에 레이어드 아이템으로도 훌륭합니다.\n\n소재: 면 100% / 세탁: 미지근한 물 단독 세탁 권장\n모델 착용 사이즈: M (모델 키 173cm, 55kg)")
            .price(BigDecimal.valueOf(39000)).discountPrice(BigDecimal.valueOf(29000))
            .stock(50).brand("MOMO").category(tops)
            .mainImage("https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("소프트 크롭 니트 스웨터")
            .description("부드러운 아크릴·울 혼방 소재로 제작된 크롭 니트 스웨터입니다. 살짝 짧은 기장감이 하이웨이스트 하의와 찰떡 궁합을 자랑하며, 여성스러운 실루엣을 연출해줍니다.\n\n적당한 두께감으로 봄·가을 간절기에 단독 착용하기 좋고, 겨울에는 코트나 패딩 안에 레이어드하면 보온성과 스타일을 동시에 잡을 수 있습니다. 피부에 닿아도 따갑지 않은 논-이치(non-itch) 원사를 사용했습니다.\n\n소재: 아크릴 60%, 울 30%, 나일론 10%\n세탁: 손세탁 또는 드라이클리닝 권장\n색상: 아이보리, 베이지, 핑크, 네이비, 차콜")
            .price(BigDecimal.valueOf(55000)).discountPrice(BigDecimal.valueOf(44000))
            .stock(40).brand("KNIT CO").category(tops)
            .mainImage("https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("베이직 라운드넥 반팔 티셔츠")
            .description("매일 입고 싶은 기본 중의 기본, 라운드넥 반팔 티셔츠입니다. 20수 싱글 코튼 원단을 사용하여 적당한 두께감과 부드러운 촉감을 구현했으며, 잦은 세탁에도 쉽게 늘어나지 않는 내구성을 갖추고 있습니다.\n\n남녀 모두 착용 가능한 유니섹스 핏으로, 단독 착용 시 깔끔하고 재킷이나 셔츠 아래 이너로 활용해도 좋습니다. 총 7가지 컬러(블랙, 화이트, 그레이, 네이비, 베이지, 카키, 라벤더)를 준비했으니 취향에 맞게 선택하세요.\n\n소재: 면 100% (20수 싱글) / 세탁: 30°C 이하 세탁기 사용 가능\n핏: 레귤러핏 / 사이즈: XS~XXL")
            .price(BigDecimal.valueOf(19000))
            .stock(100).brand("BASIC LAB").category(tops)
            .mainImage("https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("플로럴 프린트 시폰 블라우스")
            .description("화사한 플로럴 프린트가 돋보이는 시폰 블라우스입니다. 봄부터 여름까지 시즌 내내 활용할 수 있으며, 가벼운 시폰 소재 특유의 드레이프감이 여성스러운 무드를 연출합니다.\n\n앞면의 V넥 디자인이 얼굴 라인을 갸름하게 보여주고, 퍼프 소매 디테일이 어깨 라인을 부드럽게 감싸줍니다. 슬랙스와 매치하면 오피스 룩으로, 데님 스커트와 함께하면 페미닌 데이트 룩으로 활용할 수 있습니다.\n\n소재: 폴리에스터 100% (시폰) / 안감: 있음\n세탁: 손세탁 권장 / 다림질: 중온\n색상: 크림 플로럴, 핑크 플로럴, 블루 플로럴")
            .price(BigDecimal.valueOf(45000)).discountPrice(BigDecimal.valueOf(36000))
            .stock(35).brand("BLOOM").category(tops)
            .mainImage("https://images.unsplash.com/photo-1434389677669-e08b4cac3105?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("기모 크롭 맨투맨")
            .description("포근한 기모 안감이 더해진 크롭 기장 맨투맨입니다. 부드럽고 따뜻한 착용감으로 쌀쌀한 날씨에도 편안하게 입을 수 있으며, 하이웨이스트 팬츠나 스커트와 매칭 시 다리가 길어 보이는 비율 효과를 줍니다.\n\n소매와 밑단에 리브 밴딩 처리가 되어 있어 착용 시 단정한 실루엣을 유지하며, 운동할 때나 편한 외출 시 모두 활용하기 좋은 데일리 아이템입니다.\n\n소재: 면 80%, 폴리에스터 20% (내부 기모)\n세탁: 뒤집어서 세탁 권장 / 건조기 사용 불가\n색상: 블랙, 화이트, 멜란지 그레이, 크림")
            .price(BigDecimal.valueOf(42000)).discountPrice(BigDecimal.valueOf(33000))
            .stock(45).brand("COZY FIT").category(tops)
            .mainImage("https://images.unsplash.com/photo-1556821840-3a63f15732ce?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("린넨 오버사이즈 셔츠")
            .description("천연 린넨(아마) 소재로 제작된 시원한 오버사이즈 셔츠입니다. 린넨 특유의 살짝 거친 질감과 자연스러운 구김이 빈티지한 멋을 더해주며, 뛰어난 흡습성과 통기성으로 한여름에도 쾌적하게 착용할 수 있습니다.\n\n오버사이즈 핏으로 원피스처럼 단독 착용하거나, 이너 위에 가볍게 걸쳐 레이어드 룩을 연출하기에도 좋습니다. 세탁할수록 더 부드러워지는 린넨의 특성을 느껴보세요.\n\n소재: 린넨 100% / 세탁: 찬물 손세탁 권장\n색상: 내추럴 베이지, 화이트, 라이트 블루\n*린넨 소재 특성상 자연스러운 네프(매듭)가 있을 수 있으며, 이는 불량이 아닙니다.")
            .price(BigDecimal.valueOf(52000))
            .stock(28).brand("LINEN HOUSE").category(tops)
            .mainImage("https://images.unsplash.com/photo-1598554747436-c9293d6a588f?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("캐시미어 터틀넥 니트")
            .description("고급 캐시미어 혼방 원사로 제작된 클래식 터틀넥 니트입니다. 울과 캐시미어의 조합으로 가볍지만 보온성이 뛰어나며, 피부에 닿는 느낌이 매우 부드럽습니다.\n\n슬림핏 실루엣으로 코트나 재킷 안에 레이어드 했을 때 깔끔한 라인이 살아나며, 단독 착용 시에도 고급스러운 룩을 완성합니다. 목 부분의 터틀넥은 두 번 접어서 착용하거나 한 번 접어 자연스럽게 연출할 수 있습니다.\n\n소재: 울 50%, 캐시미어 20%, 나일론 30%\n세탁: 반드시 드라이클리닝 / 보관 시 접어서 보관 (행거 불가)\n색상: 블랙, 아이보리, 카멜, 차콜, 버건디")
            .price(BigDecimal.valueOf(62000)).discountPrice(BigDecimal.valueOf(49000))
            .stock(22).brand("KNIT CO").category(tops)
            .mainImage("https://images.unsplash.com/photo-1620799140408-edc6dcb6d633?w=600&h=800&fit=crop&q=80").build());

        // ===========================
        //    하의 (5)
        // ===========================
        products.add(Product.builder()
            .name("슬림핏 스트레치 데님 팬츠")
            .description("고신축 스트레치 원단을 사용하여 활동성을 극대화한 슬림핏 데님 팬츠입니다. 면과 스판덱스의 최적 배합으로 데님 특유의 멋은 살리면서도 편안한 착용감을 제공합니다.\n\n슬림한 핏이 다리 라인을 깔끔하게 정리해주며, 허리 안쪽에 밴딩이 숨겨져 있어 장시간 앉아 있어도 불편함이 없습니다. 티셔츠, 셔츠, 재킷 등 어떤 상의와도 무난하게 어울리는 필수 베이직 아이템입니다.\n\n소재: 면 98%, 스판덱스 2% / 세탁: 찬물 단독 세탁 (색상 이염 주의)\n색상: 인디고 블루, 워싱 블루, 블랙\n핏: 슬림핏 / 기장: 30~34인치 선택 가능")
            .price(BigDecimal.valueOf(59000)).discountPrice(BigDecimal.valueOf(49000))
            .stock(30).brand("DENIM LAB").category(bottoms)
            .mainImage("https://images.unsplash.com/photo-1542272604-787c3835535d?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("와이드 핏 세미 슬랙스")
            .description("깔끔한 핏의 와이드 세미 슬랙스입니다. 폴리에스터·레이온 혼방 소재로 고급스러운 광택감이 있으며, 구김이 적어 관리가 매우 편리합니다.\n\n허리 뒷면에 밴딩이 들어가 있어 편안한 착용감을 제공하면서도 앞면은 깔끔한 핏을 유지합니다. 오피스 룩부터 캐주얼 데일리까지 다양한 스타일에 활용 가능하며, 블라우스·니트·재킷 등 어떤 상의와도 잘 어울립니다.\n\n소재: 폴리에스터 65%, 레이온 30%, 스판덱스 5%\n세탁: 세탁기 사용 가능 (약탈수) / 다림질: 중온\n색상: 블랙, 차콜, 베이지, 네이비, 카키")
            .price(BigDecimal.valueOf(65000)).discountPrice(BigDecimal.valueOf(52000))
            .stock(35).brand("OFFICE FIT").category(bottoms)
            .mainImage("https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("미디 플리츠 스커트")
            .description("우아한 플리츠(주름) 디테일이 돋보이는 미디 기장 스커트입니다. 걸을 때마다 자연스럽게 흔들리는 플리츠가 여성스러운 무드를 극대화하며, 가벼운 소재로 계절에 구애받지 않고 착용할 수 있습니다.\n\n허리 부분은 밴딩 처리되어 있어 사이즈 선택 걱정 없이 편안하게 착용 가능하며, 블라우스·니트·티셔츠 등 다양한 상의와 매치하여 페미닌·캐주얼·세미포멀 룩을 연출할 수 있습니다.\n\n소재: 폴리에스터 100% / 안감: 있음\n세탁: 손세탁 권장 (플리츠 유지를 위해 세탁망 사용)\n색상: 블랙, 베이지, 카키, 머스타드, 스카이블루")
            .price(BigDecimal.valueOf(48000)).discountPrice(BigDecimal.valueOf(38000))
            .stock(32).brand("BLOOM").category(bottoms)
            .mainImage("https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("하이웨이스트 A라인 데님 스커트")
            .description("레트로한 감성의 하이웨이스트 A라인 데님 스커트입니다. 허리 라인을 높게 잡아주어 다리가 길어 보이는 효과가 있으며, A라인 실루엣이 힙 라인을 자연스럽게 커버해줍니다.\n\n미디움 워싱 처리된 데님으로 빈티지한 분위기를 연출하며, 앞면에 메탈 버튼 디테일이 포인트가 됩니다. 크롭 상의나 넣어 입는 스타일링과 완벽하게 어울리며, 스니커즈부터 로퍼, 힐까지 다양한 신발과 매칭 가능합니다.\n\n소재: 면 99%, 스판덱스 1% / 세탁: 찬물 단독 세탁\n길이: 미니(43cm) / 색상: 미디엄 워싱, 라이트 워싱")
            .price(BigDecimal.valueOf(39000))
            .stock(40).brand("DENIM LAB").category(bottoms)
            .mainImage("https://images.unsplash.com/photo-1582142306909-195724d33ffc?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("조거 트레이닝 팬츠")
            .description("편안함을 극대화한 조거 핏 트레이닝 팬츠입니다. 안감에 부드러운 기모 처리가 되어 있어 가을·겨울에 따뜻하게 착용할 수 있으며, 허리 드로스트링과 밑단 리브 밴딩으로 핏 조절이 자유롭습니다.\n\n운동할 때는 물론, 집에서 편하게 쉴 때나 간단한 외출 시에도 활용도가 높은 아이템입니다. 양 옆 포켓과 뒷면 패치 포켓까지 넉넉한 수납 공간을 제공합니다.\n\n소재: 면 80%, 폴리에스터 20% (내부 기모)\n세탁: 세탁기 사용 가능 / 건조기 사용 불가 (수축 주의)\n색상: 블랙, 멜란지 그레이, 네이비, 차콜")
            .price(BigDecimal.valueOf(35000)).discountPrice(BigDecimal.valueOf(28000))
            .stock(55).brand("COZY FIT").category(bottoms)
            .mainImage("https://images.unsplash.com/photo-1552902865-b72c031ac5ea?w=600&h=800&fit=crop&q=80").build());

        // ===========================
        //    아우터 (5)
        // ===========================
        products.add(Product.builder()
            .name("울 블렌드 클래식 롱코트")
            .description("고급 울 60% 혼방 원단으로 제작된 클래식 롱코트입니다. 무릎 아래까지 오는 넉넉한 기장감으로 겨울철 보온성이 뛰어나며, 안감이 있어 바람막이 효과도 우수합니다.\n\n더블 버튼 디자인과 넓은 카라가 고급스러운 인상을 주며, 허리 벨트를 활용해 다양한 실루엣을 연출할 수 있습니다. 출퇴근 룩부터 포멀한 자리까지 두루 활용 가능한 시즌 필수 아이템입니다.\n\n소재: 울 60%, 폴리에스터 40% / 안감: 있음\n세탁: 드라이클리닝 전용\n색상: 카멜, 블랙, 차콜, 베이지\n핏: 레귤러핏 / 기장: 약 108cm(M 기준)")
            .price(BigDecimal.valueOf(189000)).discountPrice(BigDecimal.valueOf(149000))
            .stock(20).brand("CLASSIC").category(outerwear)
            .mainImage("https://images.unsplash.com/photo-1539533018447-63fcce2678e3?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("린넨 블렌드 라이트 재킷")
            .description("린넨과 면의 혼방 소재로 통기성이 뛰어난 라이트 재킷입니다. 봄·여름 간절기에 에어컨 바람이나 선선한 저녁 바람을 막아줄 가벼운 아우터로 제격입니다.\n\n릴랙스드 핏으로 이너에 셔츠나 티셔츠를 자유롭게 매칭할 수 있으며, 앞면의 플랩 포켓 디테일이 캐주얼한 멋을 더합니다. 자연스러운 린넨의 질감이 시간이 갈수록 빈티지한 매력을 더해줍니다.\n\n소재: 린넨 55%, 면 45% / 안감: 없음\n세탁: 찬물 손세탁 / 건조: 그늘 건조\n색상: 내추럴 베이지, 라이트 카키, 아이보리")
            .price(BigDecimal.valueOf(89000))
            .stock(15).brand("LINEN HOUSE").category(outerwear)
            .mainImage("https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("경량 구스다운 숏패딩")
            .description("가볍지만 보온성이 뛰어난 경량 구스다운 숏패딩입니다. 프리미엄 회색 구스 충전재(FP 700)를 사용하여 얇은 두께에도 체온을 효과적으로 유지하며, 발수 가공된 외피로 가벼운 눈이나 비에도 안심할 수 있습니다.\n\n숏 기장으로 활동성이 좋고, 접어서 보관할 수 있는 경량 파우치가 포함되어 있어 휴대가 간편합니다. 이너 또는 아우터로 다양하게 활용하세요.\n\n충전재: 구스다운 90%, 스몰페더 10% (FP 700)\n외피: 나일론 100% (발수 코팅) / 안감: 나일론 100%\n세탁: 중성세제 손세탁 / 건조: 낮은 온도 텀블 건조\n색상: 블랙, 네이비, 올리브, 아이보리")
            .price(BigDecimal.valueOf(129000)).discountPrice(BigDecimal.valueOf(99000))
            .stock(25).brand("WINTER LAB").category(outerwear)
            .mainImage("https://images.unsplash.com/photo-1544022613-e87ca75a784a?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("데님 트러커 재킷")
            .description("클래식 데님 트러커 재킷입니다. 12온스 코튼 데님 원단을 사용하여 적당한 두께감과 견고한 내구성을 갖추었으며, 미디엄 빈티지 워싱 처리로 멋스러운 색감을 표현했습니다.\n\n어깨와 가슴 라인은 여유 있게, 허리 부분은 적당히 잡아주는 클래식 트러커 핏이 남녀 모두에게 잘 어울립니다. 후드티, 플란넬 셔츠, 스트라이프 티 등 다양한 이너와의 레이어드를 통해 매번 다른 무드를 연출할 수 있습니다.\n\n소재: 면 100% (12oz 데님) / 세탁: 찬물 뒤집어 단독 세탁\n메탈 버튼: 골드 안티크 / 색상: 미디엄 워싱, 라이트 워싱, 블랙 워싱")
            .price(BigDecimal.valueOf(75000)).discountPrice(BigDecimal.valueOf(62000))
            .stock(30).brand("DENIM LAB").category(outerwear)
            .mainImage("https://images.unsplash.com/photo-1576871337622-98d48d1cf531?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("체크 패턴 울 블레이저")
            .description("세련된 글렌 체크 패턴의 오버핏 블레이저입니다. 울 혼방 소재 특유의 고급스러운 질감과 클래식한 체크 패턴이 조화를 이루어, 단벌 아이템으로도 세련된 스타일링이 가능합니다.\n\n어깨 패드가 적절히 들어가 있어 구조적인 실루엣을 유지하면서도 오버핏 디자인으로 편안한 착용감을 제공합니다. 슬랙스와 매치하면 포멀한 셋업 룩으로, 데님과 함께하면 스마트 캐주얼 룩으로 연출할 수 있습니다.\n\n소재: 울 50%, 폴리에스터 50% / 안감: 폴리에스터 100%\n세탁: 드라이클리닝 전용\n색상: 그레이 체크, 브라운 체크, 네이비 체크")
            .price(BigDecimal.valueOf(115000)).discountPrice(BigDecimal.valueOf(89000))
            .stock(18).brand("CLASSIC").category(outerwear)
            .mainImage("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=600&h=800&fit=crop&q=80").build());

        // ===========================
        //    원피스 (5)
        // ===========================
        products.add(Product.builder()
            .name("플로럴 미디 원피스")
            .description("싱그러운 꽃 패턴이 돋보이는 봄·여름 시즌 미디 원피스입니다. 가벼운 시폰 소재와 안감 구성으로 비침 걱정 없이 착용할 수 있으며, 허리 셔링과 함께 제공되는 패브릭 벨트로 다양한 실루엣을 연출할 수 있습니다.\n\nV넥 디자인이 쇄골 라인을 아름답게 보여주고, 약간의 퍼프가 가미된 반팔 소매가 사랑스러운 무드를 더합니다. 플랫슈즈, 스트랩 샌들, 로퍼 등 다양한 슈즈와 잘 어울립니다.\n\n소재: 폴리에스터 100% (시폰) / 안감: 있음\n세탁: 손세탁 권장 / 길이: 약 115cm(M 기준)\n색상: 크림 플라워, 네이비 플라워, 핑크 플라워")
            .price(BigDecimal.valueOf(69000))
            .stock(25).brand("BLOOM").category(dresses)
            .mainImage("https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("새틴 슬립 드레스")
            .description("고급 새틴 소재의 슬립 드레스입니다. 실크처럼 부드러운 촉감과 은은한 광택이 우아하고 세련된 분위기를 연출하며, 조절 가능한 스파게티 스트랩으로 원하는 핏을 맞출 수 있습니다.\n\n바이어스 컷(사선 재단)으로 제작되어 몸의 곡선을 자연스럽게 감싸주며, 무릎 아래 미디 기장으로 다양한 자리에 착용할 수 있습니다. 재킷이나 카디건과 레이어드하면 데일리 룩으로, 단독 착용 시 파티나 특별한 날의 드레스 룩으로 활용하세요.\n\n소재: 폴리에스터 97%, 스판덱스 3% (새틴)\n세탁: 손세탁 또는 드라이클리닝 / 다림질 불가\n색상: 블랙, 아이보리, 슬레이트 블루, 와인")
            .price(BigDecimal.valueOf(75000)).discountPrice(BigDecimal.valueOf(59000))
            .stock(18).brand("SILK & MORE").category(dresses)
            .mainImage("https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("니트 피팅 미니 원피스")
            .description("보들보들한 니트 소재의 피팅 미니 원피스입니다. 몸에 자연스럽게 밀착되는 핏으로 여성스러운 바디라인을 연출하며, 적당한 신축성이 있어 활동하기에도 불편함이 없습니다.\n\n가을에는 단독 착용, 겨울에는 롱코트나 무스탕 안에 레이어드하여 시즌 내내 활용할 수 있는 만능 아이템입니다. 롱부츠, 앵클부츠와 함께 매칭하면 완벽한 가을·겨울 룩이 완성됩니다.\n\n소재: 아크릴 50%, 나일론 30%, 울 20%\n세탁: 손세탁 또는 드라이클리닝 / 길이: 약 82cm(M 기준)\n색상: 블랙, 베이지, 브라운, 차콜")
            .price(BigDecimal.valueOf(58000)).discountPrice(BigDecimal.valueOf(46000))
            .stock(28).brand("KNIT CO").category(dresses)
            .mainImage("https://images.unsplash.com/photo-1496747611176-843222e1e57c?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("스트라이프 셔츠 원피스")
            .description("클래식한 스트라이프 패턴의 셔츠 원피스입니다. 면 100% 소재로 쾌적하고 편안한 착용감을 제공하며, 카라와 단추 디테일이 단정하면서도 스타일리시한 무드를 연출합니다.\n\n동봉된 패브릭 벨트로 허리를 잡아주면 라인이 살아나는 A라인 실루엣을 만들 수 있고, 벨트 없이 루즈하게 입으면 편안한 캐주얼 룩이 됩니다. 단독으로 입거나 레깅스·팬츠 위에 걸쳐서 다양하게 스타일링하세요.\n\n소재: 면 100% / 세탁: 세탁기 사용 가능 (30°C 이하)\n길이: 약 100cm(M 기준) / 색상: 블루 스트라이프, 블랙 스트라이프")
            .price(BigDecimal.valueOf(62000))
            .stock(22).brand("MOMO").category(dresses)
            .mainImage("https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("홀터넥 이브닝 드레스")
            .description("우아하고 세련된 홀터넥 디자인의 이브닝 드레스입니다. 목 뒤에서 묶는 홀터넥 구조가 쇄골과 어깨 라인을 아름답게 드러내며, 스트레치 저지 소재로 편안한 착용감과 세련된 핏을 동시에 제공합니다.\n\n무릎 아래 미디 기장으로 격식 있는 자리에 잘 어울리며, 등 부분의 트임 디테일이 뒤태까지 아름답게 연출합니다. 작은 클러치백과 스틸레토 힐을 더하면 완벽한 이브닝 룩이 완성됩니다.\n\n소재: 폴리에스터 92%, 스판덱스 8% (저지)\n세탁: 드라이클리닝 권장 / 길이: 약 120cm(M 기준)\n색상: 블랙, 딥네이비, 버건디")
            .price(BigDecimal.valueOf(88000)).discountPrice(BigDecimal.valueOf(72000))
            .stock(15).brand("SILK & MORE").category(dresses)
            .mainImage("https://images.unsplash.com/photo-1566174053879-31528523f8ae?w=600&h=800&fit=crop&q=80").build());

        // ===========================
        //    가방 (4)
        // ===========================
        products.add(Product.builder()
            .name("미니 체인 크로스백")
            .description("데일리 사용에 최적화된 컴팩트 사이즈의 미니 크로스백입니다. 부드러운 PU 레더 소재에 고급스러운 메탈 체인 스트랩이 달려 있어, 캐주얼한 차림에도 고급스러운 포인트를 더해줍니다.\n\n내부에는 카드 슬롯과 오픈 포켓이 있어 스마트폰, 카드, 립스틱 등 필수 소지품을 깔끔하게 수납할 수 있습니다. 체인 스트랩은 탈착 가능하여 클러치백으로도 활용할 수 있습니다.\n\n소재: PU 레더 / 하드웨어: 골드 또는 실버 선택 가능\n크기: W 20 × H 14 × D 6cm / 스트랩 길이: 약 120cm\n색상: 블랙, 아이보리, 베이지, 와인")
            .price(BigDecimal.valueOf(49000)).discountPrice(BigDecimal.valueOf(39000))
            .stock(40).brand("BAG STUDIO").category(bags)
            .mainImage("https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("캔버스 데일리 토트백")
            .description("넉넉한 수납력의 캔버스 토트백입니다. 16온스 두꺼운 캔버스 원단을 사용하여 가벼우면서도 튼튼하며, A4 서류·13인치 노트북·텀블러까지 넉넉하게 들어가는 대용량 사이즈입니다.\n\n내부에 지퍼 포켓과 오픈 포켓이 있어 소지품을 깔끔하게 정리할 수 있으며, 분리형 패브릭 파우치가 포함되어 있어 소소한 물건들을 따로 보관할 수 있습니다. 통학·통근·쇼핑 등 다목적으로 활용하기 좋은 에코 프렌들리 아이템입니다.\n\n소재: 면 100% (16oz 캔버스) / 세탁: 찬물 손세탁 (건조 후 약간 수축 가능)\n크기: W 42 × H 35 × D 15cm / 손잡이 길이: 약 55cm\n색상: 내추럴, 블랙, 올리브")
            .price(BigDecimal.valueOf(35000))
            .stock(60).brand("ECO BAG").category(bags)
            .mainImage("https://images.unsplash.com/photo-1544816155-12df9643f363?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("레더 버킷백")
            .description("트렌디한 버킷 실루엣의 레더 숄더백입니다. 소프트 카우하이드(소가죽) 소재로 제작되어 사용할수록 자연스러운 에이징이 진행되며 나만의 빈티지한 색감으로 변해갑니다.\n\n드로스트링(조임끈)으로 입구를 여닫는 구조로 소지품을 안전하게 보관할 수 있으며, 내부에는 지퍼 포켓과 핸드폰 전용 포켓이 있어 정리가 편합니다. 어깨에 걸쳐도, 손에 들어도 스타일리시한 투웨이 디자인입니다.\n\n소재: 카우하이드 (소가죽) / 내피: 면 100%\n크기: W 26 × H 28 × D 18cm / 스트랩 길이: 약 105cm (조절 가능)\n색상: 탄 브라운, 블랙, 카멜")
            .price(BigDecimal.valueOf(125000)).discountPrice(BigDecimal.valueOf(98000))
            .stock(12).brand("LEATHER CO").category(bags)
            .mainImage("https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("노트북 수납 데일리 백팩")
            .description("세련된 외관에 실용성을 더한 데일리 백팩입니다. 방수 코팅 원단을 사용하여 갑작스러운 비에도 내부 소지품을 안전하게 보호하며, 15.6인치 노트북 전용 수납 공간과 다수의 정리 포켓이 마련되어 있어 수납력이 뛰어납니다.\n\n인체공학적 등판 패딩과 조절 가능한 어깨 스트랩으로 장시간 착용에도 편안하며, 사이드 물병 포켓과 숨겨진 뒷면 지퍼 포켓까지 갖추고 있어 통학·통근·여행 등 모든 상황에 최적화된 가방입니다.\n\n소재: 폴리에스터 100% (방수 코팅)\n크기: W 30 × H 42 × D 14cm / 무게: 약 680g\n색상: 블랙, 네이비, 차콜, 올리브그린")
            .price(BigDecimal.valueOf(79000)).discountPrice(BigDecimal.valueOf(65000))
            .stock(35).brand("BAG STUDIO").category(bags)
            .mainImage("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=600&h=800&fit=crop&q=80").build());

        // ===========================
        //    신발 (4)
        // ===========================
        products.add(Product.builder()
            .name("클래식 화이트 스니커즈")
            .description("어디에나 잘 어울리는 미니멀 디자인의 클래식 화이트 스니커즈입니다. 깔끔한 올화이트 컬러에 가죽 느낌의 합성 소재를 사용하여 고급스러우면서도 관리가 편리합니다.\n\n가벼운 EVA 미드솔이 장시간 보행에도 편안한 쿠셔닝을 제공하며, 고무 아웃솔이 미끄러짐을 방지합니다. 청바지·슬랙스·원피스·스커트 등 어떤 하의와도 자연스럽게 어울리는 만능 슈즈입니다.\n\n소재: 합성 가죽 / 안감: 텍스타일 / 밑창: 고무 + EVA\n세탁: 젖은 천으로 닦아 관리 / 워셔블(세탁기) 불가\n사이즈: 220~280mm (5mm 단위)")
            .price(BigDecimal.valueOf(79000)).discountPrice(BigDecimal.valueOf(65000))
            .stock(45).brand("STRIDE").category(shoes)
            .mainImage("https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("스웨이드 앵클 부츠")
            .description("부드러운 스웨이드 소재의 앵클 부츠입니다. 6cm 블록힐이 안정감 있는 보행을 도와주며, 사이드 지퍼로 편하게 신고 벗을 수 있습니다.\n\n발목을 감싸주는 기장이 다리 라인을 길어 보이게 하며, 슬림 팬츠·스커트·원피스 등 다양한 스타일에 매치 가능합니다. 가을부터 초봄까지 시즌 내내 활용도가 높은 아이템입니다.\n\n소재: 스웨이드 (합성) / 안감: 텍스타일 / 밑창: TPR\n굽 높이: 약 6cm (블록힐) / 총 높이: 약 15cm\n사이즈: 225~255mm / 색상: 블랙, 카키, 브라운")
            .price(BigDecimal.valueOf(95000)).discountPrice(BigDecimal.valueOf(78000))
            .stock(20).brand("HEEL STUDIO").category(shoes)
            .mainImage("https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("레더 스트랩 샌들")
            .description("깔끔한 레더 스트랩 디자인의 플랫 샌들입니다. 발을 감싸주는 두 줄 스트랩이 안정감 있는 착화감을 제공하며, 버클로 사이즈를 미세하게 조절할 수 있습니다.\n\n쿠션감 있는 아나토미컬 인솔(해부학적 인솔)이 발의 아치를 지지해 장시간 걸어도 편안하며, 천연 가죽 소재가 발에 빠르게 적응합니다. 여름 여행·데일리·리조트 룩에 두루 활용할 수 있는 아이템입니다.\n\n소재: 천연 가죽(상단), 합성 고무(밑창)\n사이즈: 220~260mm / 색상: 탄 브라운, 블랙, 내추럴\n관리: 가죽 전용 크림으로 관리 권장")
            .price(BigDecimal.valueOf(42000))
            .stock(50).brand("STRIDE").category(shoes)
            .mainImage("https://images.unsplash.com/photo-1562273138-f46be4ebdf33?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("클래식 페니 로퍼")
            .description("클래식한 디자인의 페니 로퍼입니다. 매끈한 천연 가죽 어퍼와 전통적인 페니 슬롯 디테일이 세련되고 단정한 인상을 주며, 오랜 시간 신어도 편안한 쿠셔닝을 제공합니다.\n\n슬립온 구조로 신고 벗기가 간편하며, 슬랙스·청바지·치노 팬츠·스커트 등 다양한 하의와 매칭하여 스마트 캐주얼·비즈니스 캐주얼·데일리 룩에 활용할 수 있습니다. 시간이 갈수록 가죽이 발에 맞게 길들여져 더욱 편안해집니다.\n\n소재: 천연 가죽(소가죽) / 안감: 가죽 / 밑창: 라바 솔\n사이즈: 225~275mm / 색상: 블랙, 브라운, 버건디\n관리: 슈즈 크림 정기 도포 권장")
            .price(BigDecimal.valueOf(88000)).discountPrice(BigDecimal.valueOf(72000))
            .stock(25).brand("LEATHER CO").category(shoes)
            .mainImage("https://images.unsplash.com/photo-1533867617858-e7b97e060509?w=600&h=800&fit=crop&q=80").build());

        // ===========================
        //    액세서리 (3)
        // ===========================
        products.add(Product.builder()
            .name("레이어드 골드 목걸이 세트")
            .description("트렌디한 레이어드 스타일의 골드 목걸이 3종 세트입니다. 14K 골드 도금(금장) 처리되어 물·땀에 강하고 변색이 거의 없으며, 니켈프리(Nickel-Free) 소재로 민감한 피부에도 안심하고 착용할 수 있습니다.\n\n체인 길이가 각각 38cm, 42cm, 50cm로 구성되어 있어 원하는 조합으로 레이어드하거나 단독으로 착용해도 예쁩니다. 데일리 액세서리로 활용하거나, 선물용으로도 인기 있는 아이템입니다. 전용 파우치 포함.\n\n소재: 스테인리스 스틸 + 14K 골드 도금\n체인 길이: 38cm / 42cm / 50cm (각각 5cm 연장 체인 포함)\n색상: 골드, 로즈골드, 실버 / 포장: 전용 벨벳 파우치 포함")
            .price(BigDecimal.valueOf(28000)).discountPrice(BigDecimal.valueOf(22000))
            .stock(80).brand("GOLD TOUCH").category(accessories)
            .mainImage("https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("담수 진주 드롭 귀걸이")
            .description("우아한 담수 진주를 사용한 드롭형 귀걸이입니다. 6~7mm 크기의 내추럴 담수 진주가 귀 아래에서 자연스럽게 흔들려 은은한 우아함을 더해주며, 925 스털링 실버 소재로 변색과 피부 자극 걱정이 없습니다.\n\n가벼운 무게감으로 장시간 착용에도 귀가 아프지 않으며, 데일리·오피스·포멀 등 어떤 룩에도 자연스럽게 어울리는 만능 액세서리입니다. 각 진주의 모양과 광택이 미세하게 다를 수 있으나, 이는 천연 소재 특유의 매력입니다.\n\n소재: 925 스털링 실버 + 담수 진주 (6~7mm)\n전체 길이: 약 3cm / 무게: 약 2g (한쪽)\n포장: 전용 케이스 포함")
            .price(BigDecimal.valueOf(32000))
            .stock(60).brand("PEARL CO").category(accessories)
            .mainImage("https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=600&h=800&fit=crop&q=80").build());

        products.add(Product.builder()
            .name("실크 멀티 스카프")
            .description("부드러운 실크 혼방 소재의 멀티 스카프입니다. 천연 실크의 은은한 광택감이 어떤 스타일에든 고급스러운 포인트를 더해주며, 다양한 착용 방법으로 활용도가 무궁무진합니다.\n\n목에 두르면 클래식한 스카프로, 머리에 묶으면 레트로 헤어밴드로, 가방 핸들에 감으면 스타일리시한 가방 장식으로 변신합니다. 봄·여름에는 가벼운 자외선 차단용으로, 가을·겨울에는 코트와 함께 스타일링 포인트로 활용하세요.\n\n소재: 실크 70%, 폴리에스터 30% / 크기: 70 × 70cm\n세탁: 드라이클리닝 권장 (물세탁 시 색빠짐 주의)\n패턴: 기하학, 페이즐리, 플로럴, 스트라이프")
            .price(BigDecimal.valueOf(25000)).discountPrice(BigDecimal.valueOf(19000))
            .stock(70).brand("STYLE ADD").category(accessories)
            .mainImage("https://images.unsplash.com/photo-1601924994987-69e26d50dc26?w=600&h=800&fit=crop&q=80").build());

        productRepository.saveAll(products);
        log.info("Products initialized (v{}): {} items", DATA_VERSION, products.size());
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
                .minOrderAmount(BigDecimal.valueOf(30000)).maxUses(9999)
                .expiryDate(LocalDateTime.of(2027, 12, 31, 23, 59, 59)).build(),
            Coupon.builder().code("SAVE5000").name("5,000원 할인쿠폰")
                .discountType(Coupon.DiscountType.FIXED).discountValue(BigDecimal.valueOf(5000))
                .minOrderAmount(BigDecimal.valueOf(50000)).maxUses(9999)
                .expiryDate(LocalDateTime.of(2027, 12, 31, 23, 59, 59)).build(),
            Coupon.builder().code("SUMMER20").name("여름맞이 20% 할인")
                .discountType(Coupon.DiscountType.PERCENT).discountValue(BigDecimal.valueOf(20))
                .minOrderAmount(BigDecimal.valueOf(80000)).maxUses(500)
                .expiryDate(LocalDateTime.of(2026, 9, 30, 23, 59, 59)).build()
        );
        couponRepository.saveAll(coupons);
        log.info("Coupons initialized. Codes: WELCOME10, SAVE5000, SUMMER20");
    }
}
