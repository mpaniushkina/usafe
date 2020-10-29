package com.covrsecurity.io.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Vibrator;
import androidx.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.ui.interfaces.IKeyboardListener;

public class DigitalKeyboard extends LinearLayout implements View.OnClickListener {

    @ColorInt
    private final int whiteColor;

    private TextView mBtnOne;
    private TextView mBtnTwo;
    private TextView mBtnThree;
    private TextView mBtnFour;
    private TextView mBtnFive;
    private TextView mBtnSix;
    private TextView mBtnSeven;
    private TextView mBtnEight;
    private TextView mBtnNine;
    private TextView mBtnZero;

    public ImageView erasedButton;

    private static final long VIBRATION_LENGTH = 60;

    private IKeyboardListener mKeyboardListener;
    private Vibrator mVibrator = null;
    private SoundPool mSoundPool = null;
    private boolean mSoundPoolLoaded = false;
    private Integer mSoundId = null;
    private boolean mClickNotInterceded;

    public DigitalKeyboard(Context context) {
        super(context);
        whiteColor = getResources().getColor(R.color.white);
        initView(context, false, false);
    }

    public DigitalKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        whiteColor = getResources().getColor(R.color.white);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DigitalKeyboardAttr, 0, 0);
        try {
            initView(context,
                    ta.getBoolean(R.styleable.DigitalKeyboardAttr_canSound, false),
                    ta.getBoolean(R.styleable.DigitalKeyboardAttr_canVibrate, false));
        } finally {
            ta.recycle();
        }
    }

    public DigitalKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        whiteColor = getResources().getColor(R.color.white);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DigitalKeyboardAttr, 0, 0);
        try {
            initView(context,
                    ta.getBoolean(R.styleable.DigitalKeyboardAttr_canSound, false),
                    ta.getBoolean(R.styleable.DigitalKeyboardAttr_canVibrate, false));
        } finally {
            ta.recycle();
        }
    }

    @Deprecated
    @Override
    public void setOnTouchListener(OnTouchListener l) {
    }

    @Override
    public boolean onFilterTouchEventForSecurity(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN && event.getAction() != MotionEvent.ACTION_UP) {
            return true;
        }
        mClickNotInterceded = (event.getFlags() & MotionEvent.FLAG_WINDOW_IS_OBSCURED) != MotionEvent.FLAG_WINDOW_IS_OBSCURED;
        return super.onFilterTouchEventForSecurity(event);
    }

    private void initView(Context context, boolean canSound, boolean canVibrate) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout item = (LinearLayout) inflater.inflate(R.layout.keyboard_digital, this, true);

        mBtnOne = item.findViewById(R.id.keyboard_btn_one);
        mBtnTwo = item.findViewById(R.id.keyboard_btn_two);
        mBtnThree = item.findViewById(R.id.keyboard_btn_three);
        mBtnFour = item.findViewById(R.id.keyboard_btn_four);
        mBtnFive = item.findViewById(R.id.keyboard_btn_five);
        mBtnSix = item.findViewById(R.id.keyboard_btn_six);
        mBtnSeven = item.findViewById(R.id.keyboard_btn_seven);
        mBtnEight = item.findViewById(R.id.keyboard_btn_eight);
        mBtnNine = item.findViewById(R.id.keyboard_btn_nine);
        mBtnZero = item.findViewById(R.id.keyboard_btn_zero);

        erasedButton = item.findViewById(R.id.erasedButton);

        mBtnOne.setOnClickListener(this);
        mBtnTwo.setOnClickListener(this);
        mBtnThree.setOnClickListener(this);
        mBtnFour.setOnClickListener(this);
        mBtnFive.setOnClickListener(this);
        mBtnSix.setOnClickListener(this);
        mBtnSeven.setOnClickListener(this);
        mBtnEight.setOnClickListener(this);
        mBtnNine.setOnClickListener(this);
        mBtnZero.setOnClickListener(this);

        erasedButton.setOnClickListener(this);

        mBtnOne.setOnTouchListener((v, event) -> getTouchListener((TextView) v, event));
        mBtnTwo.setOnTouchListener((v, event) -> getTouchListener((TextView) v, event));
        mBtnThree.setOnTouchListener((v, event) -> getTouchListener((TextView) v, event));
        mBtnFour.setOnTouchListener((v, event) -> getTouchListener((TextView) v, event));
        mBtnFive.setOnTouchListener((v, event) -> getTouchListener((TextView) v, event));
        mBtnSix.setOnTouchListener((v, event) -> getTouchListener((TextView) v, event));
        mBtnSeven.setOnTouchListener((v, event) -> getTouchListener((TextView) v, event));
        mBtnEight.setOnTouchListener((v, event) -> getTouchListener((TextView) v, event));
        mBtnNine.setOnTouchListener((v, event) -> getTouchListener((TextView) v, event));
        mBtnZero.setOnTouchListener((v, event) -> getTouchListener((TextView) v, event));

        erasedButton.setOnTouchListener((v, event) -> getTouchListener((TextView) v, event));

        if (isVibrationAllowedBySettings() && canVibrate) {
            mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (!mVibrator.hasVibrator()) {
                mVibrator = null;
            }
        }

        if (isSoundAllowedBySettings() && canSound) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mSoundPool = new SoundPool(100, AudioManager.STREAM_MUSIC, 0);
            } else {
                SoundPool.Builder builder = new SoundPool.Builder();
                AudioAttributes.Builder attrsBuilder = new AudioAttributes.Builder();
                attrsBuilder.setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION);
                attrsBuilder.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
                AudioAttributes attrs = attrsBuilder.build();
                builder.setAudioAttributes(attrs);
                builder.setMaxStreams(100);
                mSoundPool = builder.build();
            }
            mSoundId = mSoundPool.load(context, R.raw.click, 1);
            mSoundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> mSoundPoolLoaded = true);
        }
    }

    private boolean getTouchListener(TextView textView, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mClickNotInterceded) {
                textView.setBackground(getResources().getDrawable(R.drawable.keyboard_digital_state_pressed));
            } else {
                textView.setBackground(getResources().getDrawable(R.drawable.keyboard_digital_state_pressed_compromised));
            }
            textView.setTextColor(Color.WHITE);
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            textView.setBackground(getResources().getDrawable(R.drawable.keyboard_digital_state_enabled));
            textView.setTextColor(whiteColor);
            textView.performClick();
            return true;
        }
        return false;
    }

    public void setKeyboardListener(IKeyboardListener keyboardListener) {
        mKeyboardListener = keyboardListener;
    }

    @Override
    public void onClick(View v) {
        if (mKeyboardListener != null) {
            switch (v.getId()) {
                case R.id.keyboard_btn_one:
                case R.id.keyboard_btn_two:
                case R.id.keyboard_btn_three:
                case R.id.keyboard_btn_four:
                case R.id.keyboard_btn_five:
                case R.id.keyboard_btn_six:
                case R.id.keyboard_btn_seven:
                case R.id.keyboard_btn_eight:
                case R.id.keyboard_btn_nine:
                case R.id.keyboard_btn_zero:
                    playSound();
                    vibrate();
                    mKeyboardListener.onKeyboardButtonClick(mClickNotInterceded, ((TextView) v).getText().charAt(0));
                    break;
            }
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mSoundPool != null) {
            mSoundPool.release();
        }
    }

    // check vibration settings here
    private boolean isVibrationAllowedBySettings() {
        return AppAdapter.settings().getKeyboardVibrationEnabled();
    }

    // check vibration settings here
    private boolean isSoundAllowedBySettings() {
        return AppAdapter.settings().getKeyboardSoundEnabled();
    }

    public void shake() {
        Animation shakeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.keyboard_shake);
        startAnimation(shakeAnimation);
    }

    private void vibrate() {
        if (mVibrator != null) {
            mVibrator.vibrate(VIBRATION_LENGTH);
        }
    }

    private void playSound() {
        if (mSoundPool != null && mSoundPoolLoaded && mSoundId != null) {
            if (getContext() != null) {
                AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
                float soundLevel = (float) am.getStreamVolume(AudioManager.STREAM_NOTIFICATION) / (float) am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
                mSoundPool.play(mSoundId, soundLevel, soundLevel, 1, 0, 1f);
            } else {
                mSoundPool.play(mSoundId, 1f, 1f, 1, 0, 1f);
            }
        }
    }
}