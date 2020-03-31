package cf.projectspro.bank;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class totransfer extends AppCompatActivity {
    private Button sendmoney;
    private EditText amount;
    String to_user_uid;
    private FirebaseAuth mAuth;
    private DatabaseReference from;
    private DatabaseReference to;
    private String uid;
    private String name,fromuser,touser;
    long to_user_amount,totalfrom,totalto,from_user_amount,to_user_prev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_totransfer);
        Bundle bundle = getIntent().getExtras();
        to_user_amount = bundle.getLong("to_user_amount");
        to_user_uid = bundle.getString("to_user_uid");
        name =bundle.getString("name");
       // Toast.makeText(this, to_user_uid, Toast.LENGTH_SHORT).show();

        amount= findViewById(R.id.money_to_transfer);
        sendmoney = findViewById(R.id.transfer_money_btn);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()==null){
            Intent intent = new Intent(totransfer.this,Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else {
            uid = mAuth.getCurrentUser().getUid();
        }

        from = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

          to = FirebaseDatabase.getInstance().getReference().child("Users").child(to_user_uid);


      from.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           to_user_prev = (long) dataSnapshot.child("amount").getValue();
           fromuser= (String) dataSnapshot.child("name").getValue();
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });

      to.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              from_user_amount = (long) dataSnapshot.child("amount").getValue();
              touser = (String) dataSnapshot.child("name").getValue();
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });

      sendmoney.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              if (TextUtils.isEmpty(amount.getText().toString())) {
                  Toast.makeText(totransfer.this, "Enter Appropriate Amount", Toast.LENGTH_SHORT).show();
              } else {
                  long amt = Long.parseLong(amount.getText().toString().trim());
                  if (amt > 0) {
                      if (to_user_prev < amt) {
                          Toast.makeText(totransfer.this, "Insufficient Funds", Toast.LENGTH_SHORT).show();
                          String random = UUID.randomUUID().toString();
                          DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("transactions").child(uid).child(random);
                          ref.child("amount").setValue(amt);
                          long time = timestamp();
                          ref.child("code").setValue(-time);
                          ref.child("from").setValue(false);
                          ref.child("src").setValue(failed());
                          ref.child("status").setValue(false);
                          ref.child("to").setValue(name);
                          ref.child("trans_id").setValue(time);

                          DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("trans_ids").child(String.valueOf(time));
                          reference.child("amount").setValue(amt);
                          reference.child("status").setValue(false);
                          reference.child("done_by").setValue(fromuser);
                          reference.child("to").setValue(name);
                          Intent intent = new Intent(totransfer.this, MainActivity.class);
                          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                          startActivity(intent);
                          finish();
                      } else {
                          totalfrom = to_user_prev - amt;
                          totalto = to_user_amount + amt;
                          from.child("amount").setValue(totalfrom);
                          to.child("amount").setValue(totalto);
                          String random = UUID.randomUUID().toString();
                          DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("transactions").child(uid).child(random);
                          ref.child("amount").setValue(amt);
                          long time = timestamp();
                          ref.child("code").setValue(-time);
                          ref.child("from").setValue(false);
                          ref.child("src").setValue(success());
                          ref.child("status").setValue(true);
                          ref.child("to").setValue(name);
                          ref.child("trans_id").setValue(time);

                          String random1 = UUID.randomUUID().toString();
                          DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("transactions").child(to_user_uid).child(random1);
                          ref1.child("amount").setValue(amt);
                          ref1.child("code").setValue(-time);
                          ref1.child("from").setValue(true);
                          ref1.child("src").setValue(success());
                          ref1.child("status").setValue(true);
                          ref1.child("to").setValue(fromuser);
                          ref1.child("trans_id").setValue(time);

                          DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("trans_ids").child(String.valueOf(time));
                          reference.child("amount").setValue(amt);
                          reference.child("status").setValue(true);
                          reference.child("done_by").setValue(fromuser);
                          reference.child("to").setValue(name);
                          reference.child("touid").setValue(to_user_uid);
                          reference.child("fromuid").setValue(uid);
//                          Toast.makeText(totransfer.this, "Money Transfered!", Toast.LENGTH_SHORT).show();
                          Intent intent = new Intent(totransfer.this, payment_processing_screen.class);
                          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                          startActivity(intent);
                          finish();
                      }
                  } else {
                      Toast.makeText(totransfer.this, "Invalid Amount", Toast.LENGTH_SHORT).show();
                  }
              }

          }
      });

    }

    private String failed() {
        return "https://firebasestorage.googleapis.com/v0/b/bank-f7765.appspot.com/o/failed.png?alt=media&token=4b6ef20f-5958-4edf-ad6d-279bbee57879";
    }

    public Long timestamp(){
        Long tsLong = System.currentTimeMillis();
        return tsLong;
    }

    private String success(){
        return "https://firebasestorage.googleapis.com/v0/b/bank-f7765.appspot.com/o/done.png?alt=media&token=e7920069-13a3-499f-8818-75cb25bc77fb";
    }
}
