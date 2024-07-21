package org.dci.myfinance;

public class FAQ {
    private String question;
    private String answer;
    private boolean isAnswerVisible;

    public FAQ(String question, String answer) {
        this.question = question;
        this.answer = answer;
        isAnswerVisible = false;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isAnswerVisible() {
        return isAnswerVisible;
    }

    public void setAnswerVisible(boolean answerVisible) {
        isAnswerVisible = answerVisible;
    }
}
