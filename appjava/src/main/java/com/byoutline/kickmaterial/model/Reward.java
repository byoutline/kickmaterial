package com.byoutline.kickmaterial.model;

import org.joda.time.DateTime;
import org.parceler.Parcel;

@Parcel(Parcel.Serialization.FIELD)
public class Reward implements RewardItem {

    public double minimum;
    public String reward;
    // optional fields
    public String description;
    public boolean shippingEnabled;
    public DateTime estimatedDeliveryOn;
    public DateTime updatedAt;
    public int backersCount;

    @Override
    public int getItemType() {
        return ITEM;
    }
}