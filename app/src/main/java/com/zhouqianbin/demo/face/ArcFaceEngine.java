package com.zhouqianbin.demo.face;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

import com.arcsoft.ageestimation.ASAE_FSDKAge;
import com.arcsoft.ageestimation.ASAE_FSDKEngine;
import com.arcsoft.ageestimation.ASAE_FSDKError;
import com.arcsoft.ageestimation.ASAE_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.genderestimation.ASGE_FSDKEngine;
import com.arcsoft.genderestimation.ASGE_FSDKError;
import com.arcsoft.genderestimation.ASGE_FSDKFace;
import com.arcsoft.genderestimation.ASGE_FSDKGender;
import com.arcsoft.liveness.ErrorInfo;
import com.arcsoft.liveness.FaceInfo;
import com.arcsoft.liveness.LivenessEngine;
import com.arcsoft.liveness.LivenessInfo;
import com.blankj.utilcode.util.ThreadUtils;
import com.zhouqianbin.demo.AppSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * @Copyright (C), 2018, 漳州科能电器有限公司
 * @FileName: ArcFaceEngine
 * @Author: 周千滨
 * @Date: 2018/11/30 9:11
 * @Description:
 * @Version: 1.0.0
 * @UpdateHistory: 修改历史
 * @修改人: 周千滨
 * @修改描述: 创建文件
 */

public class ArcFaceEngine {

    private ArcFaceEngine() { }

    public static ArcFaceEngine getInstance() {
        return SingleHolder.INSTANCE;
    }

    private static class SingleHolder {
        private static final ArcFaceEngine INSTANCE = new ArcFaceEngine();
    }

    private static final String TAG = ArcFaceEngine.class.getSimpleName();


    /**
     * 存放检测到的人脸信息列表
     */
    private List<AFD_FSDKFace> mAfdFsdkFaceList = new ArrayList<AFD_FSDKFace>();

    /**
     * 人脸检测引擎
     */
    private AFD_FSDKEngine mAfdFsdkEngine = new AFD_FSDKEngine();
    /**
     * 人脸检测错误对象
     */
    private AFD_FSDKError mAfdFsdkError = new AFD_FSDKError();

    /**
     * 人脸追踪引擎
     */
    private AFT_FSDKEngine mAftFsdkEngine = new AFT_FSDKEngine();
    private List<AFT_FSDKFace> mAftFsdkFaceList = new ArrayList<>();
    private AFT_FSDKError mAftFsdkError = new AFT_FSDKError();


    /**
     * 人脸识别引擎
     */
    private AFR_FSDKEngine mAfrFsdkEngine = new AFR_FSDKEngine();
    private AFR_FSDKError mAfrFsdkError = new AFR_FSDKError();


    /**
     * 年龄检测引擎
     */
    private ASAE_FSDKEngine mAsaeFsdkEngine = new ASAE_FSDKEngine();
    private ASAE_FSDKError mAsaeFsdkError = new ASAE_FSDKError();
    private List<ASAE_FSDKAge> mAsaeFsdkAgeList = new ArrayList<ASAE_FSDKAge>();

    /**
     * 人脸性别检测引擎
     */
    private ASGE_FSDKEngine mAsgeFsdkEngine = new ASGE_FSDKEngine();
    private List<ASGE_FSDKGender> mAsgeFsdkGenderList = new ArrayList<ASGE_FSDKGender>();
    private ASGE_FSDKError mAsgeFsdkError = new ASGE_FSDKError();

    /**
     * 检测完后的人脸信息
     */
    private List<DetectFaceInfoEntity> mFaceInfoEntityList = new ArrayList<>();


    /**
     * 活体检测引擎
     */
    private LivenessEngine mLivenessEngine = new LivenessEngine();
    private ErrorInfo mLivenessError = new ErrorInfo();
    private List<FaceInfo> mFaceInfoList = new ArrayList<>();
    private FaceInfo mFaceInfo = new FaceInfo();
    private List<LivenessInfo> mLivenessInfoList = new ArrayList<>();


    /**
     * 初始化引擎
     */
    public void initEngine(Context context, OnFaceEngineInitListen faceEngineInitListen) {

        mLivenessError = mLivenessEngine.activeEngine(
                context,
                AppSetting.ARC_APP_ID,
                AppSetting.ARC_LIVENESS_KEY);

        if(mLivenessError.getCode() != ErrorInfo.MOK
                && mLivenessError.getCode() != ErrorInfo.MERR_AL_BASE_ALREADY_ACTIVATED){
            if(null != faceEngineInitListen){
                faceEngineInitListen.initError("活体检测激活失败 " + mLivenessError.getCode());
            }
            return;
        }

        mAfdFsdkError = mAfdFsdkEngine.
                AFD_FSDK_InitialFaceEngine(
                        AppSetting.ARC_APP_ID,
                        AppSetting.ARC_DETECT_KEY,
                        AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT,
                        16,
                        5);

        mAsaeFsdkError = mAsaeFsdkEngine.ASAE_FSDK_InitAgeEngine(
                AppSetting.ARC_APP_ID,
                AppSetting.ARC_AGE_KEY);

        mAfrFsdkError = mAfrFsdkEngine.AFR_FSDK_InitialEngine(
                AppSetting.ARC_APP_ID,
                AppSetting.ARC_FACERECOGN_KEY);

        mAftFsdkError = mAftFsdkEngine.AFT_FSDK_InitialFaceEngine(
                AppSetting.ARC_APP_ID,
                AppSetting.ARC_TRACK_KEY,
                AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT,
                16,
                5);

        mAsgeFsdkError = mAsgeFsdkEngine.ASGE_FSDK_InitgGenderEngine(
                AppSetting.ARC_APP_ID,
                AppSetting.ARC_GENDER_KEY);

        //活体检测支持静态图片和视频流，不过用于静态图片非常少就直接写视频流格式
        mLivenessError = mLivenessEngine.initEngine(
                context,
                LivenessEngine.AL_DETECT_MODE_VIDEO);

        if(null == faceEngineInitListen){
            return;
        }

        if (
                mAfdFsdkError.getCode() != AFD_FSDKError.MOK
                        && mAsaeFsdkError.getCode() != ASAE_FSDKError.MOK
                        && mAfrFsdkError.getCode() != AFR_FSDKError.MOK
                        && mAftFsdkError.getCode() != AFT_FSDKError.MOK
                        && mAsgeFsdkError.getCode() != ASGE_FSDKError.MOK
                        && mLivenessError.getCode() != ErrorInfo.MOK
                        && mLivenessError.getCode() != ErrorInfo.MERR_AL_BASE_ALREADY_ACTIVATED) {
            //其中一个没初始化成功则初始化失败
            Log(TAG, "引擎初始化失败 " +
                    "mAfdFsdkError" + mAfdFsdkError.getCode() + " " +
                    "mAsaeFsdkError" + mAsaeFsdkError.getCode() + " " +
                    "mAfrFsdkError" + mAfrFsdkError.getCode() + " " +
                    "mAftFsdkError" + mAftFsdkError.getCode() + " " +
                    "mAsgeFsdkError" + mAsgeFsdkError.getCode());

            faceEngineInitListen.initError("引擎初始化失败 " +
                    "mAfdFsdkError" + mAfdFsdkError.getCode() + " " +
                    "mAsaeFsdkError" + mAsaeFsdkError.getCode() + " " +
                    "mAfrFsdkError" + mAfrFsdkError.getCode() + " " +
                    "mAftFsdkError" + mAftFsdkError.getCode() + " " +
                    "mAsgeFsdkError" + mAsgeFsdkError.getCode() + " " +
                    "mLivenessError" + mLivenessError.getCode());
            return;
        }
        Log(TAG, "引擎初始化成功");
        faceEngineInitListen.initSuccess();
    }


    /**
     * 释放资源
     */
    public void destory() {
        if (null != mAfdFsdkEngine) {
            mAfdFsdkEngine.AFD_FSDK_UninitialFaceEngine();
        }
        if (null != mAsaeFsdkEngine) {
            mAsaeFsdkEngine.ASAE_FSDK_UninitAgeEngine();
        }
        if (null != mAfrFsdkEngine) {
            mAfrFsdkEngine.AFR_FSDK_UninitialEngine();
        }
        if (null != mAftFsdkEngine) {
            mAftFsdkEngine.AFT_FSDK_UninitialFaceEngine();
        }
        if (null != mAsgeFsdkEngine) {
            mAsgeFsdkEngine.ASGE_FSDK_UninitGenderEngine();
        }
        if (null != mLivenessEngine) {
            mLivenessEngine.unInitEngine();
        }
    }


    private ASAE_FSDKFace mAsaeFsdkFace = new ASAE_FSDKFace();
    private ASGE_FSDKFace mAsgeFsdkFace = new ASGE_FSDKFace();
    private List<ASAE_FSDKFace> mAsaeFsdkFaceList = new ArrayList<>();
    private List<ASGE_FSDKFace> mAsgeFsdkFaceList = new ArrayList<>();



    /**
     * 检测人脸静态图片
     *
     * @param bytes
     * @param width
     * @param height
     */
    public void detectFaceStaticImage(byte[] bytes,
                                      int width,
                                      int height,
                                      OnFaceDetectResult faceDetectResult) {

        if (null == faceDetectResult) {
            return;
        }
        mAfdFsdkFaceList.clear();
        //输入的data数据为NV21格式（如Camera里NV21格式的preview数据），其中height不能为奇数，人脸检测返回结果保存在result。
        mAfdFsdkError = mAfdFsdkEngine.AFD_FSDK_StillImageFaceDetection(
                bytes,
                width,
                height,
                AFD_FSDKEngine.CP_PAF_NV21,
                mAfdFsdkFaceList);
        if (mAfdFsdkError.getCode() != AFD_FSDKError.MOK) {
            Log(TAG, "人脸检测失败 " + mAfdFsdkError.getCode());
            faceDetectResult.detectError("人脸检测失败 " + mAfdFsdkError.getCode());
            return;
        }
        Log(TAG, "人脸检测成功 " + mAfdFsdkFaceList.toString());
        if (null != mAfdFsdkFaceList && mAfdFsdkFaceList.size() > 0) {
            //检测特征
            detectFaceFeatureImage(mAfdFsdkFaceList,bytes, width, height);
            //检测年龄，性别
            detectFaceInfoImage(mAfdFsdkFaceList, bytes, width, height, faceDetectResult);
        } else {
            faceDetectResult.detectNotFace();
        }
    }


    /**
     * 检测人脸面部信息（图片）
     *
     * @param mAfdFsdkFaceList
     * @param bytes
     * @param width
     * @param height
     * @param faceDetectResult
     */
    private void detectFaceInfoImage(List<AFD_FSDKFace> mAfdFsdkFaceList,
                                     byte[] bytes,
                                     int width,
                                     int height,
                                     OnFaceDetectResult faceDetectResult) {
        //清空数据
        mAsaeFsdkAgeList.clear();
        mAsgeFsdkGenderList.clear();
        mAsaeFsdkFaceList.clear();
        mAsgeFsdkFaceList.clear();
        mFaceInfoEntityList.clear();

        for (AFD_FSDKFace aftFsdkFace : mAfdFsdkFaceList) {
            ASAE_FSDKFace clone = mAsaeFsdkFace.clone();
            clone.setDegree(aftFsdkFace.getDegree());
            clone.setRect(aftFsdkFace.getRect());
            mAsaeFsdkFaceList.add(clone);
        }
        mAsaeFsdkError = mAsaeFsdkEngine.ASAE_FSDK_AgeEstimation_Image(
                bytes,
                width,
                height,
                ASAE_FSDKEngine.CP_PAF_NV21,
                mAsaeFsdkFaceList,
                mAsaeFsdkAgeList);

        for (AFD_FSDKFace aftFsdkFace : mAfdFsdkFaceList) {
            ASGE_FSDKFace clone = mAsgeFsdkFace.clone();
            clone.setDegree(aftFsdkFace.getDegree());
            clone.setRect(aftFsdkFace.getRect());
            mAsgeFsdkFaceList.add(clone);
        }

        //输入的data数据为NV21格式（如Camera里NV21格式的preview数据），其中height不能为奇数，人脸检测返回结果保存在result。
        mAsgeFsdkError = mAsgeFsdkEngine.ASGE_FSDK_GenderEstimation_Image(
                bytes,
                width,
                height,
                ASGE_FSDKEngine.CP_PAF_NV21,
                mAsgeFsdkFaceList,
                mAsgeFsdkGenderList);

        if (mAsgeFsdkError.getCode() != ASGE_FSDKError.MOK
                || mAsaeFsdkError.getCode() != ASAE_FSDKError.MOK) {
            Log(TAG, "人脸信息检测失败 " + mAsgeFsdkError.getCode() + " " +
                    mAsaeFsdkError.getCode());
            faceDetectResult.detectError("人脸信息检测失败 " + mAsgeFsdkError.getCode() +
                    " " + mAsaeFsdkError.getCode());
            return;
        }

        for (int index = 0; index < mAfdFsdkFaceList.size(); index++) {
            DetectFaceInfoEntity faceInfoEntity = new DetectFaceInfoEntity();
            faceInfoEntity.setFaceRect(mAfdFsdkFaceList.get(index).getRect());
            faceInfoEntity.setFaceDegree(mAfdFsdkFaceList.get(index).getDegree());
            faceInfoEntity.setFaceAge(mAsaeFsdkAgeList.get(index).getAge());
            faceInfoEntity.setFaceGender(mAsgeFsdkGenderList.get(index).getGender());
            faceInfoEntity.setFaceFeture(mFaceFetureList.get(index).getFeatureData());
            mFaceInfoEntityList.add(faceInfoEntity);
        }
        Log(TAG, "人脸信息检测成功 " + mFaceInfoEntityList.toString());
        faceDetectResult.detectResult(mFaceInfoEntityList);
    }


    /**
     * 检测人脸视频流
     *
     * @param bytes
     * @param width
     * @param height
     */
    public void detectFaceVideoStream(byte[] bytes,
                                      int width,
                                      int height,
                                      OnFaceDetectResult faceDetectResult) {
        mAftFsdkFaceList.clear();
        mAftFsdkError = mAftFsdkEngine.AFT_FSDK_FaceFeatureDetect(
                bytes,
                width,
                height,
                AFT_FSDKEngine.CP_PAF_NV21,
                mAftFsdkFaceList);

        if (mAftFsdkError.getCode() != AFT_FSDKError.MOK) {
            Log(TAG, "人脸检测失败" + mAftFsdkError.getCode());
            faceDetectResult.detectError("人脸检测失败 " + mAftFsdkError.getCode());
            return;
        }
        Log(TAG, "人脸检测成功 " + mAftFsdkFaceList.toString());
        if (null != mAftFsdkFaceList && mAftFsdkFaceList.size() > 0) {
            detectFaceFeatureVideo(mAftFsdkFaceList.get(0), bytes, width, height);
            detectFaceInfoStream(mAftFsdkFaceList, bytes, width, height, faceDetectResult);
        } else {
            faceDetectResult.detectNotFace();
        }
    }


    /**
     * 检测人脸面部信息（视频流）
     *
     * @param aftFsdkFaceList
     * @param bytes
     * @param width
     * @param height
     * @param faceDetectResult
     */
    private void detectFaceInfoStream(List<AFT_FSDKFace> aftFsdkFaceList,
                                      byte[] bytes,
                                      int width,
                                      int height,
                                      OnFaceDetectResult faceDetectResult) {
        mAsaeFsdkAgeList.clear();
        mAsgeFsdkGenderList.clear();
        mAsaeFsdkFaceList.clear();
        mAsgeFsdkFaceList.clear();
        mFaceInfoEntityList.clear();
        mLivenessInfoList.clear();
        mFaceInfoList.clear();


        for (AFT_FSDKFace aftFsdkFace : aftFsdkFaceList) {
            ASAE_FSDKFace clone = mAsaeFsdkFace.clone();
            clone.setDegree(aftFsdkFace.getDegree());
            clone.setRect(aftFsdkFace.getRect());
            mAsaeFsdkFaceList.add(clone);

            FaceInfo faceInfo = mFaceInfo.clone();
            faceInfo.setRect(aftFsdkFace.getRect());
            faceInfo.setDegree(aftFsdkFace.getDegree());
            mFaceInfoList.add(faceInfo);
        }

        mLivenessError = mLivenessEngine.startLivenessDetect(
                bytes,
                width,
                height,
                LivenessEngine.CP_PAF_NV21,
                mFaceInfoList,
                mLivenessInfoList);

        mAsaeFsdkError = mAsaeFsdkEngine.ASAE_FSDK_AgeEstimation_Video(
                bytes,
                width,
                height,
                ASAE_FSDKEngine.CP_PAF_NV21,
                mAsaeFsdkFaceList,
                mAsaeFsdkAgeList);

        for (AFT_FSDKFace aftFsdkFace : aftFsdkFaceList) {
            ASGE_FSDKFace clone = mAsgeFsdkFace.clone();
            clone.setDegree(aftFsdkFace.getDegree());
            clone.setRect(aftFsdkFace.getRect());
            mAsgeFsdkFaceList.add(clone);
        }

        //输入的data数据为NV21格式（如Camera里NV21格式的preview数据），其中height不能为奇数，人脸检测返回结果保存在result。
        mAsgeFsdkError = mAsgeFsdkEngine.ASGE_FSDK_GenderEstimation_Video(
                bytes,
                width,
                height,
                ASGE_FSDKEngine.CP_PAF_NV21,
                mAsgeFsdkFaceList,
                mAsgeFsdkGenderList);

        if (mAsgeFsdkError.getCode() != ASGE_FSDKError.MOK ||
                mAsaeFsdkError.getCode() != ASAE_FSDKError.MOK ||
                mLivenessError.getCode() != ErrorInfo.MOK) {
            Log(TAG, "人脸信息检测失败 " + mAsgeFsdkError.getCode() + " " + mAsaeFsdkError.getCode());
            faceDetectResult.detectError("人脸信息检测失败 " + mAsgeFsdkError.getCode() + " " + mAsaeFsdkError.getCode());
            return;
        }

        for (int index = 0; index < aftFsdkFaceList.size(); index++) {
            DetectFaceInfoEntity faceInfoEntity = new DetectFaceInfoEntity();
            faceInfoEntity.setFaceRect(aftFsdkFaceList.get(index).getRect());
            faceInfoEntity.setFaceDegree(aftFsdkFaceList.get(index).getDegree());
            faceInfoEntity.setFaceAge(mAsaeFsdkAgeList.get(index).getAge());
            faceInfoEntity.setFaceGender(mAsgeFsdkGenderList.get(index).getGender());
            if(mLivenessInfoList.size()>0){
                faceInfoEntity.setLivenessCode(mLivenessInfoList.get(index).getLiveness());
            }
            faceInfoEntity.setFaceFeture(mFaceFetureList.get(index).getFeatureData());
            mFaceInfoEntityList.add(faceInfoEntity);
        }
        Log(TAG, "人脸信息检测成功 " + mFaceInfoEntityList.toString());
        faceDetectResult.detectResult(mFaceInfoEntityList);

    }


    private AFR_FSDKFace mAfrFsdkFace = new AFR_FSDKFace();
    private List<AFR_FSDKFace> mFaceFetureList = new ArrayList<>();

    /**
     * 检测人脸的特征(静态图片)
     *
     * @param afdFsdkFaceList
     * @param bytes
     * @param width
     * @param height
     */
    public void detectFaceFeatureImage(List<AFD_FSDKFace> afdFsdkFaceList,
                                       final byte[] bytes,
                                       final int width,
                                       final int height) {
        //清除数据
        mFaceFetureList.clear();
        //循环检测每一个人脸特征
        for (final AFD_FSDKFace afdFsdkFace : afdFsdkFaceList){
            final AFR_FSDKFace clone = mAfrFsdkFace.clone();
            mAfrFsdkError = mAfrFsdkEngine.AFR_FSDK_ExtractFRFeature(
                    bytes,
                    width,
                    height,
                    AFR_FSDKEngine.CP_PAF_NV21,
                    afdFsdkFace.getRect(),
                    afdFsdkFace.getDegree(),
                    clone);

            if (mAfrFsdkError.getCode() != AFR_FSDKError.MOK) {
                Log(TAG, "检测特征失败 " + mAfrFsdkError.getCode());
                mFaceFetureList.add(new AFR_FSDKFace());
            }else {
                Log(TAG, "检测特征成功 " + clone.getFeatureData());
                mFaceFetureList.add(clone);
            }
        }
    }


    public void detectFaceFeatureVideo(AFT_FSDKFace aftFsdkFace,
                                       byte[] bytes,
                                       int width,
                                       int height) {
        mFaceFetureList.clear();
        AFR_FSDKFace clone = mAfrFsdkFace.clone();
        mAfrFsdkError = mAfrFsdkEngine.AFR_FSDK_ExtractFRFeature(
                    bytes,
                    width,
                    height,
                    AFR_FSDKEngine.CP_PAF_NV21,
                    aftFsdkFace.getRect(),
                    aftFsdkFace.getDegree(),
                    clone);

            if (mAfrFsdkError.getCode() != AFR_FSDKError.MOK) {
                Log(TAG, "检测特征失败 " + mAfrFsdkError.getCode());
                mFaceFetureList.add(new AFR_FSDKFace());
            }else {
                Log(TAG, "检测特征成功 " + clone.getFeatureData());
                mFaceFetureList.add(clone);
            }
    }


    /**
     * 人脸比对结果
     */
    private AFR_FSDKMatching afrFsdkMatching = new AFR_FSDKMatching();

    /**
     * 人脸比对
     *
     * @param afrFsdkFace1
     * @param afrFsdkFace2
     */
    public float faceComparison(AFR_FSDKFace afrFsdkFace1, AFR_FSDKFace afrFsdkFace2) {
        mAfrFsdkError = mAfrFsdkEngine.AFR_FSDK_FacePairMatching(
                afrFsdkFace1,
                afrFsdkFace2,
                afrFsdkMatching);
        if (mAfrFsdkError.getCode() != AFR_FSDKError.MOK) {
            Log(TAG, "比对失败");
            return 0;
        }
        Log(TAG, "比对成功 " + afrFsdkMatching.getScore());
        return afrFsdkMatching.getScore();
    }


    /*日志*/
    public void setLogSwitch(boolean isOpenLogSwitch){
        this.isOpenLogSwitch = isOpenLogSwitch;
    }
    private boolean isOpenLogSwitch;
    private void Log(String tag,String text){
        if(isOpenLogSwitch){
            Log.d(tag,text);
        }
    }


}
