package com.yeoreodigm.server.controller;

import com.yeoreodigm.server.domain.Member;
import com.yeoreodigm.server.domain.NoteAuthority;
import com.yeoreodigm.server.domain.Places;
import com.yeoreodigm.server.domain.TravelNote;
import com.yeoreodigm.server.dto.constraint.SessionConst;
import com.yeoreodigm.server.dto.note.CallNoteInfoResponseDto;
import com.yeoreodigm.server.service.PlaceService;
import com.yeoreodigm.server.service.TravelNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/note")
public class TravelNoteApiController {

    private final TravelNoteService travelNoteService;

    private final PlaceService placeService;

    @GetMapping("/{travelNoteId}")
    public CallNoteInfoResponseDto callNoteInfo(
            @PathVariable("travelNoteId") Long travelNoteId,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {
        TravelNote travelNote = travelNoteService.callNoteInfo(travelNoteId);

        NoteAuthority noteAuthority = travelNoteService.checkNoteAuthority(member, travelNoteId);

        if (noteAuthority == NoteAuthority.ROLE_OWNER) {
            //--------수정하기--------
            List<Places> placesRecommended = placeService.searchPlaces("폭포", 1, 4);
            //----------------------
            return new CallNoteInfoResponseDto(noteAuthority, travelNote, placesRecommended);
        } else if (noteAuthority == NoteAuthority.ROLE_COMPANION) {
            return new CallNoteInfoResponseDto(noteAuthority, travelNote);
        } else {
            return new CallNoteInfoResponseDto(noteAuthority, travelNote);
        }
    }

}
