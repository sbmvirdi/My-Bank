package cf.projectspro.bank.ui.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cf.projectspro.bank.R;
import cf.projectspro.bank.ui.adapters.NotificationAdapter;
import cf.projectspro.bank.ui.modelClasses.Notification;
import cf.projectspro.bank.ui.activities.Login;

/**
 * A simple {@link Fragment} subclass.
 */
public class Notifications extends Fragment {

    View layout;
    RecyclerView rec;
    private Query ref;
    private FirebaseAuth mAuth;
    private String uid;
    private ShimmerFrameLayout shimmerFrameLayout;
    public static final String TAG = Notifications.class.getSimpleName();


    public Notifications() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(getActivity(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        } else {
            uid = mAuth.getCurrentUser().getUid();
        }
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_notifications, container, false);
        rec = layout.findViewById(R.id.notification_rec);
        rec.setHasFixedSize(true);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("transactions").child(uid).orderByChild("code");
        ref.keepSynced(true);
        DividerItemDecoration dv = new DividerItemDecoration(layout.getContext(), DividerItemDecoration.HORIZONTAL);
        rec.addItemDecoration(dv);
        rec.setLayoutManager(new LinearLayoutManager(getContext()));


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Notification> notifies = new ArrayList<>();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    notifies.add(dataSnapshot.getValue(Notification.class));
                }

                rec.setAdapter(new NotificationAdapter(notifies, getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled:"+error.toString());
            }
        });

        return layout;
    }

}
