package com.college.events.dto;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class ScoreSubmissionRequest {
    private Long eventId;
    private String teamName;
    private Integer scoreValue;
    private Long judgeId;

    // Explicit Getters to guarantee compilation even if Lombok plugins freeze
    public Long getEventId() { return this.eventId; }
    public String getTeamName() { return this.teamName; }
    public Integer getScoreValue() { return this.scoreValue; }
    public Long getJudgeId() { return this.judgeId; }
}