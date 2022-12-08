package com.yeoreodigm.server.domain;

public enum AccommodationType {
    PENSION("펜션", 1),
    GUEST_HOUSE("게스트하우스", 2),
    HOTEL("호텔/콘도", 3),
    MINBAK("민박", 4),
    MOTEL("모텔", 5),
    CAMPING("캠핑", 6),
    OTHERS("기타", 7);

    private final String kName;

    private final int index;

    AccommodationType(String kName, int index) {
        this.kName = kName;
        this.index = index;
    }

    public String getKName() { return kName; }

    public int getIndex() { return index; }

    public static AccommodationType getEnum(int index) {
        return switch (index) {
            case 1 -> PENSION;
            case 2 -> GUEST_HOUSE;
            case 3 -> HOTEL;
            case 4 -> MINBAK;
            case 5 -> MOTEL;
            case 6 -> CAMPING;
            case 7 -> OTHERS;
            default -> null;
        };
    }

}
