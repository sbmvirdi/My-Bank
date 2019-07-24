package cf.projectspro.bank;

import java.lang.ref.SoftReference;

public class notify {
   private String src,to;
   private long trans_id;
   private long amount;
   private boolean status,from;

    public notify(long amount, String src, String to,long trans_id,boolean status,boolean from) {
        this.amount = amount;
        this.src = src;
        this.to = to;
        this.trans_id = trans_id;
        this.status = status;
        this.from = from;

    }
    public notify(){

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

    public long gettrans_id(){
        return trans_id;
  }

    public boolean isFrom() {
        return from;
    }
}
