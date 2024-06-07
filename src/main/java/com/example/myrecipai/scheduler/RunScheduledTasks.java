package com.example.myrecipai.scheduler;

import com.example.myrecipai.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class RunScheduledTasks {
    @Autowired
    private RecipeService recipeService;
    @Scheduled(cron = "0 0 5 ? * *")
    public void fillMissingWeights() throws InterruptedException {
        recipeService.fillMissingWeight();
    }
    @Scheduled(cron = "0 0 6 ? * *")
    public void fillMissingVolumes() throws InterruptedException {
        recipeService.fillMissingVolume();
    }
    @Scheduled(cron = "0 0 7 ? * *")
    public void fillMissingTimeCalPrice() throws InterruptedException {
        recipeService.fillMissingTimeCalPrice();
    }
}
