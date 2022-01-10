package cf.projectspro.bank.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import cf.projectspro.bank.R;
import cf.projectspro.bank.databinding.ActivityTotransferBinding;

public class totransfer extends AppCompatActivity {
    private Button sendmoney;
    private EditText amount;
    String to_user_uid;
    private FirebaseAuth mAuth;
    private String uid;
    private String name;
    long to_user_amount;
    private ActivityTotransferBinding activityTotransferBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTotransferBinding = ActivityTotransferBinding.inflate(LayoutInflater.from(this));
        setContentView(activityTotransferBinding.getRoot());


        Bundle bundle = getIntent().getExtras();
        to_user_amount = bundle.getLong("to_user_amount");
        to_user_uid = bundle.getString("to_user_uid");
        name = bundle.getString("name");

        amount = findViewById(R.id.money_to_transfer);
        sendmoney = findViewById(R.id.transfer_money_btn);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(totransfer.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            uid = mAuth.getCurrentUser().getUid();
        }


        sendmoney.setOnClickListener(v->{

            // checking if user has entered the correct amount to proceed to transaction
            if (!TextUtils.isEmpty(amount.getText().toString().trim())){
                Intent sendMoneyIntent = new Intent(this, payment_processing_screen.class);
                sendMoneyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                sendMoneyIntent.putExtra("senderUid", uid);
                sendMoneyIntent.putExtra("receiverUid", to_user_uid);
                sendMoneyIntent.putExtra("name", name);
                sendMoneyIntent.putExtra("amount",amount.getText().toString().trim());
                startActivity(sendMoneyIntent);
                finish();
            }else {
                Toast.makeText(totransfer.this, "Enter Appropriate Amount", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
