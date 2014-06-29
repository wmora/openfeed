package com.williammora.openfeed.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.williammora.openfeed.R;

public class WelcomeFragment extends Fragment {

    private WelcomeInterface mListener;

    public interface WelcomeInterface {
        public void onTwitterSignIn();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);

        setUpTwitterButton(rootView);

        return rootView;
    }

    private void setUpTwitterButton(View view) {
        CardView twitterSignIn = (CardView) view.findViewById(R.id.twitter_sign_in_button);
        twitterSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onTwitterSignIn();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof WelcomeInterface) {
            mListener = (WelcomeInterface) activity;
        }
        super.onAttach(activity);
    }

}
