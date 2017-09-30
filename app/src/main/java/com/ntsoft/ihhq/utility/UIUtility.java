package com.ntsoft.ihhq.utility;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 1/4/2016.
 */
public class UIUtility {
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }
    public static int getScreenWidthDP(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int dp = Math.round(width / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        return dp;
    }
    public static int getScreenWidthPixel(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
//        int dp = Math.round(width / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        return width;
    }
    public static Typeface getFont(Context ctx) {

        AssetManager assetManager = ctx.getAssets();

        Typeface tf = Typeface.createFromAsset(assetManager, "");

        return tf;
    }
//    public static void hideSoftKeyboard(Context context, View view) {
//        try {
//            InputMethodManager inputMethodManager = (InputMethodManager) context
//                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
//            inputMethodManager
//                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    public static void showSoftKeyboard(Context context, EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }
    public static void hideSoftKeyboard(Activity activity) {
        if (keyboardShown(activity)) {
            InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }

    }
    public static boolean keyboardShown(Context mContext) {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputMethodManager.isAcceptingText()) {
            return true;
        } else {
            return false;
        }


    }

    public static void setImageViewSize(ImageView imageview, int screenWidth, int screenHeight){

        imageview.setMinimumWidth(screenWidth);
        imageview.setMinimumHeight(screenHeight);
    }

    public static void setRelativeLayoutSize(RelativeLayout relativeLayout, int screenWidth, int screenHeight){
        ViewGroup.LayoutParams layoutParams = null;
        try{
            layoutParams =  relativeLayout.getLayoutParams();
        }catch (Exception e){
            e.printStackTrace();
        }
        layoutParams.width = screenWidth ;
        layoutParams.height = screenHeight;
        relativeLayout.setLayoutParams(layoutParams);
    }
    public static void setLinearLayoutSize(LinearLayout linearLayout, int screenWidth, int screenHeight){
        ViewGroup.LayoutParams layoutParams = null;
        try{
            layoutParams =  linearLayout.getLayoutParams();
        }catch (Exception e){
            e.printStackTrace();
        }
        layoutParams.width = screenWidth ;
        layoutParams.height = screenHeight;
        linearLayout.setLayoutParams(layoutParams);
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

}
