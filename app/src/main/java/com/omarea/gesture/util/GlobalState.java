package com.omarea.gesture.util;

public class GlobalState {
    public static boolean testMode = false;
    public static int iosBarColor = Integer.MIN_VALUE;
    public static Runnable updateBar;
    public static int displayHeight = 2340;
    public static int displayWidth = 1080;
    // 增强模式（需要Root或者ADB）
    public static boolean enhancedMode = false;
}
