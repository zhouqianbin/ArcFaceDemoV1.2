package com.zhouqianbin.demo.face;

import android.content.Context;
import android.util.Log;

import com.arcsoft.liveness.ErrorInfo;
import com.arcsoft.liveness.FaceInfo;
import com.arcsoft.liveness.LivenessEngine;
import com.arcsoft.liveness.LivenessInfo;
import com.zhouqianbin.demo.AppSetting;

import java.util.ArrayList;
import java.util.List;

public class ArcLivenessEngine {

    private ArcLivenessEngine() {
    }

    public static ArcLivenessEngine getInstance() {
        return SingleHolder.INSTANCE;
    }

    private static class SingleHolder {
        private static final ArcLivenessEngine INSTANCE = new ArcLivenessEngine();
    }

    private static final String TAG = ArcLivenessEngine.class.getSimpleName();

    private LivenessEngine mLivenessEngine = new LivenessEngine();
    private ErrorInfo mLivenessError = new ErrorInfo();

    public void initEngine(Context context, long detectModel) {
        mLivenessError = mLivenessEngine.activeEngine(
                context,
                AppSetting.ARC_APP_ID,
                AppSetting.ARC_LIVENESS_KEY);

        if (mLivenessError.getCode() == ErrorInfo.MOK ||
                mLivenessError.getCode() == ErrorInfo.MERR_AL_BASE_ALREADY_ACTIVATED) {
            Log.d(TAG, "活体检测引擎激活成功 ");

            mLivenessError = mLivenessEngine.initEngine(context, detectModel);
            if (mLivenessError.getCode() == ErrorInfo.MOK ||
                    mLivenessError.getCode() == ErrorInfo.MERR_AL_BASE_ALREADY_ACTIVATED) {
                Log.d(TAG, "活体检测引擎初始化成功");
            }else {
                Log.d(TAG, "活体检测引擎初始化失败 " + mLivenessError.getCode());
            }
        }else {
            Log.d(TAG, "活体检测引擎激活失败 " + mLivenessError.getCode());
        }

    }


    private List<LivenessInfo> mLivenessInfoList = new ArrayList<>();

    public List<LivenessInfo> startLivenessDetect(byte[] data,
                                   int width,
                                   int height,
                                   List<FaceInfo> faceInfos) {
        mLivenessInfoList.clear();
        mLivenessError = mLivenessEngine.startLivenessDetect(
                data,
                width,
                height,
                LivenessEngine.CP_PAF_NV21,
                faceInfos,
                mLivenessInfoList);

        if(mLivenessError.getCode() == ErrorInfo.MOK){
            return mLivenessInfoList;
        }
        Log.d(TAG,"活体检测失败 " + mLivenessError.getCode());
        return null;
    }


    public void destory() {
        if (null != mLivenessEngine) {
            mLivenessEngine.unInitEngine();
        }
    }


}
