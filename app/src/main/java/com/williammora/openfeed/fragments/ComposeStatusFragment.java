package com.williammora.openfeed.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.williammora.openfeed.R;
import com.williammora.openfeed.services.TwitterService;

import twitter4j.StatusUpdate;

public class ComposeStatusFragment extends Fragment {

    private static final String SAVED_TEXT = "SAVED_TEXT";

    public interface ComposeStatusFragmentListener {
        public void onStatusUpdateRequested();
    }

    private static final int CHARACTER_LIMIT = 140;

    private EditText mComposeText;
    private TextView mCharactersLeft;
    private ComposeStatusFragmentListener mListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose_status, container, false);

        mComposeText = (EditText) view.findViewById(R.id.compose_text);

        if (savedInstanceState != null) {
            mComposeText.setText(savedInstanceState.getString(SAVED_TEXT));
        }

        mComposeText.addTextChangedListener(new ComposeStatusTextWatcher());
        mCharactersLeft = (TextView) view.findViewById(R.id.characters_left_indicator);

        CardView sendButton = (CardView) view.findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StatusUpdate statusUpdate = new StatusUpdate(getStatusText());
                TwitterService.getInstance().updateStatus(statusUpdate);
                mListener.onStatusUpdateRequested();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ComposeStatusFragmentListener) {
            mListener = (ComposeStatusFragmentListener) activity;
        } else {
            throw new ClassCastException("You must implement ComposeStatusFragmentListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCharactersLeft();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_TEXT, mComposeText.getText().toString());
    }

    private void updateCharactersLeft() {
        int charactersLeft = CHARACTER_LIMIT - mComposeText.getText().length();
        mCharactersLeft.setText(getString(R.string.compose_characters_left, charactersLeft,
                charactersLeft == 1 ? getString(R.string.compose_characters_singular) :
                        getString(R.string.compose_characters_plural)));
    }

    public String getStatusText() {
        return mComposeText.getText().toString();
    }

    private class ComposeStatusTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            updateCharactersLeft();
        }
    }
}
