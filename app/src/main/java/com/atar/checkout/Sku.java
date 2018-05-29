package com.atar.checkout;

import com.android.billingclient.api.SkuDetails;

public class Sku {

    private String title, price, description, id;
    private boolean isPurchased;

    public Sku(SkuDetails details){
        title = details.getTitle();
        price = details.getPrice();
        description = details.getDescription();
        id = details.getSku();
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }
}