package com.viber.bot;

import com.viber.bot.api.ViberBot;
import com.viber.bot.profile.BotProfile;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nullable;

@Configuration
@ConfigurationProperties(prefix = "application.viber-bot")
@Getter@Setter
public class BotConfiguration {

    private String authToken;


    private String name;

    @Nullable

    private String avatar;

    @Bean
    ViberBot viberBot() {
        return new ViberBot(new BotProfile(name, avatar), authToken);
    }

    @Bean
    ViberSignatureValidator signatureValidator() {
        return new ViberSignatureValidator(authToken);
    }
}
