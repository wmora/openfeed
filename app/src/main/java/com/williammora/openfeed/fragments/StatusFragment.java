package com.williammora.openfeed.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.williammora.openfeed.R;
import com.williammora.openfeed.activities.StatusActivity;

import twitter4j.Status;

public class StatusFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        Status mStatus = (Status) getActivity().getIntent().getSerializableExtra(StatusActivity.EXTRA_STATUS);

        TextView mStatusText = (TextView) view.findViewById(R.id.status_text);
        mStatusText.setText(mStatus.getText());

        return view;
    }

}
