package cf.projectspro.bank.ui.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import cf.projectspro.bank.R;
import cf.projectspro.bank.ui.activities.Login;
import cf.projectspro.bank.ui.fragments.Dashboard;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {
    private Button signout;
    private View layout;
    private FirebaseAuth mAuth;
    private ImageView verified, dp;
    private TextView nameprofile;
    private String uid, name;
    private boolean session;

    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_profile, container, false);
        signout = layout.findViewById(R.id.signout);
        nameprofile = layout.findViewById(R.id.name_profile);
        final Activity activity = getActivity();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        verified = layout.findViewById(R.id.verified);
        dp = layout.findViewById(R.id.image_view);

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.signOut();
                Intent intent = new Intent(getActivity(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                activity.finish();
            }
        });
        @SuppressLint("ResourceType") Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(getContext(), R.layout.animation);
        // Inflate the layout for this fragment

        DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        df.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = (String) dataSnapshot.child("name").getValue();
                nameprofile.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        verified.startAnimation(hyperspaceJumpAnimation);

        char first = Dashboard.first_letter;
        //Toast.makeText(activity, first+"", Toast.LENGTH_SHORT).show();
        if (Character.isAlphabetic(first)) {
            //  Toast.makeText(activity, first+"", Toast.LENGTH_SHORT).show();
            int res = getResources().getIdentifier("@drawable/" + first, "drawable", getContext().getPackageName());
            dp.setImageResource(res);
        } else {
            Picasso.get().load(R.drawable.user).into(dp);
        }


        if (dp.getDrawable() == null) {
            Picasso.get().load(R.drawable.user).into(dp);
        }
        return layout;
    }

}
