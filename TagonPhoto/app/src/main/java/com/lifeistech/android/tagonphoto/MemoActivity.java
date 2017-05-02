package com.lifeistech.android.tagonphoto;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

public class MemoActivity extends AppCompatActivity {

    String tagName;
    FrameLayout frame;
    private ImageView [] tags = new ImageView[2];
    boolean flag = false;
    public static ImageView picture;
    float x;
    float y;
    int REQUEST_ORIGIN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);


        picture = (ImageView) findViewById(R.id.picture);
        frame = (FrameLayout) findViewById(R.id.framelayout);
        Intent intent = getIntent();

        for (int i = 0; i < 2; i++){
            tags[i] = (ImageView) findViewById(getResources().getIdentifier("tag" + i, "id", getPackageName()));
            //getIdentifier("fusen" + i, "id", getPackageName())でNullPointerException
        }


        Intent imageintent = new Intent();
        imageintent.setType("image/*");
        imageintent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(imageintent, REQUEST_ORIGIN);

        //付箋にタッチ時の動きをセット
        tags[0].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                tagName = "small";
                ClipData data = ClipData.newPlainText("fusen0", "drag");
                view.startDrag(data, new View.DragShadowBuilder(view), view, 0);
                return false;
            }
        });

        tags[1].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                tagName = "medium";
                ClipData data = ClipData.newPlainText("fusen1", "drag");
                view.startDrag(data, new View.DragShadowBuilder(view), view, 0);
                return false;
            }
        });

        //付箋をドロップした時の動きをセット
        for (int i = 0; i < 2; i++){
            tags[i].setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View view, DragEvent dragEvent) {
                    if(dragEvent.getAction() == DragEvent.ACTION_DRAG_ENDED) {
                        if (flag) {
                            switch (tagName) {
                                case "small":
                                    addView(0);
                                    break;

                                case "medium":
                                    addView(1);
                                    break;
                            }
                        }
                        return true;
                    }

                    return false;
                }
            });
        }

        //画像を表示するImageViewに付箋のドラッグを監視させる
        picture.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()){
                    case DragEvent.ACTION_DRAG_EXITED:
                        flag = false;
                        break;
                    case DragEvent.ACTION_DROP:
                        x = dragEvent.getX();
                        y = dragEvent.getY();
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        flag = true;
                        break;

                }
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            try{
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                picture.setImageBitmap(img);

                in.close();
            }catch (Exception e){
                Toast.makeText(this, "例外です", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addView(final int tagNum){
        final EditText editText = new EditText(this);
        int [] location = new int[2];
        final ImageView image = new ImageView(getApplicationContext());
        image.setImageResource(getResources().getIdentifier("fusen" + tagNum, "drawable", getPackageName()));

        /*
        image.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {

                tagName = "medium";
                ClipData data = ClipData.newPlainText("fusen1", "drag");
                view.startDrag(data, new View.DragShadowBuilder(view), view, 0);

                return false;
            }
        });
        */


        frame.addView(image, tags[tagNum].getWidth(), tags[tagNum].getHeight());
        image.setTranslationX(x - (tags[tagNum].getWidth()) / 2);
        image.setTranslationY(y - (tags[tagNum].getHeight()) / 2);
        image.getLocationInWindow(location);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //frame.addView(editText);
            }
        });

        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                frame.removeView(image);
                return false;
            }
        });





    }



}
