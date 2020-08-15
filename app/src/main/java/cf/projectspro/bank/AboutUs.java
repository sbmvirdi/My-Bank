package cf.projectspro.bank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AboutUs extends AppCompatActivity {
    private TextView mEmaildev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        mEmaildev = findViewById(R.id.emaildev);
        mEmaildev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setData(Uri.parse("mailto:"));
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"shubhamvirdic3@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Mail to Developer");

                try {
                    startActivity(Intent.createChooser(i, "sendmail"));
                    finish();
                } catch (Exception e) {
                    Toast.makeText(AboutUs.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
}
