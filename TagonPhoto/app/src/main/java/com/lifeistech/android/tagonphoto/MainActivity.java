package com.lifeistech.android.tagonphoto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<MemoButton> mButtons;
    ButtonAdapter mButtonAdapter;
    ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listView);
        mButtons = new ArrayList<MemoButton>();

        mButtons.add(new MemoButton("+"));
        mButtons.add(new MemoButton("Note"));

        mButtonAdapter = new ButtonAdapter(this, R.layout.activity_memo_button, mButtons);
        mListView.setAdapter(mButtonAdapter);

    }


}
