package com.example.house.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SuggestionDTO {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("community_id")
    private String communityId;
}