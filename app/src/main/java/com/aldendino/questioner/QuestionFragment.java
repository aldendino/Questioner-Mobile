package com.aldendino.questioner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.Space;
import android.widget.TextView;

public class QuestionFragment extends Fragment {
    private TextView questionView;
    private TextView answerView;
    private FrameLayout animatedFrame;
    private View fadeView;

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
        animatedFrame = (FrameLayout) rootView.findViewById(R.id.animatedFrame);
        fadeView = (View) rootView.findViewById(R.id.fadeView);
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

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(tag, "" + questionIndex);
    }

    private void setQuestion() {
        if(question != null) {
            questionView.setText(questionIndex + ". " + question.getQuestion());
            answerView.setText(question.getAnswer().toString());
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
