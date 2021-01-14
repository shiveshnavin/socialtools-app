package com.dotpot.app;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.dotpot.app.adapters.GenriXAdapter;
import com.dotpot.app.interfaces.GenricCallback;
import com.dotpot.app.interfaces.GenricDataCallback;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.services.DBService;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utils.CircularRevealPopup;
import com.dotpot.app.utils.FadePopup;
import com.dotpot.app.utils.ResourceUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by shivesh on 28/6/17.
 */

public class utl {

    public static final String MY_PREFS_NAME = "app_data";
    static final String HTML_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
    public static boolean DISPLAY_ENABLED = true;
    public static boolean DEBUG_ENABLED = true;
    public static Gson js = new Gson();
    public static Context ctx;

    ;
    public static Pattern pattern = Pattern.compile(HTML_PATTERN);

    ;
    public static String
            REGULAR = "font_regular.ttf", TEXT = "font_style.ttf", THIN = "font_thin.ttf";
    public static String TAG = "TAG UTL";
    public static Integer TYPE_EMAIL = 120, TYPE_PHONE = 293, TYPE_DEF = 101;
    public static EditText input;
    public static SharedPreferences.Editor editor;// = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
    public static Dialog dialog;

    public static void init(Context ctxx) {

        js = new Gson();
        ctx = ctxx;

        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        DEBUG_ENABLED = ApplicationInfo.FLAG_DEBUGGABLE == 2|| mFirebaseRemoteConfig.getBoolean("debug");
    }

    public static String getHtml(Context ctx, String text, @ColorRes int color) {

        ;

        return "<font color=\"" + utl.getHexFromRes(
                ctx.getResources(), color) + "\">" + text + "</font>";
    }

    public static String getHtml(Context ctx, Object text) {

        ;

        return "<font color=\"" + utl.getHexFromRes(
                ctx.getResources(), R.color.colorAccent) + "\">" + text + "</font>";
    }

    public static boolean hasHTMLTags(String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    public static void animate_avd(ImageView img) {

        try {
            final Drawable drawable = img.getDrawable();

            if (drawable instanceof Animatable) {
                ((Animatable) drawable).start();
            }
        } catch (Exception e) {

            utl.e("ERROR WHILE ANIMATING IMAGEVIEW");
        }


    }

    public static void animate(View app, String property, int initv, int finalv, boolean repeat, int dur) {


        ObjectAnimator colorAnim = ObjectAnimator.ofInt(app, property,
                initv, finalv);
        colorAnim.setEvaluator(new ArgbEvaluator());

        if (repeat) {
            colorAnim.setRepeatMode(ValueAnimator.REVERSE);
            colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        }

        colorAnim.setDuration(dur);
        colorAnim.start();

    }

    public static void animateBackGround(View app, String initcolor, String finalcolor, boolean repeat, int dur) {

        String property = "backgroundColor";
        ObjectAnimator colorAnim = ObjectAnimator.ofInt(app, property,
                Color.parseColor(initcolor), Color.parseColor(finalcolor));
        colorAnim.setEvaluator(new ArgbEvaluator());

        if (repeat) {
            colorAnim.setRepeatMode(ValueAnimator.REVERSE);
            colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        }

        colorAnim.setDuration(dur);
        colorAnim.start();

    }

    public static String getHrsFromMin(int mins) {
        String st = "";

        if (mins < 60) {
            return st + mins + " mins";
        }
        int hrs = mins / 60;
        st = "" + hrs + " hr ";
        int mns = mins - hrs * 60;
        st = st + " " + mns + " mins";

        return st;

    }

    public static int getColor(@ColorRes int c) {
        int col = 0;
        col = ctx.getResources().getColor(c);
        return col;
    }

    public static void setStatusBarColor(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = context.getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            int statusBarHeight = getStatusBarHeight(context);

            View view = new View(context);
            view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.getLayoutParams().height = statusBarHeight;
            ((ViewGroup) w.getDecorView()).addView(view);
            view.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public static int getStatusBarHeight(Activity context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void fullScreen(Activity act) {
        try {
            act.requestWindowFeature(Window.FEATURE_NO_TITLE);
            act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e) {
            if (utl.DEBUG_ENABLED) e.printStackTrace();
        }
    }

    public static int getH(Context ctx) {
        WindowManager windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getHeight();

    }

    public static int getW(Context ctx) {
        WindowManager windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getWidth();

    }

    public static Typeface getFace(String font, Context ctx) {
        Typeface face = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/" + font);

        if (face == null) {
            face = Typeface.createFromAsset(ctx.getAssets(),
                    "fonts/" + TEXT);
        }
        return face;

    }

    public static Typeface setFace(@FontRes int font, TextView textView) {
        Context ctx = textView.getContext();
        Typeface face = ResourcesCompat.getFont(textView.getContext(), font);
        textView.setTypeface(face);
        return face;

    }

    public static Typeface setFace(String font, TextView textView) {
        Context ctx = textView.getContext();
        Typeface face = Typeface.createFromAsset(ctx.getAssets(),
                "fonts/" + font);

        if (face == null) {
            face = Typeface.createFromAsset(ctx.getAssets(),
                    "fonts/" + TEXT);
        }
        textView.setTypeface(face);
        return face;

    }

    public static void changeTextColor(final TextView textView, int startColor, int endColor,
                                       final long animDuration, final long animUnit) {
        if (textView == null) return;

        final int startRed = Color.red(startColor);
        final int startBlue = Color.blue(startColor);
        final int startGreen = Color.green(startColor);

        final int endRed = Color.red(endColor);
        final int endBlue = Color.blue(endColor);
        final int endGreen = Color.green(endColor);

        final CountDownTimer ct = new CountDownTimer(animDuration, animUnit) {
            //animDuration is the time in ms over which to run the animation
            //animUnit is the time unit in ms, updateImages color after each animUnit

            @Override
            public void onTick(long l) {
                int red = (int) (endRed + (l * (startRed - endRed) / animDuration));
                int blue = (int) (endBlue + (l * (startBlue - endBlue) / animDuration));
                int green = (int) (endGreen + (l * (startGreen - endGreen) / animDuration));

                textView.setTextColor(Color.rgb(red, green, blue));
            }

            @Override
            public void onFinish() {
                textView.setTextColor(Color.rgb(endRed, endGreen, endBlue));

            }
        };

        ct.start();
    }

    public static String refineString(String red, String rep) {
        red = red.replaceAll("[^a-zA-Z0-9]", rep);
        return red;
    }

    public static boolean containsSpecialCharacter(String s) {
        return (s == null) ? false : s.matches("[^A-Za-z0-9 ]");
    }

    public static boolean isEmpty(String s) {
        return isNull(s) || s.length() < 1;
    }

    public static boolean isNull(String is) {
        return is == null || ("" + is).equals("null");
    }

    public static boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    public static void copyToClipBoard(String s, Context ctx) {
        ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("message", s);
        clipboard.setPrimaryClip(clip);
        utl.toast(ctx, "Copied to clipboard !");
    }


    public static void setImageTintColor(@ColorRes int rightTint, ImageView imageView) {
        ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(ResourceUtils.getColor(rightTint)));
    }
    public static void setTint(@ColorRes int rightTint, ImageView imageView) {
        ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(ResourceUtils.getColor(rightTint)));
    }

    public static String colorToHexNoAlpha(int color) {
        try {
            return
                    "#" + Integer.toHexString(color).toUpperCase().substring(2);
        } catch (Exception e) {

            return "#000000";
        }

    }

    public static void setImageTint(ImageView img, boolean isVector, @ColorRes int color) {

        if (isVector) {
            img.setColorFilter(ContextCompat.getColor(img.getContext(), color), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            img.setColorFilter(ContextCompat.getColor(img.getContext(), color),
                    android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }

    public static String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDateTime(Date date, String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String convertMongoDate(String val, String format) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat(format);
        try {
            String finalStr = outputFormat.format(inputFormat.parse(val));
            System.out.println(finalStr);
            return finalStr;
        } catch (ParseException e) {
            if (utl.DEBUG_ENABLED) e.printStackTrace();
        }
        return "";
    }

    public static boolean matchSearch(String fullstring, String substring) {
        if (fullstring == null || substring == null) return false;

        return fullstring.toLowerCase().contains(substring.toLowerCase());
    }

    //put marble in the bag
    public static String insert(String bag, String marble, int index) {
        String bagBegin = bag.substring(0, index);
        String bagEnd = bag.substring(index);
        return bagBegin + marble + bagEnd;
    }

    public static String getDateFormatted(Date date) {


        SimpleDateFormat dateF = new SimpleDateFormat("dd MMM yyyy");
        return dateF.format(date);

    }

    public static String getDateTimeFormatted(Date date) {


        SimpleDateFormat dateF = new SimpleDateFormat("dd MMM yyyy hh:mm a");
        return dateF.format(date);

    }

    public static void log(String t) {

        if (DEBUG_ENABLED)
            Log.d("" + TAG, "" + t);
    }

    public static void log(Object t) {
        if (DEBUG_ENABLED)
            Log.d("" + TAG, "" + utl.js.toJson(t));
    }

    public static void log(String t, String tt) {
        if (DEBUG_ENABLED)
            Log.d("" + t, "" + tt);
    }

    public static void l(String t) {
        if (DEBUG_ENABLED)
            Log.d("" + TAG, "" + t);
    }

    public static void l(String t, String tt) {
        if (DEBUG_ENABLED)
            Log.d("" + t, "" + tt);
    }

    public static void e(String TAG, Object t) {
        if (DEBUG_ENABLED)
            Log.e("" + TAG, "" + js.toJson(t));
    }

    public static void e(Object... t) {
        if (DEBUG_ENABLED)
            Log.e("" + TAG, "" + js.toJson(t));
    }

    public static void e(Object t) {
        if (DEBUG_ENABLED)
            Log.e("" + TAG, "" + js.toJson(t));
    }

    public static void e(String t) {
        if (DEBUG_ENABLED)
            Log.e("" + TAG, "" + t);
    }

    public static void e(String t, String tt) {
        if (DEBUG_ENABLED)
            Log.e("" + t, "" + tt);
    }

    public static void l(Object t) {
        if (DEBUG_ENABLED)
            Log.d("" + TAG, "" + t);
    }

    public static void l(String t, Object tt) {
        if (DEBUG_ENABLED)
            Log.d("" + t, "" + tt);
    }

    public static Bitmap getBitmapFromDrawable(Context context, @DrawableRes int drawableId) {
        Drawable drawable = AppCompatResources.getDrawable(context, drawableId);

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawableCompat || drawable instanceof VectorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    @SuppressLint("NewApi")
    public static Bitmap blurRenderScript(Context context, Bitmap smallBitmap, int radius) {
        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;
    }

    private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }

    public static void logout() {

        try {
            FirebaseAuth.getInstance().signOut();
        } catch (Exception e) {
            if (utl.DEBUG_ENABLED) e.printStackTrace();
        }

        try {
            App.mGoogleApiClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });

        } catch (Exception e) {
            if (utl.DEBUG_ENABLED) e.printStackTrace();
        }

        try {
            removeUserData();
            DBService helper = DBService.getInstance(App.getAppContext());
            helper.deleteAllData();

        } catch (Exception e) {
            if (utl.DEBUG_ENABLED) e.printStackTrace();
        }

    }

    public static String getHexFromRes(Resources resources, @ColorRes int res) {
        return String.format("#%06X", (0xFFFFFF & resources.getColor(res)));

    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(String pathToFile,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathToFile, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathToFile, options);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth);
        float scaleHeight = ((float) newHeight);
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap
                (bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public static File savebitmap(String filename, Bitmap bitmap, int q) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;

        File file = new File(filename);
        if (file.exists()) {
            file.delete();
            file = new File(filename);

        }
        try {


            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, q, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            if (utl.DEBUG_ENABLED) e.printStackTrace();
        }
        Log.e("file", "" + file);
        return file;

    }

    public static int getDominantColor1(Bitmap bitmap) {

        if (bitmap == null)
            throw new NullPointerException();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int pixels[] = new int[size];

        Bitmap bitmap2 = bitmap.copy(Bitmap.Config.ARGB_4444, false);

        bitmap2.getPixels(pixels, 0, width, 0, 0, width, height);

        final List<HashMap<Integer, Integer>> colorMap = new ArrayList<HashMap<Integer, Integer>>();
        colorMap.add(new HashMap<Integer, Integer>());
        colorMap.add(new HashMap<Integer, Integer>());
        colorMap.add(new HashMap<Integer, Integer>());

        int color = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        Integer rC, gC, bC;
        for (int i = 0; i < pixels.length; i++) {
            color = pixels[i];

            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);

            rC = colorMap.get(0).get(r);
            if (rC == null)
                rC = 0;
            colorMap.get(0).put(r, ++rC);

            gC = colorMap.get(1).get(g);
            if (gC == null)
                gC = 0;
            colorMap.get(1).put(g, ++gC);

            bC = colorMap.get(2).get(b);
            if (bC == null)
                bC = 0;
            colorMap.get(2).put(b, ++bC);
        }

        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            int max = 0;
            int val = 0;
            for (Map.Entry<Integer, Integer> entry : colorMap.get(i).entrySet()) {
                if (entry.getValue() > max) {
                    max = entry.getValue();
                    val = entry.getKey();
                }
            }
            rgb[i] = val;
        }

        int dominantColor = Color.rgb(rgb[0], rgb[1], rgb[2]);

        return dominantColor;
    }

    public static Float distance(Double lat_a, Double lng_a, Double lat_b, Double lng_b) {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }

    public static int getApkVerison(Context ctx) {
        try {
            int versionCode = BuildConfig.VERSION_CODE;
            String versionName = BuildConfig.VERSION_NAME;
            return versionCode;
        } catch (Exception e) {
            if (utl.DEBUG_ENABLED) e.printStackTrace();
            return 0;

        }
    }

    @SuppressWarnings("ResourceType")
    public static void changeColorDrawable(ImageView imageView, @DrawableRes int res) {

        DrawableCompat.setTint(imageView.getDrawable(), ContextCompat.getColor(ctx, res));


    }

    public static Float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static Float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static void hideSoftKeyboard(Activity activity) {

        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

        } catch (Exception e) {

        }


    }

    public static void showSoftKeyboard(Activity activity, View linearLayout) {

        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                linearLayout.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }

    public static void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(900);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public static void addPressReleaseAnimation(final View base) {

        final Animation press = AnimationUtils.loadAnimation(base.getContext(), R.anim.rec_zoom_in);
        final Animation release = AnimationUtils.loadAnimation(base.getContext(), R.anim.rec_zoom_nomal);

        base.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        base.startAnimation(press);
                        break;
                    case MotionEvent.ACTION_UP:

                        base.startAnimation(release);
                        break;
                    case MotionEvent.ACTION_CANCEL:

                        base.startAnimation(release);
                        break;
                    default:
                        break;
                }


                return false;
            }
        });


    }

    @SuppressWarnings("ResourceAsColor")
    public static void snack(Activity act, String t) {

        View rootView = act.getWindow().getDecorView().getRootView();
        Snackbar snackbar = Snackbar.make(rootView, "" + t, Snackbar.LENGTH_LONG);
        // snackbar.setActionTextColor(act.getResources().getColor(R.color.material_red_A400));
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(act.getResources().getColor(R.color.colorAccent));

        int snackbarTextId = R.id.snackbar_text;
        TextView textView = (TextView) snackbarView.findViewById(snackbarTextId);
        textView.setTextColor(Color.WHITE);

        if (DISPLAY_ENABLED)
            snackbar.show();

    }

    public static void snack(View rootView, String t) {

        Context act = rootView.getContext();

        Snackbar snackbar = Snackbar.make(rootView, "" + t, Snackbar.LENGTH_LONG);
        // snackbar.setActionTextColor(act.getResources().getColor(R.color.material_red_A400));
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(act.getResources().getColor(R.color.colorAccent));

        int snackbarTextId = R.id.snackbar_text;
        TextView textView = (TextView) snackbarView.findViewById(snackbarTextId);
        textView.setTextColor(Color.WHITE);

        if (DISPLAY_ENABLED)
            snackbar.show();

    }

    public static void snack(Activity act, String t, String a, final GenricCallback cb) {

        View rootView = act.getWindow().getDecorView().getRootView();

        Snackbar snackbar = Snackbar.make(rootView, "" + t, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(act.getResources().getColor(R.color.grey_100));
        snackbar.setAction("" + a, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb.onStart();
            }
        });
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(act.getResources().getColor(R.color.colorAccent));

        int snackbarTextId = R.id.snackbar_text;
        TextView textView = (TextView) snackbarView.findViewById(snackbarTextId);
        textView.setTextColor(Color.WHITE);

        if (DISPLAY_ENABLED)
            snackbar.show();

    }

    public static void snack(View rootView, String t, String a, final GenricCallback cb) {


        Context act = rootView.getContext();

        Snackbar snackbar = Snackbar.make(rootView, "" + t, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(act.getResources().getColor(R.color.grey_100));
        snackbar.setAction("" + a, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb.onStart();
            }
        });
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(act.getResources().getColor(R.color.colorAccent));

        int snackbarTextId = R.id.snackbar_text;
        TextView textView = (TextView) snackbarView.findViewById(snackbarTextId);
        textView.setTextColor(Color.WHITE);

        if (DISPLAY_ENABLED)
            snackbar.show();

    }

    public static void diag(Context c, String title, String desc, String action, final ClickCallBack click) {


        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(desc);
        alertDialogBuilder.setNeutralButton(action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                click.done(dialogInterface);

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    ;

    public static void diag(Context c, String title, String desc, boolean isCancellable, String action, final ClickCallBack click) {


        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(desc);
        alertDialogBuilder.setCancelable(isCancellable);
        alertDialogBuilder.setNeutralButton(action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                click.done(dialogInterface);

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static String getRoom(String uid1, String uid2) {

        ;
        String cr = uid1 + uid2;
        char[] chr = cr.toCharArray();
        Arrays.sort(chr);

        return new String(chr);

    }

    public static void diag(Context c, String title, String desc) {
        try {
            final AlertDialog.Builder
                    alertDialogBuilder = new AlertDialog.Builder
                    (c);
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setMessage(Html.fromHtml(desc));
            alertDialogBuilder.setNeutralButton("OK", new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface
                                                    dialog, int id) {
                            dialog.cancel();
                        }
                    });


            AlertDialog alertDialog
                    = alertDialogBuilder.create();


            alertDialog.show();
        } catch (Exception e) {
            if (utl.DEBUG_ENABLED) e.printStackTrace();
        }
    }

    public static void toast(Context c, String t) {


        try {
            if (DISPLAY_ENABLED)
                Toast.makeText(c, t, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            if (utl.DEBUG_ENABLED) e.printStackTrace();
        }
    }

    public static View diagInputTextImage(Context ctx,
                                          String hint,
                                          final InputDialogCallback textCb) {

        return diagInputTextImage(ctx, hint, textCb, null, -1, null, null);
    }

    public static View diagInputTextImage(Context ctx,
                                          String hint,
                                          final InputDialogCallback textCb,
                                          String text,
                                          @DrawableRes int imageRes,
                                          String imgUrl,
                                          final InputDialogCallback imageCb) {


        final BottomSheetDialog mBottomSheetDialog;

        mBottomSheetDialog = new BottomSheetDialog(ctx);
        LayoutInflater inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inf.inflate(R.layout.utl_diag_image_text, null);


        LinearLayout contImage;
        ImageView subImage;
        TextInputLayout contText;
        TextInputEditText subText;
        ImageView done;

        contImage = (LinearLayout) rootView.findViewById(R.id.cont_image);
        subImage = (ImageView) rootView.findViewById(R.id.sub_image);
        contText = (TextInputLayout) rootView.findViewById(R.id.cont_text);
        subText = (TextInputEditText) rootView.findViewById(R.id.sub_text);
        done = (ImageView) rootView.findViewById(R.id.done);

        addPressReleaseAnimation(done);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = subText.getText().toString();
                textCb.onDone(value);
                mBottomSheetDialog.dismiss();
            }
        });

        if (text != null)
            subText.setText(text);
        contText.setHint(hint);
        subText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    {
                        String value = subText.getText().toString();
                        textCb.onDone(value);
                        mBottomSheetDialog.dismiss();

                    }
                    return true;
                }
                return false;
            }
        });

        if (imageRes == -1 && isEmpty(imgUrl)) {
            contImage.setVisibility(View.GONE);
        } else {

            try {


                if (imgUrl != null && imgUrl.length() > 4) {
                    if (imgUrl.startsWith("http"))
                        Picasso.get().load(imgUrl).placeholder(R.drawable.loading_bg)
                                .into(subImage);
                    else {

                        File f = new File(imgUrl);
                        Picasso.get().load(f).placeholder(R.drawable.loading_bg)
                                .into(subImage);
                    }
                } else {
                    Picasso.get().load(imageRes).placeholder(R.drawable.ic_add_attachment)
                            .into(subImage);
                }

            } catch (Exception e) {

            }

            addPressReleaseAnimation(contImage);
            contImage.setOnClickListener((v -> {
                if (imageCb != null)
                    imageCb.onDone(subText.getText().toString());
                mBottomSheetDialog.dismiss();
            }));
        }


        mBottomSheetDialog.setContentView(rootView);
        mBottomSheetDialog.show();


        return subText;

    }

    public static View inputDialogBottom(Context ctx, String hint, final int TYPE, final InputDialogCallback callback) {
        return diagInputTextImage(ctx, hint, callback);
    }

    public static void diagBottom(Context ctx, String text) {

        diagBottom(ctx, "", text, true, "DISMISS", new GenricCallback() {
            @Override
            public void onStart() {

            }
        });
    }

    public static void diagBottom(Context ctx, String title, String text, boolean cancellableOnTouchOutside, String actionText, final GenricCallback callback) {
        final BottomSheetDialog mBottomSheetDialog;
        ;


        mBottomSheetDialog = new BottomSheetDialog(ctx);
        LayoutInflater inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sheetView = inf.inflate(R.layout.utl_diag_bottom, null);

        final TextView textT = (TextView) sheetView.findViewById(R.id.text);
        final TextView titleT = (TextView) sheetView.findViewById(R.id.title);


        Button done = (Button) sheetView.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();
                callback.onStart();
            }
        });

        mBottomSheetDialog.setCanceledOnTouchOutside(cancellableOnTouchOutside);


        if (title.length() < 2) {
            titleT.setVisibility(View.GONE);
        }
        done.setText(actionText);
        textT.setText(Html.fromHtml(text));
        titleT.setText(title);


        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();


    }

    public static BottomSheetDialog diagBottomList(Context ctx, String title, RecyclerView.Adapter adapter, boolean cancellableOnTouchOutside, String actionText, final GenricCallback callback) {

        final BottomSheetDialog mBottomSheetDialog;
        ;


        mBottomSheetDialog = new BottomSheetDialog(ctx);
        LayoutInflater inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sheetView = inf.inflate(R.layout.utl_diag_bottom_list, null);

        final RecyclerView list = (RecyclerView) sheetView.findViewById(R.id.list);
        final TextView titleT = (TextView) sheetView.findViewById(R.id.title);


        Button done = (Button) sheetView.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();
                callback.onStart();
            }
        });

        mBottomSheetDialog.setCanceledOnTouchOutside(cancellableOnTouchOutside);


        if (title.length() < 2) {
            titleT.setVisibility(View.GONE);
        }
        done.setText(actionText);
        titleT.setText(title);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx, RecyclerView.VERTICAL, false);
        list.setLayoutManager(linearLayoutManager);
        list.setAdapter(adapter);


        mBottomSheetDialog.setContentView(sheetView);


        if (ctx instanceof BaseActivity) {
            BaseActivity act = (BaseActivity) ctx;
            if (!act.isFinishing())
                try {
                    mBottomSheetDialog.show();
                } catch (Exception e) {
                }
        }
        return mBottomSheetDialog;


    }

    public static BottomSheetDialog diagBottomMenu(Context ctx, String title, String[] menus, int[] icons,
                                                   boolean cancellableOnTouchOutside,
                                                   String actionText, final GenricDataCallback callback) {
        final BottomSheetDialog mBottomSheetDialog;
        ;


        mBottomSheetDialog = new BottomSheetDialog(ctx);
        LayoutInflater inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sheetView = inf.inflate(R.layout.utl_diag_bottom_list, null);

        final RecyclerView list = (RecyclerView) sheetView.findViewById(R.id.list);
        final TextView titleT = (TextView) sheetView.findViewById(R.id.title);


        ArrayList<String> items = new ArrayList<>(Arrays.asList(menus));
        GenriXAdapter<String> adapter = new GenriXAdapter<String>(ctx, R.layout.utl_row_menu, items) {
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int pos = (viewHolder.getAdapterPosition());
                final String men = items.get(viewHolder.getAdapterPosition());
                final CustomViewHolder vh = (CustomViewHolder) viewHolder;
                vh.textView(R.id.txt).setText(men);

                if (icons != null)
                    vh.imageView(R.id.img).setImageResource(icons[pos]);
                vh.base.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mBottomSheetDialog.dismiss();
                        callback.onStart(men, pos);
                    }
                });


            }
        };


        Button done = (Button) sheetView.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();
            }
        });

        mBottomSheetDialog.setCanceledOnTouchOutside(cancellableOnTouchOutside);


        if (title.length() < 2) {
            titleT.setVisibility(View.GONE);
        }
        done.setText(actionText);
        titleT.setText(title);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx, RecyclerView.VERTICAL, false);
        list.setLayoutManager(linearLayoutManager);
        list.setAdapter(adapter);


        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();


        return mBottomSheetDialog;


    }

    public static BottomSheetDialog diagBottomGrid(Context ctx, String title, RecyclerView.Adapter adapter, int numberOfColumns, boolean cancellableOnTouchOutside, String actionText, final GenricCallback callback) {
        final BottomSheetDialog mBottomSheetDialog;
        ;


        mBottomSheetDialog = new BottomSheetDialog(ctx);
        LayoutInflater inf = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sheetView = inf.inflate(R.layout.utl_diag_bottom_list, null);

        final RecyclerView list = (RecyclerView) sheetView.findViewById(R.id.list);
        final TextView titleT = (TextView) sheetView.findViewById(R.id.title);


        Button done = (Button) sheetView.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBottomSheetDialog.dismiss();
                callback.onStart();
            }
        });

        mBottomSheetDialog.setCanceledOnTouchOutside(cancellableOnTouchOutside);


        if (title.length() < 2) {
            titleT.setVisibility(View.GONE);
        }
        done.setText(actionText);
        titleT.setText(title);
        // LinearLayoutManager linearLayoutManager=new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(new GridLayoutManager(ctx, numberOfColumns));

        list.setAdapter(adapter);


        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();


        return mBottomSheetDialog;


    }

    public static Dialog diagInfo(View anchor, String text, String actionText, int drawableIcon, final ClickCallBack click) {

        final View dialogView = View.inflate(anchor.getContext(), R.layout.utl_diag_success, null);
        CircularRevealPopup popup = new CircularRevealPopup(anchor.getContext(), anchor, dialogView);
        Dialog d = popup.popup();
        TextView stText = dialogView.findViewById(R.id.text);
        Button done = dialogView.findViewById(R.id.done);
        ImageView img = dialogView.findViewById(R.id.img);


        img.setImageDrawable(anchor.getContext().getDrawable(drawableIcon));

      /*  RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(dialogView.getWidth(),dialogView.getWidth());
        img.setLayoutParams(params);
*/
        if (actionText != null)
            done.setText(actionText);

        if (text != null)
            stText.setText(text);

        if (click != null) {
            done.setOnClickListener((v) -> {
                popup.dismiss();
                click.done(d);
            });
        }


        return d;

    }

    public static Dialog diagInfo2(Context ctx, String text, String actionText, boolean cancellable, final ClickCallBack click) {

        try {
            final View dialogView = View.inflate(ctx, R.layout.utl_diag_success_dark, null);
            FadePopup popup = new FadePopup(ctx, null, dialogView);

            Dialog d = popup.popup();

            d.findViewById(R.id.dialog).setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimary));
            TextView stText = dialogView.findViewById(R.id.text);
            Button done = dialogView.findViewById(R.id.done);
            ImageView img = dialogView.findViewById(R.id.img);
            ImageView cancle = d.findViewById(R.id.cancle);
            popup.cancle.setImageResource(R.drawable.ic_cancel_white);

            if (!cancellable) {

                popup.cancle.setVisibility(View.GONE);
                d.setCancelable(false);
                d.setCanceledOnTouchOutside(false);
            }
/*

        cancle.setColorFilter(ContextCompat.getColor(cancle.getContext(), R.color.white),
                android.graphics.PorterDuff.Mode.MULTIPLY);
*/


            img.setImageDrawable(ctx.getDrawable(R.drawable.logo_inv));

      /*  RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(dialogView.getWidth(),dialogView.getWidth());
        img.setLayoutParams(params);
*/
            if (actionText != null)
                done.setText(actionText);

            if (text != null)
                stText.setText(text);

            if (click != null) {
                done.setOnClickListener((v) -> {
                    popup.dismiss();
                    click.done(d);
                });
            }


            return d;
        } catch (Resources.NotFoundException e) {
            if (utl.DEBUG_ENABLED) e.printStackTrace();

            return null;
        }

    }

    public static Dialog diagInfo2(Context ctx, String text, String actionText, int drawableIcon, final ClickCallBack click) {

        try {
            final View dialogView = View.inflate(ctx, R.layout.utl_diag_success_dark, null);
            FadePopup popup = new FadePopup(ctx, null, dialogView);

            Dialog d = popup.popup();

            d.findViewById(R.id.dialog).setBackgroundColor(ctx.getResources().getColor(R.color.colorPrimary));
            TextView stText = dialogView.findViewById(R.id.text);
            Button done = dialogView.findViewById(R.id.done);
            ImageView img = dialogView.findViewById(R.id.img);
            ImageView cancle = d.findViewById(R.id.cancle);
            popup.cancle.setImageResource(R.drawable.ic_cancel_white);
/*

        cancle.setColorFilter(ContextCompat.getColor(cancle.getContext(), R.color.white),
                android.graphics.PorterDuff.Mode.MULTIPLY);
*/


            img.setImageDrawable(ctx.getDrawable(drawableIcon));

      /*  RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(dialogView.getWidth(),dialogView.getWidth());
        img.setLayoutParams(params);
*/
            if (actionText != null)
                done.setText(actionText);

            if (text != null)
                stText.setText(text);

            if (click != null) {
                done.setOnClickListener((v) -> {
                    popup.dismiss();
                    click.done(d);
                });
            }
            stText.setVerticalScrollBarEnabled(true);
            stText.setMaxLines(25);

            return d;
        } catch (Resources.NotFoundException e) {
            if (utl.DEBUG_ENABLED) e.printStackTrace();

            return null;
        }

    }

    public static Dialog diagSuccess(View anchor, String text, String actionText, final ClickCallBack click) {

        return diagInfo(anchor, text, actionText, R.drawable.ic_done_tick, click);


    }

    public static String getFileNameFromUri(Uri uri, Context ctx) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = ctx.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static String getRealPathFromUri(Uri uri, Context ctx) {
        String result = null;
        try {
            if (uri == null)
                return null;

            String documentID;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                String[] pathParts = uri.getPath().split("/");
                documentID = pathParts[pathParts.length - 1];
            } else {
                String pathSegments[] = uri.getLastPathSegment().split(":");
                documentID = pathSegments[pathSegments.length - 1];
            }
            String mediaPath = MediaStore.Images.Media.DATA;

            Cursor imageCursor = ctx.getContentResolver().query(uri, new String[]{mediaPath}, MediaStore.Images.Media._ID + "=" + documentID, null, null);
            if (imageCursor.moveToFirst()) {
                result = imageCursor.getString(imageCursor.getColumnIndex(mediaPath));
            }
        } catch (Exception e) {
            if (utl.DEBUG_ENABLED) e.printStackTrace();
        }
        if (result == null)
            result = uri.getPath().replace("file://", "").replace("%20", " ");

        return result;
    }

    public static FirebaseUser getUser() {

        return FirebaseAuth.getInstance().getCurrentUser();

    }

    public static String getFCMTOken() {

        //  String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //  return refreshedToken;
        return "";
    }

    public static void setKey(String k, String v, Context ctx) {

        utl.l("KEY SET Key:  -" + k + "- VAL: " + v);
        editor = ctx.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(k, v);

        editor.commit();

    }

    public static String getKey(String k, Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString(k, null);

        utl.l("KEY GET : " + k + " VAL: " + restoredText);

        return restoredText;
    }

    public static void setShared(Context ctx) {
        /*
        editor.putString("shopName", "Elena");
        editor.putInt("idName", 12);
        editor.commit();*/


        SharedPreferences prefs = ctx.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("installed", null);
        if (restoredText != null) {

            String name = prefs.getString("installed", "true");

            Log.d("INSTALL ", " ALREADY INSTALL ");
        } else {
            Log.d("INSTALL ", " FIRST INSTALL ");

            editor = ctx.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("installed", "true");

            editor.commit();
        }

    }

    public static void copyFile(File src, File dst) {
        try {

            if (!src.exists())
                return;
            InputStream in = new FileInputStream(src);
            OutputStream os = new FileOutputStream(dst);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
            in.close();
            os.close();

        } catch (Exception e) {
            if (utl.DEBUG_ENABLED) e.printStackTrace();
        }


    }

    public static boolean removeUserData() {


        utl.setKey("user", null, ctx);
        return true;
    }

    public static boolean writeUserData(GenricUser guser, Context ctx) {
        utl.setKey("user", js.toJson(guser), ctx);
        Log.d("DATA WROTE", "" + utl.getKey("user", ctx));
        return true;
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    public static String uid(int l) {
        final String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        System.out.println("uuid Full= " + uuid);
        String ret = uuid.substring(0, Math.min(uuid.length(), l));
        ;
        System.out.println("uuid " + l + " = " + ret);
        return ret;
    }

    public static int randomInt() {

        int ret = 1;
        Random random = new Random();
        ret = random.nextInt();
        return ret;
    }

    public static int randomInt(int len) {

        int ret = 1;
        Random random = new Random();
        ret = random.nextInt();
        if (ret < 0) {
            ret = ret * -1;
        }


        while (getIntLen(ret) > len) {
            ret = ret / 10;
        }


        return ret;
    }

    public static int getIntLen(int n) {

        int len = 0;

        while (n > 0) {
            len++;
            n = n / 10;
        }

        return len;

    }

    public static GenricUser readUserData() {
        if (getKey("user", App.getAppContext()) == null)
            return null;

        Gson g = new Gson();
        try {
            Log.d("DATA READ", "");
            //Log.d("DATA READ",""+fop.read(user));
            GenricUser guser = g.fromJson(getKey("user", App.getAppContext()), GenricUser.class);
            return guser;


        } catch (JsonSyntaxException e) {

            if (utl.DEBUG_ENABLED) e.printStackTrace();

            return null;
        }
    }

    public static String dev() {
        return "_" + utl.refineString(Build.DEVICE, "_") + "_" + utl.refineString(Build.MANUFACTURER, "_");
    }

    public static String getRealPathFromUri(Context ctx, Uri uri) {
        String result = "";
        String documentID;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            String[] pathParts = uri.getPath().split("/");
            documentID = pathParts[pathParts.length - 1];
        } else {
            String pathSegments[] = uri.getLastPathSegment().split(":");
            documentID = pathSegments[pathSegments.length - 1];
        }
        String mediaPath = MediaStore.Images.Media.DATA;
        Cursor imageCursor = ctx.getContentResolver().query(uri, new String[]{mediaPath}, MediaStore.Images.Media._ID + "=" + documentID, null, null);
        if (imageCursor.moveToFirst()) {
            result = imageCursor.getString(imageCursor.getColumnIndex(mediaPath));
        }
        return result;
    }


    public static boolean saveVideoThumb(String filenameTarget, String video) {
        Bitmap bmp;

        bmp = ThumbnailUtils.createVideoThumbnail(video, MediaStore.Video.Thumbnails.MINI_KIND);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filenameTarget);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            if (utl.DEBUG_ENABLED) e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                    return true;
                }
            } catch (IOException e) {
                if (utl.DEBUG_ENABLED) e.printStackTrace();
                return false;
            }

            return false;
        }


    }

    public static void logEvent(String title, Object b) {


        try {

            //  App.ses.child(utl.refineString( ins,"_")).child("user").setValue((new Gson()).toJson(b).replace("\\\"","\"").replace("\"{","{").replace("}\"","}"));
        } catch (Exception e) {
            if (utl.DEBUG_ENABLED) e.printStackTrace();
        }

    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context ctx) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String requireNotNull(String data1) {
        if (data1 == null)
            return "";
        return data1;
    }

    boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.toLowerCase().contains(context.getPackageName()) ||
                                context.getPackageName().toLowerCase().contains(activeProcess.toLowerCase())
                        ) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().toLowerCase().contains(context.getPackageName()) ||
                    context.getPackageName().toLowerCase().contains(componentInfo.getPackageName().toLowerCase())
            ) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }


    public static interface ClickCallBack {

        public void done(DialogInterface dialogInterface);

    }

    public static interface InputDialogCallback {
        public void onDone(String text);
    }

    public static class JSONParser<T> {
        public ArrayList<T> parseJSONArray(String parseArray, Class<T> type) {
            ArrayList<T> arrayList = new ArrayList<>();
            try {

                JSONArray jsonArray = new JSONArray(parseArray);
                for (int i = 0; i < jsonArray.length(); i++) {
                    T ob = utl.js.fromJson(jsonArray.get(i).toString(), type);
                    arrayList.add(ob);
                }


            } catch (Exception e) {
                if (utl.DEBUG_ENABLED) e.printStackTrace();
            }

            return arrayList;
        }
    }


    public static class NotificationMessage {

        public static String TYPE_MESSAGE = "TYPE_MESSAGE";
        public static String TYPE_REQUEST = "TYPE_REQUEST";
        public static String TYPE_PROMO = "TYPE_PROMO";
        public static String TYPE_REPLY = "TYPE_REPLY";

        public String title;
        public String message;
        public String type;
        public Long time;
        public int icon = R.drawable.ic_notifications_black_48dp;
        public String className;
        public HashMap<String, String> extras;


        public NotificationMessage(String type, String title, String message, Long time, int icon, Intent intent) {
            this.title = title;
            this.type = type;
            this.message = message;
            this.time = time;
            this.icon = icon;

            className = intent.getComponent().getClassName();
            // utl.e("NotifM","class "+className);

            extras = new HashMap<>();
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Object value = bundle.get(key);

                    extras.put(key, value.toString());

                }
            }

        }

        public static void saveNotification(NotificationMessage notif, Context ctx) {
            String jNtr = utl.getKey("notifs", ctx);
            if (jNtr == null) {
                jNtr = "[]";
            }
            JSONParser<NotificationMessage> parser = new JSONParser<NotificationMessage>();
            ArrayList<NotificationMessage> notificationMessages = parser.parseJSONArray(jNtr, NotificationMessage.class);

            notificationMessages.add(notif);

            utl.setKey("notifs", utl.js.toJson(notificationMessages), ctx);


        }

        public static ArrayList<NotificationMessage> getAll(Context ctx) {

            String jNtr = utl.getKey("notifs", ctx);
            if (jNtr == null) {
                jNtr = "[]";
            }
            JSONParser<NotificationMessage> parser = new JSONParser<NotificationMessage>();


            return parser.parseJSONArray(jNtr, NotificationMessage.class);

        }

        public static void deleteAll(Context ctx) {

            String jNtr = utl.getKey("notifs", ctx);
            if (jNtr == null) {
                jNtr = "[]";
            }
            JSONParser<NotificationMessage> parser = new JSONParser<NotificationMessage>();
            ArrayList<NotificationMessage> notificationMessages = parser.parseJSONArray(jNtr, NotificationMessage.class);

            notificationMessages = new ArrayList<>();
            utl.setKey("notifs", utl.js.toJson(notificationMessages), ctx);


        }

        public static NotificationMessage deleteMessage(Long id, Context ctx) {
            String jNtr = utl.getKey("notifs", ctx);
            if (jNtr == null) {
                jNtr = "[]";
            }
            JSONParser<NotificationMessage> parser = new JSONParser<NotificationMessage>();
            ArrayList<NotificationMessage> notificationMessages = parser.parseJSONArray(jNtr, NotificationMessage.class);

            for (NotificationMessage m : notificationMessages) {
                if (m.time.equals(id)) {
                    notificationMessages.remove(m);
                    utl.setKey("notifs", utl.js.toJson(notificationMessages), ctx);

                    return m;
                }
            }


            return null;

        }

        public String getTimeFormatted() {

            Date startDate = new Date(time);
            Date endDate = Calendar.getInstance().getTime();


            //milliseconds
            long different = endDate.getTime() - startDate.getTime();

            if (different < 60000) {
                return "just now";
            }
/*
        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);*/

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


            if (elapsedDays > 7) {

            } else if (elapsedDays > 0) {

                format = new SimpleDateFormat("MMMM dd hh:mm a");

            } else {
                format = new SimpleDateFormat("hh:mm a");
            }

            return format.format(startDate);
        }

        public Intent getIntent(Context ctx) {

            Intent it = new Intent();

            String activityToStart = className;
            //  utl.e("NotifM","Class name searchc "+className);
            try {
                Class<?> c = Class.forName(activityToStart);
                it = new Intent(ctx, c);

                Set keys = extras.keySet();
                Iterator iterable = keys.iterator();

                try {
                    if (iterable.hasNext()) {
                        do {
                            String key = iterable.next().toString();

                            it.putExtra(key, extras.get(key));
                        } while (iterable.hasNext());
                    }
                } catch (Exception e) {

                }

            } catch (ClassNotFoundException ignored) {
                // utl.e("NotifM","Class name searchc  not found "+ignored.getMessage());
            }


            return it;

        }

    }


}