package com.covrsecurity.io.ui.fragment.authorized.vault;

import android.app.AlertDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.IamApp;
import com.covrsecurity.io.model.databinding.CovrVaultBaseModel;
import com.covrsecurity.io.model.databinding.ToolbarModel;
import com.covrsecurity.io.ui.activity.AuthorizedActivity;
import com.covrsecurity.io.ui.viewmodel.base.StubViewModel;
import com.covrsecurity.io.ui.viewmodel.base.StubViewModelFactory;
import com.covrsecurity.io.utils.DatabaseOperationsWrapper;
import com.covrsecurity.io.utils.DialogUtils;
import com.covrsecurity.io.utils.KeyboardUtils;

import javax.inject.Inject;

/**
 * Created by Lenovo on 02.06.2017.
 * public class AndroidBug5497Workaround
 * For more information, see https://code.google.com/p/android/issues/detail?id=5497
 */
public abstract class CovrVaultBaseEditFragment<ContentBinding extends ViewDataBinding, EditModel extends CovrVaultBaseModel> extends
        CovrVaultBaseParentFragment<ContentBinding> {

    private static final String TAG = CovrVaultBaseEditFragment.class.getSimpleName();

    public static final String DB_RECORD_ID = "DB_RECORD_ID";
    public static final String BASE_EDIT_MODEL = "BASE_EDIT_MODEL";
    public static final String ACTION_NEW_ITEM = CovrVaultBaseEditFragment.class.getCanonicalName() + ".NEW_ITEM";

    @Inject
    StubViewModelFactory vmFactory;

    private AlertDialog mAlertDialog;

    protected ToolbarModel mToolbarModel;
    protected EditModel mBaseEditModel;
    protected long mDbRecordId = -1;

    @Override
    public void closeChildFragment() {
    }

    @Override
    public void showChildFragment() {
    }

    @Override
    protected int getFragmentContainerId() {
        return 0;
    }

    @Override
    protected int getTitleId() {
        return R.string.covr_vault;
    }

    @NonNull
    @Override
    protected Class<StubViewModel> getViewModelClass() {
        return StubViewModel.class;
    }

    @NonNull
    @Override
    protected ViewModelProvider.Factory getViewModelFactory() {
        return vmFactory;
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        if (mBaseEditModel == null && getArguments() != null) {
            mBaseEditModel = getArguments().getParcelable(BASE_EDIT_MODEL);
            mDbRecordId = getArguments().getLong(DB_RECORD_ID);
        }
        if (mBaseEditModel == null) {
            mBaseEditModel = getDefaultEditModel();
            mDbRecordId = -1;
        }
        if (mToolbarModel == null) {
            mToolbarModel = new ToolbarModel.Builder()
                    .setLeftButtonText(R.string.cancel)
                    .setLeftButtonClick(v -> cancel())
                    .setTitleText(mDbRecordId == -1 ? R.string.covr_vault_payment_card_edit_toolbar_button_new : R.string.covr_vault_payment_card_edit_toolbar_button_edit)
                    .setRightButtonText(R.string.covr_vault_payment_card_edit_toolbar_button_save)
                    .setRightButtonClick(v -> save())
                    .create();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AuthorizedActivity) getActivity()).setShouldFragmentBeRetained(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        KeyboardUtils.hideKeyboard(getActivity());
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
    }

    @Override
    public void onKeyboardBackPressed() {
        super.onKeyboardBackPressed();
        ((AuthorizedActivity) getActivity()).setShouldFragmentBeRetained(false);
    }

    protected void cancel() {
        KeyboardUtils.hideKeyboard(getActivity());
        ((AuthorizedActivity) getActivity()).setShouldFragmentBeRetained(false);
        onBackPressed();
    }

    private void save() {
        KeyboardUtils.hideKeyboard(getActivity());
        if (saveEntity()) {
            ((AuthorizedActivity) getActivity()).setShouldFragmentBeRetained(false);
        }
    }

    protected boolean saveEntity() {
        boolean empty = TextUtils.isEmpty(mBaseEditModel.getDescription());
        if (empty) {
            mAlertDialog = DialogUtils.showOkDialog(getActivity(), getString(R.string.covr_vault_payment_card_edit_save_dialog_title), getString(getAlertDialogMessage()), true);
        } else {
            IamApp.getInstance().getDatabaseOperationsWrapper().insertOrReplaceAsync(mBaseEditModel, mDbRecordId, new DatabaseOperationsWrapper.Result<Void>() {
                @Override
                public void onSuccess(Void result) {
                    sendMessage();
                    CovrVaultBaseEditFragment.this.onBackPressed();
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }, true);
        }
        return !empty;
    }

    private void sendMessage() {
        Intent intent = new Intent(ACTION_NEW_ITEM);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    protected abstract EditModel getDefaultEditModel();

    @StringRes
    protected abstract int getAlertDialogMessage();
}
