package com.example.campusapp;

import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.campusapp.activity.AddLostFoundActivity;
import com.example.campusapp.activity.SearchActivity;
import com.example.campusapp.activity.fragment.LostFoundFragment;
import com.example.campusapp.activity.fragment.MessageWallFragment;
import com.example.campusapp.activity.fragment.MyFragment;
import com.example.campusapp.activity.fragment.ScheduleFragment;
import com.example.campusapp.util.ToastUtil;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends FragmentActivity {

    BoomMenuButton boomMenuButton;
    Window window;
    BottomBar bottomBar;

    @BindView(R.id.viewPager)
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        boomMenuButton = (BoomMenuButton) findViewById(R.id.boom);
        bottomBar = findViewById(R.id.bottomBar);
        initViews();

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        window = getWindow();

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_classTable) {
                    viewPager.setCurrentItem(0);
                    window.setStatusBarColor(0xff3A82F8);
                    boomMenuButton.setNormalColor(0xff3A82F8);
                }else if(tabId == R.id.tab_lostFound){
                    viewPager.setCurrentItem(1);
                    window.setStatusBarColor(0xffF46538);
                    boomMenuButton.setNormalColor(0xffF46538);
                }else if(tabId == R.id.tab_info){
                    viewPager.setCurrentItem(2);
                    window.setStatusBarColor(0xffEA2B68);
                    boomMenuButton.setNormalColor(0xffEA2B68);
                }else if(tabId == R.id.tab_my){
                    viewPager.setCurrentItem(3);
                    window.setStatusBarColor(0xffF1C12F);
                    boomMenuButton.setNormalColor(0xffF1C12F);
                }
            }
        });

        int[] subButtonColors = {
                0xffAA00FF,
                0xff2962FF,
                0xff00C853,
                0xffFF0808,
                0xffFF6D00,
                0xff2962FF
        };

        int imgs[] = {
                R.drawable.photo,
                R.drawable.category,
                R.drawable.tag,
                R.drawable.hot,
                R.drawable.recommend,
                R.drawable.setting
        };

        String texts[] = {
                "上传失物招领",
                "上传信息墙",
                "搜索失物招领",
                "搜索信息墙",
                "好友",
                "设置"
        };

        for (int i = 0; i < boomMenuButton.getButtonPlaceEnum().buttonNumber(); i++) {
            boomMenuButton.addBuilder(new TextOutsideCircleButton.Builder()
                    .normalImageRes(imgs[i])
                    .normalText(texts[i])
                    .normalColor(subButtonColors[i])
                    .textSize(13)
//                    .isRound(false)
//                    .shadowCornerRadius(Util.dp2px(15))
//                    .buttonCornerRadius(Util.dp2px(15))
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int buttonIndex) {
//                            Toast.makeText(getBaseContext(), "Clicked " + buttonIndex, Toast.LENGTH_SHORT).show();
                            // When the boom-button corresponding this builder is clicked.
                            if (buttonIndex == 0) {
                                Intent intent = new Intent(getBaseContext(), AddLostFoundActivity.class);
                                intent.putExtra("type", 1);
                                startActivity(intent);
                            } else if (buttonIndex == 1) {
                                Intent intent = new Intent(getBaseContext(), AddLostFoundActivity.class);
                                intent.putExtra("type", 2);
                                startActivity(intent);
                            } else if (buttonIndex == 2) {
                                Intent t = new Intent(getBaseContext(), SearchActivity.class);
                                startActivity(t);
                            } else if (buttonIndex == 3) {
                                Intent t = new Intent(getBaseContext(), SearchActivity.class);
                                startActivity(t);
                            } else if (buttonIndex == 4) {
                                Intent t = new Intent(getBaseContext(), TestActivity.class);
                                startActivity(t);
                            } else if (buttonIndex == 5) {
                                bottomBar.selectTabAtPosition(3);
                                viewPager.setCurrentItem(3);
                                window.setStatusBarColor(0xffF1C12F);
                                boomMenuButton.setNormalColor(0xffF1C12F);
                            }
                        }
                    }));
        }


    }

    private void initViews() {
        List<Fragment> fragmentList = new ArrayList<>();
        ScheduleFragment scheduleFragment = new ScheduleFragment();
        LostFoundFragment lostFoundFragment = new LostFoundFragment();
        MessageWallFragment messageWallFragment = new MessageWallFragment();
        MyFragment myFragment = new MyFragment();

        fragmentList.add(scheduleFragment);
        fragmentList.add(lostFoundFragment);
        fragmentList.add(messageWallFragment);
        fragmentList.add(myFragment);

        viewPager.setAdapter(new MainMenuFragmentAdapter(getSupportFragmentManager(), fragmentList, this));
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    System.out.println("scroll to page ");
                    viewPager.setCurrentItem(0);
                    window.setStatusBarColor(0xff3A82F8);
                    boomMenuButton.setNormalColor(0xff3A82F8);
                    bottomBar.selectTabAtPosition(0);
                } else if (position == 1) {
                    viewPager.setCurrentItem(1);
                    window.setStatusBarColor(0xffF46538);
                    boomMenuButton.setNormalColor(0xffF46538);
                    bottomBar.selectTabAtPosition(1);
                } else if (position == 2) {
                    viewPager.setCurrentItem(2);
                    window.setStatusBarColor(0xffEA2B68);
                    boomMenuButton.setNormalColor(0xffEA2B68);
                    bottomBar.selectTabAtPosition(2);
                } else if (position == 3) {
                    viewPager.setCurrentItem(3);
                    window.setStatusBarColor(0xffF1C12F);
                    boomMenuButton.setNormalColor(0xffF1C12F);
                    bottomBar.selectTabAtPosition(3);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void changeIconImgBottomMargin(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                changeIconImgBottomMargin((ViewGroup) child);
            } else if (child instanceof ImageView) {
                ViewGroup.MarginLayoutParams lp = ((ViewGroup.MarginLayoutParams) child.getLayoutParams());
                lp.bottomMargin = 4;
                child.requestLayout();
            }
        }
    }

    class MainMenuFragmentAdapter extends FragmentPagerAdapter {

        List<Fragment> flist;
        Context mContext;


        public MainMenuFragmentAdapter(FragmentManager fm, List<Fragment> list, Context context) {
            super(fm);
            this.flist = list;
            this.mContext = context;
        }


        @Override
        public Fragment getItem(int arg0) {
            // TODO Auto-generated method stub

            Fragment fragment = flist.get(arg0);
            return fragment;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return flist.size();
        }

    }


    /**
     * 连续按两次返回键就退出
     */
    private int keyBackClickCount = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (keyBackClickCount++) {
                case 0:
                    ToastUtil.show(this, getResources().getString(R.string.press_again_exit));
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            keyBackClickCount = 0;
                        }
                    }, 3000);
                    break;
                case 1:
                    finish();
                    System.exit(0);
                    break;
                default:
                    break;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
