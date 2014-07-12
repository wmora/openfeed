package com.williammora.openfeed.picasso;

import android.graphics.Bitmap;
import android.view.View;

import com.squareup.picasso.Transformation;

public abstract class AbstractTransformation implements Transformation {

    protected View mHolder;
    protected long id;

    public AbstractTransformation(View holder, long statusId) {
        mHolder = holder;
        id = statusId;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        float aspectRatio = getAspectRatio(source);
        int width = mHolder.getWidth();
        int height = (int) (width * aspectRatio);
        Bitmap transformedBitmap = Bitmap.createScaledBitmap(source, width, height, false);
        if (transformedBitmap != source) {
            source.recycle();
        }
        return transformedBitmap;
    }

    protected abstract float getAspectRatio(Bitmap source);

    @Override
    public String key() {
        return String.format("%s_%d_%d", getClass().getSimpleName(), id, mHolder.getId());
    }

}
