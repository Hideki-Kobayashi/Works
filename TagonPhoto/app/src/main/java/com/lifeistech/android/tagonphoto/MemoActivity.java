package com.lifeistech.android.tagonphoto;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MemoActivity extends AppCompatActivity {

    int picnum = 0;
    String tagName;
    FrameLayout frame;
    private ImageView [] tags = new ImageView[2];
    boolean flag = false;
    public static ImageView picture;
    float x;
    float y;
    int REQUEST_ORIGIN = 0;
    String [] tagNames = {"small", "medium"};
    //private EditText editText;

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




        /*
        ImageView dragView = (ImageView) findViewById(R.id.tag0);
        DragViewListener listener = new DragViewListener(dragView);
        dragView.setOnTouchListener(listener);
        */
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


        //int [] location = new int[2];
        final ImageView image = new ImageView(getApplicationContext());
        image.setImageResource(getResources().getIdentifier("fusen" + tagNum, "drawable", getPackageName()));




        /*
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                tagName = tagNames[tagNum];
                ClipData data = ClipData.newPlainText("fusen" + tagNum, "drag");
                view.startDrag(data, new View.DragShadowBuilder(view), view, 0);
                return false;
            }
        });

        image.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {

                tagName = tagNames[tagNum];
                ClipData data = ClipData.newPlainText("fusen" + tagNum, "drag");
                view.startDrag(data, new View.DragShadowBuilder(view), view, 0);

                return false;
            }
        });
        */



        frame.addView(image, tags[tagNum].getWidth(), tags[tagNum].getHeight());
        image.setTranslationX(x - (tags[tagNum].getWidth()) / 2);
        image.setTranslationY(y - (tags[tagNum].getHeight()) / 2);
        //image.getLocationInWindow(location);

        ImageView dragView = (ImageView) findViewById()
        DragViewListener listener = new DragViewListener(dragView);
        dragView.setOnTouchListener(listener);




        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editText.setText("sample");
                FrameLayout.LayoutParams editTextParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                editText.setLayoutParams(editTextParams);

                frame.addView(editText);
                editText.setTranslationX(x - (tags[tagNum].getWidth()) / 3);
                editText.setTranslationY(y - (tags[tagNum].getHeight()) / 2);
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

    public class DragViewListener implements View.OnTouchListener {
        // ドラッグ対象のView
        private ImageView dragView;
        // ドラッグ中に移動量を取得するための変数
        private int oldx;
        private int oldy;
        public DragViewListener(ImageView dragView) {
            this.dragView = dragView;
        }

        @Override  public boolean onTouch(View view, MotionEvent event) {
            // タッチしている位置取得
            int x = (int) event.getRawX();
            int y = (int) event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    // 今回イベントでのView移動先の位置
                    int left = dragView.getLeft() + (x - oldx);
                    int top = dragView.getTop() + (y - oldy);
                    // Viewを移動する
                    dragView.layout(left, top, left + dragView.getWidth(), top + dragView.getHeight());
                    break;
            }    // 今回のタッチ位置を保持
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
