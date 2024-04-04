package com.example.myrecipai.controller;

import com.example.myrecipai.service.ChatGptService;
import com.theokanning.openai.assistants.Assistant;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {
    @Autowired
    private ChatGptService chatGptService;
    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.api.assistant.cups-to-grams}")
    private String assistCupsToGrams;

    @Value("${openai.api.assistant.grams-to-cups}")
    private String assistGramsToCups;

    @GetMapping("/assistant")
    public Assistant assistant(@RequestParam String assistantId){
        OpenAiService openAiService = new OpenAiService(openaiApiKey);
        return openAiService.retrieveAssistant(assistantId);
    }

    @GetMapping("/cupsToGrams")
    public String cupsToGrams(@RequestParam String msg) throws InterruptedException {
        return chatGptService.getMessage(msg, assistCupsToGrams);
    }

    @GetMapping("/gramsToCups")
    public String gramsToCups(@RequestParam String msg) throws InterruptedException {
        return chatGptService.getMessage(msg, assistGramsToCups);
    }

    @GetMapping("/image")
    public String image(@RequestParam String prompt){
        return chatGptService.getImage(prompt);
    }
}
