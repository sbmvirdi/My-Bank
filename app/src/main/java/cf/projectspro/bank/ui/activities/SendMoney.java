package cf.projectspro.bank.ui.activities;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cf.projectspro.bank.R;
import cf.projectspro.bank.ui.adapters.UserAdapter;
import cf.projectspro.bank.ui.modelClasses.User;

public class SendMoney extends AppCompatActivity {
    private RecyclerView userview;
    private FirebaseAuth mAuth;
    private Query ref;
    private String uid;
    private EditText mSearchBar;
    private String mSearchString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);
        userview = findViewById(R.id.users_rec);
        mAuth = FirebaseAuth.getInstance();
        // ref = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("name");
        ref = FirebaseDatabase.getInstance().getReference().child("Users");

        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(SendMoney.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        uid = mAuth.getCurrentUser().getUid();
        mSearchBar = findViewById(R.id.searchBar);

        Refresh("");
        mSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Refresh(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        userview.setHasFixedSize(true);
        ref.keepSynced(true);
        userview.setLayoutManager(new LinearLayoutManager(this));


    }


    void Refresh(String mSearchString) {

        Query firebasequery = ref.orderByChild("name").startAt(mSearchString).endAt(mSearchString + "\uf8ff");

        firebasequery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> usersList = new ArrayList<>();
                for (DataSnapshot userSnapshot:snapshot.getChildren()){
                    User user = userSnapshot.getValue(User.class);
                    if (!user.uid.equals(uid)){
                        usersList.add(user);
                    }

                    userview.setAdapter(new UserAdapter(usersList,SendMoney.this));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
