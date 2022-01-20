package cf.projectspro.bank.ui.modelClasses;

import androidx.annotation.Keep;

@Keep
public class Promotion {

    public String ad_url;
    public String ad_text;

    public Promotion() {
    }

    public Promotion(String ad_url, String ad_text) {
        this.ad_url = ad_url;
        this.ad_text = ad_text;
    }

    public String getAd_url() {
        return ad_url;
    }

    public String getAd_text() {
        return ad_text;
    }
}
