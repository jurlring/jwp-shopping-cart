package woowacourse.shoppingcart.domain;

import woowacourse.shoppingcart.domain.Product;

public enum Products {

    COLD_BREW_MALT(new Product("콜드 브루 몰트", 4800,
            "https://image.istarbucks.co.kr/upload/store/skuimg/2021/02/[9200000001636]_20210225093600536.jpg")),
    VANILLA_CREAM_COLD_BREW(new Product("바닐라 크림 콜드 브루", 4500,
            "https://image.istarbucks.co.kr/upload/store/skuimg/2021/04/[9200000000487]_20210430112319040.jpg")),
    SIGNATURE_HOT_CHOCOLATE(new Product("시그니처 핫 초콜릿", 5500,
            "https://image.istarbucks.co.kr/upload/store/skuimg/2021/04/[72]_20210415140949967.jpg")),
    JEJU_COLD_BREW(new Product("제주 비자림 콜드 브루", 6500,
            "https://image.istarbucks.co.kr/upload/store/skuimg/2022/03/[9200000002672]_20220311105511600.jpg")),
    MINT_COLD_BREW(new Product("롤린 민트 초코 콜드 브루", 2800,
            "https://image.istarbucks.co.kr/upload/store/skuimg/2022/04/[9200000003988]_20220406113215251.jpg")),
    CHEER_UP(new Product("기운내라임", 4800,
            "https://image.istarbucks.co.kr/upload/store/skuimg/2021/04/[9300000002853]_20210419104333070.jpg")),
    COLD_BREW(new Product("콜드 브루", 1800,
            "https://image.istarbucks.co.kr/upload/store/skuimg/2021/04/[9200000000038]_20210430113202458.jpg")),
    LAVENDER(new Product("라벤더 카페 브레베", 5800,
            "https://image.istarbucks.co.kr/upload/store/skuimg/2022/04/[9200000004119]_20220412083025862.png")),
    RUM(new Product("럼 샷 코르타도", 5500,
            "https://image.istarbucks.co.kr/upload/store/skuimg/2021/02/[9200000001086]_20210225090838931.jpg")),
    BIANCO(new Product("사케라또 비안코 오버 아이스", 6800,
            "https://image.istarbucks.co.kr/upload/store/skuimg/2021/02/[9200000002095]_20210225095033382.jpg")),
    AFFOGARE(new Product("사케라또 아포가토", 4800,
            "https://image.istarbucks.co.kr/upload/store/skuimg/2021/03/[9200000003505]_20210322093241535.jpg")),
    FLAT_WHITE(new Product("바닐라 플랫 화이트", 5800,
            "https://image.istarbucks.co.kr/upload/store/skuimg/2021/04/[9200000002406]_20210415135507733.jpg"));


    private final Product product;

    Products(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }
}
