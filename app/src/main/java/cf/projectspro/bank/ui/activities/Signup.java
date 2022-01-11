package cf.projectspro.bank.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cf.projectspro.bank.R;
import cf.projectspro.bank.databinding.ActivitySignupBinding;
import cf.projectspro.bank.ui.viewModels.SignUpViewModel;

public class Signup extends AppCompatActivity {

    private ProgressDialog signUpProgressDialog;
    private ActivitySignupBinding binding;
    private SignUpViewModel viewModel;
    public static final String TAG = Signup.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());


        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        viewModel.getStatus().observe(this,success->{
            if (success){

                Log.e(TAG, "getStatus: SignUp: Success");
                Intent intent = new Intent(Signup.this, MainActivity.class);
                startActivity(intent);
                finish();
                dismissSignUpProgressDialog();

            }else{
                Log.e(TAG, "getStatus: SignUp: Failed");
            }
        });

        initializeProgressDialog();
        hideSignUpProgressDialog();

        binding.toLoginScreen.setOnClickListener(view -> {
            Intent intent = new Intent(Signup.this, Login.class);
            startActivity(intent);
            finish();
        });

        binding.signupNow.setOnClickListener(v->{
            signUpUser();
        });

    }

    private void initializeProgressDialog() {
        signUpProgressDialog = new ProgressDialog(this);
        signUpProgressDialog.setMessage("Please Wait ...");
    }

    public void showSignUpProgressDialog(){
        signUpProgressDialog.show();
    }

    public void hideSignUpProgressDialog(){
        signUpProgressDialog.hide();
    }

    public void dismissSignUpProgressDialog(){
        signUpProgressDialog.dismiss();
    }


    /**
     * function to initiate sign up process
     */
    private void signUpUser() {

        showSignUpProgressDialog();

        final String Name = binding.nameSignup.getText().toString().trim();
        final String Email = binding.emailSignup.getText().toString().trim();
        final String Pass = binding.passSignup.getText().toString().trim();

        if (!TextUtils.isEmpty(binding.nameSignup.getText().toString().trim()) && !TextUtils.isEmpty(binding.emailSignup.getText().toString()) && !TextUtils.isEmpty(binding.passSignup.getText().toString().trim())) {

            if (!validateUserFullName(Name)){
                dismissSignUpProgressDialog();
                Toast.makeText(this, "Name is not valid!", Toast.LENGTH_SHORT).show();
            }else if (!validateUserEmail(Email)){
                dismissSignUpProgressDialog();
                Toast.makeText(this, "Email address not valid!", Toast.LENGTH_SHORT).show();
            }else if (Pass.length() < 6){
                dismissSignUpProgressDialog();
                Toast.makeText(this, "Password Length less than 6", Toast.LENGTH_SHORT).show();
            }else{
                Log.e(TAG, "SignUpUser: valid details:");
                viewModel.signUpUserWithEmailAndPassword(Name,Email,Pass);
            }
        } else {
            Toast.makeText(this, "Fill All Details", Toast.LENGTH_SHORT).show();
            dismissSignUpProgressDialog();
        }

    }

    /**
     * function to validate user full name
     * @param name name of the user
     * @return true if valid otherwise false
     */
    public static boolean validateUserFullName(String name) {

        /*validates normal name and
        Peter Müller
        François Hollande
        Patrick O'Brian
        Silvana Koch-Mehrin types of names
         */
        String regex = "^[\\p{L} .'-]+$";
        Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(name);
        return matcher.find();
    }


    /**
     * function to validate user email address
     * @param email name of the user
     * @return true if valid otherwise false
     */
    public static boolean validateUserEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
}
