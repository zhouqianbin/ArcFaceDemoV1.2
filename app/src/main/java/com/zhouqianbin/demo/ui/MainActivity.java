package com.zhouqianbin.demo.ui;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.zhouqianbin.demo.face.OnFaceEngineInitListen;
import com.zhouqianbin.demo.R;
import com.zhouqianbin.demo.face.ArcFaceEngine;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        initFaceEngine();
        findViewById(R.id.main_face_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,FaceRegisterActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.main_face_detect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,FaceDetectActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 初始化人脸引擎
     */
    private void initFaceEngine() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArcFaceEngine.getInstance().initEngine(MainActivity.this.getApplicationContext(),new OnFaceEngineInitListen() {
                    @Override
                    public void initSuccess() {
                        Log.d(TAG,"人脸识别引擎初始化成功");
                    }

                    @Override
                    public void initError(String errorMsg) {
                        Log.d(TAG,"人脸识别引擎初始化失败" + errorMsg);
                    }
                });
            }
        }).start();
    }

    /**
     * 请求权限
     */
    private void requestPermission(){
        AndPermission.with(this)
                .runtime()
                .permission(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Toast.makeText(MainActivity.this,"权限获取成功",Toast.LENGTH_SHORT).show();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Toast.makeText(MainActivity.this,"权限获取失败",Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ArcFaceEngine.getInstance().destory();
    }

}
