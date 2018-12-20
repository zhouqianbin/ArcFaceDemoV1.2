package com.zhouqianbin.demo.face;

import java.util.List;

public interface OnFaceDetectResult {

    /**
     * 人脸检测结果
     * @param faceInfoEntityList
     */
    void detectResult(List<DetectFaceInfoEntity> faceInfoEntityList);

    /**
     * 检测出错
     * @param errorMsg
     */
    void detectError(String errorMsg);

    /**
     * 检测不到人脸
     */
    void detectNotFace();

}
