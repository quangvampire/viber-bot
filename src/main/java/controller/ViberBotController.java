package controller;

import com.viber.bot.api.ViberBot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping(value = "/viber-bot")
public class ViberBotController {
    @Inject
    private ViberBot viberBot;

    @GetMapping(value = "started")
    public void onConvertationStarted(){
        viberBot.onTextMessage("(hi|hello)", (event, message, response) -> response.send("Hi " + event.getSender().getName()));
    }
}
