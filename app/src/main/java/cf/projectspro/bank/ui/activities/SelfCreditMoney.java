package cf.projectspro.bank.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import cf.projectspro.bank.R;
import cf.projectspro.bank.databinding.ActivityMoneyBinding;
import cf.projectspro.bank.repository.MyBankRepo;
import cf.projectspro.bank.ui.viewModels.SelfCreditMoneyViewModel;

public class SelfCreditMoney extends AppCompatActivity {

    private long amount_to_add;
    private FirebaseAuth mAuth;
    private String uid;
    private SelfCreditMoneyViewModel viewModel;
    private ActivityMoneyBinding binding;
    public static final String TAG = SelfCreditMoney.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMoneyBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SelfCreditMoneyViewModel.class);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(SelfCreditMoney.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            uid = mAuth.getCurrentUser().getUid();
        }

        // to track the status of the self credit transaction
        viewModel.getStatus().observe(this, status -> {

            Log.e(TAG, "getStatus: Self Credit Process Status:" +status);

            if (status.equals(MyBankRepo.STATUS_SELF_CREDIT_INITIATED)) {

                binding.processPayment.setText("Processing");
                binding.processPayment.setEnabled(false);
                binding.processPayment.setClickable(false);

            } else if (status.equals(MyBankRepo.STATUS_SELF_CREDIT_SUCCESS) || status.equals(MyBankRepo.STATUS_SELF_CREDIT_FAILED)) {
                Toast.makeText(SelfCreditMoney.this, "Amount Credited", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SelfCreditMoney.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // when user clicks on add money
        binding.processPayment.setOnClickListener(view -> {

            if (TextUtils.isEmpty(binding.moneyValue.getText().toString())) {
                Toast.makeText(SelfCreditMoney.this, "Enter Amount", Toast.LENGTH_SHORT).show();
            } else if (!isNetworkAvailable()){
                Toast.makeText(SelfCreditMoney.this, "No Internet Available", Toast.LENGTH_SHORT).show();
            } else{
                amount_to_add = Long.parseLong(binding.moneyValue.getText().toString());
                if (amount_to_add <= 0 || amount_to_add > 1000) {
                    Toast.makeText(SelfCreditMoney.this, "Enter Appropriate Amount", Toast.LENGTH_SHORT).show();
                } else {
                    viewModel.initiateSelfCredit(uid,amount_to_add);
                }
            }
        });
    }

    /**
     * function to check if device has internet connectivity
     * @return boolean of internet connectivity
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
