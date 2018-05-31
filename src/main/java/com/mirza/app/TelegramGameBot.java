package com.mirza.app;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

/**
 * Created by yach0217 on 30.05.2018.
 */
@Component
public class TelegramGameBot extends AbstractTelegramBot {


    @Override
    public void onUpdateReceived(Update update) {
        if(game) {
            Message message = update.getMessage();
            Chat chat = message.getChat();
            try {
                switch (message.getText()){
                    case "стоп":
                        game = false;
                        break;
                    case "хуи":
                        execute(sendMessageRemoveKeyboard(chat.getId()));
                        break;
                    default:
                        execute(sendMsgKeyboard(chat.getId(), "Ваш ответ?", buttons));
                }
            } catch (Exception e) {
                System.out.println(e.fillInStackTrace().getMessage());
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "MirzaSvoyakGameBot";
    }

    @Override
    public String getBotToken() {
        return "598001641:AAHAvtHs5Q_o3EtXRMZjwmSFVsrlohHQQEc";
    }
}