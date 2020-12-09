package com.covrsecurity.io.ui.adapter;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.app.IamApp;
import com.covrsecurity.io.app.GlideApp;
import com.covrsecurity.io.domain.entity.StatusEntity;
import com.covrsecurity.io.domain.entity.TransactionEntity;
import com.covrsecurity.io.utils.StatusUtils;
import com.covrsecurity.io.utils.ConstantsUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by alex on 11.5.16.
 */
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

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        final CheckingPendingRequest pendingRequest = getItem(position);
        if (pendingRequest != null) {
//
//            holder.mItem.setOnClickListener(v -> {
//                if (isSelectionState) {
//                    pendingRequest.changeState();
//                    boolean checked = pendingRequest.isChecked();
////                    if (!checked) {
////                        mShouldSelectAll = false;
////                    }
//                    if (checked) {
//                        holder.mItem.setBackgroundColor(AppAdapter.resources().getColor(R.color.faded_tealish));
//                    } else {
//                        holder.mItem.setBackground(AppAdapter.resources().getDrawable(R.drawable.about_clickable_bg));
//                    }
//                    if (mHistoryClickListener != null) {
//                        mHistoryClickListener.onAllItemsSelected(isAllSelectedNaive());
//                    }
//                } else {
//                    if (mHistoryClickListener != null) {
//                        mHistoryClickListener.onHistoryClick(pendingRequest);
//                    }
//                }
//            });
//
//            if (pendingRequest.isChecked()) {
//                holder.mItem.setBackgroundColor(AppAdapter.resources().getColor(R.color.faded_tealish));
//            } else {
//                holder.mItem.setBackground(AppAdapter.resources().getDrawable(R.drawable.about_clickable_bg));
//            }
//
//            holder.mDate.setText(pendingRequest.getRequest().getTitle());
//
//            long lastTimeHistoryViewed = AppAdapter.settings().getLastTimeHistoryViewed();
//
//            holder.mNewElementIndicator.setVisibility(
//                    (pendingRequest.getValidTo() > lastTimeHistoryViewed &&
//                            !pendingRequest.isViewed() &&
//                            pendingRequest.getStatus() == StatusEntity.EXPIRED)
//                            ? View.VISIBLE : View.INVISIBLE);
//
//            holder.mPartnerTitle.setText(pendingRequest.getCompany().getName());
            String date;

            if (pendingRequest.getUpdatedAt() != 0) {
                date = DateUtils.getRelativeDateTimeString(AppAdapter.context(), pendingRequest.getUpdatedAt(),
                        ConstantsUtils.MILLISECONDS_IN_SECOND, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();
            } else {
                date = DateUtils.getRelativeDateTimeString(AppAdapter.context(), pendingRequest.getCreated(),
                        ConstantsUtils.MILLISECONDS_IN_SECOND, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();
            }
            if (pendingRequest.getStatus() == StatusEntity.EXPIRED) {
                if (pendingRequest.getValidTo() != 0) {
                    date = DateUtils.getRelativeDateTimeString(AppAdapter.context(), pendingRequest.getValidTo(),
                            ConstantsUtils.MILLISECONDS_IN_SECOND, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();
                }
            }
//            holder.mTime.setTextColor(pendingRequest.getStatus() == StatusEntity.EXPIRED
//                    ? AppAdapter.resources().getColor(R.color.red)
//                    : AppAdapter.resources().getColor(R.color.text_color_gray));
            holder.mTime.setText(getString(R.string.history_status_text,
                    StatusUtils.getStatusText(IamApp.getInstance(), pendingRequest) != null ? StatusUtils.getStatusText(IamApp.getInstance(), pendingRequest) : "",
                    date));
//            //holder.mNewElementIndicator.setVisibility(pendingRequest. ? View.VISIBLE : View.INVISIBLE);
//            GlideApp.with(AppAdapter.context())
//                    .load(pendingRequest.getCompany().getLogo())
//                    .centerCrop()
//                    .error(R.drawable.about_logo);
//        }

            holder.mItem.setOnClickListener(v -> {
                if (mHistoryClickListener != null) {
                    mHistoryClickListener.onHistoryItemsClick(pendingRequest);
                }
            });
            holder.mPartnerTitle.setText(pendingRequest.getRequest().getTitle());
            holder.mDate.setText("");
        }
    }

    @Deprecated
    // TODO: 04.01.2017 make more efficient method
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
        //Collections.sort(mPendingRequests, (lhs, rhs) -> new Long(rhs.getSortTime().getMillis() - lhs.getSortTime().getMillis()).intValue());
        notifyDataSetChanged();
    }

//    public List<TransactionEntity> getAll() {
//        List<TransactionEntity> itemsList = new ArrayList<>();
//        itemsList.add()
//        return itemsList;
//        return toPendingRequests(mPendingRequests);
//    }

    public List<TransactionEntity> getAll() {
        List<TransactionEntity> itemsList = new ArrayList<>();
        TransactionEntity entity = new TransactionEntity("id", null, "companyClientId", "templateId", null, 100600, 100500, null, "createdByIp", "verifiedByIp", 100500, 100500, "acceptHistoryMessage", "rejectHistoryMessage", "expiredHistoryMessage", "historyMessage", null, false, null, null);
        itemsList.add(entity);
        itemsList.add(entity);
        itemsList.add(entity);
        itemsList.add(entity);
        itemsList.add(entity);
        return itemsList;
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
        private TextView mDate;
        private TextView mTime;
        private View mItem;
        private View mNewElementIndicator;
//        private ImageView mArrowView;

        public HistoryViewHolder(View view) {
            super(view);
            mItem = view.findViewById(R.id.rl_history_item);
            mNewElementIndicator = view.findViewById(R.id.history_new_element_indicator);
            mPartnerTitle = view.findViewById(R.id.tv_history_title);
            mDate = view.findViewById(R.id.tv_history_date);
            mTime = view.findViewById(R.id.tv_history_time);
//            mArrowView = view.findViewById(R.id.v_history_right_arrow);
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
                    r.getRequest(), r.isViewed(), r.getSignature(), r.getBiometric());
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
