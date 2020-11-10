package com.covrsecurity.io.ui.fragment.unauthorized;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentEnterPersonalCodeBinding;
import com.covrsecurity.io.ui.activity.UnauthorizedActivity;
import com.covrsecurity.io.ui.component.PersonalCodeLayout;
import com.covrsecurity.io.ui.interfaces.IKeyboardListener;
import com.covrsecurity.io.ui.viewmodel.createpersonalcode.CreatePersonalCodeViewModel;
import com.covrsecurity.io.utils.KeyboardUtils;

import static com.covrsecurity.io.utils.KeyboardUtils.showKeyboard;

/**
 * Created by elena on 4/28/16.
 */
public abstract class EnterPersonalCodeFragment extends
        BaseUnauthorizedViewModelFragment<FragmentEnterPersonalCodeBinding, CreatePersonalCodeViewModel>
        implements
        IKeyboardListener,
        PersonalCodeLayout.LenghtCodeChecker {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (infoPagerIsDisplayed()) {
            mBinding.infoPager.setVisibility(View.VISIBLE);
        }
        mBinding.personCodeLL.setLenghtCodeChecker(this);
        return view;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_enter_personal_code;
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mBinding.digitalKeyboard.setKeyboardListener(this);
    }

    @Override
    public void onKeyboardButtonClick(boolean clickNotInterceded, char value) {
        if (clickNotInterceded) {
            mBinding.personCodeLL.numberEntered(value);
        } else {
            showToast(R.string.touch_been_intercepted_alert);
        }
    }

    protected abstract boolean infoMessageIsDisplayed();

    protected abstract boolean infoPagerIsDisplayed();
}

