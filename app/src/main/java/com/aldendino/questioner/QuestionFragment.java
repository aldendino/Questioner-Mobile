package com.aldendino.questioner;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class QuestionFragment extends Fragment {
    private TextView questionView;
    private TextView answerView;

    public final String welcome = "Open an XML file to start.";

    private Question question = null;
    private int questionIndex;

    private boolean hidden = true;

    public QuestionFragment() {
        this.question = new Question(welcome, "");
    }

    @Override
    public void setArguments(Bundle args) {
        Question questionArg = (Question) args.get("question");
        questionIndex = args.getInt("index");
        if(questionArg != null) {
            this.question = questionArg;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_main, container, false);
        questionView = (TextView) rootView.findViewById(R.id.questionText);
        answerView = (TextView) rootView.findViewById(R.id.answerText);
        questionView.setMovementMethod(new ScrollingMovementMethod());
        answerView.setMovementMethod(new ScrollingMovementMethod());
        answerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                toggleAnswer();
                return false;
            }
        });

        if(question == null) {
            questionView.setText(welcome);
        }
        else {
            setQuestion();
        }
        return rootView;
    }

    private void setQuestion() {
        if(question != null) {
            questionView.setText(questionIndex + ". " + question.getQuestion());
            clearAnswer();
        }
    }

    private void toggleAnswer() {
        if(question != null) {
            if (hidden) {
                setAnswer();
            } else {
                clearAnswer();
            }
        }
    }

    private void clearAnswer() {
        answerView.setBackgroundColor(getResources().getColor(R.color.dark));
        answerView.setText("");
        hidden = true;
    }

    private void setAnswer() {
        answerView.setBackgroundColor(Color.WHITE);
        answerView.setText(question.getAnswer().toString());
        hidden = false;
    }
}
