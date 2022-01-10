package cf.projectspro.bank.ui.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import cf.projectspro.bank.R;
import cf.projectspro.bank.databinding.ActivityDetailsTransBinding;

public class Details_Trans extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailsTransBinding binding = ActivityDetailsTransBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());


        Bundle bundle = getIntent().getExtras();
        String to = bundle.getString("to");
        String trans_id = bundle.getString("trans_id");
        String amount = bundle.getString("amount");
        String statusImage = bundle.getString("image");


        boolean status = bundle.getBoolean("status");
        boolean from_stat = bundle.getBoolean("from_status");
        @SuppressLint("ResourceType") Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.animation);


        if (!TextUtils.isEmpty(to)) {
            binding.detailName.setText(to);
        }

        if (!TextUtils.isEmpty(amount)) {
            binding.detailAmount.setText(amount);
        }

        if (!TextUtils.isEmpty(statusImage)) {
            Picasso.get().load(statusImage).into(binding.detailImage);
        }

        if (!TextUtils.isEmpty(trans_id)) {
            binding.detailsTransid.setText(trans_id);
        }


        if (from_stat) {
            binding.detailsFrom.setText("From:");
        }


        if (status) {
            binding.detailStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
            binding.detailStatus.setText("Success");
        } else {
            binding.detailStatus.setTextColor(getResources().getColor(R.color.colorAccent));
            binding.detailStatus.setText("Failed");
        }

        binding.detailImage.startAnimation(hyperspaceJumpAnimation);

    }
}
