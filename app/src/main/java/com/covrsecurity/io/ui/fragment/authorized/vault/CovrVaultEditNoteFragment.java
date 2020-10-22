package com.covrsecurity.io.ui.fragment.authorized.vault;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;

import com.covrsecurity.io.R;
import com.covrsecurity.io.databinding.FragmentCovrVaultEditNoteBinding;
import com.covrsecurity.io.model.databinding.CovrVaultEditNoteModel;

/**
 * Created by Lenovo on 10.05.2017.
 */

public class CovrVaultEditNoteFragment extends CovrVaultBaseEditFragment<FragmentCovrVaultEditNoteBinding, CovrVaultEditNoteModel> {

    private static final String TAG = CovrVaultEditNoteFragment.class.getSimpleName();

    public static Fragment newInstance() {
        return new CovrVaultEditNoteFragment();
    }

    @Override
    protected void initBinding(LayoutInflater inflater) {
        super.initBinding(inflater);
        mBinding.setToolbarModel(mToolbarModel);
        mBinding.setNoteModel(mBaseEditModel);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_covr_vault_edit_note;
    }

    @Override
    protected CovrVaultEditNoteModel getDefaultEditModel() {
        return new CovrVaultEditNoteModel();
    }

    @Override
    protected int getAlertDialogMessage() {
        return R.string.covr_vault_edit_note_save_dialog_message;
    }
}
