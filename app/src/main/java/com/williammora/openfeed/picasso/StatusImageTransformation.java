package com.williammora.openfeed.picasso;

import android.graphics.Bitmap;
import android.view.View;

public class StatusImageTransformation extends AbstractTransformation {

    public StatusImageTransformation(View holder, long statusId) {
        super(holder, statusId);
    }

    @Override
    protected float getAspectRatio(Bitmap source) {
        return (float) source.getHeight() / (float) source.getWidth();
    }

}
