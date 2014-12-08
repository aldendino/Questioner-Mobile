package com.aldendino.questioner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends ActionBarActivity{

    public static String QUESTION_KEY = "question";
    public static String INDEX_KEY = "index";

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    private static final int READ_REQUEST_CODE = 42;

    public final String welcome = "Open an XML file to start.";
    public final String help = "To do so, click the icon in the top right corner.";

    private ArrayList<Question> qm;
    private Random generator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        getSupportActionBar().getThemedContext();

        if (savedInstanceState != null) {
            qm = savedInstanceState.getParcelableArrayList("array");
        }
        else {
            Question welcomeQuestion = new Question(welcome, help);
            qm = new ArrayList<>();
            qm.add(welcomeQuestion);
        }

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //private int previous;

            @Override
            public void onPageScrolled(int i, float v, int i2) {
                /*if(v == 0.0 && previous != i) {
                    ScreenSlidePagerAdapter sspa = (ScreenSlidePagerAdapter) mPagerAdapter;
                    QuestionFragment qp = (QuestionFragment) sspa.getItem(previous);
                    try {
                        if (qp != null) qp.clearAnswer();
                    }
                    catch(Exception e) {
                        Log.d(tag, e.toString());
                    }
                    previous = i;
                }*/
            }

            @Override
            public void onPageSelected(int i) {
                setHome();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        if (savedInstanceState != null) {
            mPager.setCurrentItem(savedInstanceState.getInt("currentItem", 0), true);
        }

        Intent intent = getIntent();
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_VIEW)) {
            Uri data = intent.getData();
            loadData(data);
        }

        generator = new Random();
        setHome();
    }

    private void setHome() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(mPager.getCurrentItem() > 0);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("currentItem", mPager.getCurrentItem());
        bundle.putParcelableArrayList("array", qm);
        super.onSaveInstanceState(bundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_load) {
            performFileSearch();
            return true;
        }
        else if(id == R.id.action_random) {
            scrollToRandom();
            return true;
        }
        else if (id == android.R.id.home) {
            mPager.setCurrentItem(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/xml");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri uri = resultData.getData();
                loadData(uri);
            }
        }
    }

    private void loadData(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            inputStream.close();
            qm = Question.parseXML(stringBuilder.toString());
            if(qm.size() == 0) throw new IOException();
            mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(mPagerAdapter);
        }
        catch(Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_message)
                    .setTitle(R.string.dialog_title);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    //
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            Log.d(getClass().getSimpleName(), e.toString());
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        //private SparseArray<QuestionFragment> frags = new SparseArray<>();

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position >= qm.size() || position < 0) return null;
            QuestionFragment fragment;// = frags.get(position);
            //if(fragment == null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(QUESTION_KEY, qm.get(position));
                bundle.putInt(INDEX_KEY, position + 1);
                fragment = new QuestionFragment();
                fragment.setArguments(bundle);
                //frags.put(position, fragment);
            //}
            return fragment;
        }

        @Override
        public int getCount() {
            return qm.size();
        }
    }

    private void scrollToRandom() {
        if(qm.size() > 1) {
            int random = generator.nextInt(qm.size());
            int current = mPager.getCurrentItem();
            while(random == current) {
                random = generator.nextInt(qm.size());
            }
            mPager.setCurrentItem(random, true);
        }
    }
}
