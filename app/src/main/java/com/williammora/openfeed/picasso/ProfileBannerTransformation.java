package com.williammora.openfeed.picasso;

import android.graphics.Bitmap;
import android.view.View;

public class ProfileBannerTransformation extends AbstractTransformation {

    public ProfileBannerTransformation(View holder, long statusId) {
        super(holder, statusId);
    }

    @Override
    protected float getAspectRatio(Bitmap source) {
        return 1 / 2f; // Mobile retina aspect ratio
    }


}
