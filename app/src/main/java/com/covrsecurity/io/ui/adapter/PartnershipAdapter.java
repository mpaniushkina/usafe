package com.covrsecurity.io.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.app.GlideApp;
import com.covrsecurity.io.domain.entity.MerchantEntity;
import com.covrsecurity.io.ui.component.IPartnershipClickListener;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by alex on 2.5.16.
 */
public class PartnershipAdapter extends RecyclerView.Adapter<PartnershipAdapter.PartnershipViewHolder> {

    private List<MerchantEntity> mPartnershipsList;
    private IPartnershipClickListener mPartnershipClickListener;

    public static class PartnershipViewHolder extends RecyclerView.ViewHolder {

        private TextView mPartnerTitle;
        private ImageView mPartnerLogo;
        private TextView mAction;
        private RelativeLayout mItem;
//        private View mNewElementIndicator;

        public PartnershipViewHolder(View view) {
            super(view);
            mPartnerLogo = view.findViewById(R.id.iv_partnership_logo);
            mPartnerTitle = view.findViewById(R.id.tv_partnership_title);
            mAction = view.findViewById(R.id.tv_partnership_action);
            mItem = view.findViewById(R.id.rl_partnership_item);
//            mNewElementIndicator = (View) view.findViewById(R.id.partnership_new_element_indicator);
        }
    }

    public PartnershipAdapter(final List<MerchantEntity> partnershipsList, IPartnershipClickListener partnershipClickListener) {
        this.mPartnershipsList = new ArrayList<>();
        this.mPartnershipsList.addAll(partnershipsList);
        this.mPartnershipClickListener = partnershipClickListener;
    }

    public void setData(List<MerchantEntity> partnershipsList) {
//        if(mPartnershipsList == null) {
//            mPartnershipsList = new ArrayList<>();
//        }
//        if(partnershipsList != null && !partnershipsList.isEmpty()) {
//            for (MerchantEntity partnership : partnershipsList) {
//                if (!isPartnershipAlreadyExist(partnership)) {
//                    mPartnershipsList.add(partnership);
//                }
//            }
//        }
        if (partnershipsList != null) {
            mPartnershipsList = partnershipsList;
        } else {
            if (mPartnershipsList == null) {
                mPartnershipsList = new ArrayList<>();
            }
            Timber.e("setData: partnershipsList == null!");
        }
        notifyDataSetChanged();
    }

    private boolean isPartnershipAlreadyExist(MerchantEntity partnershipToAdd) {
        if (mPartnershipsList != null && !mPartnershipsList.isEmpty()) {
            for (MerchantEntity partnership : mPartnershipsList) {
                if (partnership.getId().equals(partnershipToAdd.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public PartnershipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PartnershipViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.partnership_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PartnershipViewHolder holder, int position) {
        final MerchantEntity partnership = getItem(position);
        if (partnership != null) {
            holder.mItem.setOnClickListener(v -> {
                if (mPartnershipClickListener != null) {
                    mPartnershipClickListener.onPartnershipClicked(partnership);
                }
            });
            holder.mPartnerTitle.setText(partnership.getCompany().getName());

            // TODO: 10.2.17  
            //It was removed because of COVR-678 (The list of connections is marked with dots)
            //holder.mNewElementIndicator.setVisibility(partnership.isViewed() ? View.INVISIBLE : View.VISIBLE);
            GlideApp.with(AppAdapter.context())
                    .load(partnership.getCompany().getLogo())
                    .centerCrop()
                    .error(R.drawable.about_logo)
                    .into(holder.mPartnerLogo);
        }
    }

    @Override
    public int getItemCount() {
        return mPartnershipsList == null ? 0 : mPartnershipsList.size();
    }

    private MerchantEntity getItem(int position) {
        return mPartnershipsList == null ? null : mPartnershipsList.get(position);
    }


}
