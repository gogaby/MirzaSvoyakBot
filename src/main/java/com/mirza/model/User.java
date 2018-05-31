package com.mirza.model;

/**
 * Created by yach0217 on 31.05.2018.
 */
public class User extends org.telegram.telegrambots.api.objects.User{

    private int rating;
    private Integer wins;
    private Integer games;
    private Integer id;
    private String firstName;
    private String lastName;

    @Override
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getGames() {
        return games;
    }

    public void setGames(Integer games) {
        this.games = games;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void addWin(){
        if (wins != null){
            wins++;
        } else wins = 1;
    }

    public void addGame(){
        if (games != null){
            games++;
        } else games = 1;
    }

    public String getName() {
        if (lastName != null) {
            return firstName + " " + lastName;
        }
        return firstName;
    }


}