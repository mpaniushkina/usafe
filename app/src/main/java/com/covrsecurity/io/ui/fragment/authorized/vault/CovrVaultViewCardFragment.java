package com.covrsecurity.io.ui.fragment.authorized.vault;

import android.Manifest;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.Loader;

import android.view.View;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.CovrApp;
import com.covrsecurity.io.databinding.FragmentCovrVaultViewCardBinding;
import com.covrsecurity.io.greendao.model.database.RecordType;
import com.covrsecurity.io.model.databinding.CovrVaultViewCardModel;
import com.covrsecurity.io.model.databinding.PaymentCardEditModel;
import com.covrsecurity.io.ui.loaders.ContentWrapper;
import com.covrsecurity.io.utils.DialogUtils;
import com.covrsecurity.io.utils.FragmentAnimationSet;

import static com.covrsecurity.io.utils.ConstantsUtils.CALL_PHONE_REQUEST_CODE;

/**
 * Created by Lenovo on 12.05.2017.
 */

public class CovrVaultViewCardFragment extends CovrVaultBaseViewFragment<FragmentCovrVaultViewCardBinding, PaymentCardEditModel> {

    private static final String TAG = CovrVaultViewCardFragment.class.getSimpleName();

    private CovrVaultViewCardModel mViewCardModel;
    private PaymentCardEditModel mPaymentCardEditModel;

    public static Fragment newInstance(long recordId, boolean windowMode) {
        return newInstance(recordId, windowMode, true);
    }

    public static Fragment newInstance(long recordId, boolean windowMode, boolean showAnimationOnStart) {
        Bundle args = new Bundle();
        args.putLong(RECORD_ID, recordId);
        args.putBoolean(WINDOW_MODE, windowMode);
        args.putBoolean(SHOW_ANIMATION_ON_START, showAnimationOnStart);
        CovrVaultViewCardFragment fragment = new CovrVaultViewCardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initContentBinding(FragmentCovrVaultViewCardBinding viewCardBinding) {
        if (mViewCardModel == null) {
            mViewCardModel = new CovrVaultViewCardModel(false, false);
        }
        viewCardBinding.setViewCardModel(mViewCardModel);
        viewCardBinding.setToFavoriteAction(v -> toFavoriteAction());
        viewCardBinding.setShowCardNumberAction(v -> mViewCardModel.invertShowCardNumber());
        viewCardBinding.setShowCvvAction(v -> mViewCardModel.invertShowCvv());
        viewCardBinding.covrVaultViewCardPhoneIcon.setOnClickListener(v -> callAction());
        if (!mWindowMode) {
            viewCardBinding.setEditAction(v -> editAction());
            viewCardBinding.setDeleteAction(v -> deleteAction());
        } else {
            viewCardBinding.covrVaultEditDeletePanel.getRoot().setVisibility(View.GONE);
            viewCardBinding.covrVaultCloseOpenPanel.getRoot().setVisibility(View.VISIBLE);
            viewCardBinding.covrVaultCloseOpenPanel.covrVaultLeftBottomButton.setOnClickListener(v -> openFullScreenFragment(RecordType.PAYMENT_CARD));
            viewCardBinding.covrVaultCloseOpenPanel.covrVaultRightBottomButton.setOnClickListener(v -> closeChildFragment());
        }
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_covr_vault_view_card;
    }

    @Override
    protected int getSecondaryToolbarTitle() {
        return R.string.history_details_title;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case CALL_PHONE_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeCall();
                } else {
                    showToast(R.string.covr_vault_view_card_call_permission_denied);
                }
            }
        }
    }

    @Override
    public Loader<ContentWrapper<PaymentCardEditModel>> onCreateLoader(int id, Bundle args) {
        mBinding.progressLayout.container.setVisibility(View.VISIBLE);
        return super.onCreateLoader(id, args);
    }

    @Override
    protected PaymentCardEditModel doLoad() {
        return CovrApp.getInstance().getDatabaseOperationsWrapper().queryUnique(mRecordId, PaymentCardEditModel.class);
    }

    @Override
    protected void onLoadSuccess(PaymentCardEditModel content) {
        mPaymentCardEditModel = content;
        mContentBinding.setPaymentCardModel(mPaymentCardEditModel);
        mBinding.progressLayout.container.setVisibility(View.GONE);
    }

    @Override
    protected void onLoadError(Exception e) {
        mBinding.progressLayout.container.setVisibility(View.GONE);
    }

    private void toFavoriteAction() {
        setFavoriteAction(mPaymentCardEditModel.invertFavorite());
    }

    protected void editAction() {
        super.editAction();
        Bundle bundle = new Bundle();
        bundle.putParcelable(CovrVaultEditPaymentCardFragment.BASE_EDIT_MODEL, mPaymentCardEditModel);
        bundle.putLong(CovrVaultEditPaymentCardFragment.DB_RECORD_ID, mRecordId);
        replaceFragment(CovrVaultEditPaymentCardFragment.newInstance(), bundle, true, FragmentAnimationSet.SLIDE_LEFT);
    }

    private void deleteAction() {
        DialogUtils.showAlertDialog(getActivity(), R.string.covr_vault_view_card_delete_dialog_title, R.string.covr_vault_view_card_delete_dialog_message,
                R.string.delete, (dialog, which) -> deleteRecordAndClose(),
                R.string.cancel, null,
                false, true);
    }

    private void callAction() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CALL_PHONE)) {
                showToast(R.string.covr_vault_view_card_call_permission_denied);
            } else {
                this.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_REQUEST_CODE);
            }
        } else {
            makeCall();
        }
    }

    private void makeCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + mPaymentCardEditModel.getPhoneNumber()));
        startActivity(callIntent);
    }
}
