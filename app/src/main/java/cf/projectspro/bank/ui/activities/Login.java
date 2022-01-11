package cf.projectspro.bank.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import cf.projectspro.bank.R;
import cf.projectspro.bank.databinding.ActivityLoginBinding;
import cf.projectspro.bank.ui.viewModels.LoginViewModel;


public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressDialog loginProgressDialog;
    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;
    public static final String TAG = Login.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        mAuth = FirebaseAuth.getInstance();

        initializeProgressDialog();
        hideLoginProgressDialog();

        if (mAuth.getCurrentUser() != null) {

            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        binding.signup.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Signup.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        binding.forgotPassword.setOnClickListener(v->{
            Intent forgotIntent = new Intent(this,ForgotPassword.class);
            startActivity(forgotIntent);
        });

        viewModel.getLoginStatus().observe(this,success->{
            Log.e(TAG, "onCreate: success:"+success);
            if (success){
                Intent intent = new Intent(Login.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                dismissLoginProgressDialog();
                startActivity(intent);
                finish();

            }else{
                Toast.makeText(Login.this, "Incorrect Details", Toast.LENGTH_SHORT).show();
                binding.login.setText("Log In");
                binding.login.setClickable(true);
                dismissLoginProgressDialog();
            }
        });

        binding.login.setOnClickListener(view -> {
            showLoginProgressDialog();

            binding.login.setText("Logging In ...");
            binding.login.setClickable(false);

            if (!TextUtils.isEmpty(binding.email.getText().toString().trim()) && !TextUtils.isEmpty(binding.pass.getText().toString().trim())) {
                viewModel.loginUserWithEmailAndPassword(binding.email.getText().toString().trim(),binding.pass.getText().toString().trim());
            } else {
                binding.login.setText("Login");
                binding.login.setClickable(true);
                Toast.makeText(Login.this, "Fill All Details", Toast.LENGTH_SHORT).show();
                dismissLoginProgressDialog();
            }
        });

    }

    private void initializeProgressDialog() {
        loginProgressDialog = new ProgressDialog(this);
        loginProgressDialog.setMessage("Please Wait ...");
    }

    public void showLoginProgressDialog(){
        loginProgressDialog.show();
    }

    public void hideLoginProgressDialog(){
        loginProgressDialog.hide();
    }

    public void dismissLoginProgressDialog(){
        loginProgressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
