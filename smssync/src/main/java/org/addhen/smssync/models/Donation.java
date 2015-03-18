package org.addhen.smssync.models;

import com.github.jberkel.pay.me.model.SkuDetails;

/**
 * Model for donation amount
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class Donation extends Model {

    private SkuDetails skuDetails;

    public SkuDetails getSkuDetails() {
        return skuDetails;
    }

    public Donation(SkuDetails skuDetails) {
        this.skuDetails = skuDetails;
    }

    @Override
    public String toString() {
        return "Donation{" +
                "skuDetails=" + skuDetails +
                '}';
    }
}
