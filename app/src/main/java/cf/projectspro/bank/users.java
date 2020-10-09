package cf.projectspro.bank;

import androidx.annotation.Keep;

@Keep
public class users {
    public String name, uid;
    public long amount;

    public users(String name, String uid, long amount) {
        this.name = name;
        this.uid = uid;
        this.amount = amount;
    }

    public users() {
    }

    public String getname() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public long getamount() {
        return amount;
    }
}
