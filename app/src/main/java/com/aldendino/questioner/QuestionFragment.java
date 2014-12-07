package com.aldendino.questioner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.TextView;

public class QuestionFragment extends Fragment {
    private TextView questionView;
    private TextView answerView;
    private View fadeView;

    private static final String QUESTION_STATE = "questionState";
    private static final String INDEX_STATE = "indexState";
    private static final String HIDDEN_STATE = "hiddenState";

    private final String tag = "tag";

    public final String welcome = "Open an XML file to start.";

    private Question question = null;
    private int questionIndex;

    private boolean hidden = true;

    public QuestionFragment() {
        this.question = new Question(welcome, "");
    }

    @Override
    public void setArguments(Bundle args) {
        Question questionArg = (Question) args.get(MainActivity.QUESTION_KEY);
        questionIndex = args.getInt(MainActivity.INDEX_KEY);
        if(questionArg != null) {
            this.question = questionArg;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_main, container, false);
        fadeView = rootView.findViewById(R.id.fadeView);
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

        if(savedInstanceState != null) {
            question = savedInstanceState.getParcelable(QUESTION_STATE);
            questionIndex = savedInstanceState.getInt(INDEX_STATE);
            hidden = savedInstanceState.getBoolean(HIDDEN_STATE);
            Log.d(tag, "" +hidden);
        }

        if(question != null) {
            setQuestion();
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(QUESTION_STATE, question);
        outState.putInt(INDEX_STATE, questionIndex);
        outState.putBoolean(HIDDEN_STATE, hidden);
        super.onSaveInstanceState(outState);
    }

    private void setQuestion() {
        if(question != null) {
            questionView.setText(questionIndex + ". " + question.getQuestion());
            answerView.setText(question.getAnswer().toString());
            if(hidden) {
                clearAnswer();
            }
            else {
                setAnswer();
            }
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

    void clearAnswer() {
        fadeView.animate().alpha(1.0f).setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return smootherStep(0.0f, 1.0f, input);
            }
        });
        hidden = true;
    }

    private void setAnswer() {
        fadeView.animate().alpha(0.0f).setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return smootherStep(0.0f, 1.0f, input);
            }
        });
        hidden = false;
    }

    public static float smootherStep(float start, float end, float t) {
        float affection = end - start;
        t = t*t*t*(t*(6f*t - 15f) + 10f);
        affection *= t;
        return start + affection;
    }
}
