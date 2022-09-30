package com.yeoreodigm.server.domain;

public enum AccommodationType {
    PENSION("펜션", 1),
    MINBAK("민박", 2),
    MOTEL("모텔", 3),
    GUEST_HOUSE("게스트하우스", 4),
    HOTEL("호텔/콘도", 5),
    CAMPING("캠핑", 6),
    OTHERS("기타", 7);

    private final String kName;

    private final int index;

    AccommodationType(String kName, int index) {
        this.kName = kName;
        this.index = index;
    }

    public String getKName() { return kName; }

    public static AccommodationType getEnum(int index) {
        return switch (index) {
            case 1 -> PENSION;
            case 2 -> MINBAK;
            case 3 -> MOTEL;
            case 4 -> GUEST_HOUSE;
            case 5 -> HOTEL;
            case 6 -> CAMPING;
            case 7 -> OTHERS;
            default -> null;
        };
    }

}
