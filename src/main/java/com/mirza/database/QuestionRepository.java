package com.mirza.database;
import com.mirza.model.Question;
import com.mirza.model.Topic;
import com.mirza.model.TopicSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by yach0217 on 29.05.2018.
 */

@Repository
public class QuestionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    DataSourceTransactionManager dataSourceTransactionManager;


    private static final String check_package = "select * from packages where short_name = ? limit 1";
    private static final String insert_package = "INSERT INTO packages VALUES (?, ?, ?)";
    private static final String insert_topic = "INSERT INTO topics (id, name, author, package_id) VALUES (?, ?, ?, ?)";
    private static final String insert_question = "INSERT INTO questions (id, question_text, music_link, image_link, amswer_image_link, comment, answers, wrong_answers, cost, theme_id)\n" +
                                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";


    private static final String select_random_package = "select * from packages order by random() limit 1";
    private static final String select_filled_package = "SELECT * FROM questions JOIN (SELECT topics.id as topic_id,\n" +
            "  topics.name as topic_name,\n" +
            "  topics.author as topic_author,\n" +
            "  topics.package_id as package_id,\n" +
            "  tmp.name as package_name,\n" +
            "  tmp.short_name as package_short_name\n" +
            "                              from topics JOIN (select * from packages ORDER BY random() LIMIT 1)tmp ON topics.package_id = tmp.id LIMIT ?)tmp2\n" +
            "  on questions.theme_id = tmp2.topic_id ORDER BY theme_id, cost;";
    private static final String select_all_packages = "select short_name from packages";
    private static final String select_topics = "select * from topics where package_id = ? LIMIT ?";
    private static final String select_questions = "select * from questions WHERE theme_id = ? ORDER BY cost ";


    @Transactional(propagation = Propagation.REQUIRED)
    public void savePackage(TopicSet topicSet){
        System.out.println("START");
        jdbcTemplate.update(insert_package, topicSet.getId(), topicSet.getDescription(), topicSet.getShortName());
        List<Question> questions = new ArrayList<>();
        topicSet.getTopics().forEach(topic -> questions.addAll(topic.getQuestions()));
        saveTopics(topicSet.getTopics());
        saveQuestions(questions);
        System.out.println("STOP");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void saveTopics(List<Topic> topics){
       jdbcTemplate.batchUpdate(insert_topic, new BatchPreparedStatementSetter() {
           @Override
           public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
               Topic topic = topics.get(i);
               preparedStatement.setObject(1, topic.getId());
               preparedStatement.setString(2, topic.getTopicName());
               preparedStatement.setString(3, topic.getAuthor());
               preparedStatement.setObject(4, topic.getPackageId());
           }

           @Override
           public int getBatchSize() {
               return topics.size();
           }
       });
        try {
            jdbcTemplate.getDataSource().getConnection().close();
        } catch (SQLException e) {

        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    private void saveQuestions(List<Question> questions){
        jdbcTemplate.batchUpdate(insert_question, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                Question question = questions.get(i);
                preparedStatement.setObject(1, question.getId());
                preparedStatement.setString(2, question.getQuestion());
                preparedStatement.setString(3, question.getAudioLink());
                preparedStatement.setString(4, question.getImageLink());
                preparedStatement.setString(5, question.getAnswerPictureLink());
                preparedStatement.setString(6, question.getComment());
                preparedStatement.setString(7, String.join("###", question.getAnswers()));
                if (question.getWrongAnswers() != null) {
                    preparedStatement.setString(8, String.join("###", question.getWrongAnswers()));
                } else preparedStatement.setArray(8, null);
                preparedStatement.setInt(9,question.getCost());
                preparedStatement.setObject(10, question.getTopicId());
            }
            @Override
            public int getBatchSize() {
                return questions.size();
            }
        });

    }


    public boolean packageExist(String fileName){
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(check_package, fileName);
        return sqlRowSet.next();
    }


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public TopicSet getRandomTopicSet(Integer topicCount){
        TopicSet topicSet = new TopicSet();
        jdbcTemplate.query(select_random_package, resultSet -> {
            topicSet.setId((UUID) resultSet.getObject("id"));
            topicSet.setShortName(resultSet.getString("short_name"));
            topicSet.setDescription(resultSet.getString("name"));
        });
        topicSet.setTopics(getTopics(topicSet.getId(), topicCount));
        return topicSet;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Topic> getTopics(UUID packageId, Integer topicCount){
        List<Topic> topics = new ArrayList<>();
        jdbcTemplate.query(select_topics,new Object[]{packageId, topicCount}, resultSet -> {
         Topic topic = new Topic();
         topic.setId((UUID) resultSet.getObject("id"));
         topic.setPackageId((UUID) resultSet.getObject("package_id"));
         topic.setTopicName(resultSet.getString("name"));
         topic.setAuthor(resultSet.getString("author"));
         topics.add(topic);
         List<Question> questions = new ArrayList<>();
         jdbcTemplate.query(select_questions, new Object[]{topic.getId()}, new RowCallbackHandler() {
             @Override
             public void processRow(ResultSet resultSet) throws SQLException {
                 Question question = new Question();
                 question.setId((UUID) resultSet.getObject("id"));
                 question.setTopicId((UUID) resultSet.getObject("theme_id"));
                 question.setCost(resultSet.getInt("cost"));
                 question.setQuestion(resultSet.getString("question_text"));
                 question.setAnswers(Arrays.asList((resultSet.getString("answers")).split("###")));
                 String wrong_answers = resultSet.getString("wrong_answers");
                 if (wrong_answers != null) {
                 question.setWrongAnswers(Arrays.asList(wrong_answers.split("###")));
                 }
                 question.setImageLink(resultSet.getString("image_link"));
                 question.setAudioLink(resultSet.getString("music_link"));
                 question.setComment(resultSet.getString("comment"));
                 question.setAnswerPictureLink(resultSet.getString("amswer_image_link"));
                 questions.add(question);
             }
         });
         topic.setQuestions(questions);
        });
        return topics;
    }



    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<String> getPackageList(){
        List<String> packageList = new ArrayList<>();
        jdbcTemplate.query(select_all_packages, resultSet -> {
            packageList.add(resultSet.getString("short_name"));

        });
        return packageList;
    }

}