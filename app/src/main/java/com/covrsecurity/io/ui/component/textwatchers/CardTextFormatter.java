package com.covrsecurity.io.ui.component.textwatchers;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by Lenovo on 25.05.2017.
 */

public class CardTextFormatter implements TextWatcher {

    private final int TOTAL_DIGITS; // max numbers of digits in pattern: 0000 x 4
    private final int TOTAL_SYMBOLS; // size of pattern 0000 0000 0000 0000
    private final int DIVIDER_POSITION; // means divider position is every 4th symbol beginning with 0
    private final int DIVIDER_MODULO; // means divider position is every 5th symbol beginning with 1

    private final char DIVIDER;

    public CardTextFormatter() {
        this(16, 4, ' ');
    }

    public CardTextFormatter(final int totalDigits, final int dividerPosition) {
        this(totalDigits, dividerPosition, ' ');
    }

    public CardTextFormatter(final int totalDigits, final int dividerPosition, final char divider) {
        TOTAL_DIGITS = totalDigits;
        DIVIDER_POSITION = dividerPosition;
        DIVIDER = divider;
        DIVIDER_MODULO = DIVIDER_POSITION + 1;
        TOTAL_SYMBOLS = totalDigits + totalDigits / DIVIDER_MODULO;
    }

    public int getTotalSymbols() {
        return TOTAL_SYMBOLS;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
            s.replace(0, s.length(), buildCorrecntString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
        }
    }

    private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
        boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
        for (int i = 0; i < s.length(); i++) { // chech that every element is right
            if (i > 0 && (i + 1) % dividerModulo == 0) {
                isCorrect &= divider == s.charAt(i);
            } else {
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return isCorrect;
    }

    private String buildCorrecntString(char[] digits, int dividerPosition, char divider) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatted.append(digits[i]);
                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                    formatted.append(divider);
                }
            }
        }

        return formatted.toString();
    }

    private char[] getDigitArray(final Editable s, final int size) {
        char[] digits = new char[size];
        int index = 0;
        for (int i = 0; i < s.length() && index < size; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }
}
