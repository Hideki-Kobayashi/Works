package com.lifeistech.android.tagonphoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<MemoButton> mButtons;
    ButtonAdapter mButtonAdapter;
    ListView mListView;
    int REQUEST_ORIGIN = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listView);
        mButtons = new ArrayList<MemoButton>();

        mButtons.add(new MemoButton("+"));
        mButtons.add(new MemoButton("Note"));

        //mButtonAdapter = new ButtonAdapter(this, R.layout.activity_memo_button, mButtons);
        //mListView.setAdapter(mButtonAdapter);

    }


    public void click(View v) {
        Intent i = new Intent(getApplication(), MemoActivity.class);
        startActivity(i);
    }


    /*
    public void select(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_ORIGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            try{
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                //MemoActivity.picture.setImageBitmap(img);

                in.close();
            }catch (Exception e){
                Toast.makeText(this, "例外です", Toast.LENGTH_SHORT).show();
            }
        }
    }*/


}
