package com.yeoreodigm.server.dto.detail;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TravelNoteDetailInfo {

    private String title;

    private String period;

    private List<String> region;

    private List<String> theme;

}
