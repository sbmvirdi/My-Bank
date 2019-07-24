package cf.projectspro.bank;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class Notifications extends Fragment {

 View layout;
 RecyclerView rec;
 private Query ref;
 private FirebaseAuth mAuth;
 private String uid;
 private SkeletonScreen sk;
 private ShimmerFrameLayout shimmerFrameLayout;


    public Notifications() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null ){
            Intent intent = new Intent(getActivity(),Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }else {
           uid=  mAuth.getCurrentUser().getUid();
        }
        // Inflate the layout for this fragment
        layout =  inflater.inflate(R.layout.fragment_notifications, container, false);
        rec = layout.findViewById(R.id.notification_rec);
        //shimmerFrameLayout = layout.findViewById(R.id.shimmer_view_container);
        rec.setHasFixedSize(true);

//         FirebaseDatabase.getInstance().setPersistenceEnabled(true);
         FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("transactions").child(uid).orderByChild("code");
        ref.keepSynced(true);
        DividerItemDecoration dv = new DividerItemDecoration(layout.getContext(),DividerItemDecoration.HORIZONTAL);
        rec.addItemDecoration(dv);
        rec.setLayoutManager(new LinearLayoutManager(getActivity()));
        FirebaseRecyclerOptions firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<notify>().setQuery(ref,notify.class).build();
        FirebaseRecyclerAdapter<notify,notifyHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<notify, notifyHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull notifyHolder holder, int position, @NonNull notify model) {
            holder.setamount(model.getAmount());
            holder.setImg(model.getSrc());
            holder.setname(model.getTo());
            holder.setTrans_id(model.gettrans_id());
            holder.getstatus(model.isStatus());
            holder.getfrom(model.isFrom());

            }

            @NonNull
            @Override
            public notifyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.notification_post,parent,false);
                return new notifyHolder(v);
            }
        };

        rec.setAdapter(firebaseRecyclerAdapter);

       // sk = Skeleton.bind(rec).adapter(firebaseRecyclerAdapter).frozen(false).shimmer(true).load(R.layout.notification_post).show();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //sk.hide();
               // shimmerFrameLayout.stopShimmer();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        try {
            firebaseRecyclerAdapter.startListening();

        }catch (Exception error){
            Toast.makeText(getContext(), ""+error, Toast.LENGTH_SHORT).show();

        }


           // shimmerFrameLayout.startShimmer();




        return layout;
    }

    public class notifyHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView image;
        TextView to,amount,trans_id;
        String im;
        TextView From_text,currency;
        Boolean status,from;
        public notifyHolder(View itemView) {
            super(itemView);
            mView = itemView;
            currency = mView.findViewById(R.id.INR);
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(),Details_Trans.class);
                    intent.putExtra("to",to.getText().toString().trim());
                    intent.putExtra("trans_id",trans_id.getText().toString().trim());
                    intent.putExtra("amount",amount.getText().toString().trim());
                    intent.putExtra("image",im);
                    intent.putExtra("status",status);
                    intent.putExtra("from_status",from);
                    startActivity(intent);
                }
            });

        }

        void setname(String t){
            to = mView.findViewById(R.id.notify_name);
            to.setText(t);
        }

        void setamount(long t){
            amount = mView.findViewById(R.id.notify_amount);
            amount.setText(t+"");
        }

        void setImg(String imag){
            image = mView.findViewById(R.id.notify_image);
            Picasso.get().load(imag).into(image);
            im = imag;
        }
        void setTrans_id(long id){
            trans_id = mView.findViewById(R.id.notify_trans_id);
            trans_id.setText(id +"");
        }
        void getstatus(boolean st){
            status = st;
            if(status){
                amount.setTextColor(getResources().getColor(R.color.colorPrimary));
                currency.setTextColor(getResources().getColor(R.color.colorPrimary));
            }else{
                amount.setTextColor(getResources().getColor(R.color.colorAccent));
                currency.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        }

        void getfrom(boolean st){
            from = st;
            if(from){
                From_text = mView.findViewById(R.id.from_text);
                From_text.setText("From:");
            }
        }
    }

    public Long timestamp(){
        Long tsLong = System.currentTimeMillis()/1000;
        return tsLong;
    }

}
