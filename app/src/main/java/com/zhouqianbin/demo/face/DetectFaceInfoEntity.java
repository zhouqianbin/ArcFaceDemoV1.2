package com.zhouqianbin.demo.face;

import android.graphics.Rect;

import java.util.Arrays;

public class DetectFaceInfoEntity {

    private Rect faceRect;

    private int faceDegree;

    private byte[] faceFeture;

    private int faceAge;

    private int faceGender;

    private int livenessCode;

    public int getLivenessCode() {
        return livenessCode;
    }

    public void setLivenessCode(int livenessCode) {
        this.livenessCode = livenessCode;
    }

    public Rect getFaceRect() {
        return faceRect;
    }

    public void setFaceRect(Rect faceRect) {
        this.faceRect = faceRect;
    }

    public int getFaceDegree() {
        return faceDegree;
    }

    public void setFaceDegree(int faceDegree) {
        this.faceDegree = faceDegree;
    }

    public byte[] getFaceFeture() {
        return faceFeture;
    }

    public void setFaceFeture(byte[] faceFeture) {
        this.faceFeture = faceFeture;
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

    @Override
    public String toString() {
        return "DetectFaceInfoEntity{" +
                "faceRect=" + faceRect +
                ", faceDegree=" + faceDegree +
                ", faceFeture=" + Arrays.toString(faceFeture) +
                ", faceAge=" + faceAge +
                ", faceGender=" + faceGender +
                ", livenessCode=" + livenessCode +
                '}';
    }
}


