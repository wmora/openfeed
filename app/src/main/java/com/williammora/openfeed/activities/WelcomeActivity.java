package com.williammora.openfeed.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.williammora.openfeed.R;
import com.williammora.openfeed.fragments.WelcomeFragment;

public class WelcomeActivity extends Activity implements WelcomeFragment.WelcomeInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new WelcomeFragment())
                    .commit();
        }
        setTitle(R.string.title_activity_welcome);
    }

    @Override
    public void onTwitterSignIn() {
        goToHome();
    }

    private void goToHome() {
        Intent intent = new Intent();
        intent.setClass(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
