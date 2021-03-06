package com.covrsecurity.io.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;
import com.covrsecurity.io.app.GlideApp;
import com.covrsecurity.io.domain.entity.MerchantEntity;
import com.covrsecurity.io.ui.component.IPartnershipClickListener;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class PartnershipAdapter extends RecyclerView.Adapter<PartnershipAdapter.PartnershipViewHolder> {

    private List<MerchantEntity> mPartnershipsList;
    private IPartnershipClickListener mPartnershipClickListener;
    private MerchantEntity partnership;

    public class PartnershipViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mPartnerLogo;

        public PartnershipViewHolder(View view) {
            super(view);
            mPartnerLogo = view.findViewById(R.id.connectionLogo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mPartnershipClickListener != null) mPartnershipClickListener.onPartnershipClicked(mPartnershipsList.get(getAdapterPosition()));
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

    @Override
    public PartnershipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PartnershipViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_connections_rv, parent, false));
    }

    @Override
    public void onBindViewHolder(PartnershipViewHolder holder, int position) {
        partnership = getItem(position);
        GlideApp.with(AppAdapter.context())
                .load(partnership.getCompany().getLogo())
                .centerCrop()
                .error(R.drawable.iamlogo2x)
                .into(holder.mPartnerLogo);
    }

    @Override
    public int getItemCount() {
        return mPartnershipsList == null ? 0 : mPartnershipsList.size();
    }

    private MerchantEntity getItem(int position) {
        return mPartnershipsList == null ? null : mPartnershipsList.get(position);
    }


}
