package com.williammora.openfeed.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.williammora.openfeed.R;

public class ComposeStatusFragment extends Fragment {

    private static final String SAVED_TEXT = "SAVED_TEXT";

    private static final int CHARACTER_LIMIT = 140;

    private EditText mComposeText;
    private TextView mCharactersLeft;

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

        return view;
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
