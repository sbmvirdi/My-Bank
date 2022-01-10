package cf.projectspro.bank.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cf.projectspro.bank.interfaces.LoadData;
import cf.projectspro.bank.ui.modelClasses.User;

/**
 * Repository to process database queries
 */
public class MyBankRepo {
    private static final String TAG = MyBankRepo.class.getSimpleName();
    private static MyBankRepo instance;

    // status of the transaction
    public static final Integer STATUS_INITIATED = 0;
    public static final Integer STATUS_DEDUCT_FROM_SENDER = 1;
    public static final Integer STATUS_CREDITED_TO_RECIPIENT = 2;
    public static final Integer STATUS_SUCCESS = 3;
    public static final Integer STATUS_FAILED_INSUFFICIENT_FUNDS=4;
    private static final Integer STATUS_FAILED_RECIPIENT_SIDE = 5;
    private static final Integer STATUS_FAILED_SENDER_SIDE = 6;

    private MyBankRepo(){

    }

    /**
     * function to access the My Bank repo object
     * @return
     */
    public static MyBankRepo getInstance(){
        if (instance == null){
            instance = new MyBankRepo();
        }

        return instance;
    }


    /**
     * function to transfer money from one user to another
     * @param senderUid sender user
     * @param receiverUid receiver user
     * @param amount amount to be sent
     * @param status status live data to track the progress of transaction
     */
    public void SendMoney(String senderUid,String receiverUid,int amount, MutableLiveData<Integer> status){
        // fetch the details of the sender user
        status.setValue(STATUS_INITIATED);

        // get the sender user
        getUserFromDatabaseByUid(senderUid,sender->{
            Log.e(TAG, "SendMoney: inside getUserFromDatabaseByUid sender");
            // check if the sender user is not null
            if (sender!=null){
                Log.e(TAG, "SendMoney: sender not null");
                // get the receiver user
                getUserFromDatabaseByUid(receiverUid,receiver->{
                    // check if the receiver user is not null
                    Log.e(TAG, "SendMoney: inside getUserFromDatabaseByUid receiver" );
                    if (receiver!=null){
                        Log.e(TAG, "SendMoney: receiver not null");
                        // check if the amount to be transferred is less than equal to sender balance
                        if (isTransactionFeasible(sender,amount)){
                            Log.e(TAG, "SendMoney: transaction is feasible");
                            // initiate the transaction
                            initiateTransactionBetweenUsers(sender,receiver,status,amount,success->{
                               // if the transaction is success
                                Log.e(TAG, "SendMoney: initiate transaction between users");
                                if (success){
                                    Log.e(TAG, "SendMoney: success");
                                    // write success logs
                                    status.setValue(STATUS_SUCCESS);
                                    writeSuccessTransactionLogs(sender,receiver,amount);
                                }
                            });
                        }else{
                            Log.e(TAG, "SendMoney: not feasible");
                            // write failed logs
                            status.setValue(STATUS_FAILED_INSUFFICIENT_FUNDS);
                            writeFailedTransactionLogs(sender,receiver,amount);
                        }
                    }
                });
            }
        });

    }

    /**
     * function to write transaction logs when the transaction has failed
     * @param sender sender user
     * @param receiver receiver user
     * @param amount amount to be sent
     */
    private void writeFailedTransactionLogs(User sender, User receiver, int amount) {

        Map<String,Object> senderLog = new HashMap<>();
        String senderRandom = UUID.randomUUID().toString();
        DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference().child("transactions").child(sender.uid).child(senderRandom);

        long time = timestamp();
        senderLog.put("amount",amount);
        senderLog.put("code",-time);
        senderLog.put("from",false);
        senderLog.put("src",failed());
        senderLog.put("status",failed());
        senderLog.put("to",receiver.name);
        senderLog.put("trans_id",time);
        senderRef.setValue(senderLog);

        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("trans_ids").child(String.valueOf(time));
        HashMap<String,Object> adminLog = new HashMap<>();

        adminLog.put("amount",amount);
        adminLog.put("status",true);
        adminLog.put("done_by",sender.name);
        adminLog.put("to",receiver.name);

        adminRef.setValue(adminLog);


    }

    /**
     * function to write transaction logs when the transaction is successful
     * @param sender sender user
     * @param receiver receiver user
     * @param amount amount to be sent
     */
    private void writeSuccessTransactionLogs(User sender, User receiver, int amount) {

        Map<String,Object> senderLog = new HashMap<>();
        String senderRandom = UUID.randomUUID().toString();
        DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference().child("transactions").child(sender.uid).child(senderRandom);

        long time = timestamp();
        senderLog.put("amount",amount);
        senderLog.put("code",-time);
        senderLog.put("from",false);
        senderLog.put("src",success());
        senderLog.put("status",true);
        senderLog.put("to",receiver.name);
        senderLog.put("trans_id",time);
        senderRef.setValue(senderLog);


        String receiverRandom = UUID.randomUUID().toString();
        DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference().child("transactions").child(receiver.uid).child(receiverRandom);
        Map<String,Object> receiverLog = new HashMap<>();

        receiverLog.put("amount",amount);
        receiverLog.put("code",-time);
        receiverLog.put("from",true);
        receiverLog.put("src",success());
        receiverLog.put("status",true);
        receiverLog.put("to",sender.name);
        receiverLog.put("trans_id",time);
        receiverRef.setValue(receiverLog);


        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("trans_ids").child(String.valueOf(time));
        HashMap<String,Object> adminLog = new HashMap<>();

        adminLog.put("amount",amount);
        adminLog.put("status",true);
        adminLog.put("done_by",sender.name);
        adminLog.put("to",receiver.name);
        adminLog.put("touid",receiver.uid);
        adminLog.put("fromuid",sender.uid);

        adminRef.setValue(adminLog);

    }

    /**
     * function to perform transaction between the users
     * @param sender sender user
     * @param receiver receiver user
     * @param status status live data
     * @param amount amount to be sent
     * @param loadData returning the status
     */
    private void initiateTransactionBetweenUsers(User sender, User receiver, MutableLiveData<Integer> status, int amount, LoadData<Boolean> loadData) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        updateUserAmounts(sender, receiver, amount);

        usersRef.child(sender.uid).setValue(sender).addOnCompleteListener(task->{
            if (task.isSuccessful()){
                status.setValue(STATUS_DEDUCT_FROM_SENDER);
                usersRef.child(receiver.uid).setValue(receiver).addOnCompleteListener(task1->{
                    if (task.isSuccessful()){
                        status.setValue(STATUS_CREDITED_TO_RECIPIENT);
                        loadData.onDataLoaded(true);
                    }else{
                        status.setValue(STATUS_FAILED_RECIPIENT_SIDE);
                        loadData.onDataLoaded(false);
                    }
                });
            }else{
                status.setValue(STATUS_FAILED_SENDER_SIDE);
                loadData.onDataLoaded(false);
            }
        });

    }

    public void updateUserAmounts(User sender, User receiver, int amount) {
        sender.amount -= amount;
        receiver.amount += amount;
    }


    /**
     * function to check if the transaction is feasible
     * @param user sender user
     * @param amount amount to be sent
     * @return boolean represening feasibly
     */
    public boolean isTransactionFeasible(User user, int amount) {
        return amount > 0 && amount <= user.amount;
    }

    public void getUserFromDatabaseByUid(String uid,LoadData<User> loadData){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                loadData.onDataLoaded(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadData.onDataLoaded(null);
            }
        });
    }


    /**
     * function to send password reset email
     * @param userEmail email id of the user
     */
    public void sendUserPasswordResetEmail(String userEmail,LoadData<Boolean> loadData){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task->{
            if (task.isSuccessful()){
                loadData.onDataLoaded(true);
            }else{
                Log.d(TAG, "sendUserPasswordResetEmail: "+task.getException().toString());
                loadData.onDataLoaded(false);
            }
        });
    }

    private String failed() {
        return "https://firebasestorage.googleapis.com/v0/b/bank-f7765.appspot.com/o/failed.png?alt=media&token=4b6ef20f-5958-4edf-ad6d-279bbee57879";
    }

    public Long timestamp() {
        Long tsLong = System.currentTimeMillis();
        return tsLong;
    }

    private String success() {
        return "https://firebasestorage.googleapis.com/v0/b/bank-f7765.appspot.com/o/done.png?alt=media&token=e7920069-13a3-499f-8818-75cb25bc77fb";
    }
}
