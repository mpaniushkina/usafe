package com.covrsecurity.io.ui.fragment.authorized.vault;

import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentPaymentCardEditBinding;
import com.covrsecurity.io.model.databinding.PaymentCardEditModel;
import com.covrsecurity.io.ui.component.textwatchers.BaseTextWatcher;
import com.covrsecurity.io.ui.component.textwatchers.CardTextFormatter;
import com.covrsecurity.io.ui.component.textwatchers.ExpireDateTextWatcher;
import com.covrsecurity.io.ui.component.textwatchers.ITextChangedListener;
import com.covrsecurity.io.utils.CardType;
import com.covrsecurity.io.utils.ConstantsUtils;
import com.covrsecurity.io.utils.DataBindingCustomAdapter;

/**
 * Created by Lenovo on 10.05.2017.
 */

public class CovrVaultEditPaymentCardFragment extends CovrVaultBaseEditFragment<FragmentPaymentCardEditBinding, PaymentCardEditModel> {

    private static final String TAG = CovrVaultEditPaymentCardFragment.class.getSimpleName();

    public static Fragment newInstance() {
        return new CovrVaultEditPaymentCardFragment();
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mBinding.setToolbarModel(mToolbarModel);
        mBinding.setPaymentCardModel(mBaseEditModel);
        setupTextWatchers(mBinding);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_payment_card_edit;
    }

    @Override
    protected PaymentCardEditModel getDefaultEditModel() {
        return new PaymentCardEditModel();
    }

    @Override
    protected int getAlertDialogMessage() {
        return R.string.covr_vault_payment_card_edit_save_dialog_message;
    }

    private void setupTextWatchers(FragmentPaymentCardEditBinding binding) {
        final CardTextFormatter watcher = new CardTextFormatter(getResources().getInteger(R.integer.payment_card_edit_card_number_max_length), 4);
        binding.cardNumberEdit.addTextChangedListener(watcher);
        binding.cardNumberEdit.addTextChangedListener(new BaseTextWatcher((ITextChangedListener) (s, start, before, count) -> {
            String cardNumberStr = s.toString().replaceAll("\\D", "");
            CardType cardType = CardType.detect(cardNumberStr);
            mBaseEditModel.setCardType(cardType);
            if (s.length() == watcher.getTotalSymbols() && !cardType.validate(cardNumberStr)) {
                DataBindingCustomAdapter.setUnderlineColor(binding.cardNumberEdit, R.color.red);
            } else {
                DataBindingCustomAdapter.setUnderlineColor(binding.cardNumberEdit, R.color.transparent);
            }
        }));
        binding.cardNumberEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String cardNumberStr = binding.cardNumberEdit.getText().toString().replaceAll("\\D", "");
                CardType cardType = CardType.detect(cardNumberStr);
                if (TextUtils.isEmpty(cardNumberStr) || cardType.validate(cardNumberStr)) {
                    DataBindingCustomAdapter.setUnderlineColor(binding.cardNumberEdit, R.color.transparent);
                } else {
                    DataBindingCustomAdapter.setUnderlineColor(binding.cardNumberEdit, R.color.red);
                }
            }
        });
        binding.phoneNumberEdit.addTextChangedListener(new BaseTextWatcher(editable -> {
            String string = editable.toString();
            int length = string.length();
            if (length > 1 && string.charAt(length - 1) == '+') {
                editable.replace(length - 1, length, "");
            }
        }));
        binding.expDateEdit.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                binding.expDateEdit.setText("");
            }
            return false;
        });
        binding.expDateEdit.addTextChangedListener(new ExpireDateTextWatcher(binding.expDateEdit, ConstantsUtils.DEFAULT_EXPIRE_DATE_PATTERN));
    }
}
