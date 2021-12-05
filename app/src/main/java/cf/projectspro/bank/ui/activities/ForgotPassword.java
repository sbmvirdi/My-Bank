package cf.projectspro.bank.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import cf.projectspro.bank.databinding.ActivityForgotPasswordBinding;
import cf.projectspro.bank.viewModels.ForgotPasswordViewModel;

public class ForgotPassword extends AppCompatActivity {
    ActivityForgotPasswordBinding activityForgotPasswordBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityForgotPasswordBinding = ActivityForgotPasswordBinding.inflate(LayoutInflater.from(this));
        setContentView(activityForgotPasswordBinding.getRoot());

        ForgotPasswordViewModel viewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);

        viewModel.getStatus().observe(this, status -> {

            // if the email is sent successfully
            if (status){
                activityForgotPasswordBinding.resetStatus.setTextColor(Color.parseColor("#53a653"));
                activityForgotPasswordBinding.resetStatus.setText("Reset email sent successfully!");
                activityForgotPasswordBinding.resetStatus.setVisibility(View.VISIBLE);
            }else{
                // if email fails to send
                activityForgotPasswordBinding.resetStatus.setTextColor(Color.parseColor("#c62828"));
                activityForgotPasswordBinding.resetStatus.setText("Failed to sent reset email!");
                activityForgotPasswordBinding.resetStatus.setVisibility(View.VISIBLE);
            }
        });

        // when send reset email is clicked
        activityForgotPasswordBinding.sendResetEmailButton.setOnClickListener(v->{
            // reset the error in email text field
            activityForgotPasswordBinding.email.setError(null);

            // if email entered is empty
            if (TextUtils.isEmpty(activityForgotPasswordBinding.email.getText().toString().trim())){
                activityForgotPasswordBinding.email.setError("Enter a valid email");
            }else{
                // initiate reset email process
                viewModel.sendResetPasswordEmail(activityForgotPasswordBinding.email.getText().toString());
            }
        });


    }
}