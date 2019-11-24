package cf.projectspro.bank;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class SendMoney extends AppCompatActivity {
    private RecyclerView userview;
    private FirebaseAuth mAuth;
    private Query ref;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);
        userview = findViewById(R.id.users_rec);
        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("name");
        if(mAuth.getCurrentUser() == null){
            Intent intent = new Intent(SendMoney.this,Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        uid = mAuth.getCurrentUser().getUid();
        userview.setHasFixedSize(true);
        ref.keepSynced(true);
        FirebaseRecyclerOptions firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<users>().setQuery(ref,users.class).build();
        FirebaseRecyclerAdapter<users,userHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<users, userHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull userHolder holder, int position, @NonNull users model) {
                String to_uid = model.getUid();
                 holder.setname(model.getname());
                 holder.getamount(model.getamount());
                 holder.getuid(to_uid);

            }

            @NonNull
            @Override
            public userHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(SendMoney.this).inflate(R.layout.user_detail,parent,false);
                return new userHolder(v);
            }
        };
       userview.setLayoutManager(new LinearLayoutManager(this));
      userview.setAdapter(firebaseRecyclerAdapter);
      try {
          firebaseRecyclerAdapter.startListening();
      }catch (Exception ex){
          Log.e("EX USER ADAPTER",ex.toString());
      }


    }
    public class userHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView user;
        String to_user_uid;
        long to_user_amount;
        String Name;
        public userHolder(final View itemView) {
            super(itemView);
            mView = itemView;

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (uid.equals(to_user_uid)){
                        Toast.makeText(SendMoney.this, "You Can't Send Money To Yourself", Toast.LENGTH_SHORT).show();
                    }else{
                    Intent intent = new Intent(SendMoney.this,totransfer.class);
                    intent.putExtra("to_user_uid",to_user_uid);
                    intent.putExtra("to_user_amount",to_user_amount);
                    intent.putExtra("name",Name);
                    startActivity(intent);}
                }
            });
        }


        void setname(String name){
            user = mView.findViewById(R.id.user_name);
            user.setText(name);
            Name = name;

        }

        void getuid(String uid){
           to_user_uid = uid;
        }

        void getamount(long amount){
            to_user_amount = amount;

        }
    }
}
