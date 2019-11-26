package cf.projectspro.bank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminSettings extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference greeting;
    private String uid;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_settings);

        mAuth = FirebaseAuth.getInstance();
        name  = findViewById(R.id.greetings);


        if (mAuth.getCurrentUser() == null){
            Intent i = new Intent(AdminSettings.this,Login.class);
            startActivity(i);
            finish();
        }else{

            uid = mAuth.getCurrentUser().getUid();
        }

        greeting = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        greeting.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String names  = (String) dataSnapshot.child("name").getValue();
                String[] _names = names.split(" ");
                name.setText("Hello "+_names[0]+"!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
