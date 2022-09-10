package cf.projectspro.bank.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import cf.projectspro.bank.R;
import cf.projectspro.bank.databinding.FragmentDashboardBinding;
import cf.projectspro.bank.ui.activities.Login;
import cf.projectspro.bank.ui.activities.MainActivity;
import cf.projectspro.bank.ui.activities.SelfCreditMoney;
import cf.projectspro.bank.ui.activities.SendMoney;
import cf.projectspro.bank.ui.adapters.SliderIntroAdapter;
import cf.projectspro.bank.ui.modelClasses.Transaction;
import cf.projectspro.bank.ui.viewModels.DashboardFragmentViewModel;


/**
 * A simple {@link Fragment} subclass.
 * sbmvirdi
 */
public class Dashboard extends Fragment {

    private FirebaseAuth mAuth;
    private String uid;
    public static char firstLetter;
    private FragmentDashboardBinding binding;
    private DashboardFragmentViewModel viewModel;
    private static final String TAG = MainActivity.class.getSimpleName();

    public Dashboard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(LayoutInflater.from(requireContext()),container,false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DashboardFragmentViewModel.class);

        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();

        // checking authentication of the user
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(getActivity(), Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();

        } else {
            uid = mAuth.getCurrentUser().getUid();
        }

        viewModel.loadUserByUid(uid);
        viewModel.getUser().observe(getViewLifecycleOwner(),user -> {
            if (user!=null){
                binding.name.setText(user.name);
                binding.Amount.setText(String.valueOf(user.amount));
                firstLetter = user.name.toLowerCase().charAt(0);
            }
        });

        viewModel.loadPromotionalData();
        viewModel.getPromotionalData().observe(getViewLifecycleOwner(),promotion->{
            if (Objects.nonNull(promotion)) {
                binding.loadingText.setText(promotion.ad_text);
                Picasso.get().load(promotion.ad_url).into(binding.dashboardAd);
                binding.adsImage.setImageResource(R.drawable.ads);
                binding.loadingText.setBackgroundResource(R.color.colorPrimaryDark);
            }
        });


        viewModel.loadUserVerified();
        viewModel.getUserVerified().observe(getViewLifecycleOwner(),verified->{
            Log.e(TAG, "onViewCreated: isUserVerified:"+verified);
            if (verified){
                changeButtonBackground(binding.sendmoneynow,true);
                changeButtonBackground(binding.addMoney,true);
            }else{
                changeButtonBackground(binding.sendmoneynow,false);
                changeButtonBackground(binding.addMoney,false);
            }
        });


        // Click Listeners
        binding.addMoney.setOnClickListener(view1 -> {
            if (hasConnection()) {
                navigateToSelfCreditActivity();
            } else {
                Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        });


        binding.sendmoneynow.setOnClickListener(view12 -> {
            if (hasConnection()) {
                Intent intent = new Intent(getContext(), SendMoney.class);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.loadRecentTransactions(uid,10);
        viewModel.getRecentTransactions().observe(getViewLifecycleOwner(),recentTransactions->{
            if (!recentTransactions.isEmpty()){
                for (Transaction transaction:recentTransactions){
                    Log.e(TAG, "getRecentTransactions: Transaction:"+transaction.trans_id);
                }
            }
        });

        viewModel.loadSlides();
        viewModel.getSlideModels().observe(getViewLifecycleOwner(),slides ->{
            if (slides!=null && !slides.isEmpty()){
                SliderIntroAdapter sliderIntroAdapter = new SliderIntroAdapter(getContext(), slides);
                binding.imageSlider.setSliderAdapter(sliderIntroAdapter);
                binding.imageSlider.startAutoCycle();
                binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
                binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                binding.imageSlider.setScrollTimeInMillis(2000);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void changeButtonBackground(Button button, boolean state){

        GradientDrawable background = (GradientDrawable) button.getBackground();
        button.setEnabled(state);

        if (state){
            background.setColor(Color.parseColor("#6561f6"));
            background.setTint(Color.parseColor("#6561f6"));

        }else{
            background.setColor(Color.parseColor("#d3d3d3"));
            background.setTint(Color.parseColor("#d3d3d3"));


        }
    }

    private void navigateToSelfCreditActivity() {

        Intent intent = new Intent(getActivity(), SelfCreditMoney.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }


    public boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }


}
