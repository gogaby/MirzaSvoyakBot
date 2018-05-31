package com.mirza.app;

import com.mirza.model.Question;
import com.mirza.model.Topic;
import com.mirza.model.TopicSet;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by yach0217 on 31.05.2018.
 */
public class Game implements Runnable {

    private static final String[] PLUS = {"+"};
    private static final String[] YES_NO = {"да", "нет"};
    private static final String[] BREAK = {"да", "нет", "пауза"};
    private static final String[] PAUSED = {"да", "нет", "продолжить"};
    private static final long RATE = 100;
    private static final long INTERMISSION = 8000;
    private static final long SUCCESSIVE_QUESTION = 10000;
    private static final long FIRST_QUESTION = 15000;
    private static final long ANSWER = 30000;
    private static final String[] EMPTY = new String[0];
    private boolean paused;

    @Autowired
    TelegramGameBot gameBot;

    public int getCurrentTopic() {
        return topicId;
    }

    public State getStatus() {
        return state;
    }

    public enum State {
        BEFORE_TOPIC,
        BEFORE_FIRST_QUESTION,
        BEFORE_QUESTION,
        QUESTION,
        ANSWER,
        AFTER_QUESTION,
        SPECIAL_SCORE,
        JUDGE_DECISION,
        REGISTRATION,
        BEFORE_GAME,
        AFTER_GAME
    }

    private TopicSet set;
    private Topic currentTopic;
    private Question currentQuestion;
    private int topicId;
    private int stopAt;

    private Map<Integer, String> users = new HashMap<Integer, String>();
    private Map<Integer, Integer> score = new HashMap<Integer, Integer>();

    private Set<Integer> presentPlayers = new HashSet<>();
    private int minutesWaited;

    private List<Integer> answers;
    private int correct;

    private long actionExpires;
    private State state;
    private List<Integer> topics;

    private volatile Executor executor = Executors.newSingleThreadExecutor();

    private Timer timer;
    private int lastQuestionId;

    public Game() {
        executor.execute(() -> {
            Game.this.set = set;
            topicId = 0;
            stopAt = topics.size();
                state = State.BEFORE_GAME;
                sendMessage("Добро пожаловать", null, 60000);
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        executor.execute(Game.this);
                    }
                }, RATE, RATE);
        });
    }

    private void sendMessage(String message, String[] keyboard) {
        gameBot.sendMsgKeyboard(1L, message, keyboard);
    }

    private void startGame() {
        StringBuilder list = new StringBuilder();
        state = State.BEFORE_TOPIC;
    }

    private void sendMessage(String text, String[] keyboard, long delay) {
        actionExpires = Long.MAX_VALUE;
        gameBot.sendMsgKeyboard(1L, text, keyboard);
    }



    @Override
    public void run() {
        if (System.currentTimeMillis() >= actionExpires) {

        }
    }
    private class CallBack implements Consumer<Integer> {
        private long delay;

        public CallBack(long delay) {
            this.delay = delay;
        }

        @Override
        public void accept(Integer id) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (!paused || delay == 600000) {
                        actionExpires = System.currentTimeMillis() + delay;
                    }
                }
            });
        }
    }
}