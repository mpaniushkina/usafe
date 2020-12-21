package com.covrsecurity.io.ui.adapter;

import android.annotation.SuppressLint;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.app.IamApp;
import com.covrsecurity.io.domain.entity.TransactionEntity;
import com.covrsecurity.io.utils.ConstantsUtils;
import com.covrsecurity.io.utils.StatusUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.covrsecurity.io.domain.entity.StatusEntity.EXPIRED;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private boolean isSelectionState;
    private boolean mShouldSelectAll;

    private List<CheckingPendingRequest> mPendingRequests;
    private IHistoryClickListener mHistoryClickListener;

    public HistoryAdapter(List<TransactionEntity> partnerships, IHistoryClickListener iHistoryClickListener) {
        addAll(partnerships);
        mHistoryClickListener = iHistoryClickListener;
    }

    public void setShouldSelectAll(boolean shouldSelectAll) {
        mShouldSelectAll = shouldSelectAll;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HistoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_item, parent, false));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        final CheckingPendingRequest pendingRequest = getItem(position);
        if (pendingRequest != null) {
            switch (pendingRequest.getStatus()) {
                case ACCEPTED:
                    holder.mNewElementIndicator.setBackground(AppAdapter.resources().getDrawable(R.drawable.ic_completed));
                    break;
                case REJECTED:
                    holder.mNewElementIndicator.setBackground(AppAdapter.resources().getDrawable(R.drawable.ic_failed));
                    break;
                case EXPIRED:
                    holder.mNewElementIndicator.setBackground(AppAdapter.resources().getDrawable(R.drawable.ic_failed));
                    break;
                case ACTIVE:
                    holder.mNewElementIndicator.setBackground(AppAdapter.resources().getDrawable(R.drawable.ic_incoming));
                    break;
                case FAILED_BIOMETRIC:
                    holder.mNewElementIndicator.setBackground(AppAdapter.resources().getDrawable(R.drawable.ic_biometric_icon_failed));
                    break;
            }
            holder.mPartnerTitle.setText(pendingRequest.getRequest() != null ? pendingRequest.getRequest().getTitle() : "");
            String date;
            if (pendingRequest.getUpdatedAt() != 0) {
                date = DateUtils.getRelativeDateTimeString(AppAdapter.context(), pendingRequest.getUpdatedAt(),
                        ConstantsUtils.MILLISECONDS_IN_SECOND, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();
            } else {
                date = DateUtils.getRelativeDateTimeString(AppAdapter.context(), pendingRequest.getCreated(),
                        ConstantsUtils.MILLISECONDS_IN_SECOND, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();
            }
            if (pendingRequest.getStatus() == EXPIRED) {
                if (pendingRequest.getValidTo() != 0) {
                    date = DateUtils.getRelativeDateTimeString(AppAdapter.context(), pendingRequest.getValidTo(),
                            ConstantsUtils.MILLISECONDS_IN_SECOND, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();
                }
            }
            holder.mTime.setText(getString(R.string.history_status_text, StatusUtils.getStatusText(IamApp.getInstance(), pendingRequest), date));

            holder.mItem.setOnClickListener(v -> {
                if (mHistoryClickListener != null) {
                    mHistoryClickListener.onHistoryItemsClick(pendingRequest);
                }
            });
        }
    }

    @Deprecated
    private boolean isAllSelectedNaive() {
        for (CheckingPendingRequest request : mPendingRequests) {
            if (!request.isChecked()) {
                return false;
            }
        }
        return true;
    }

    public CheckingPendingRequest getItem(int position) {
        return mPendingRequests == null ? null : mPendingRequests.get(position);
    }

    @Override
    public int getItemCount() {
        return mPendingRequests == null ? 0 : mPendingRequests.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addAll(List<TransactionEntity> data) {
        if (mPendingRequests == null) {
            mPendingRequests = new ArrayList<>();
        }
        mPendingRequests.addAll(toCheckingPendingRequests(data));
        notifyDataSetChanged();
    }

    public List<TransactionEntity> getAll() {
        return toPendingRequests(mPendingRequests);
    }

    public List<CheckingPendingRequest> getAllChecking() {
        return mPendingRequests;
    }

    public void setSelectionState(boolean selectionState) {
        this.isSelectionState = selectionState;
        onSelectionStateChanged();
    }

    private void onSelectionStateChanged() {
        if (isSelectionState) {
            showCheckBoxes();
        } else {
            hideCheckBoxes();
        }
    }

    public void selectAll() {
        if (mPendingRequests != null && !mPendingRequests.isEmpty()) {
            for (CheckingPendingRequest request : mPendingRequests)
                request.setChecked(true);
        }
        notifyDataSetChanged();
    }

    public void unSelectAll() {
        if (mPendingRequests != null && !mPendingRequests.isEmpty()) {
            for (CheckingPendingRequest request : mPendingRequests)
                request.setChecked(false);
        }
        notifyDataSetChanged();
    }

    public void showCheckBoxes() {
        notifyDataSetChanged();
    }

    public void hideCheckBoxes() {
        unSelectAll();
    }

    public void removeItems(final List<String> idsToRemove) {
        if (mPendingRequests != null && !mPendingRequests.isEmpty() && idsToRemove != null && !idsToRemove.isEmpty()) {
            for (Iterator<CheckingPendingRequest> iterator = mPendingRequests.iterator(); iterator.hasNext(); ) {
                TransactionEntity value = iterator.next();
                for (String id : idsToRemove) {
                    if (id.equals(value.getId())) {
                        iterator.remove();
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    public void removeItem(int position) {
        mPendingRequests.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mPendingRequests.size());
    }

    public void setViewed(TransactionEntity request) {
        for (TransactionEntity request1 : mPendingRequests) {
            if (request.getId().equals(request1.getId())) {
                request1.setViewed(true);
            }
        }
    }

    private List<CheckingPendingRequest> toCheckingPendingRequests(List<TransactionEntity> requests) {
        if (requests == null) {
            return new ArrayList<>(0);
        }
        ArrayList<CheckingPendingRequest> listRequests = new ArrayList<>(requests.size());
        for (TransactionEntity r : requests) {
            CheckingPendingRequest chpr = new CheckingPendingRequest(r, mShouldSelectAll);
            listRequests.add(chpr);
        }
        return listRequests;
    }

    private List<TransactionEntity> toPendingRequests(List<CheckingPendingRequest> requests) {
        return new ArrayList<>(requests);
    }

    private String getString(@StringRes int id) {
        return AppAdapter.resources().getString(id);
    }

    private String getString(@StringRes int id, Object... formatArgs) {
        return AppAdapter.resources().getString(id, formatArgs);
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView mPartnerTitle;
        private TextView mTime;
        private View mItem;
        private View mNewElementIndicator;

        public HistoryViewHolder(View view) {
            super(view);
            mItem = view.findViewById(R.id.rl_history_item);
            mNewElementIndicator = view.findViewById(R.id.historyNewElementIndicator);
            mPartnerTitle = view.findViewById(R.id.tv_history_title);
            mTime = view.findViewById(R.id.tv_history_time);
        }
    }

    public static class CheckingPendingRequest extends TransactionEntity {

        private boolean mIsChecked;

        public CheckingPendingRequest(TransactionEntity r) {
            this(r, false);
        }

        public CheckingPendingRequest(TransactionEntity r, boolean isChecked) {
            super(r.getId(), r.getCompany(), r.getCompanyClientId(), r.getTemplateId(),
                    r.getTemplate(), r.getValidTo(), r.getValidFrom(), r.getStatus(),
                    r.getCreatedByIp(), r.getVerifiedByIp(), r.getCreated(), r.getUpdatedAt(),
                    r.getAcceptHistoryMessage(), r.getRejectHistoryMessage(),
                    r.getExpiredHistoryMessage(), r.getFailedBiometricHistoryMessage(),
                    r.getRequest(), r.isViewed(), r.getSignature(), r.getBiometric(), r.getReferenceId(), r.getReferenceType());
            mIsChecked = isChecked;
        }

        public boolean isChecked() {
            return this.mIsChecked;
        }

        public void setChecked(boolean isChecked) {
            this.mIsChecked = isChecked;
        }

        public void changeState() {
            this.mIsChecked = !this.mIsChecked;
        }
    }

    public interface IHistoryClickListener {

        void onHistoryClick(TransactionEntity pendingTransaction);

        void onAllItemsSelected(boolean allSelected);

        void onHistoryItemsClick(TransactionEntity pendingTransaction);
    }

}
