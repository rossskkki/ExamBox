package com.siki.exambox.config;

import com.siki.exambox.Service.AssService;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiCofig {

    @Autowired
    private OpenAiChatModel openAiChatModel;

    public AssService assService(){
        AssService as = AiServices.builder(AssService.class)
                .chatModel(openAiChatModel)
                .build();
        return as;
    }
}
