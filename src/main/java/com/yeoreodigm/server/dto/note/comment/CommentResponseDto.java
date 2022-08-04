package com.yeoreodigm.server.dto.note.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentResponseDto {

    private Long commentId;

    private LocalDateTime createdTime;

}
