package com.zhouqianbin.demo;

import android.app.Application;

import com.arcsoft.liveness.LivenessEngine;
import com.blankj.utilcode.util.Utils;
import com.zhouqianbin.demo.face.ArcFaceEngine;
import com.zhouqianbin.demo.face.ArcLivenessEngine;
import com.zhouqianbin.demo.face.OnFaceEngineInitListen;

import org.litepal.LitePal;

/**
 * @Copyright (C), 2018, 漳州科能电器有限公司
 * @FileName: FaceApplication
 * @Author: 周千滨
 * @Date: 2018/11/30 9:07
 * @Description:
 * @Version: 1.0.0
 * @UpdateHistory: 修改历史
 * @修改人: 周千滨
 * @修改描述: 创建文件
 */

public class FaceApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        Utils.init(this.getApplicationContext());


    }



}
