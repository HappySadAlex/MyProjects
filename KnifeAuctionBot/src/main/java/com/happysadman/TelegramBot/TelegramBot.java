package com.happysadman.TelegramBot;

import com.happysadman.Config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    public TelegramBot(BotConfig config){
        this.config = config;
    }


    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String name = update.getMessage().getChat().getFirstName();

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, name);
                    break;
                default:
                    sendMessage(chatId, "There is no such command yet :(");
            }

        }

    }


    public void startCommandReceived(long chatId, String name){
        String answer = "Hello, " + name +", nice to meet you!";
        sendMessage(chatId, answer);
    }

    public void sendMessage(long chatId, String message){
        SendMessage send = new SendMessage();
        send.setChatId(chatId);
        send.setText(message);

        try{
            execute(send);
        }catch (TelegramApiException ignored){   }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }
}
