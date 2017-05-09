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

    public static ImageView picture;
    float x;
    float y;
    int REQUEST_ORIGIN = 0;

    int clickFlag = 0;
    int tagCounter = 0;
    //ふせんを出した回数をカウント

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);


        picture = (ImageView) findViewById(R.id.picture);
        frame = (FrameLayout) findViewById(R.id.framelayout);
        //Intent intent = getIntent();

        for (int i = 0; i < 2; i++){
            tags[i] = (ImageView) findViewById(getResources().getIdentifier("tag" + i, "id", getPackageName()));
            //getIdentifier("fusen" + i, "id", getPackageName())でNullPointerException
        }


        Intent imageintent = new Intent();
        imageintent.setType("image/*");
        imageintent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(imageintent, REQUEST_ORIGIN);

        ImageView dragView0 = tags[0];
        DragViewListener listener0 = new DragViewListener(dragView0);
        dragView0.setOnTouchListener(listener0);

        ImageView dragView1 = tags[1];
        DragViewListener listener1 = new DragViewListener(dragView1);
        dragView1.setOnTouchListener(listener1);

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
        final ImageView image = new ImageView(getApplicationContext());

        image.setImageResource(getResources().getIdentifier("fusen" + tagNum, "drawable", getPackageName()));
        frame.addView(image, tags[tagNum].getWidth(), tags[tagNum].getHeight());

        ImageView dragView = image;
        DragViewListener listener = new DragViewListener(dragView);
        dragView.setOnTouchListener(listener);


        //付箋にEditTextを出す
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                if(clickFlag == 0){
                    editText.setHint("text");
                    FrameLayout.LayoutParams editTextParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    editText.setLayoutParams(editTextParams);

                    frame.addView(editText);
                    editText.setTranslationX(x - (tags[tagNum].getWidth()) / 3);
                    editText.setTranslationY(y - (tags[tagNum].getHeight()) / 2);

                }
                */
            }
        });


        /*
        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                frame.removeView(image);
                return false;
            }
        });
        */


    }


    public class DragViewListener implements View.OnTouchListener {
        // ドラッグ対象のView
        private ImageView dragView;
        // ドラッグ中に移動量を取得するための変数
        private int oldx;
        private int oldy;

        public DragViewListener(ImageView dragView) {
            this.dragView = dragView;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // タッチしている位置取得
            int x = (int) event.getRawX();
            int y = (int) event.getRawY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("touch", "down");
                    addView(0);
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 今回イベントでのView移動先の位置
                    int left = dragView.getLeft() + (x - oldx);
                    int top = dragView.getTop() + (y - oldy);
                    // Viewを移動する
                    dragView.layout(left, top, left + dragView.getWidth(), top
                            + dragView.getHeight());
                    break;
            }

            // 今回のタッチ位置を保持
            oldx = x;
            oldy = y;
            // イベント処理完了
            return true;
        }
    }








































    // 保存するメソッド
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
    }



}
