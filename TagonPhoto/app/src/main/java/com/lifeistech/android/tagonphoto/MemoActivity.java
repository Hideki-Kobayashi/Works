package com.lifeistech.android.tagonphoto;

import android.content.ClipData;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MemoActivity extends AppCompatActivity {

    String tagName;
    FrameLayout frame;
    private ImageView [] tags = new ImageView[2];
    boolean flag = false;
    private ImageView picture;
    float x;
    float y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        picture = (ImageView) findViewById(R.id.picture);
        frame = (FrameLayout) findViewById(R.id.framelayout);
        for (int i = 0; i < 2; i++){
            tags[i] = (ImageView) findViewById(getResources().getIdentifier("tag" + i, "id", getPackageName()));
            //getIdentifier("fusen" + i, "id", getPackageName())でNullPointerException
        }

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

    public void addView(int tagNum){
        ImageView image = new ImageView(getApplicationContext());
        image.setImageResource(getResources().getIdentifier("fusen" + tagNum, "drawable", getPackageName()));
        frame.addView(image, tags[tagNum].getWidth(), tags[tagNum].getHeight());
        image.setTranslationX(x - (tags[tagNum].getWidth()) / 2);
        image.setTranslationY(y - (tags[tagNum].getHeight()) / 2);
    }

}
