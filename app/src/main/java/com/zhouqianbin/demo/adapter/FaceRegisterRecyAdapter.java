package com.zhouqianbin.demo.adapter;

import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhouqianbin.demo.R;
import com.zhouqianbin.demo.entity.FaceInfoEntity;
import com.zhouqianbin.demo.face.DetectFaceInfoEntity;

import java.util.List;

/**
 * @Copyright (C), 2018, 漳州科能电器有限公司
 * @FileName: FaceRegisterRecyAdapter
 * @Author: 周千滨
 * @Date: 2018/12/18 16:08
 * @Description:
 * @Version: 1.0.0
 * @UpdateHistory: 修改历史
 * @修改人: 周千滨
 * @修改描述: 创建文件
 */

public class FaceRegisterRecyAdapter extends BaseQuickAdapter<FaceInfoEntity, BaseViewHolder> {

    public FaceRegisterRecyAdapter(int layoutResId, @Nullable List<FaceInfoEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FaceInfoEntity item) {
        ImageView imageView = helper.getView(R.id.item_face_regis_img_user);
        byte[] faceImage = item.getFaceImage();
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(faceImage, 0, faceImage.length));

        helper.setText(R.id.item_face_regis_tv_name, item.getFaceName());
        helper.setText(R.id.item_face_regis_tv_age, String.valueOf(item.getFaceAge()));
        switch (item.getFaceGender()) {
            case 0:
                helper.setText(R.id.item_face_regis_tv_gender, "男");
                break;
            case 1:
                helper.setText(R.id.item_face_regis_tv_gender, "女");
                break;
            default:
                helper.setText(R.id.item_face_regis_tv_gender, "未知");
                break;
        }
    }


}
