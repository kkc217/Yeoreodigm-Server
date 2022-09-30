package com.yeoreodigm.server.domain;

public enum RestaurantType {
    CLOSURE("폐업", 1),
    WESTERN("양식", 2),
    CHINESE("중식", 3),
    BUNSIK("분식", 4),
    CAFE("카페/베이커리", 5),
    KOREAN("한식", 6),
    JAPANESE("일식", 7);

    private final String kName;

    private final int index;

    RestaurantType(String kName, int index) {
        this.kName = kName;
        this.index = index;
    }

    public String getKName() { return kName; }

    public static RestaurantType getEnum(int index) {
        return switch (index) {
            case 0 -> CLOSURE;
            case 1 -> WESTERN;
            case 2 -> CHINESE;
            case 3 -> BUNSIK;
            case 4 -> CAFE;
            case 5 -> KOREAN;
            case 6 -> JAPANESE;
            default -> null;
        };
    }

}
