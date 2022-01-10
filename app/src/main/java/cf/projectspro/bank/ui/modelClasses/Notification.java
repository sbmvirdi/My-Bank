package cf.projectspro.bank.ui.modelClasses;

import androidx.annotation.Keep;

import java.lang.ref.SoftReference;

@Keep
public class Notification {
    public String src, to;
    public long trans_id;
    public long amount;
    public boolean status, from;

    public Notification(long amount, String src, String to, long trans_id, boolean status, boolean from) {
        this.amount = amount;
        this.src = src;
        this.to = to;
        this.trans_id = trans_id;
        this.status = status;
        this.from = from;

    }

    public Notification() {

    }

    public long getAmount() {
        return amount;
    }

    public String getSrc() {
        return src;
    }

    public String getTo() {
        return to;
    }

    public boolean isStatus() {
        return status;
    }

    public long gettrans_id() {
        return trans_id;
    }

    public boolean isFrom() {
        return from;
    }
}
