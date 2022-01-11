package cf.projectspro.bank.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;

import java.util.ArrayList;

import cf.projectspro.bank.databinding.ActivityPaymentProcessingScreenBinding;
import cf.projectspro.bank.ui.viewModels.MoneyTransferViewModel;

public class PaymentProcessingScreen extends AppCompatActivity {

    private String senderUid,receiverUid,name;
    private int amount;
    private ActivityPaymentProcessingScreenBinding paymentProcessingScreenBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paymentProcessingScreenBinding = ActivityPaymentProcessingScreenBinding.inflate(LayoutInflater.from(this));
        setContentView(paymentProcessingScreenBinding.getRoot());

        // setting the listener for status of the transaction
        MoneyTransferViewModel viewModel = new ViewModelProvider(this).get(MoneyTransferViewModel.class);

        // validation status of the transaction to perform certain action
        viewModel.getStatus().observe(this, status -> {

                    Log.e("ToTransfer", "getStatus(): "+status);
                    if (status >=3){
                        paymentProcessingScreenBinding.transactionSteps.done(true);

                        new Handler(getMainLooper()).postDelayed(()->{
                            Intent mainScreenIntent = new Intent(this,MainActivity.class);
                            startActivity(mainScreenIntent);
                            finish();
                        },2000);


                    }
                    else {
                        paymentProcessingScreenBinding.transactionSteps.done(true);
                        paymentProcessingScreenBinding.transactionSteps.go(status, true);
                    }

        });

        paymentProcessingScreenBinding.transactionSteps.setSteps(new ArrayList<String>() {{
            add("Initiating");
            add("Deducting from Sender");
            add("Sending to recipient");
            add("Successful");
        }});

        if (getIntent().getExtras()!=null) {

            // extracting the details of the user
            senderUid = getIntent().getExtras().getString("senderUid");
            receiverUid = getIntent().getExtras().getString("receiverUid");
            name = getIntent().getExtras().getString("name");
            amount = Integer.parseInt(getIntent().getExtras().getString("amount"));

            // Initiating sending money
            viewModel.sendMoney(senderUid,receiverUid,amount);

        }


    }

    @Override
    public void onBackPressed() {
        // can't go back while transaction is in progress
    }
}
