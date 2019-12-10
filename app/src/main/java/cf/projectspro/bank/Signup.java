package cf.projectspro.bank;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {
    private EditText name,email,pass;
    private Button signup,login;
    private FirebaseAuth mAuth;
    private String uid;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        name = findViewById(R.id.name_signup);
        email = findViewById(R.id.email_signup);
        pass = findViewById(R.id.pass_signup);
        signup = findViewById(R.id.signup_now);
        login = findViewById(R.id.to_login_screen);
        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait ...");
        pd.hide();
        mAuth = FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Signup.this,Login.class);
                startActivity(intent);
                finish();
            }


        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup_the_user();
            }
        });

    }


    private void signup_the_user() {
         pd.show();
        final String Name = name.getText().toString().trim();
        final String Pass = pass.getText().toString().trim();
        final String Email = email.getText().toString().trim();

       // Toast.makeText(this, Email+" "+pass, Toast.LENGTH_SHORT).show();
        if (!TextUtils.isEmpty(Name) && !TextUtils.isEmpty(Email) && !TextUtils.isEmpty(Pass) && Pass.length() >6) {


            if (!Email.contains("@") && !Email.contains(".")) {
                Toast.makeText(this, "Enter valid email!", Toast.LENGTH_SHORT).show();
            }
            else{
                mAuth.createUserWithEmailAndPassword(Email, Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Signup.this, "Successful", Toast.LENGTH_SHORT).show();
                            mAuth.signInWithEmailAndPassword(Email, Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        uid = mAuth.getCurrentUser().getUid();

                                        // Toast.makeText(Signup.this, "" + uid, Toast.LENGTH_SHORT).show();

                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                                        DatabaseReference isadmin = FirebaseDatabase.getInstance().getReference().child("uids").child(uid);
                                        isadmin.setValue(false);
                                        ref.child("name").setValue(Name);
                                        ref.child("amount").setValue(0);
                                        ref.child("uid").setValue(uid);
                                        ref.child("session").setValue(true);
                                        isadmin.setValue(false);
                                        Intent intent = new Intent(Signup.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                        pd.dismiss();
                                    } else {

                                        Intent intent = new Intent(Signup.this, Login.class);
                                        startActivity(intent);
                                        finish();
                                        pd.dismiss();
                                    }
                                }
                            });


                        } else {
                            Toast.makeText(Signup.this, "Sign up Failed", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }

                    }
                });
        }
        }else {
            Toast.makeText(this, "Fill All Details With Password Length Greater Than 6", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }

    }
}
