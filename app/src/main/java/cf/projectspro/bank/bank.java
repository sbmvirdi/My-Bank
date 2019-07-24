package cf.projectspro.bank;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class bank extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

