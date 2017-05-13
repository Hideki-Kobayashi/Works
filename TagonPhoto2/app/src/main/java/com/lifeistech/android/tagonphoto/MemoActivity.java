package com.lifeistech.android.tagonphoto;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MemoActivity extends AppCompatActivity {

    int picnum = 0;
    FrameLayout frame;
    private ImageView [] tags = new ImageView[2];
    private DragViewListener [] listeners = new DragViewListener[2];

    public static ImageView picture;
    EditText editText;

    int left;
    int top;

    float lastX;
    float lastY;

    int REQUEST_ORIGIN = 0;

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

        Intent imageintent = new Intent();
        imageintent.setType("image/*");
        imageintent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(imageintent, REQUEST_ORIGIN);

        for (int i = 0; i < tags.length; i++) {
            listeners[i] = new DragViewListener(tags[i]);
            tags[i].setOnTouchListener(listeners[i]);
        }
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

    public class DragViewListener implements View.OnTouchListener {
        // ドラッグ対象のView
        private ImageView dragView;
        // ドラッグ中に移動量を取得するための変数
        private int oldx;
        private int oldy;
        boolean flag = false;

        public DragViewListener(ImageView dragView) {
            this.dragView = dragView;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionevent) {
            // タッチしている位置取得
            int x = (int) motionevent.getRawX();
            int y = (int) motionevent.getRawY();

            switch (motionevent.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    // 今回イベントでのView移動先の位置
                     left = dragView.getLeft() + (x - oldx);
                     top = dragView.getTop() + (y - oldy);
                    // Viewを移動する
                    dragView.layout(left, top, left + dragView.getWidth(), top + dragView.getHeight());
                    break;

                case MotionEvent.ACTION_UP:
                    lastX = view.getX();
                    lastY = view.getY();
                    if (view.getId() == R.id.tag0) {
                            addView(0);
                    } else if (view.getId() == R.id.tag1) {
                            addView(1);
                    }
                    if(flag == false){
                        editText = new EditText(getApplicationContext());
                        editText.setHint("text");
                        FrameLayout.LayoutParams editTextParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                        editText.setLayoutParams(editTextParams);
                        frame.addView(editText);
                        editText.setTranslationX(left + 10);
                        editText.setTranslationY(top);
                        Log.d("edittext", "set");
                        flag = true;
                    }
                    break;
            }
            // 今回のタッチ位置を保持
            oldx = x;
            oldy = y;
            // イベント処理完了
            return true;
        }
    }

    public class DragViewListener2 implements View.OnTouchListener {
        // ドラッグ対象のView
        private ImageView dragView;
        // ドラッグ中に移動量を取得するための変数
        private int oldx;
        private int oldy;

        public DragViewListener2(ImageView dragView) {
            this.dragView = dragView;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionevent) {
            // タッチしている位置取得
            int x = (int) motionevent.getRawX();
            int y = (int) motionevent.getRawY();

            switch (motionevent.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    // 今回イベントでのView移動先の位置
                    left = dragView.getLeft() + (x - oldx);
                    top = dragView.getTop() + (y - oldy);
                    // Viewを移動する
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dragView.getWidth(), dragView.getHeight());
                    layoutParams.setMargins(left, top, 0, 0);
                    dragView.setLayoutParams(layoutParams);
                    //editText.setLayoutParams(layoutParams);
                    Log.d("MotionEvent", "Move");
                    break;

            }

            // 今回のタッチ位置を保持
            oldx = x;
            oldy = y;
            // イベント処理完了
            return true;
        }
    }

    public void addView(final int tagNum){
        final EditText editText = new EditText(this);
        ImageView image = new ImageView(this);
        image.setImageResource(getResources().getIdentifier("fusen" + tagNum, "drawable", getPackageName()));
        frame.addView(image, tags[tagNum].getWidth(), tags[tagNum].getHeight());
        image.setTranslationX(lastX);
        image.setTranslationY(lastY);

        DragViewListener2 listener2 = new DragViewListener2(image);
        image.setOnTouchListener(listener2);

        //付箋にEditTextを出す
        /*
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    editText.setHint("text");
                    FrameLayout.LayoutParams editTextParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    editText.setLayoutParams(editTextParams);
                    frame.addView(editText);
                    editText.setTranslationX(left + 10);
                    editText.setTranslationY(top);
                Log.d("click", "on");
            }

        });*/

        /*
        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((ViewGroup)view.getParent()).removeView(view);
                Log.d("longclick","on");
                return false;
            }
        });
        Log.d("onLongClickListener", "set");
        */



    }










































    // 保存するメソッド
    /*
    public void save() throws Exception {
        try {
            frame.setDrawingCacheEnabled(true);
            Bitmap save_bmp = Bitmap.createBitmap(frame.getDrawingCache());
            String folderpath = Environment.getExternalStorageDirectory() + "/TagonPhoto/";
            File folder = new File(folderpath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File file = new File(folderpath, "sample" + picnum + ".png");
            if (file.exists()) {
                for (; file.exists(); picnum++) {
                    file = new File(folderpath, "sample" + picnum + ".png");
                }
            }
            FileOutputStream outStream = new FileOutputStream(file);
            save_bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.close();
            Toast.makeText(
                    getApplicationContext(),
                    "Image saved",
                    Toast.LENGTH_SHORT).show();
            frame.setDrawingCacheEnabled(false);
            showFolder(file);
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "例外1です", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this, "例外2です", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // イメージファイルが保存されたことを通知するメソッド
    private void showFolder(File path) throws Exception {
        try {
            ContentValues values = new ContentValues();
            ContentResolver contentResolver = getApplicationContext()
                    .getContentResolver();
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATE_MODIFIED,
                    System.currentTimeMillis() / 1000);
            values.put(MediaStore.Images.Media.SIZE, path.length());
            values.put(MediaStore.Images.Media.TITLE, path.getName());
            values.put(MediaStore.Images.Media.DATA, path.getPath());
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            Toast.makeText(this, "例外です", Toast.LENGTH_SHORT).show();
            throw e;

        }
    }

    // メニューを作るメソッド
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    // メニューのボタンが押された時に呼ばれるメソッド
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save){
            try{
                save();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }*/



}
