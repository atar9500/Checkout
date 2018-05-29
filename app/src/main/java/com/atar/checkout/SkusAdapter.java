package com.atar.checkout;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.billingclient.api.Purchase;

import java.util.List;

public class SkusAdapter extends RecyclerView.Adapter<SkusAdapter.SkuHolder> {

    /**
     * Data
     */
    private List<Sku> mSkus;
    private SkuCallback mCallback;
    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCallback.onBuyCallback((Sku) v.getTag());
        }
    };

    /**
     * Constructor
     */
    public SkusAdapter(List<Sku> skus, SkuCallback callback){
        mSkus = skus;
        mCallback = callback;
    }

    /**
     * RecyclerView.Adapter Methods
     */
    @NonNull
    @Override
    public SkuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SkuHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sku, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SkuHolder holder, int position) {
        Sku sku = mSkus.get(position);
        holder.mTitle.setText(sku.getTitle());
        holder.mDescription.setText(sku.getDescription());
        holder.mBuy.setText(sku.getPrice());
        holder.mBuy.setEnabled(!sku.isPurchased());
    }

    @Override
    public int getItemCount() {
        return mSkus == null ? 0 : mSkus.size();
    }

    /**
     * Class Methods
     */
    public void updateStatus(Purchase purchase){
        for(int i = 0; i < mSkus.size(); i++){
            Sku sku = mSkus.get(i);
            if(sku.getId().equals(purchase.getOrderId())){
                sku.setPurchased(true);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void updateStatus(Sku sku){
        for(int i = 0; i < mSkus.size(); i++){
            Sku skuFromList = mSkus.get(i);
            if(skuFromList.getId().equals(sku.getId())){
                skuFromList.setPurchased(true);
                notifyItemChanged(i);
                break;
            }
        }
    }

    /**
     * Inner Classes
     */
    class SkuHolder extends RecyclerView.ViewHolder{

        /**
         * UI Widgets
         */
        private TextView mTitle, mDescription;
        private Button mBuy;

        /**
         * Constructor
         */
        SkuHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.sku_title);
            mDescription = itemView.findViewById(R.id.sku_desc);
            mBuy = itemView.findViewById(R.id.sku_buy);
            mBuy.setOnClickListener(mClickListener);
        }
    }

    /**
     * Inner Callbacks
     */
    public interface SkuCallback{
        void onBuyCallback(Sku sku);
    }

}
