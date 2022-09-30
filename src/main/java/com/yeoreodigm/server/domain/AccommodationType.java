package com.yeoreodigm.server.domain;

public enum AccommodationType {
    OTHERS("기타", 1),
    PENSION("펜션", 2),
    MINBAK("민박", 3),
    MOTEL("모텔", 4),
    GUEST_HOUSE("게스트하우스", 5),
    HOTEL("호텔/콘도", 6),
    CAMPING("캠핑", 7);

    private final String kName;

    private final int index;

    AccommodationType(String kName, int index) {
        this.kName = kName;
        this.index = index;
    }

    public String getKName() { return kName; }

    public static AccommodationType getEnum(int index) {
        return switch (index) {
            case 0 -> OTHERS;
            case 1 -> PENSION;
            case 2 -> MINBAK;
            case 3 -> MOTEL;
            case 4 -> GUEST_HOUSE;
            case 5 -> HOTEL;
            case 6 -> CAMPING;
            default -> null;
        };
    }

}
