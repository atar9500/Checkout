package com.atar.checkout;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    /**
     * Data
     */
    private BillingClient mClient;
    private List<Sku> mSkus;

    /**
     * UI Widgets
     */
    private SkusAdapter mAdapter;

    /**
     * AppCompatActivity Methods
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSkus = new ArrayList<>();

        initUIWidgets();

        mClient = BillingClient.newBuilder(this).setListener(this).build();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(int responseCode) {
                switch (responseCode) {
                    case BillingClient.BillingResponse.OK:
                        retrieveSkus();
                        break;
                    default:
                        showError(R.string.something_went_wrong);
                        break;
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                showError(R.string.could_not_connect);
            }
        });
    }

    /**
     * Class Methods
     */
    private void initUIWidgets() {
        mAdapter = new SkusAdapter(mSkus, new SkusAdapter.SkuCallback() {
            @Override
            public void onBuyCallback(Sku sku) {
                purchase(sku);
            }
        });
        RecyclerView list = findViewById(R.id.am_list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(mAdapter);
        list.setHasFixedSize(true);
    }

    private void retrieveSkus(){
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(Arrays.asList("beer", "coffee", "hamburger", "chocolate"))
                .setType(BillingClient.SkuType.INAPP);
        mClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                switch (responseCode) {
                    case BillingClient.BillingResponse.OK:
                        if(skuDetailsList == null || skuDetailsList.isEmpty()){
                            showError(R.string.no_products_were_found);
                            return;
                        }
                        for(SkuDetails details: skuDetailsList){
                            mSkus.add(new Sku(details));
                        }
                        mAdapter.notifyDataSetChanged();
                        updateSkusStatus();
                        break;
                    default:
                        showError(R.string.something_went_wrong);
                        break;
                }
            }
        });
    }

    private void updateSkusStatus(){
        mClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP,
                new PurchaseHistoryResponseListener() {
            @Override
            public void onPurchaseHistoryResponse(int responseCode, List<Purchase> purchasesList) {
                switch (responseCode) {
                    case BillingClient.BillingResponse.OK:
                        if(purchasesList == null || purchasesList.isEmpty()){
                            showError(R.string.no_purchases_were_found);
                            return;
                        }
                        for(Purchase purchase: purchasesList){
                            mAdapter.updateStatus(purchase);
                        }
                        break;
                    default:
                        showError(R.string.something_went_wrong);
                        break;
                }
            }
        });
    }

    private void showError(int stringResource){
        Snackbar.make(findViewById(R.id.main_activity), stringResource, Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {}
                }).show();
    }

    private void purchase(Sku sku){
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSku(sku.getId())
                .setType(BillingClient.SkuType.INAPP)
                .build();
        int responseCode = mClient.launchBillingFlow(this, flowParams);
        switch (responseCode) {
            case BillingClient.BillingResponse.OK:
                mAdapter.updateStatus(sku);
                Toast.makeText(this, "PAYMENT SUCCEEDED", Toast.LENGTH_SHORT).show();
                break;
            default:
                showError(R.string.could_not_make_purchase);
                break;
        }
    }

    /**
     * PurchasesUpdatedListener Methods
     */
    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        switch (responseCode) {
            case BillingClient.BillingResponse.OK:
                if(purchases == null || purchases.isEmpty()){
                    showError(R.string.no_purchases_were_found);
                    return;
                }
                for(Purchase purchase: purchases){
                    mAdapter.updateStatus(purchase);
                }
                break;
            default:
                showError(R.string.something_went_wrong);
                break;
        }
    }
}