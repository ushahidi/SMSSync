package org.addhen.smssync.models;

/**
 * Model for donation amount
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class Donation extends Model {

    private String title;

    private String amount;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Donation{" +
                "title='" + title + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}
