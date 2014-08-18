package com.williammora.openfeed.widgets;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.williammora.openfeed.R;
import com.williammora.openfeed.listeners.SwipeDismissTouchListener;

public final class Snackbar extends RelativeLayout {

    public enum SnackbarType {
        SINGLE_LINE(48, 1), MULTI_LINE(80, 2);

        private int height;
        private int maxLines;

        SnackbarType(int height, int maxLines) {
            this.height = height;
            this.maxLines = maxLines;
        }

        public int getHeight() {
            return height;
        }

        public int getMaxLines() {
            return maxLines;
        }
    }

    private Activity mContext;
    private SnackbarType mType;
    private String mText;
    private int mColor;
    private int mTextColor;
    private boolean mHasAction;
    private String mActionLabel;
    private int mActionColor;
    private OnClickListener mActionListener;

    private Snackbar(Builder builder) {
        super(builder.mActivity);
        mContext = builder.mActivity;
        mType = builder.mType;
        mText = builder.mText;
        mColor = builder.mColor;
        mTextColor = builder.mTextColor;
        mHasAction = builder.mHasAction;
        mActionLabel = builder.mActionLabel;
        mActionColor = builder.mActionColor;
        mActionListener = builder.mActionListener;
        init();
    }

    private void init() {
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(mContext)
                .inflate(R.layout.snackbar, this, true);

        layout.setBackgroundColor(mColor);

        float scale = mContext.getResources().getDisplayMetrics().density;
        int height = (int) (mType.getHeight() * scale + 0.5f);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height);

        layout.setLayoutParams(params);

        TextView snackbarText = (TextView) layout.findViewById(R.id.snackbar_text);
        snackbarText.setText(mText);
        snackbarText.setTextColor(mTextColor);
        snackbarText.setMaxLines(mType.getMaxLines());

        TextView snackbarAction = (TextView) layout.findViewById(R.id.snackbar_action);
        if (mHasAction && !mActionLabel.isEmpty()) {
            snackbarAction.setText(mActionLabel);
            snackbarAction.setTextColor(mActionColor);
            snackbarAction.setOnClickListener(mActionListener);
        } else {
            snackbarAction.setVisibility(GONE);
        }

        setClickable(true);

        setOnTouchListener(new SwipeDismissTouchListener(this, null,
                new SwipeDismissTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(Object token) {
                        return true;
                    }

                    @Override
                    public void onDismiss(View view, Object token) {
                        if (view != null) {
                            dismiss();
                        }
                    }
                }));
    }

    public void show() {
        ViewGroup root = (ViewGroup) mContext.findViewById(android.R.id.content);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        root.addView(this, params);
        Animation slideIn = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_from_bottom);
        Animation fadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
        fadeOut.setStartOffset(3000);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(slideIn);
        animationSet.addAnimation(fadeOut);
        startAnimation(animationSet);
    }

    private void dismiss() {
        clearAnimation();
        ((ViewGroup) getParent()).removeView(this);
    }

    public static class Builder {

        private final Activity mActivity;
        private SnackbarType mType = SnackbarType.SINGLE_LINE;
        private String mText;
        private int mColor = 0xff323232;
        private int mTextColor = Color.WHITE;
        private boolean mHasAction = false;
        private String mActionLabel;
        private int mActionColor = Color.GREEN;
        private OnClickListener mActionListener;

        public Builder(Activity context) {
            mActivity = context;
        }

        public Builder withType(SnackbarType type) {
            mType = type;
            return this;
        }

        public Builder withText(String text) {
            mText = text;
            return this;
        }

        public Builder withColor(int color) {
            mColor = color;
            return this;
        }

        public Builder withTextColor(int textColor) {
            mTextColor = textColor;
            return this;
        }

        public Builder withActionButton(boolean hasAction) {
            mHasAction = hasAction;
            return this;
        }

        public Builder withActionLabel(String actionButtonLabel) {
            mActionLabel = actionButtonLabel;
            return this;
        }

        public Builder withActionColor(int actionColor) {
            mActionColor = actionColor;
            return this;
        }

        public Builder withActionClickListener(OnClickListener listener) {
            mActionListener = listener;
            return this;
        }

        public Snackbar build() {
            return new Snackbar(this);
        }

    }

}
