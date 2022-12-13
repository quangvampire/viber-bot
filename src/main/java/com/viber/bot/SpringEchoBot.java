package com.viber.bot;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.CharStreams;
import com.google.common.util.concurrent.Futures;
import com.viber.bot.Request;
import com.viber.bot.ViberSignatureValidator;
import com.viber.bot.api.ViberBot;
import com.viber.bot.message.TextMessage;
import com.viber.bot.profile.BotProfile;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationListener;

import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RestController
@SpringBootApplication
public class SpringEchoBot extends SpringBootServletInitializer implements ApplicationListener<ApplicationReadyEvent>{

    @Inject
    private ViberBot bot;

    @Inject
    private Environment environment;

    @Inject
    private ViberSignatureValidator signatureValidator;

    private Class<Application> applicationClass = Application.class;
    private String webhookUrl =  "https://18fa-2001-ee0-1a58-555b-e5a4-502e-b7a8-f74a.ap.ngrok.io";



    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
        return application.sources(applicationClass);
    }

    @Override
    @Async
    public void onApplicationEvent(ApplicationReadyEvent appReadyEvent) {
        try {
            bot.setWebhook(webhookUrl).get();
            System.out.println(" bot start");
        } catch (Exception e) {
            e.printStackTrace();
        }


        bot.onMessageReceived((event, message, response) -> {
            System.out.println("message : " + message);
            response.send(message);
        }); // echos everything back

        bot.onConversationStarted(event -> Futures.immediateFuture(Optional.of( // send 'Hi UserName' when conversation is started
                new TextMessage("Hi " + event.getUser().getName()))));

        System.out.println("event : " + bot.getAccountInfo());
        bot.onSubscribe((listener,event) -> {
            System.out.println("listener : " + listener);
            System.out.println("event : " + event);
        });
    }
    public static void main(String[] args) {
        SpringApplication.run(SpringEchoBot.class, args);
    }


    @PostMapping(value = "/", produces = "application/json")
    public String incoming(@RequestBody String json,
                           @RequestHeader("X-Viber-Content-Signature") String serverSideSignature)
            throws ExecutionException, InterruptedException, IOException {
        Preconditions.checkState(signatureValidator.isSignatureValid(serverSideSignature, json), "invalid signature");
        @Nullable InputStream response = bot.incoming(Request.fromJsonString(json)).get();
        return response != null ? CharStreams.toString(new InputStreamReader(response, Charsets.UTF_16)) : null;
    }
    @GetMapping(value = "")
    public String getString(){
        return "Hello";
    }
}