package cf.projectspro.bank;

public class users {
    private String name, uid;
    private long amount;

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
