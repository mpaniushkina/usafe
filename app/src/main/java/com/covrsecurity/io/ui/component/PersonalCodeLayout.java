package com.covrsecurity.io.ui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.covrsecurity.io.R;

import java.util.Arrays;

/**
 * Created by elena on 4/28/16.
 */
public class PersonalCodeLayout extends RelativeLayout implements View.OnClickListener {

    private final static int CODE_LENGTH = 6;

    private RelativeLayout personalCodeLL;
    private char[] mEnteredText = new char[CODE_LENGTH];
    private LenghtCodeChecker lenghtCodeChecker;
    private int mPrevLength;

    private ImageView eraseBtn;
    private ConstraintLayout eraseLayout;

    public PersonalCodeLayout(Context context) {
        super(context);
        initView(context);
    }

    public PersonalCodeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PersonalCodeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setLenghtCodeChecker(LenghtCodeChecker lenghtCodeChecker) {
        this.lenghtCodeChecker = lenghtCodeChecker;
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        personalCodeLL = (RelativeLayout) inflater.inflate(R.layout.personl_code_displayer, this, true);
        personalCodeLL.findViewById(R.id.erased_button).setOnClickListener(this);

//        eraseLayout = (ConstraintLayout) inflater.inflate(R.layout.keyboard_digital, this, true);
//        eraseLayout.findViewById(R.id.erasedButton).setOnClickListener(this);


        Arrays.fill(mEnteredText, Character.MIN_VALUE);
    }

    public void numberEntered(char enteredNumber) {
        int length = getNonEmptyLength(mEnteredText);
        if (length < CODE_LENGTH) {
            mEnteredText[length] = enteredNumber;
            TextView view = findItem();
            if (view != null) {
                view.setSelected(true);
            }
        }
        if (isCodeLengthValid(getNonEmptyLength(mEnteredText))) {
            lenghtCodeChecker.codeLengthOK();
        }
    }

    public void numberErased() {
        TextView view = findItem();
        if (view != null) {
            view.setSelected(false);
        }

    }

    public void numberErased(int highlightedItem) {
        TextView view = findItem(highlightedItem);
        if (view != null) {
            view.setSelected(false);
        }
    }


    private TextView findItem() {
        return findItem(getNonEmptyLength(mEnteredText));
    }

    private TextView findItem(int highlightedItem) {
        int highlightedResourceId = getResources().getIdentifier("code_entered_" + highlightedItem, "id", getContext().getApplicationContext().getPackageName());
        return (TextView) personalCodeLL.findViewById(highlightedResourceId);
//        return (TextView) eraseLayout.findViewById(highlightedResourceId);
    }

    @Override
    public void onClick(View v) {
        int length = getNonEmptyLength(mEnteredText);
        if (length > 0) {
            numberErased();
            mEnteredText[length - 1] = Character.MIN_VALUE;
        }

        if (!isCodeLengthValid(length)) {
            lenghtCodeChecker.codeLengthNOK();
        }
    }

    public boolean isCodeLayoutFull() {
        return getNonEmptyLength(mEnteredText) == CODE_LENGTH;
    }

    private boolean isCodeLengthValid(int length) {
        boolean valid = length == CODE_LENGTH && mPrevLength != length;
        mPrevLength = length;
        return valid;
    }

    public char[] getEnteredText() {
        return mEnteredText;
    }

    public void clearText() {
        Arrays.fill(mEnteredText, Character.MIN_VALUE);
        for (int i = 0; i < CODE_LENGTH; i++) {
            numberErased(i + 1);
        }
    }

    private int getNonEmptyLength(char[] chars) {
        int length = 0;
        for (char c : chars) {
            if (c != Character.MIN_VALUE) {
                length++;
            }
        }
        return length;
    }

    public interface LenghtCodeChecker {
        void codeLengthOK();

        void codeLengthNOK();
    }
}
