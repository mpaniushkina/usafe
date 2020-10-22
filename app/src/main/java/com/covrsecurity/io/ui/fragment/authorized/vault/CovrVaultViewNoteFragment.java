package com.covrsecurity.io.ui.fragment.authorized.vault;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.loader.content.Loader;
import android.view.View;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.CovrApp;
import com.covrsecurity.io.databinding.FragmentCovrVaultViewNoteBinding;
import com.covrsecurity.io.greendao.model.database.RecordType;
import com.covrsecurity.io.model.databinding.CovrVaultEditNoteModel;
import com.covrsecurity.io.ui.loaders.ContentWrapper;
import com.covrsecurity.io.utils.DialogUtils;
import com.covrsecurity.io.utils.FragmentAnimationSet;

/**
 * Created by Lenovo on 12.05.2017.
 */

public class CovrVaultViewNoteFragment extends CovrVaultBaseViewFragment<FragmentCovrVaultViewNoteBinding, CovrVaultEditNoteModel> {

    private static final String TAG = CovrVaultViewNoteFragment.class.getSimpleName();

    private CovrVaultEditNoteModel mEditNoteModel;

    public static Fragment newInstance(long recordId, boolean windowMode) {
        return newInstance(recordId, windowMode, true);
    }

    public static Fragment newInstance(long recordId, boolean windowMode, boolean showAnimationOnStart) {
        Bundle args = new Bundle();
        args.putLong(RECORD_ID, recordId);
        args.putBoolean(WINDOW_MODE, windowMode);
        args.putBoolean(SHOW_ANIMATION_ON_START, showAnimationOnStart);
        CovrVaultViewNoteFragment fragment = new CovrVaultViewNoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initContentBinding(FragmentCovrVaultViewNoteBinding viewNoteBinding) {
        if (mEditNoteModel == null) {
            mEditNoteModel = new CovrVaultEditNoteModel();
        }
        viewNoteBinding.setNoteEditModel(mEditNoteModel);
        viewNoteBinding.setToFavoriteAction(v -> toFavoriteAction());
        if (!mWindowMode) {
            viewNoteBinding.setEditAction(v -> editAction());
            viewNoteBinding.setDeleteAction(v -> deleteAction());
        } else {
            viewNoteBinding.covrVaultEditDeletePanel.getRoot().setVisibility(View.GONE);
            viewNoteBinding.covrVaultCloseOpenPanel.getRoot().setVisibility(View.VISIBLE);
            viewNoteBinding.covrVaultCloseOpenPanel.covrVaultLeftBottomButton.setOnClickListener(v -> openFullScreenFragment(RecordType.NOTE));
            viewNoteBinding.covrVaultCloseOpenPanel.covrVaultRightBottomButton.setOnClickListener(v -> closeChildFragment());
        }
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_covr_vault_view_note;
    }

    @Override
    protected int getSecondaryToolbarTitle() {
        return R.string.history_details_title;
    }

    @Override
    public Loader<ContentWrapper<CovrVaultEditNoteModel>> onCreateLoader(int id, Bundle args) {
        mBinding.progressLayout.container.setVisibility(View.VISIBLE);
        return super.onCreateLoader(id, args);
    }

    @Override
    protected CovrVaultEditNoteModel doLoad() {
        return CovrApp.getInstance().getDatabaseOperationsWrapper().queryUnique(mRecordId, CovrVaultEditNoteModel.class);
    }

    @Override
    protected void onLoadSuccess(CovrVaultEditNoteModel content) {
        mEditNoteModel = content;
        mContentBinding.setNoteEditModel(mEditNoteModel);
        mBinding.progressLayout.container.setVisibility(View.GONE);
    }

    @Override
    protected void onLoadError(Exception e) {
        mBinding.progressLayout.container.setVisibility(View.GONE);
    }

    private void toFavoriteAction() {
        setFavoriteAction(mEditNoteModel.invertFavorite());
    }

    protected void editAction() {
        super.editAction();
        Bundle bundle = new Bundle();
        bundle.putParcelable(CovrVaultEditNoteFragment.BASE_EDIT_MODEL, mEditNoteModel);
        bundle.putLong(CovrVaultEditNoteFragment.DB_RECORD_ID, mRecordId);
        replaceFragment(CovrVaultEditNoteFragment.newInstance(), bundle, true, FragmentAnimationSet.SLIDE_LEFT);
    }

    private void deleteAction() {
        DialogUtils.showAlertDialog(getActivity(), R.string.covr_vault_view_note_delete_dialog_title, R.string.covr_vault_view_card_delete_dialog_message,
                R.string.delete, (dialog, which) -> deleteRecordAndClose(),
                R.string.cancel, null,
                false, true);
    }
}
