package cf.projectspro.bank.ui.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import cf.projectspro.bank.R;
import cf.projectspro.bank.databinding.FragmentProfileBinding;
import cf.projectspro.bank.ui.activities.Login;
import cf.projectspro.bank.ui.fragments.Dashboard;
import cf.projectspro.bank.ui.viewModels.ProfileFragmentViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {

    private FirebaseAuth mAuth;
    private String uid;
    private FragmentProfileBinding binding;
    private ProfileFragmentViewModel viewModel;
    public static final String TAG = Profile.class.getSimpleName();

    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(LayoutInflater.from(requireContext()),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileFragmentViewModel.class);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser()!=null){
            uid = mAuth.getCurrentUser().getUid();
        }else {
            navigateToLoginPage();
        }

        binding.signout.setOnClickListener(view1 -> {
            mAuth.signOut();
            navigateToLoginPage();
        });

        @SuppressLint("ResourceType")
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.animation);

        viewModel.loadNameOfUser(uid);
        viewModel.getUserName().observe(getViewLifecycleOwner(),name->{
            Log.e(TAG, "getNameOfUser:Name:"+name);
            binding.nameProfile.setText(name);
        });


        viewModel.loadUserVerified();
        viewModel.getIsUserVerified().observe(getViewLifecycleOwner(),verified->{
            Log.e(TAG, "onViewCreated: getIsUserVerified:verified:"+verified);
            if (verified){
                binding.disclaimer.setVisibility(View.GONE);
                binding.verifyEmail.setVisibility(View.GONE);
                binding.verified.setImageResource(R.drawable.verified);
            }else{
                binding.disclaimer.setVisibility(View.VISIBLE);
                binding.verifyEmail.setVisibility(View.VISIBLE);
                binding.verified.setImageResource(R.drawable.failed);
            }
        });

        binding.verifyEmail.setOnClickListener(view1 -> {
            viewModel.verifyEmail(success->{
                if (success){
                    Toast.makeText(requireContext(), "Email sent successfully!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(requireContext(), "failed to sent email for verification!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.verified.startAnimation(hyperspaceJumpAnimation);

        char first = Dashboard.firstLetter;
        if (Character.isAlphabetic(first)) {
            int res = getResources().getIdentifier("@drawable/" + first, "drawable", getContext().getPackageName());
            binding.dp.setImageResource(res);
        } else {
            Picasso.get().load(R.drawable.user).into(binding.dp);
        }

        if (binding.dp.getDrawable() == null) {
            Picasso.get().load(R.drawable.user).into(binding.dp);
        }

    }

    private void navigateToLoginPage() {
        Intent intent = new Intent(getActivity(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
