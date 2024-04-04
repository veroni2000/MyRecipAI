package com.example.myrecipai.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatGptAssistantDTO {
    private String message;
    private String assistantId;
}
