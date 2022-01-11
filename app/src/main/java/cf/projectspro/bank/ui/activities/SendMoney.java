package cf.projectspro.bank.ui.activities;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import cf.projectspro.bank.databinding.ActivitySendMoneyBinding;
import cf.projectspro.bank.ui.adapters.UserAdapter;
import cf.projectspro.bank.ui.modelClasses.User;
import cf.projectspro.bank.ui.viewModels.SendMoneyViewModel;

public class SendMoney extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String uid;
    private EditText mSearchBar;
    private String mSearchString = "";
    private SendMoneyViewModel viewModel;
    private ActivitySendMoneyBinding binding;
    public static final String TAG = SendMoney.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendMoneyBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(SendMoney.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        uid = mAuth.getCurrentUser().getUid();
        mSearchBar = findViewById(R.id.searchBar);

        viewModel = new ViewModelProvider(this).get(SendMoneyViewModel.class);

        Refresh("");
        mSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Refresh(s.toString());
            }
        });


        binding.usersRec.setHasFixedSize(true);
        binding.usersRec.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getUserList().observe(this,users -> {
            if (users==null || users.isEmpty()){
                Log.e(TAG, "getUserList: observe: users list empty");
            }else{
                binding.usersRec.setAdapter(new UserAdapter(users,this));
            }
        });

    }


    void Refresh(String mSearchString) {
        viewModel.getUsers(uid,mSearchString);
    }

}
