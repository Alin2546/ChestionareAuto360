package com.chestionare.chestionare360.Model.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinishDuelRequest {

    private Long duelId;
    private int player1Score;
    private int player2Score;


}
