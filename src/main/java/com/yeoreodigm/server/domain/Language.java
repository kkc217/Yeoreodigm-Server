package com.yeoreodigm.server.domain;

public enum Language {

    KO("한국어", 1),
    EN("English", 2),
    ZH("汉语", 3);

    private final String kName;

    private final int index;

    Language(String kName, int index) {
        this.kName = kName;
        this.index = index;
    }

    public String getKName() {
        return kName;
    }

    public int getIndex() {
        return index;
    }

    public static Language getEnum(String name) {
        return switch (name.toUpperCase()) {
            case "EN" -> EN;
            case "ZH" -> ZH;
            default -> KO;
        };
    }

}
