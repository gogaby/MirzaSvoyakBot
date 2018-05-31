package com.mirza.database;

import com.mirza.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by yach0217 on 31.05.2018.
 */
@Repository
public class UserRepository {


    private static final String select_user = "select * from users where users.id = ? ";
    private static final String register_user = "insert into users(id, first_name, last_name, wins, played, rating) values (?,?,?,0,0,1500)";

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public boolean userExist(org.telegram.telegrambots.api.objects.User user) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(select_user, user.getId());
        return sqlRowSet.next();
    }

    @Transactional
    public void registerUser(org.telegram.telegrambots.api.objects.User user) {
       jdbcTemplate.update(register_user, user.getId(), user.getFirstName(), user.getLastName());
    }

    @Transactional
    public User getUser(Integer id) {
        User user = new User();
        jdbcTemplate.query(select_user, new Object[]{id}, resultSet -> {
            user.setId(resultSet.getInt("id"));
            user.setFirstName(resultSet.getString("first_name"));
            user.setLastName(resultSet.getString("last_name"));
            user.setGames(resultSet.getInt("played"));
            user.setRating(resultSet.getInt("rating"));
            user.setWins(resultSet.getInt("wins"));
        });
        return user;
    }
}