package com.yeoreodigm.server.dto.accommodation;

import com.yeoreodigm.server.dto.PageResult;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Data
public class AccommodationListDto implements Serializable {

    private boolean hasPrev;

    private boolean hasNext;

    private int totalDay;

    private PageResult<List<AccommodationDto>> accommodations;

    public AccommodationListDto(int day, int totalDay, PageResult<List<AccommodationDto>> accommodations) {
        this.hasPrev = !Objects.equals(day, 1);
        this.hasNext = !Objects.equals(day, totalDay);
        this.totalDay = totalDay;
        this.accommodations = accommodations;
    }

}
