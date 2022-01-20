package cf.projectspro.bank.ui.modelClasses;

import androidx.annotation.Keep;

@Keep
public class User {
    public String name, uid;
    public long amount;

    public User(String name, String uid, long amount) {
        this.name = name;
        this.uid = uid;
        this.amount = amount;
    }

    public User() {
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

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", uid='" + uid + '\'' +
                ", amount=" + amount +
                '}';
    }
}
