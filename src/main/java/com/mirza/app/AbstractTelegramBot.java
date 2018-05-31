package com.mirza.app;

import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yach0217 on 31.05.2018.
 */
public abstract class AbstractTelegramBot extends TelegramLongPollingBot{

    String[] buttons = {"хуи", "пики"};

    static boolean game = false;

    public SendMessage sendMsgKeyboard(Long chatId, String text, String[] buttons) {
        SendMessage sendMessage = new SendMessage();
      //  sendMessage.enableMarkdown(true);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        sendMessage.setParseMode(ParseMode.HTML);
       // replyKeyboardMarkup.setSelective(true);
      //  replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        List <KeyboardButton> keyboardButtons =  Arrays.stream(buttons).map(KeyboardButton::new).collect(Collectors.toList());
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.addAll(keyboardButtons);
        keyboard.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        return sendMessage;
    }

    public SendMessage sendMessageRemoveKeyboard(Long chatId){
        SendMessage sendMessage = new SendMessage();
        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Верно!");
        sendMessage.setReplyMarkup(replyKeyboardRemove);
        sendMessage.setParseMode(ParseMode.HTML);
        return sendMessage;
    }




}