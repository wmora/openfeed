package com.williammora.openfeed.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
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

public class Snackbar extends RelativeLayout {

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

    public enum SnackbarDuration {
        LENGTH_SHORT(2000), LENGTH_LONG(3500);

        private long duration;

        SnackbarDuration(long duration) {
            this.duration = duration;
        }

        public long getDuration() {
            return duration;
        }
    }

    private SnackbarType mType = SnackbarType.SINGLE_LINE;
    private SnackbarDuration mDuration = SnackbarDuration.LENGTH_LONG;
    private CharSequence mText;
    private int mColor = 0xff323232;
    private int mTextColor = Color.WHITE;
    private CharSequence mActionLabel;
    private int mActionColor = Color.GREEN;
    private OnClickListener mActionListener;
    private boolean mAnimated = true;

    private Snackbar(Context context) {
        super(context);
    }

    public static Snackbar with(Context context) {
        return new Snackbar(context);
    }

    public Snackbar type(SnackbarType type) {
        mType = type;
        return this;
    }

    public Snackbar text(CharSequence text) {
        mText = text;
        return this;
    }

    public Snackbar color(int color) {
        mColor = color;
        return this;
    }

    public Snackbar textColor(int textColor) {
        mTextColor = textColor;
        return this;
    }

    public Snackbar actionLabel(CharSequence actionButtonLabel) {
        mActionLabel = actionButtonLabel;
        return this;
    }

    public Snackbar actionColor(int actionColor) {
        mActionColor = actionColor;
        return this;
    }

    public Snackbar actionListener(OnClickListener listener) {
        mActionListener = listener;
        return this;
    }

    public Snackbar animation(boolean withAnimation) {
        mAnimated = withAnimation;
        return this;
    }

    public Snackbar duration(SnackbarDuration duration) {
        mDuration = duration;
        return this;
    }

    private void init(Activity parent) {
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(parent)
                .inflate(R.layout.snackbar, this, true);

        layout.setBackgroundColor(mColor);

        float scale = parent.getResources().getDisplayMetrics().density;
        int height = (int) (mType.getHeight() * scale + 0.5f);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height);

        layout.setLayoutParams(params);

        TextView snackbarText = (TextView) layout.findViewById(R.id.snackbar_text);
        snackbarText.setText(mText);
        snackbarText.setTextColor(mTextColor);
        snackbarText.setMaxLines(mType.getMaxLines());

        TextView snackbarAction = (TextView) layout.findViewById(R.id.snackbar_action);
        if (!TextUtils.isEmpty(mActionLabel)) {
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

    public void show(Activity targetActivity) {
        init(targetActivity);
        ViewGroup root = (ViewGroup) targetActivity.findViewById(android.R.id.content);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        root.addView(this, params);

        if (mAnimated) {
            startSnackbarAnimation();
        } else {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            }, mDuration.getDuration());
        }
    }

    private void startSnackbarAnimation() {
        Animation slideIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_bottom);
        Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        fadeOut.setStartOffset(mDuration.getDuration());
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
}
