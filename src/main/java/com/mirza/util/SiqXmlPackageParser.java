package com.mirza.util;

import com.mirza.model.Question;
import com.mirza.model.Topic;
import com.mirza.model.TopicSet;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by yach0217 on 23.05.2018.
 */
@SuppressWarnings(value = "all")
@Component
public class SiqXmlPackageParser {

    public class AtomTypes {
        private static final String VOICE = "voice";
        private static final String IMAGE = "image";
        private static final String TEXT = "text";
        private static final String SAY = "say";
        private static final String MARKER = "marker";

    }
    public TopicSet parseSiqPackage(String packageName, String name) {

        try {
            TopicSet topicSet = new TopicSet();
            UUID packageId = UUID.randomUUID();
            topicSet.setId(packageId);
            File inputFile = new File("siqPackages" + File.separator + packageName + File.separator + "content.xml");
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputFile);
            Element rootElement = document.getRootElement();
            Element info = rootElement.element("info");
            Element authors = info.element("authors");
            StringBuilder authorsString = null;
            if (authors != null) {
                authorsString = new StringBuilder("Aвторы: ");
                Iterator<Element> iterator = authors.elementIterator("author");
                while (iterator.hasNext()) {
                    Element author = iterator.next();
                    authorsString.append(author.getText()).append(", ");
                }
                authorsString = new StringBuilder(authorsString.substring(0, authorsString.length() - 2));

            }
            Element comments = info.element("comments");
            String commentsString = null;
            if (comments != null){
                commentsString = comments.getText();
            }
            StringBuilder description = new StringBuilder()
                    .append("Пакет: ").append(name).append(System.lineSeparator())
                    .append(StringUtils.defaultString(commentsString)).append(System.lineSeparator())
                    .append(authorsString);
            topicSet.setDescription(description.toString());
            List<Topic> topicList = new ArrayList<>();
            Element rounds = rootElement.element("rounds");
            Iterator<Element> roundIterator = rounds.elementIterator("round");
            for (int i = 0; i < 3; i++) {
                Element round = roundIterator.next();
                Element themes = round.element("themes");
                Iterator<Element> themeIterator = themes.elementIterator();
                while (themeIterator.hasNext()) {
                    Topic topic = new Topic();
                    UUID topicId = UUID.randomUUID();
                    topic.setId(topicId);
                    topic.setPackageId(packageId);
                    Element theme = themeIterator.next();
                    topic.setTopicName(theme.attribute("name").getText());
                    Element themeInfo = theme.element("info");
                    StringBuilder themeAuthorsString = null;
                    if (themeInfo != null) {
                        Element themeAuthors = themeInfo.element("authors");
                        if (themeAuthors != null) {
                            themeAuthorsString = new StringBuilder();
                            Iterator iterator = themeAuthors.elementIterator("author");
                            while (iterator.hasNext()) {
                                Element themeAuthor = (Element) iterator.next();
                                themeAuthorsString.append(themeAuthor.getText()).append(", ");
                            }
                            themeAuthorsString = new StringBuilder(themeAuthorsString.substring(0, themeAuthorsString.length() - 2));
                            topic.setAuthor(themeAuthorsString.toString());
                        }
                    }
                    Element questions = theme.element("questions");
                    Iterator questionIterator = questions.elementIterator("question");
                    int cost = 0;
                    List<Question> questionList = new ArrayList<>();
                    while (questionIterator.hasNext()){
                        Question question = new Question();
                        Element questionElement = (Element) questionIterator.next();
                        Element questionScenario = questionElement.element("scenario");
                        Iterator atomIterator = questionScenario.elementIterator("atom");
                        String questionText = "";
                        while (atomIterator.hasNext()){
                            Element atom = (Element) atomIterator.next();
                            boolean marker = false;
                            Attribute type = atom.attribute("type");
                            if (type == null) {
                                questionText+=atom.getText();
                            } else {
                                switch (type.getText()) {
                                    case AtomTypes.IMAGE:
                                        if (marker) {
                                            question.setAnswerPictureLink(atom.getText());
                                        } else {
                                            question.setImageLink(atom.getText());
                                        }
                                        break;
                                    case AtomTypes.MARKER:
                                        marker = true;
                                        break;

                                    case AtomTypes.SAY:
                                        questionText += atom.getText() + "\n";
                                        break;

                                    case AtomTypes.VOICE:
                                        question.setAudioLink(atom.getText());
                                        break;
                                    default:
                                        questionText += atom.getText();
                                        break;
                                }
                            }
                        }
                        question.setTopicId(topicId);
                        question.setQuestion(questionText);
                        Element right = questionElement.element("right");
                        Iterator rightAnswerIterator = right.elementIterator("answer");
                        List<String> rightAnswers = new ArrayList<>();
                        while (rightAnswerIterator.hasNext()){
                            Element answer = (Element) rightAnswerIterator.next();
                            rightAnswers.add(answer.getText());
                        }
                        question.setAnswers(rightAnswers);
                        Element wrong = questionElement.element("wrong");
                        if (wrong != null) {
                            Iterator wrongAnswerIterator = wrong.elementIterator("answer");
                            List<String> wrongAnswers = new ArrayList<>();

                            while (wrongAnswerIterator.hasNext()) {
                                Element answer = (Element) wrongAnswerIterator.next();
                                wrongAnswers.add(answer.getText());
                            }
                            question.setWrongAnswers(wrongAnswers);
                        }
                        Element questionInfo = questionElement.element("info");
                        if (questionInfo != null){
                            Element questionCommentsElenent = questionInfo.element("comments");
                            if (questionCommentsElenent != null) {
                                question.setComment(questionCommentsElenent.getText());
                            }
                        }
                        question.setId(UUID.randomUUID());
                        question.setCost(cost+=10);
                        questionList.add(question);
                    }

                    topic.setQuestions(questionList);
                    topicList.add(topic);
                }
            }
            topicSet.setTopics(topicList);
            return topicSet;
        } catch (DocumentException e) {
            throw new RuntimeException("Can't parse " + packageName + " package", e);
        }
    }
}