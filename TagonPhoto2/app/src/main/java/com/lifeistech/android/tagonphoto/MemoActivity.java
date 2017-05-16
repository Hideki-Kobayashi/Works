package com.lifeistech.android.tagonphoto;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MemoActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    int picnum = 0;
    FrameLayout frame;
    TextView removeArea;
    private MyImageView [] tags = new MyImageView[2];
    private DragViewListener [] listeners = new DragViewListener[2];

    public static ImageView picture;
    ImageView image;

    int left;
    int top;

    float lastX;
    float lastY;
    float lastLeft;
    float lastTop;

    int REQUEST_ORIGIN = 0;
    final int REQUEST_PERMISSION_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        String[] permissions = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            // まだ権限がないので権限をユーザに権限を求める
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE);
            Log.d("PERMISSION", "denied");
        } else {
            Log.d("PERMISSION", "granted");
            // すでに権限があるのでOK (Android 6.0未満 or すでに持っている)
        }

        picture = (ImageView) findViewById(R.id.picture);
        frame = (FrameLayout) findViewById(R.id.framelayout);
        removeArea = (TextView) findViewById(R.id.removeArea);

        for (int i = 0; i < 2; i++){
            tags[i] = (MyImageView) findViewById(getResources().getIdentifier("tag" + i, "id", getPackageName()));
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

                    Log.d("POSITION A MOVE", " left: " + String.valueOf(left) + " top: " + String.valueOf(top) + " (x,y): " + "(" + String.valueOf(x) + "," + String.valueOf(y) + ")" + " old(x,y):" + "(" + String.valueOf(oldx) + "," + String.valueOf(oldy) + ")" + " viewleft: " + String.valueOf(dragView.getLeft() + (x - oldx)) + " viewtop: " + String.valueOf(dragView.getTop() + (y - oldy)));
                    break;

                case MotionEvent.ACTION_UP:
                    //lastLeft = view.getLeft();
                    //lastTop = view.getTop();
                    lastX = view.getX();
                    lastY = view.getY();
                    Log.d("TAG_LAST_POSITION", " last(left, top): " + "(" + lastLeft + "," + lastTop + ")" + " last(x,y): " + "(" + lastX + "," + lastY + ")" );

                    EditText editText = new EditText(getApplicationContext());
                    editText.setHint("text");
                    FrameLayout.LayoutParams editTextParams = new FrameLayout.LayoutParams(dragView.getWidth()-100, dragView.getHeight());
                    editTextParams.setMargins(left + 100, top, 0, 0);
                    editText.setLayoutParams(editTextParams);

                    editText.setGravity(Gravity.TOP);
                    editText.setGravity(Gravity.LEFT);

                        if (view.getId() == R.id.tag0) {
                            addView(0, editText);
                        } else if (view.getId() == R.id.tag1) {
                            addView(1, editText);
                        }

                        frame.addView(editText);



                    Log.d("POSITION A UP", " left: " + String.valueOf(left) + " top: " + String.valueOf(top) + " (x,y): " + "(" + String.valueOf(x) + "," + String.valueOf(y) + ")" + " old(x,y):" + "(" + String.valueOf(oldx) + "," + String.valueOf(oldy) + ")" + " textLeft: " + editText.getLeft() + " textTop: " + editText.getTop());
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
        private EditText editText;

        public DragViewListener2(ImageView dragView, EditText editText) {
            this.dragView = dragView;
            this.editText = editText;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionevent) {
            // タッチしている位置取得
            int x = (int) motionevent.getRawX();
            int y = (int) motionevent.getRawY();

            switch (motionevent.getAction()) {

                case MotionEvent.ACTION_MOVE:
                    // 今回イベントでのView移動先の位置
                    left = view.getLeft() + (x - oldx);
                    top = view.getTop() + (y - oldy);
                    int editLeft = (int) editText.getX() + (x - oldx);
                    int editTop = (int) editText.getY() + (y - oldy);
                    // Viewを移動する
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dragView.getWidth(), dragView.getHeight());
                    FrameLayout.LayoutParams editTextParams = new FrameLayout.LayoutParams(dragView.getWidth()-100, dragView.getHeight());
                    layoutParams.setMargins(left, top, 0, 0);
                    editTextParams.setMargins(editLeft, editTop, 0, 0);
                    dragView.setLayoutParams(layoutParams);
                    editText.setLayoutParams(editTextParams);
                    if(editText.getTop() + view.getHeight()/2 < 0) {
                        removeArea.setBackgroundColor(Color.RED);
                    } else {
                        removeArea.setBackgroundColor(0xFFa0a0a0);
                    }


                    Log.d("POSITION B MOVE", " left: " + String.valueOf(left) + " top: " + String.valueOf(top) + " (x,y): " + "(" + String.valueOf(x) + "," + String.valueOf(y) + ")" + " old(x,y):" + "(" + String.valueOf(oldx) + "," + String.valueOf(oldy) + ")" + " viewleft: " + String.valueOf(dragView.getLeft() + (x - oldx)) + " viewtop: " + String.valueOf(dragView.getTop() + (y - oldy)) + " textLeft: " + editText.getLeft() + " textTop: " + editText.getTop());
                    break;

                case MotionEvent.ACTION_UP:
                    Log.d("POSITION B UP", "gettop: " + String.valueOf(view.getTop()) + "top: " + String.valueOf(top)  + "frameTop: " + String.valueOf(frame.getTop()));

                    if(editText.getTop() + view.getHeight()/2 < 0) {
                        frame.removeView(view);
                        frame.removeView(editText);
                        removeArea.setBackgroundColor(0xFFa0a0a0);
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

    public void addView(final int tagNum, EditText editText){
        image = new ImageView(this);
        image.setImageResource(getResources().getIdentifier("fusen" + tagNum, "drawable", getPackageName()));
        frame.addView(image, tags[tagNum].getWidth(), tags[tagNum].getHeight());
        image.setTranslationX(lastX);
        image.setTranslationY(lastY);

        DragViewListener2 listener2 = new DragViewListener2(image, editText);
        image.setOnTouchListener(listener2);

        /*
        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((ViewGroup)view.getParent()).removeView(view);
                frame.removeView(view);
                Log.d("longclick","on");
                return false;
            }
        });
        */
        //Log.d("onLongClickListener", "set");



    }

    // 保存するメソッド
    public void save() throws Exception {
        try {
            frame.setDrawingCacheEnabled(true);
            Bitmap save_bmp = Bitmap.createBitmap(frame.getDrawingCache());
            //String folderpath = getExternalFilesDir(null) + "/Tagonphoto/";
            String folderpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/tagonphoto/" ;
            Log.d("folder: " , folderpath);
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
            Toast.makeText(getApplicationContext(), "例外１", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "例外２", Toast.LENGTH_SHORT).show();
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

                tags[0].setOnVisibilityChangeListener(new MyImageView.OnVisibilityChangeListener() {
                    @Override
                    public void onVisibilityChange(int visiblity) {
                        if (View.VISIBLE == visiblity) {

                        } else if (View.INVISIBLE == visiblity) {
                            try {
                                save();
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                        } else if (View.GONE == visiblity) {

                        }
                    }
                });

                tags[1].setOnVisibilityChangeListener(new MyImageView.OnVisibilityChangeListener() {
                    @Override
                    public void onVisibilityChange(int visiblity) {
                        if (View.VISIBLE == visiblity) {

                        } else if (View.INVISIBLE == visiblity) {
                            try {
                                save();
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                        } else if (View.GONE == visiblity) {

                        }
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
            tags[0].setVisibility(View.INVISIBLE);
            tags[1].setVisibility(View.INVISIBLE);
            tags[0].setVisibility(View.VISIBLE);
            tags[1].setVisibility(View.VISIBLE);

        }

        return super.onOptionsItemSelected(item);
    }





}