package cf.projectspro.bank.ui.activities;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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

import cf.projectspro.bank.R;
import cf.projectspro.bank.databinding.ActivityTotransferBinding;
import cf.projectspro.bank.viewModels.MoneyTransferViewModel;

public class totransfer extends AppCompatActivity {
    private Button sendmoney;
    private EditText amount;
    String to_user_uid;
    private FirebaseAuth mAuth;
    private DatabaseReference from;
    private DatabaseReference to;
    private String uid;
    private String name, fromuser, touser;
    long to_user_amount, totalfrom, totalto, from_user_amount, to_user_prev;
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

//
//        MoneyTransferViewModel viewModel = new ViewModelProvider(this).get(MoneyTransferViewModel.class);
//        viewModel.getStatus().observe(this, status -> {
//            Log.e("ToTransfer", "getStatus(): "+status);
//        });


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
