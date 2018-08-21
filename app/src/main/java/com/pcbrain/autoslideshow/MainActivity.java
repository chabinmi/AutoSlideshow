package com.pcbrain.autoslideshow;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Handler mHandler = new Handler();

    Button mSusumuButton;
    Button mModoruButton;
    Button mSaiseiButton;
    Timer mTimer;
    double mTimerSec = 0.0;
    Integer cursorIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSusumuButton = (Button) findViewById(R.id.button1);
        mModoruButton = (Button) findViewById(R.id.button2);
        mSaiseiButton = (Button) findViewById(R.id.button3);

        mSusumuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursorIndex += 1;
                //getContentsInfo();
            }
        });

        mModoruButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursorIndex -= 1;
                //getContentsInfo();
            }
        });

        mSaiseiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimerSec = 0.0;
                mSusumuButton.setEnabled(false);
                mModoruButton.setEnabled(false);
                //mTimerText.setText(String.format("%.1f", mTimerSec));

//                if (mTimer != null) {
//                    mTimer.cancel();
//                    mTimer = null;
//                }
            }
        });

        public void getContentsInfo() {

            // 画像の情報を取得する
            ContentResolver resolver = getContentResolver();
            Cursor cursor = resolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                    null, // 項目(null = 全項目)
                    null, // フィルタ条件(null = フィルタなし)
                    null, // フィルタ用パラメータ
                    null // ソート (null ソートなし)
            );

            if (cursor.moveToFirst()) {
                do {
                    // indexからIDを取得し、そのIDから画像のURIを取得する
                    int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    Long id = cursor.getLong(fieldIndex);
                    Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                    Log.d("ANDROID", "URI:[" + imageUri.toString() + "]");
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

    }

}
