package com.example.campusapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;

import com.example.campusapp.LoginActivity;
import com.example.campusapp.R;
import com.daimajia.androidanimations.library.Techniques;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

public class IndexSplashActivity extends AwesomeSplash {


    SharedPreferences sharedPreferences;

    //DO NOT OVERRIDE onCreate()!
    //if you need to start some services do it in initSplash()!

    @Override
    public void initSplash(ConfigSplash configSplash) {

        /* you don't have to override every property */

        //Customize Circular Reveal
        configSplash.setBackgroundColor(R.color.app_head_color); //any color you want form colors.xml
        configSplash.setAnimCircularRevealDuration(2000); //int ms
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);  //or Flags.REVEAL_LEFT
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM); //or Flags.REVEAL_TOP

        //Choose LOGO OR PATH; if you don't provide String value for path it's logo by default

        //Customize Logo
        configSplash.setLogoSplash(R.drawable.super_logo); //or any other drawable
        configSplash.setAnimLogoSplashDuration(1000); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.FadeInUp); //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)

        //Customize Title
        configSplash.setTitleSplash("Super Campus");
        configSplash.setTitleTextColor(R.color.white);
        configSplash.setTitleTextSize(45f); //float value
        configSplash.setAnimTitleDuration(1000);
        configSplash.setAnimTitleTechnique(Techniques.FadeInUp);
        configSplash.setTitleFont("iconfont.ttf"); //provide string to your font located in assets/fonts/
    }

    @Override
    public void animationsFinished() {
        //transit to another activity here
        //or do whatever you want

        //程序第一次运行
        sharedPreferences = getSharedPreferences("count", MODE_PRIVATE);
        int count = sharedPreferences.getInt("count", 0);

        if(count == 0){
            Intent t = new Intent(this,IntroActivity.class);
            startActivity(t);
            this.finish();
        }else{
            Intent t = new Intent(this,LoginActivity.class);
            startActivity(t);
            this.finish();
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("count", ++count);
        editor.remove("count");
        editor.commit();
    }
}