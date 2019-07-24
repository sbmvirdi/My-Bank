package cf.projectspro.bank;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import cf.projectspro.bank.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {
    private Button signout;
    private View layout;
    private FirebaseAuth mAuth;
    private ImageView verified;
    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout =  inflater.inflate(R.layout.fragment_profile, container, false);
        signout = layout.findViewById(R.id.signout);
        final Activity activity = getActivity();
        mAuth = FirebaseAuth.getInstance();
        verified = layout.findViewById(R.id.verified);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(),Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                activity.finish();
            }
        });
        @SuppressLint("ResourceType") Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(getContext(), R.layout.animation);
        // Inflate the layout for this fragment
        verified.startAnimation(hyperspaceJumpAnimation);
         return layout;
    }

}
