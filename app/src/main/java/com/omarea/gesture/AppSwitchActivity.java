package com.omarea.gesture;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.omarea.gesture.util.GlobalState;

import java.lang.reflect.Method;

public class AppSwitchActivity extends Activity {
    public static Intent getOpenAppIntent(final AccessibilityServiceGesture accessibilityService) {
        Intent intent = new Intent(accessibilityService, AppSwitchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    public static void backHome(final AccessibilityServiceGesture accessibilityServiceGesture) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(accessibilityServiceGesture, R.anim.gesture_prev_enter_2, R.anim.gesture_prev_exit_2);
        // 很奇怪，在三星手机的OneUI（Android P）系统上，必须先overridePendingTransition再启动startActivity方可覆盖动画
        accessibilityServiceGesture.startActivity(intent, activityOptions.toBundle());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            overridePendingTransition(0, 0);
            Intent currentIntent = getIntent();
            int animation = SpfConfig.HOME_ANIMATION_DEFAULT;
            if (currentIntent.hasExtra("animation")) {
                animation = currentIntent.getIntExtra("animation", SpfConfig.HOME_ANIMATION_DEFAULT);
            }

            if (currentIntent.hasExtra("next")) {
                String appPackageName = currentIntent.getStringExtra("next");
                if (animation == SpfConfig.HOME_ANIMATION_CUSTOM) {
                    switchApp(appPackageName, R.anim.gesture_next_enter_2, R.anim.gesture_next_exit_2);
                } else if (animation == SpfConfig.HOME_ANIMATION_BASIC) {
                    switchApp(appPackageName, R.anim.gesture_next_enter, R.anim.gesture_next_exit);
                } else if (animation == SpfConfig.HOME_ANIMATION_FAST) {
                    switchApp(appPackageName, R.anim.gesture_next_enter_fast, R.anim.gesture_next_exit_fast);
                } else {
                    switchApp(appPackageName, R.anim.gesture_next_enter_basic, R.anim.gesture_next_exit_basic);
                }
            } else if (currentIntent.hasExtra("prev")) {
                String appPackageName = currentIntent.getStringExtra("prev");
                if (animation == SpfConfig.HOME_ANIMATION_CUSTOM) {
                    switchApp(appPackageName, R.anim.gesture_prev_enter_2, R.anim.gesture_prev_exit_2);
                } else if (animation == SpfConfig.HOME_ANIMATION_BASIC) {
                    switchApp(appPackageName, R.anim.gesture_prev_enter, R.anim.gesture_prev_exit);
                } else if (animation == SpfConfig.HOME_ANIMATION_FAST) {
                    switchApp(appPackageName, R.anim.gesture_prev_enter_fast, R.anim.gesture_prev_exit_fast);
                } else {
                    switchApp(appPackageName, R.anim.gesture_prev_enter_basic, R.anim.gesture_prev_exit_basic);
                }
            } else if (currentIntent.hasExtra("form")) {
                String appPackageName = currentIntent.getStringExtra("form");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    switchToFreeForm(appPackageName);
                }
            } else if (currentIntent.hasExtra("app-window")) {
                String appPackageName = currentIntent.getStringExtra("app-window");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    switchToFreeForm(appPackageName);
                }
            } else if (currentIntent.hasExtra("app")) {
                String appPackageName = currentIntent.getStringExtra("app");
                startActivity(appPackageName);
            } else if (currentIntent.hasExtra("home")) {
                String value = currentIntent.getStringExtra("home");
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_HOME);
                if (animation == SpfConfig.HOME_ANIMATION_CUSTOM || animation == SpfConfig.HOME_ANIMATION_BASIC) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    boolean anim2 = animation == SpfConfig.HOME_ANIMATION_CUSTOM;
                    int homeAnim;
                    int appAnim;
                    if (value.equals("prev")) {
                        if (animation == SpfConfig.HOME_ANIMATION_CUSTOM) {
                            homeAnim = R.anim.gesture_prev_enter_2;
                            appAnim = R.anim.gesture_prev_exit_2;
                        } else if (animation == SpfConfig.HOME_ANIMATION_BASIC) {
                            homeAnim = R.anim.gesture_prev_enter;
                            appAnim = R.anim.gesture_prev_exit;
                        } else {
                            homeAnim = R.anim.gesture_prev_enter_basic;
                            appAnim = R.anim.gesture_prev_exit_basic;
                        }
                    } else if (value.equals("next")) {
                        if (animation == SpfConfig.HOME_ANIMATION_CUSTOM) {
                            homeAnim = R.anim.gesture_next_enter_2;
                            appAnim = R.anim.gesture_next_exit_2;
                        } else if (animation == SpfConfig.HOME_ANIMATION_BASIC) {
                            homeAnim = R.anim.gesture_next_enter;
                            appAnim = R.anim.gesture_next_exit;
                        } else {
                            homeAnim = R.anim.gesture_next_enter_basic;
                            appAnim = R.anim.gesture_next_exit_basic;
                        }
                    } else {
                        homeAnim = anim2 ? R.anim.gesture_back_home_2 : R.anim.gesture_back_home;
                        appAnim = anim2 ? R.anim.gesture_app_exit_2 : R.anim.gesture_app_exit;
                    }
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(this.getApplicationContext(), homeAnim, appAnim);
                    // 很奇怪，在三星手机的OneUI（Android P）系统上，必须先overridePendingTransition再启动startActivity方可覆盖动画
                    overridePendingTransition(homeAnim, appAnim);
                    startActivity(intent, activityOptions.toBundle());
                    overridePendingTransition(homeAnim, appAnim);
                } else {
                    startActivity(intent);
                }
            }
        } catch (Exception ex) {
            Gesture.toast("" + ex.getMessage(), Toast.LENGTH_SHORT);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 50);
    }

    public void setFreeFormMode(ActivityOptions options) {
        try {
            Method method = ActivityOptions.class.getMethod("setLaunchWindowingMode", int.class);
            method.invoke(options, 5);
        } catch (Exception e) { /* Gracefully fail */ }

    }

    private void startActivity(String packageName) {
        switchApp(packageName, R.anim.gesture_app_open_enter, R.anim.gesture_app_open_exit);
    }

    private void switchToFreeForm(String packageName) {
        // ActivityOptions activityOptions = ActivityOptions.makeTaskLaunchBehind();
        // 设置不合理的动画，可能导致切换窗口模式时奔溃，因此去掉动画
        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), 0, 0);

        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        // intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_NEW_TASK);
        setFreeFormMode(activityOptions);

        // int left = 50;
        // int top = 100;
        // int right = 50 + GlobalState.displayWidth / 2;
        // int bottom = 100 + (GlobalState.displayWidth / 2 * 16 / 9);
        // activityOptions.setLaunchBounds(new Rect(left, top, right, bottom));

        Bundle bundle = activityOptions.toBundle();
        startActivity(intent, bundle);
    }

    /*
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        startAppAsWindow(intent);
    } else {
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        pendingIntent.send();
    }
    */
    /*
    @Override
    public void startActivity(Intent intent) {
        try {
            PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            Toast.makeText(this.getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    */

    private void switchApp(String appPackageName, int enterAnimation, int exitAnimation) {
        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(this.getApplicationContext(), enterAnimation, exitAnimation);
        startActivity(getAppSwitchIntent(appPackageName), activityOptions.toBundle());
        overridePendingTransition(enterAnimation, exitAnimation);
    }

    private Intent getAppSwitchIntent(String appPackageName) {
        Intent i = getPackageManager().getLaunchIntentForPackage(appPackageName);
        // i.setFlags((i.getFlags() & ~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED) | Intent.FLAG_ACTIVITY_NEW_TASK);
        // i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // i.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        // i.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        // i.setFlags(0x10200000);
        // Log.d("getAppSwitchIntent", "" + i.getFlags());
        i.setFlags((i.getFlags() & ~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED) | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // @参考 https://blog.csdn.net/weixin_34335458/article/details/88020972
        i.setPackage(null); // 加上这句代

        // i.setFlags((i.getFlags() & ~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED) | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        // i.setFlags((i.getFlags() | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED) | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        // i.setAction(Intent.ACTION_MAIN);
        // i.addCategory(Intent.CATEGORY_LAUNCHER);
        return i;
    }
}
