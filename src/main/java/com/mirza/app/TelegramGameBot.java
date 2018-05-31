package com.mirza.app;

import com.mirza.database.QuestionRepository;
import com.mirza.model.TopicSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

/**
 * Created by yach0217 on 30.05.2018.
 */
@Component
public class TelegramGameBot extends AbstractTelegramBot {

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public void onUpdateReceived(Update update) {
         Message message = update.getMessage();
         Chat chat = message.getChat();
            try {
                switch (message.getText()){
                    case "старт":
                        Thread.sleep(3000);
                        if (gameState == GameState.START_IN_PROGRESS){
                            TopicSet topicSet = questionRepository.getRandomTopicSet(topicCount);
                            execute(new SendMessage(chatId, "Игра началась"));

                        }
                        //get gameInfo
                        break;
                    case "стоп":
                        gameStarted = false;
                        execute(new SendMessage(chatId, "Игра остановлена"));
                        break;
                    default:
                     //   execute(sendMsgKeyboard(chat.getId(), "Ваш ответ?", buttons));
                }
            } catch (Exception e) {
                System.out.println(e.fillInStackTrace().getMessage());
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