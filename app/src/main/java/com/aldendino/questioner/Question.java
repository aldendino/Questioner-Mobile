package com.aldendino.questioner;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.*;
import java.io.*;

/**
 * A representation of a basic question, consisting of a question, answer, and the option of a hint.
 */
public class Question implements Serializable {
    private String question; // The question.
    private String answer; // The answer.

    /**
     * Default constructor.
     */
    public Question() {
        this.question = null;
        this.answer = null;
    }

    /**
     * For manual creation.
     * @param question the question.
     * @param answer the answer.
     */
    public Question(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    /**
     * Get the question.
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Get the answer.
     * @return the answer.
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Parse a file for question, answer, and possibly hint tuple.
     * @param input the XML string to parse.
     * @return an ArrayList of the questions parsed from the XML file.
     * @throws java.io.IOException if the file cannot be read correctly.
     * @throws javax.xml.parsers.ParserConfigurationException if there was a parser configuration error.
     * @throws org.xml.sax.SAXException if there was a SAX error.
     */
    public static ArrayList<Question> parseXML(String input) throws IOException, ParserConfigurationException, SAXException {
        ArrayList<Question> questionArray = new ArrayList<Question>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(input)));
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if(node instanceof Element) {
                Question question = new Question();
                NodeList childNodes = node.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node childNode = childNodes.item(j);
                    if (childNode instanceof Element) {
                        String content = childNode.getLastChild().
                                getTextContent().trim();
                        String nodeName = childNode.getNodeName();
                        if (nodeName.equals("question")) {
                            question.question = content;
                        } else if (nodeName.equals("answer")) {
                            question.answer = content;
                        }
                    }
                }
                questionArray.add(question);
            }
        }
        return questionArray;
    }
}   

