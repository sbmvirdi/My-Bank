package cf.projectspro.bank;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import cf.projectspro.bank.R;

/**
 * A simple {@link Fragment} subclass.
 * sbmvirdi
 */
public class Dashboard extends Fragment {
    View layout;
    private TextView name,amount;
    private DatabaseReference ref,advertisements;
    private Button addmoney,send;
    private FirebaseAuth mAuth;
    private SliderLayout sliderLayout;
    private Button mgetvirtualcard;
    private TextView mcardnumber,mcardname,mcardamount;
    private RelativeLayout mvirtualcard;
    private String uid,adds[]={"https://firebasestorage.googleapis.com/v0/b/bank-f7765.appspot.com/o/Whats-the-Difference-Between-Online-and-Mobile-Banking_1-INARTICLE-e1508438634604-730x323.jpg?alt=media&token=96a9d9bb-d095-4b2a-ae11-e25e2d9d4ffc",
            "https://firebasestorage.googleapis.com/v0/b/bank-f7765.appspot.com/o/help-how-to-topic.png?alt=media&token=8f1ba57d-c272-48cc-9b5f-9ade10253b57",
            "https://firebasestorage.googleapis.com/v0/b/bank-f7765.appspot.com/o/BlobPostHeaders_AndroidStackedNotificationsEdit.png?alt=media&token=a24e02cc-d997-4c7c-baef-00e9874b574e"
    };
    private ViewFlipper vf;
    public Dashboard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        advertisements = FirebaseDatabase.getInstance().getReference().child("advert");
        if(mAuth.getCurrentUser() == null ){
            Intent intent = new Intent(getActivity(),Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();

        }else {
            uid = mAuth.getCurrentUser().getUid();
        }


        layout =  inflater.inflate(R.layout.fragment_dashboard, container, false);
        name = layout.findViewById(R.id.name);
        send = layout.findViewById(R.id.sendmoneynow);
        amount = layout.findViewById(R.id.Amount);
        sliderLayout = layout.findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(SliderLayout.Animations.FILL); //set indicator animation by using SliderLayout.Animations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setScrollTimeInSec(1); //set scroll delay in seconds :
        //mgetvirtualcard = layout.findViewById(R.id.getvirtualcard);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

       /* advertisements.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                adds[0] = (String)dataSnapshot.child("ad1").getValue();
                adds[1] = (String)dataSnapshot.child("ad2").getValue();
                adds[2] = (String)dataSnapshot.child("ad3").getValue();
                adds[3] = (String)dataSnapshot.child("ad4").getValue();
                //Toast.makeText(getContext(), adds[0], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        ref = database.getReference().child("Users").child(uid);
        ref.keepSynced(true);
        addmoney = layout.findViewById(R.id.add_money);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String Name = (String) dataSnapshot.child("name").getValue();
                long Amount = (long) dataSnapshot.child("amount").getValue();


               // Toast.makeText(getContext(), ""+newcard, Toast.LENGTH_SHORT).show();
               name.setText(Name);
               amount.setText(Amount+"");
               /*if (newcard == 1){
                   mvirtualcard.setVisibility(View.VISIBLE);
                   mcardname.setVisibility(View.GONE);
                   mcardamount.setVisibility(View.GONE);
                   mcardnumber.setText("**** **** **** ****");
                   mgetvirtualcard.setVisibility(View.VISIBLE);
                   mvirtualcard.setVisibility(View.VISIBLE);
               }
               else if(newcard == 0){
                   mvirtualcard.setVisibility(View.VISIBLE);
                   mgetvirtualcard.setVisibility(View.GONE);
                   mcardamount.setVisibility(View.VISIBLE);
                   mcardname.setVisibility(View.VISIBLE);
                   mvirtualcard.setVisibility(View.VISIBLE);
                   mcardnumber.setVisibility(View.VISIBLE);
                   // fetching the card details if the user has
                   // not applied for the card
                   long cardno =(long) dataSnapshot.child("cardno").getValue();
                   long credit = (long) dataSnapshot.child("credit").getValue();
                   mcardnumber.setText(cardno+"");
                   mcardname.setText(Name);
                   mcardamount.setText(Amount+"");

               }*/

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
           mgetvirtualcard.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Toast.makeText(getContext(), "Feature Under Construction", Toast.LENGTH_SHORT).show();
               }
           });
           setSliderViews();
            addmoney.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(hasConnection()){
                   // addmoney.setBackground(getResources().getDrawable(R.drawable.cornerclicked));
                    add_money_clicked();}
                    else{
                        Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (hasConnection()){
                    Intent intent = new Intent(getContext(),SendMoney.class);
                    startActivity(intent); }
                    else{
                        Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        return layout;
    }

    private void add_money_clicked() {
        Intent intent = new Intent(getActivity(),Money.class);
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

    private void setSliderViews() {

        for (int i = 0; i < 3; i++) {

            SliderView sliderView = new SliderView(getContext());

            switch (i) {
                case 0:
                    sliderView.setImageUrl(adds[0]);
                    break;
                case 1:
                    sliderView.setImageUrl(adds[1]);
                    break;
                case 2:
                    sliderView.setImageUrl(adds[2]);
                    break;
            }

            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            sliderLayout.addSliderView(sliderView);

        }}}
