package com.zhouqianbin.demo.entity;

import org.litepal.crud.LitePalSupport;

import java.util.Arrays;

/**
 * @Copyright (C), 2018, 漳州科能电器有限公司
 * @FileName: FaceInfoEntity
 * @Author: 周千滨
 * @Date: 2018/12/18 16:13
 * @Description:
 * @Version: 1.0.0
 * @UpdateHistory: 修改历史
 * @修改人: 周千滨
 * @修改描述: 创建文件
 */

public class FaceInfoEntity extends LitePalSupport{

    private int id;
    //人脸姓名
    private String faceName;
    //人脸年龄
    private int faceAge;
    //人脸性别
    private int faceGender;
    //人脸特征
    private byte[] faceFeture;
    //人脸注册图片
    private byte[] faceImage;

    public String getFaceName() {
        return faceName;
    }

    public void setFaceName(String faceName) {
        this.faceName = faceName;
    }

    public int getFaceAge() {
        return faceAge;
    }

    public void setFaceAge(int faceAge) {
        this.faceAge = faceAge;
    }

    public int getFaceGender() {
        return faceGender;
    }

    public void setFaceGender(int faceGender) {
        this.faceGender = faceGender;
    }

    public byte[] getFaceFeture() {
        return faceFeture;
    }

    public void setFaceFeture(byte[] faceFeture) {
        this.faceFeture = faceFeture;
    }

    public byte[] getFaceImage() {
        return faceImage;
    }

    public void setFaceImage(byte[] faceImage) {
        this.faceImage = faceImage;
    }

    @Override
    public String toString() {
        return "FaceInfoEntity{" +
                "faceName='" + faceName + '\'' +
                ", faceAge=" + faceAge +
                ", faceGender=" + faceGender +
                ", faceFeture=" + Arrays.toString(faceFeture) +
                ", faceImage=" + Arrays.toString(faceImage) +
                '}';
    }
}
