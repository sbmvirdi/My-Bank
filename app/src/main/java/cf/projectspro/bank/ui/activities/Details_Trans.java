package cf.projectspro.bank.ui.activities;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import cf.projectspro.bank.R;

public class Details_Trans extends AppCompatActivity {
    private TextView To, Amount, Status, Trans_id, From;
    private ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details__trans);
        Bundle bundle = getIntent().getExtras();
        To = findViewById(R.id.detail_name);
        Amount = findViewById(R.id.detail_amount);
        Status = findViewById(R.id.detail_status);
        From = findViewById(R.id.details_from);
        Trans_id = findViewById(R.id.details_transid);
        img = findViewById(R.id.detail_image);
        String to = bundle.getString("to");
        String trans_id = bundle.getString("trans_id");
        String amount = bundle.getString("amount");
        String im = bundle.getString("image");
        Log.d(Details_Trans.class.getSimpleName(), "im:"+im);
        boolean status = bundle.getBoolean("status");
        boolean from_stat = bundle.getBoolean("from_status");
        @SuppressLint("ResourceType") Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.animation);


        if (!TextUtils.isEmpty(to)) {
            To.setText(to);
        }

        if (!TextUtils.isEmpty(amount)) {
            Amount.setText(amount);
        }

        if (!TextUtils.isEmpty(im)) {
            Glide.with(this).load(im).into(img);
        }

        if (!TextUtils.isEmpty(trans_id)) {
            Trans_id.setText(trans_id);
        }


        if (from_stat) {
            From.setText("From:");
        }


        if (status) {
            Status.setTextColor(getResources().getColor(R.color.colorPrimary));
            Status.setText("Success");
        } else {
            Status.setTextColor(getResources().getColor(R.color.colorAccent));
            Status.setText("Failed");
        }
        img.startAnimation(hyperspaceJumpAnimation);

    }
}
