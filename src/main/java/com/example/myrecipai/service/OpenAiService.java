package com.example.myrecipai.service;

import com.theokanning.openai.OpenAiResponse;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.Image;
import com.theokanning.openai.messages.Message;
import com.theokanning.openai.messages.MessageRequest;
import com.theokanning.openai.runs.CreateThreadAndRunRequest;
import com.theokanning.openai.runs.Run;
import com.theokanning.openai.threads.Thread;
import com.theokanning.openai.threads.ThreadRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class OpenAiService {
    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${spring.image.directory}")
    private String imageDirectory;

    public String getMessage(String userMessage, String assistantId) throws InterruptedException {
        com.theokanning.openai.service.OpenAiService openAiService = new com.theokanning.openai.service.OpenAiService(openaiApiKey);

        List<MessageRequest> messageRequests = new ArrayList<>();
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setContent(userMessage);
        messageRequests.add(messageRequest);

        ThreadRequest threadRequest = new ThreadRequest();
        threadRequest.setMessages(messageRequests);

        CreateThreadAndRunRequest createThreadAndRunRequest = new CreateThreadAndRunRequest();
        createThreadAndRunRequest.setAssistantId(assistantId);
        createThreadAndRunRequest.setThread(threadRequest);

        Run run = openAiService.createThreadAndRun(createThreadAndRunRequest);
        Thread thread = openAiService.retrieveThread(run.getThreadId());

        while (run.getStatus().equals("queued") || run.getStatus().equals("in_progress")) {
            run = openAiService.retrieveRun(thread.getId(), run.getId());
            TimeUnit.SECONDS.sleep(1);
        }

        OpenAiResponse<Message> returnedMessages = openAiService.listMessages(thread.getId());
        String returnedValue = openAiService.retrieveMessage(run.getThreadId(), returnedMessages.firstId).getContent().get(0).getText().getValue();
        openAiService.deleteThread(run.getThreadId());

        return returnedValue;
    }

    public String getImage(String prompt) {
        com.theokanning.openai.service.OpenAiService openAiService = new com.theokanning.openai.service.OpenAiService(openaiApiKey);
        CreateImageRequest createImageRequest = new CreateImageRequest();
        createImageRequest.setPrompt(prompt);
        createImageRequest.setSize("512x512");
        Image image = openAiService.createImage(createImageRequest).getData().get(0);
        return image.getUrl();
    }

    public String saveGeneratedImage(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        String imageName = "image" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss")) + ".png";
        FileOutputStream fileOutputStream = new FileOutputStream(imageDirectory + imageName);
        FileChannel fileChannel = fileOutputStream.getChannel();
        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        return imageName;
    }
}
