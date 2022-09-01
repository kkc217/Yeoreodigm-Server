package com.yeoreodigm.server.dto.comment;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Data
public class CommentDateTime {

    private String dateTime;

    public CommentDateTime(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        if (ChronoUnit.MONTHS.between(dateTime, now) > 0) {
            this.dateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        } else if (ChronoUnit.DAYS.between(dateTime, now) > 0) {
            this.dateTime = ChronoUnit.DAYS.between(dateTime, now) + "일 전";
        } else if (ChronoUnit.HOURS.between(dateTime, now) > 0) {
            this.dateTime = ChronoUnit.HOURS.between(dateTime, now) + "시간 전";
        } else {
            this.dateTime = ChronoUnit.MINUTES.between(dateTime, now) + "분 전";
        }
    }

}
