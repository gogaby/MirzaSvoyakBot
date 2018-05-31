package com.mirza.app;

import com.mirza.database.QuestionRepository;
import com.mirza.database.UserRepository;
import com.mirza.model.TopicSet;
import com.mirza.util.SiqUnzipper;
import com.mirza.util.SiqXmlPackageParser;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.*;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yach0217 on 29.05.2018.
 */
@Component
public class TelegramSchedulerBot extends AbstractTelegramBot{


    @Autowired
    private SiqUnzipper siqUnzipper;

    @Autowired
    private QuestionRepository  questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SiqXmlPackageParser  siqXmlPackageParser;

    Map<Integer,com.mirza.model.User> registeredUsers = new HashMap<>();
    Map<Integer,com.mirza.model.User> usersCache = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        if (!gameStarted) {
            Message message = update.getMessage();
            Chat chat = message.getChat();
            try {
                if (chat.isUserChat()) {
                    processAdminMessage(message);
                } else {
                    processUserMessage(message);
                }
            } catch (Exception e) {
                System.out.println(e.fillInStackTrace().getMessage());
            }

        }
    }

    private void processUserMessage(Message message) throws TelegramApiException {
        Long chatId = message.getChatId();
        switch (message.getText()){
            case "+":
                registerUser(message.getFrom(), chatId);
                break;
            case "-":
                unRegisterUser(message.getFrom(), chatId);
                break;
            case "старт":
                execute(new SendMessage(chatId, "Игра скоро начнется"));
                createGame(new Game(registeredUsers));
                break;
            case "список":
                execute(new SendMessage(chatId, "Список пакетов: \n" + StringUtils.join(questionRepository.getPackageList(),'\n')));
                break;
        }
    }

    

    private void registerUser(User from, Long chatId) throws TelegramApiException {
        if (!registeredUsers.containsKey(from.getId())){
            com.mirza.model.User user = usersCache.get(from.getId());
            if (user == null ){
                if (!userRepository.userExist(from)){
                    execute(new SendMessage(chatId, "Новый пользователь зарегистрирован в базе: ") );
                    userRepository.registerUser(from);
                }
                user = userRepository.getUser(from.getId());
                usersCache.put(user.getId(), user);
            }
            registeredUsers.put(user.getId(), user);
        }
        sendGameRegisterInfo(chatId);
    }

    private void unRegisterUser(User from, Long chatId) throws TelegramApiException {
        registeredUsers.remove(from.getId());
        sendGameRegisterInfo(chatId);
    }

    private void sendGameRegisterInfo(Long chatId) throws TelegramApiException {
        String players = StringUtils.join(registeredUsers.values().stream().map(user -> user.getName() + " (" +
                user.getRating() + ")").collect(Collectors.toList()), ", ");
        String message = "Стандартная игра\n" + "Тем: " + topicCount +  "\nИгроки: " + StringUtils.defaultString(players);
        execute(new SendMessage(chatId, message));
    }

    private void processAdminMessage(Message message) throws TelegramApiException, IOException {
        Document document = message.getDocument();
        if (document != null) {
            File file = this.getFile(document.getFileId());
            if (file == null || file.getFilePath() == null) {
                execute(new SendMessage(message.getChatId(),"Не удалось загрузить файл"));
                return;
            }
            try {
                String fullFileName = document.getFileName();
                String ext1 = FilenameUtils.getExtension(fullFileName);
                String fileName = FilenameUtils.removeExtension(fullFileName);
                if (!"siq".equals(ext1)) {
                    execute(new SendMessage(message.getChatId(),"Неизвестный файл"));
                    return;
                }
                if (questionRepository.packageExist(fileName)){
                    execute(new SendMessage(message.getChatId(),"Пакет уже существует!"));
                    return;
                }
                siqUnzipper.unzip(file.getFilePath());
                TopicSet topicSet = siqXmlPackageParser.parseSiqPackage(file.getFilePath(), fileName);
                topicSet.setShortName(fileName);
                questionRepository.savePackage(topicSet);
                execute(new SendMessage(message.getChatId(),"Пакет успешно добавлен"));
            } catch (Exception e) {
                execute(new SendMessage(message.getChatId(),"Не удалось загрузить файл"));
            }
        }
        if (message.getText() != null) {
            switch (message.getText()) {
                case "+":
                    TopicSet topicSet = questionRepository.getRandomTopicSet(5);
                    execute(new SendMessage(message.getChatId(), topicSet.getShortName()));
                    execute(new SendMessage(message.getChatId(), topicSet.getDescription()));
                    break;
                default:
                    execute(new SendMessage(message.getChatId(), "хуй"));
            }
        }
    }

    public File getFile(String fileId) {
        try {
            GetFile args = new GetFile();
            args.setFileId(fileId);
            return execute(args);
        } catch (TelegramApiException e) {
            return null;
            }
        }

    @Override
    public String getBotUsername() {
        return "MirzaSvoyakBot";
    }

    @Override
    public String getBotToken() {
        return "587789428:AAGeFuJToFte62bw8Okz-TS_G0DoPMR2tZU";
    }

}