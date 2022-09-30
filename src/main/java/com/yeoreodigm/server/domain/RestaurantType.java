package com.yeoreodigm.server.domain;

public enum RestaurantType {
    CLOSURE("폐업", 0),
    WESTERN("양식", 1),
    CHINESE("중식", 2),
    BUNSIK("분식", 3),
    CAFE("카페/베이커리", 4),
    KOREAN("한식", 5),
    JAPANESE("일식", 6);

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
