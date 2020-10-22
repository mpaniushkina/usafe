package com.covrsecurity.io.ui.fragment.unauthorized;
/*

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.covrsecurity.io.R;
import com.covrsecurity.io.ui.activity.UnauthorizedActivity;
import com.covrsecurity.io.ui.dialog.YesNoDialog;

*/
/**
 * Created by elena on 4/28/16.
 *//*

public class InfoCreateCodeWithoutVerificationFragment extends InfoEnterCodeFragment {

    public static Fragment newInstance() {
        return new InfoCreateCodeWithoutVerificationFragment();
    }

    @Override
    public void onResume() {
        ((UnauthorizedActivity) getActivity()).setAllowShowingBottomButtons(true);
        ((UnauthorizedActivity) getActivity()).getLeftButton().setOnClickListener((view)-> {
            showCancelSetupDialog();
        });
        super.onResume();
    }

    public void showCancelSetupDialog() {
        DialogFragment newFragment = YesNoDialog.newInstance(R.string.cancel_dialog_title, R.string.cancel_dialog_message,
                new YesNoDialog.YesNoListener() {
                    @Override
                    public void onYesClicked() {
                        getActivity().finish();
                    }

                    @Override
                    public void onNoClicked() {
                    }
                });
        newFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public boolean onBackButton() {
        return false;
    }
}
*/
