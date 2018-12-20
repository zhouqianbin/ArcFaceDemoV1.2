package com.zhouqianbin.demo.ui;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;

import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.zhouqianbin.demo.R;
import com.zhouqianbin.demo.camera.CameraManager;
import com.zhouqianbin.demo.camera.CameraParamet;
import com.zhouqianbin.demo.entity.CompareFaceResult;
import com.zhouqianbin.demo.entity.DrawFaceInfoEntity;
import com.zhouqianbin.demo.entity.FaceInfoEntity;
import com.zhouqianbin.demo.face.ArcFaceEngine;
import com.zhouqianbin.demo.face.DetectFaceInfoEntity;
import com.zhouqianbin.demo.face.OnFaceDetectResult;
import com.zhouqianbin.demo.utils.FaceConvertUtils;
import com.zhouqianbin.demo.widget.FaceRectView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class FaceDetectActivity extends AppCompatActivity {

    private static final String TAG = FaceDetectActivity.class.getSimpleName();
    private SurfaceView mSurfaceView;
    private FaceRectView mFaceRectView;
    private List<FaceInfoEntity> mFaceInfoEntityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detect);
        mSurfaceView = findViewById(R.id.face_detect_surf_view);
        mFaceRectView = findViewById(R.id.face_detect_face_rect);

        ArcFaceEngine.getInstance().setLogSwitch(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mFaceInfoEntityList = LitePal.findAll(FaceInfoEntity.class);
            }
        }).start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        CameraParamet cameraParamet = new CameraParamet.Builder()
                .setCameraId(1)
                .setImageFormat(ImageFormat.JPEG)
                .setPreviewSize(1920,1080)
                .setPictureSize(1920,1080)
                .setSurfaceView(mSurfaceView)
                .Build();

        CameraManager.getInstance().openCamera(this, cameraParamet, new CameraManager.OnCameraListen() {
            @Override
            public void oppenSuss() {
                Log.d(TAG,"摄像头打开成功");
                CameraManager.getInstance().setPreviewListen(new CameraManager.OnCameraPreview() {
                    @Override
                    public void onPreview(byte[] bytes) {
                        detectFace(
                                bytes,
                                CameraManager.getInstance().getmPreviewWidth(),
                                CameraManager.getInstance().getmPreviewHeight());
                    }
                });
            }

            @Override
            public void oppenEror(String errorMsg) {
                Log.d(TAG,"摄像头打开失败 " + errorMsg);
            }
        });
    }

    long startTime;

    private void detectFace(byte[] bytes,int width,int height){
        startTime = System.currentTimeMillis();
        ArcFaceEngine.getInstance().detectFaceVideoStream(
                bytes,
                width,
                height,
                new OnFaceDetectResult() {
                    @Override
                    public void detectResult(List<DetectFaceInfoEntity> detectFaceInfoEntities) {
                        if(detectFaceInfoEntities.size() > 0){
                            long endTime = System.currentTimeMillis();
                            Log.d(TAG,"耗时时间 "+ (endTime-startTime));
                            drawDetectFaceInfo(detectFaceInfoEntities);
                            quertFaceForDb(detectFaceInfoEntities);
                        }
                    }

                    @Override
                    public void detectError(String errorMsg) {
                        Log.d(TAG,"人脸检测错误 " + errorMsg);
                    }

                    @Override
                    public void detectNotFace() {
                        mFaceRectView.clearFaceRectInfo();
                    }
                });
    }

    /**
     * 绘制人脸信息
     * @param faceInfoEntityList
     */
    private void drawDetectFaceInfo(List<DetectFaceInfoEntity> faceInfoEntityList) {
        Log.d(TAG,"绘制检测人脸信息 " + faceInfoEntityList);
        List<DrawFaceInfoEntity> drawFaceInfoEntities = new ArrayList<>();
        DrawFaceInfoEntity drawFaceInfoEntity = new DrawFaceInfoEntity();
        drawFaceInfoEntity.setFaceAge(faceInfoEntityList.get(0).getFaceAge());
        drawFaceInfoEntity.setFaceGender(faceInfoEntityList.get(0).getFaceGender());
        drawFaceInfoEntity.setLivenress(faceInfoEntityList.get(0).getLivenessCode());
        drawFaceInfoEntity.setFaceName("未知");
        Rect oldRect = faceInfoEntityList.get(0).getFaceRect();
        Rect newRect = FaceConvertUtils.adjustRect(
                oldRect,
                CameraManager.getInstance().getmPreviewWidth(),
                CameraManager.getInstance().getmPreviewHeight(),
                mFaceRectView.getWidth(),
                mFaceRectView.getHeight(),
                CameraManager.getInstance().getmCameraAngle(),
                CameraManager.getInstance().getmCameraId());
        drawFaceInfoEntity.setFaceRect(newRect);
        drawFaceInfoEntities.add(drawFaceInfoEntity);

        mFaceRectView.drawFaceRect(drawFaceInfoEntities);
    }


    List<CompareFaceResult> mRegisterCompareFaces = new ArrayList<>();
    List<CompareFaceResult> mNotRegisterCompareFaces = new ArrayList<>();
    private float currentScore;
    private int currDbIndex;
    private AFR_FSDKFace mAfrFsdkFace = new AFR_FSDKFace();

    /**
     * 查询数据库获取人脸信息
     * @param detectFaceInfoEntities
     */
    private void quertFaceForDb(List<DetectFaceInfoEntity> detectFaceInfoEntities) {
        mRegisterCompareFaces.clear();
        mNotRegisterCompareFaces.clear();

        AFR_FSDKFace afrFsdkFace = mAfrFsdkFace.clone();
        afrFsdkFace.setFeatureData(detectFaceInfoEntities.get(0).getFaceFeture());

        for (int index = 0; index < mFaceInfoEntityList.size(); index++) {
            AFR_FSDKFace dbFsdkFace = mAfrFsdkFace.clone();
            dbFsdkFace.setFeatureData(mFaceInfoEntityList.get(index).getFaceFeture());
            float score = ArcFaceEngine.getInstance().faceComparison(dbFsdkFace, afrFsdkFace);
            //LogUtils.dTag(TAG, "当前比对结果 " + score);
            if (score > currentScore) {
                currentScore = score;
                currDbIndex = index;
            }
            //是否循环到最后一个
            if (index == mFaceInfoEntityList.size() - 1) {
                Log.d("compare", "最终比对的结果值 " +
                        currentScore + " detectIndex " +
                        0 + " " + " dbIndex " +
                        currDbIndex);
                //是否大于0.6,自定设定
                if (currentScore > 0.6) {
                    //匹配数据库的人脸信息
                    mRegisterCompareFaces.add(
                            new CompareFaceResult((int) (currentScore), currDbIndex, 0));
                } else {
                    //不是注册的人脸信息
                    mNotRegisterCompareFaces.add(
                            new CompareFaceResult((int) (currentScore), currDbIndex, 0));
                }
                //重置变量
                currentScore = 0;
                currDbIndex = 0;
            }
        }

        if(mRegisterCompareFaces.size() > 0){
            FaceInfoEntity faceInfoEntity = mFaceInfoEntityList.get(mRegisterCompareFaces.get(0).getDbFaceIndex());
            List<DrawFaceInfoEntity> drawFaceInfoEntities = new ArrayList<>();
            DrawFaceInfoEntity drawFaceInfoEntity = new DrawFaceInfoEntity();
            drawFaceInfoEntity.setFaceAge(faceInfoEntity.getFaceAge());
            drawFaceInfoEntity.setFaceGender(faceInfoEntity.getFaceGender());
            drawFaceInfoEntity.setLivenress(detectFaceInfoEntities.get(0).getLivenessCode());
            drawFaceInfoEntity.setFaceName(faceInfoEntity.getFaceName());
            drawFaceInfoEntities.add(drawFaceInfoEntity);

            Rect oldRect = detectFaceInfoEntities.get(0).getFaceRect();
            Rect newRect = FaceConvertUtils.adjustRect(
                    oldRect,
                    CameraManager.getInstance().getmPreviewWidth(),
                    CameraManager.getInstance().getmPreviewHeight(),
                    mFaceRectView.getWidth(),
                    mFaceRectView.getHeight(),
                    CameraManager.getInstance().getmCameraAngle(),
                    CameraManager.getInstance().getmCameraId());
            drawFaceInfoEntity.setFaceRect(newRect);

            mFaceRectView.drawFaceRect(drawFaceInfoEntities);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraManager.getInstance().releaseCamera();
    }


}
