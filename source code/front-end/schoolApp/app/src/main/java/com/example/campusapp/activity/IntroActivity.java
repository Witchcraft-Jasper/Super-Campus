package com.example.campusapp.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.campusapp.LoginActivity;
import com.example.campusapp.R;
import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;

public class IntroActivity extends AppIntro {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addSlide(AppIntroFragment.newInstance("Welcome to Super Campus!",
                "It's a COOL campus app!",
                R.drawable.ic_slide1,
                0xFF3F7A63
        ));

        addSlide(AppIntroFragment.newInstance(
                "Get All Campus Info in One App ",
                "This app offers students the 'lost and found' part and the 'share your life' part.",
                R.drawable.ic_slide2,
                0xFFF29089
        ));


        addSlide(AppIntroFragment.newInstance(
                "Simple, yet useful",
                "The app offers you to release and find lost and found details of your campus, while identifying the object content.",
                R.drawable.ic_slide3,
                0xFFA08FD5
        ));

        addSlide(AppIntroFragment.newInstance(
                "Cool, and interesting",
                "Feel free to share your life in our app!",
                R.drawable.ic_slide4,
                0xFFF5E866
        ));

        // Fade Transition
        setTransformer(AppIntroPageTransformerType.Depth.INSTANCE);

        // Show/hide status bar
        showStatusBar(true);

        //Speed up or down scrolling
        setScrollDurationFactor(2);

        //Enable the color "fade" animation between two slides (make sure the slide implements SlideBackgroundColorHolder)
        setColorTransitionsEnabled(true);

        //Prevent the back button from exiting the slides
        setSystemBackButtonLocked(true);

        //Activate wizard mode (Some aesthetic changes)
        setWizardMode(true);

        //Show/hide skip button
        setSkipButtonEnabled(true);

        //Enable immersive mode (no status and nav bar)
        setImmersiveMode();

        //Enable/disable page indicators
        setIndicatorEnabled(true);

        //show/hide ALL buttons
        setButtonsEnabled(true);
    }

    @Override
    protected void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();

        Intent t = new Intent(IntroActivity.this, LoginActivity.class);
        startActivity(t);
    }

    @Override
    protected void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();

        Intent t = new Intent(IntroActivity.this, LoginActivity.class);
        startActivity(t);
    }
}