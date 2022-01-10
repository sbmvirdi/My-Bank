package cf.projectspro.bank.ui.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cf.projectspro.bank.R;
import cf.projectspro.bank.ui.activities.SendMoney;
import cf.projectspro.bank.ui.modelClasses.SlideModel;
import cf.projectspro.bank.ui.adapters.SliderIntroAdapter;
import cf.projectspro.bank.ui.activities.Login;
import cf.projectspro.bank.ui.activities.MainActivity;
import cf.projectspro.bank.ui.activities.Money;


/**
 * A simple {@link Fragment} subclass.
 * sbmvirdi
 */
public class Dashboard extends Fragment {
    private static final String TAG = MainActivity.class.getSimpleName();
    View layout;
    private TextView name, amount, LoadingText;
    private ImageView Ad, ads_image;
    private DatabaseReference ref, advertisements;
    private Button addmoney, send;
    private FirebaseAuth mAuth;
    private SliderView sliderLayout;
    private String uid;
    private AdView mAdView;
    private List<SlideModel> SlideList;
    public static char first_letter;

    public Dashboard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        advertisements = FirebaseDatabase.getInstance().getReference().child("Advert");
        advertisements.keepSynced(true);


        // checking authentication of the user
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(getActivity(), Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();

        } else {
            uid = mAuth.getCurrentUser().getUid();
        }

        // defining id's

        layout = inflater.inflate(R.layout.fragment_dashboard, container, false);
        name = layout.findViewById(R.id.name);
        send = layout.findViewById(R.id.sendmoneynow);
        amount = layout.findViewById(R.id.Amount);
        LoadingText = layout.findViewById(R.id.loadingText);
        Ad = layout.findViewById(R.id.dashboard_ad);
        ads_image = layout.findViewById(R.id.ads_image);
        sliderLayout = layout.findViewById(R.id.imageSlider);
        mAdView = layout.findViewById(R.id.adview);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();


        ref = database.getReference().child("Users").child(uid);
        ref.keepSynced(true);
        addmoney = layout.findViewById(R.id.add_money);


        // Value Event Listeners

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String Name = (String) dataSnapshot.child("name").getValue();
                long Amount = (long) dataSnapshot.child("amount").getValue();

                first_letter = Name.toLowerCase().charAt(0);
                name.setText(Name);
                amount.setText(Amount + "");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        advertisements.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String ad_url = (String) dataSnapshot.child("ad_url").getValue();
                String ad_text = (String) dataSnapshot.child("ad_text").getValue();


                Picasso.get().load(ad_url).into(Ad);
                LoadingText.setText(ad_text);
                LoadingText.setBackgroundResource(R.color.colorPrimaryDark);
                ads_image.setImageResource(R.drawable.ads);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // Click Listeners

        addmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasConnection()) {


                    add_money_clicked();
                } else {
                    Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasConnection()) {
                    Intent intent = new Intent(getContext(), SendMoney.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Sliding View Animation

        // Fetching all the slide images from Slides child
        DatabaseReference SlideRef = database.getReference("Slides");
        SlideList = new ArrayList<>();
        SlideRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    SlideModel obj = postSnapshot.getValue(SlideModel.class);
                    SlideList.add(obj);
                }

                SliderIntroAdapter adapterDemo = new SliderIntroAdapter(getContext(), SlideList);
                sliderLayout.setSliderAdapter(adapterDemo);
                sliderLayout.startAutoCycle();
                sliderLayout.setIndicatorAnimation(IndicatorAnimations.WORM);
                sliderLayout.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                sliderLayout.setScrollTimeInSec(2);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG + "::", "Error Fetching Slides!");
            }
        });


        //AdMob Implementation

        MobileAds.initialize(getContext());
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        mAdView.setAdListener(new AdListener() {


            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }


            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }


            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });


        return layout;
    }

    private void add_money_clicked() {
        Intent intent = new Intent(getActivity(), Money.class);
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
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }

        return false;
    }


}
