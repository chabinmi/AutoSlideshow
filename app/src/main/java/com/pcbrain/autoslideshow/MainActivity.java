package com.pcbrain.autoslideshow;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Cursor cursor;

    Button mSusumuButton;
    Button mModoruButton;
    Button mSaiseiButton;
    Timer mTimer;
    Handler mHandler = new Handler();
    //double mTimerSec = 0.0;
    Integer cursorCount = 0;
    Integer cursorIndex = 0;

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSusumuButton = (Button) findViewById(R.id.button1);
        mModoruButton = (Button) findViewById(R.id.button2);
        mSaiseiButton = (Button) findViewById(R.id.button3);

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                OpenCursor();

                // 許可されている
                cursorIndex = 0;
                getContentsInfo(1);
                if ( cursorCount > 0 )
                    cursorIndex += 1;
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            OpenCursor();

            cursorIndex = 0;
            getContentsInfo(1);
            if ( cursorCount > 0 )
                cursorIndex += 1;
        }

        mSusumuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContentsInfo(1);
                cursorIndex += 1;
            }
        });

        mModoruButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContentsInfo(-1);
                cursorIndex -= 1;
            }
        });

        mSaiseiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mTimerSec = 0.0;

                if ( mSaiseiButton.getText().equals("再生") ) {
                    mSusumuButton.setEnabled(false);
                    mModoruButton.setEnabled(false);
                    mSaiseiButton.setText("停止");

                    if (mTimer == null) {
                        mTimer = new Timer();
                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                //mTimerSec += 0.1;

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // スライドショーの動き
                                        Log.d("Contents", "cursorIndex = [" + cursorIndex.toString() + "]");
                                        getContentsInfo(1);
                                        cursorIndex += 1;
                                    }
                                });
                            }
                        }, 100, 2000);
                    }
                } else if ( mSaiseiButton.getText().equals("停止") ) {
                    //mTimerSec = 0.0;
                    mSusumuButton.setEnabled(true);
                    mModoruButton.setEnabled(true);

                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer = null;
                    }

                    mSaiseiButton.setText("再生");
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    OpenCursor();

                    cursorIndex = 0;
                    getContentsInfo(1);
                    if ( cursorCount > 0 )
                        cursorIndex += 1;
                } else {
                    // DENYを選択した時
                    mSusumuButton.setEnabled(false);
                    mModoruButton.setEnabled(false);
                    mSaiseiButton.setEnabled(false);
                }
                break;
            default:
                break;
        }
    }

    public void OpenCursor() {

        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );
    }

    public void getContentsInfo(Integer flag) {

        // 画像の情報を取得する
        if (cursorIndex == 0) {
            if ( cursor != null ) {
                cursorCount = cursor.getCount();
                Log.d("Contents", ":[" + cursorCount.toString() + "]");
            } else {
                cursorCount = 0;
/*
                mSusumuButton.setEnabled(false);
                mModoruButton.setEnabled(false);
                mSaiseiButton.setEnabled(false);
*/
            }

            if (cursorCount > 0)
                cursor.moveToFirst();
        } else {
            if (flag == 1 && cursor.moveToNext() == false)
                cursor.moveToFirst();
            if (flag == -1 && cursor.moveToPrevious() == false)
                cursor.moveToLast();
        }

        if (cursorCount > 0) {
            // indexからIDを取得し、そのIDから画像のURIを取得する
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageURI(imageUri);

            Log.d("Contents", "URI:[" + imageUri.toString() + "]");
        }
        //cursor.close();

    }

}
