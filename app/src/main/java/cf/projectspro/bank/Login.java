package cf.projectspro.bank;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email,password;
    private Button signin,signup_click;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signin = findViewById(R.id.signup_now);
        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);
        mAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait ...");
        pd.hide();
        signup_click = findViewById(R.id.sign_up_screen);
        if(mAuth.getCurrentUser() != null){

            Intent intent = new Intent(Login.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        signup_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,Signup.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                String Email = email.getText().toString().trim();
                String pass = password.getText().toString().trim();
                signin.setText("Logging In ...");
                signin.setClickable(false);

                if(!TextUtils.isEmpty(Email) && !TextUtils.isEmpty(pass)){
                    mAuth.signInWithEmailAndPassword(Email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                         if (task.isSuccessful()){
                             Intent intent = new Intent(Login.this,MainActivity.class);
                             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                             startActivity(intent);
                             finish();
                             pd.dismiss();
                         }
                         else {
                             Toast.makeText(Login.this, "Incorrect Details", Toast.LENGTH_SHORT).show();
                             signin.setText("Log In");
                             signin.setClickable(true);
                             pd.dismiss();
                         }
                        }
                    });
                }else {
                    signin.setText("Login");
                    signin.setClickable(true);
                    Toast.makeText(Login.this, "Fill All Details", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }
        });
    }
}
