package cf.projectspro.bank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

public class payment_processing_screen extends AppCompatActivity {
    private RelativeLayout deduct,transfer,success;
    private TextView paymentstatus,_paymentstatus,_paymentstatusfinal;
    private ProgressBar step1,step2;
    private ImageView step1image,step2image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_processing_screen);

        step2 = findViewById(R.id._paymentprogress);
        step1 = findViewById(R.id.paymentprogress);
        paymentstatus = findViewById(R.id.paymentstatus);
        _paymentstatus = findViewById(R.id._paymentstatus);
        _paymentstatusfinal = findViewById(R.id._paymentstatusfinal);
        step1image = findViewById(R.id.step1);
        step2image = findViewById(R.id.step2);
        deduct  = findViewById(R.id.deduct);
        transfer  = findViewById(R.id.transfer);
        success  = findViewById(R.id.success);


        deduct.setVisibility(View.VISIBLE);
        transfer.setVisibility(View.INVISIBLE);
        success.setVisibility(View.INVISIBLE);

       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               step1.setVisibility(View.INVISIBLE);
               step1image.setVisibility(View.VISIBLE);
               paymentstatus.setTextColor(Color.parseColor("#62D4BE"));
               transfer.setVisibility(View.VISIBLE);

               new Handler().postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       step2.setVisibility(View.INVISIBLE);
                       step2image.setVisibility(View.VISIBLE);
                       _paymentstatus.setTextColor(Color.parseColor("#62D4BE"));
                       success.setVisibility(View.VISIBLE);

                       new Handler().postDelayed(new Runnable() {
                           @Override
                           public void run() {
                               Intent i = new Intent(payment_processing_screen.this,MainActivity.class);
                               startActivity(i);
                               finish();
                           }
                       },2000);

                   }
               },2000);
           }
       },2000);

    }

    @Override
    public void onBackPressed() {

    }
}
