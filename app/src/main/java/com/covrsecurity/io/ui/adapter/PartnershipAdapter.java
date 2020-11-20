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
import com.covrsecurity.io.domain.entity.StatusEntity;
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
    private MerchantEntity partnership;

    public class PartnershipViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mPartnerTitle;

        public PartnershipViewHolder(View view) {
            super(view);
            mPartnerTitle = view.findViewById(R.id.connectionName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mPartnershipClickListener != null) mPartnershipClickListener.onPartnershipClicked(partnership);
        }
    }

    public PartnershipAdapter(final List<MerchantEntity> partnershipsList, IPartnershipClickListener partnershipClickListener) {
        this.mPartnershipsList = new ArrayList<>();
        this.mPartnershipsList.addAll(partnershipsList);
        this.mPartnershipClickListener = partnershipClickListener;
    }

    public void setData(List<MerchantEntity> partnershipsList) {
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
                .inflate(R.layout.item_connections_rv, parent, false));
    }

    @Override
    public void onBindViewHolder(PartnershipViewHolder holder, int position) {
        partnership = getItem(position);
//            holder.mPartnerTitle.setText(partnership.getCompany().getName());
            holder.mPartnerTitle.setText("LOGO");
    }

    @Override
    public int getItemCount() {
        return mPartnershipsList == null ? 0 : mPartnershipsList.size();
    }

    private MerchantEntity getItem(int position) {
        return mPartnershipsList == null ? null : mPartnershipsList.get(position);
    }


}
