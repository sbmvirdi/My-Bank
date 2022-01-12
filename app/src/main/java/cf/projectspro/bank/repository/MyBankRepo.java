package cf.projectspro.bank.repository;

import android.util.Log;
import android.util.StateSet;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cf.projectspro.bank.R;
import cf.projectspro.bank.interfaces.LoadData;
import cf.projectspro.bank.ui.activities.SendMoney;
import cf.projectspro.bank.ui.adapters.SliderIntroAdapter;
import cf.projectspro.bank.ui.adapters.UserAdapter;
import cf.projectspro.bank.ui.modelClasses.Promotion;
import cf.projectspro.bank.ui.modelClasses.SlideModel;
import cf.projectspro.bank.ui.modelClasses.Transaction;
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



    // status of self credit
    public static final Integer STATUS_SELF_CREDIT_INITIATED = 200;
    public static final Integer STATUS_SELF_CREDIT_AMOUNT_CREDITED = 201;
    public static final Integer STATUS_SELF_CREDIT_SUCCESS = 202;
    public static final Integer STATUS_SELF_CREDIT_FAILED = 203;

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
     * function to credit money to own account
     * @param uid uid of the user
     * @param amount amount to be credited
     * @param status status tracking of the transaction
     */
    public void creditSelfAccount(String uid,long amount,MutableLiveData<Integer> status){

        status.setValue(STATUS_SELF_CREDIT_INITIATED);
        getUserFromDatabaseByUid(uid,user->{
            // getting the details of the user
            Log.e(TAG, "creditSelfAccount: inside user");
            if (user!=null){
                Log.e(TAG, "creditSelfAccount: user not null");
                initiateSelfCredit(user,amount,status,success->{
                    if (success){
                        Log.e(TAG, "creditSelfAccount: success");
                        status.setValue(STATUS_SELF_CREDIT_SUCCESS);
                    }
                });
            }
        });
    }

    /**
     * function to initiate the transaction for self credit
     * @param user user who initiated the transaction
     * @param amount amount to be credited
     * @param status status tracking of the payment
     * @param loadData to track status of transaction
     */
    private void initiateSelfCredit(User user,long amount,MutableLiveData<Integer> status,LoadData<Boolean> loadData){

        updateUserSelfCreditAccount(user,amount);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        usersRef.child(user.uid).setValue(user).addOnCompleteListener(task->
        {
           if (task.isSuccessful()){
               status.setValue(STATUS_SELF_CREDIT_AMOUNT_CREDITED);
               writeSuccessSelfCreditLogs(user,amount);
               loadData.onDataLoaded(true);
           }else{
               writeFailedSelfCreditLogs(user,amount);
               status.setValue(STATUS_SELF_CREDIT_FAILED);
               loadData.onDataLoaded(false);
           }
        });


    }

    /**
     * function to write success logs to the user
     * @param user user who initiated the transaction
     * @param amount amount to be credited
     */
    private void writeSuccessSelfCreditLogs(User user, long amount) {

        Map<String,Object> userLog = new HashMap<>();
        String userRandom = UUID.randomUUID().toString();
        DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference().child("transactions").child(user.uid).child(userRandom);

        long time = timestamp();
        userLog.put("amount",amount);
        userLog.put("code",-time);
        userLog.put("from",false);
        userLog.put("src",success());
        userLog.put("status",true);
        userLog.put("to","Self");
        userLog.put("trans_id",time);
        senderRef.setValue(userLog);

        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("trans_ids").child(String.valueOf(time));
        HashMap<String,Object> adminLog = new HashMap<>();

        adminLog.put("amount",amount);
        adminLog.put("status",true);
        adminLog.put("done_by",user.name);
        adminLog.put("to","Self");
        adminLog.put("touid",user.uid);
        adminLog.put("fromuid",user.uid);

        adminRef.setValue(adminLog);

    }

    /**
     * function to write failed self credit logs for user
     * @param user user who initiated the transaction
     * @param amount amount to be credited
     */
    private void writeFailedSelfCreditLogs(User user, long amount) {

        Map<String,Object> userLog = new HashMap<>();
        String userRandom = UUID.randomUUID().toString();
        DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference().child("transactions").child(user.uid).child(userRandom);

        long time = timestamp();
        userLog.put("amount",amount);
        userLog.put("code",-time);
        userLog.put("from",false);
        userLog.put("src",failed());
        userLog.put("status",failed());
        userLog.put("to","Self");
        userLog.put("trans_id",time);
        senderRef.setValue(userLog);

        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("trans_ids").child(String.valueOf(time));
        HashMap<String,Object> adminLog = new HashMap<>();

        adminLog.put("amount",amount);
        adminLog.put("status",true);
        adminLog.put("done_by",user.name);
        adminLog.put("fromuid",user.uid);
        adminLog.put("touid",user.uid);
        adminLog.put("to","Self");

        adminRef.setValue(adminLog);
    }

    /**
     * function to calculate the user amount
     * @param user user who initiated the transaction
     * @param amount amount to be credited
     */
    private void updateUserSelfCreditAccount(User user, long amount) {
        user.amount+=amount;
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
        adminLog.put("fromuid",sender.uid);
        adminLog.put("touid",receiver.uid);
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

    /**
     * function to get users from database
     * @param uid uid of the user calling function
     * @param loadData to return the list of users
     */
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
     * function to send verification email
     * @param loadData to return status of verification email sent
     */
    public void verifyUserEmail(LoadData<Boolean> loadData){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null){
            loadData.onDataLoaded(false);
        }else{
            mAuth.getCurrentUser().sendEmailVerification();
            loadData.onDataLoaded(true);
        }
    }

    /**
     * function to login user
     * @param email email of the user
     * @param password password of the user
     * @param loadData to return status of login
     */
    public void loginUserWithEmailAndPassword(String email,String password,LoadData<Boolean> loadData){
        Log.e(TAG, "loginUserWithEmailAndPassword: email and pass:"+email+","+password);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               loadData.onDataLoaded(true);
           }else {
               Log.e(TAG, "loginUserWithEmailAndPassword: error:"+task.getException().getMessage());
               loadData.onDataLoaded(false);
           }
        });
    }


    /**
     * function to signup the user
     * @param name name of the user
     * @param email email of the user
     * @param password password of the user
     * @param loadData return the status of the signup process
     */
    public void signUpUserWithEmailAndPassword(String name,String email,String password,LoadData<Boolean> loadData){
        Log.e(TAG, "signUpUserWithEmailAndPassword: called");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(signUpTask->{
            Log.e(TAG, "signUpUserWithEmailAndPassword: signing up");
            if (signUpTask.isSuccessful()){
                loginUserWithEmailAndPassword(email,password,success->{
                    if (success){
                        if (mAuth.getCurrentUser()!=null) {
                            writeInitialUserData(mAuth.getCurrentUser().getUid(),name,written->{
                                if (written){
                                    Log.e(TAG, "signUpUserWithEmailAndPassword: signup completed");
                                    loadData.onDataLoaded(true);
                                }
                            });
                        }else{
                            loadData.onDataLoaded(false);
                            Log.e(TAG, "signUpUserWithEmailAndPassword: signed up: logged in: current user null");
                        }
                    }else{
                        loadData.onDataLoaded(false);
                        Log.e(TAG, "signUpUserWithEmailAndPassword: signed up :failed login");
                    }
                });
            }else{
                Log.e(TAG, "signUpUserWithEmailAndPassword: sign up failed:"+signUpTask.getException().getMessage());
                loadData.onDataLoaded(false);
            }
        });
    }


    /**
     * function to write the initial database records of the user
     * @param uid uid of the newly created user
     * @param name name of the user
     * @param loadData to return status of database write
     */
    public void writeInitialUserData(String uid,String name,LoadData<Boolean> loadData){

        Log.e(TAG, "writeInitialUserData: called:uid"+uid);
        Map<String,Object> userInitialData = new HashMap<>();
        userInitialData.put("uid",uid);
        userInitialData.put("amount",0);
        userInitialData.put("name",name);

        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).setValue(userInitialData).addOnCompleteListener(task->{
            if (task.isSuccessful()){
                Log.e(TAG, "writeInitialUserData: success");
                loadData.onDataLoaded(true);
            }else{
                Log.e(TAG, "writeInitialUserData: failed");
                loadData.onDataLoaded(false);
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

    /**
     * function to get users list
     * @param uid uid of the caller
     * @param mSearchString name keyword to be searched
     * @param loadData to return list of users
     */
    public void getUserList(String uid,String mSearchString,LoadData<List<User>> loadData){
        Query usersRef = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("name");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> usersList = new ArrayList<>();
                for (DataSnapshot userSnapshot:snapshot.getChildren()){
                    User user = userSnapshot.getValue(User.class);
                    assert user != null;
                    if (user.name.contains(mSearchString)) {
                        if (!user.uid.equals(uid)) {
                            usersList.add(user);
                        }
                    }
                    loadData.onDataLoaded(usersList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadData.onDataLoaded(null);
            }
        });
    }


    /**
     * function to get the user name by uid
     * @param uid uid of the user
     * @param loadData to return name of the user
     */
    public void getUserNameByUid(String uid, LoadData<String> loadData){
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = (String) snapshot.child("name").getValue();
                Log.e(TAG, "onDataChange: getUserNameByUid:Name:"+name);
                loadData.onDataLoaded(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: error fetching user name:"+error.getMessage());
                loadData.onDataLoaded(null);
            }
        });
    }


    /**
     * function to get promotional data
     * @param loadData to return promotional data
     */
    public void getPromotionData(LoadData<Promotion> loadData){
        FirebaseDatabase.getInstance().getReference().child("Advert").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Promotion promotion = dataSnapshot.getValue(Promotion.class);
                if (promotion!=null){
                    loadData.onDataLoaded(promotion);
                }else{
                    loadData.onDataLoaded(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: getPromotionData:"+databaseError.getMessage());
                loadData.onDataLoaded(null);
            }
        });
    }


    /**
     * function to get slides for dashboard
     * @param loadData to return back slides
     */
    public void getSliderData(LoadData<List<SlideModel>> loadData){
        FirebaseDatabase.getInstance().getReference().child("Slides").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<SlideModel> slideModelList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    SlideModel obj = postSnapshot.getValue(SlideModel.class);
                    slideModelList.add(obj);
                }
                loadData.onDataLoaded(slideModelList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadData.onDataLoaded(null);
                Log.e(TAG, "onCancelled:Error Fetching Slides:"+databaseError.getMessage());
            }
        });

    }


    /**
     * function to get the user by uid
     * @param uid uid of the user
     * @param loadData to return back the user
     */
    public void getUserByUid(String uid, LoadData<User> loadData){
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Log.e(TAG, "onDataChange: getUserByUid:user:"+user);
                loadData.onDataLoaded(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: error fetching user:"+error.getMessage());
                loadData.onDataLoaded(null);
            }
        });
    }



    /**
     * function to get transactions of the user
     * @param uid uid of the user
     * @param loadData to send back list of transaction
     */
    public void getNotificationOfUserByUid(String uid,LoadData<List<Transaction>> loadData){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query ref = database.getReference().child("transactions").child(uid).orderByChild("code");
        ref.keepSynced(true);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Transaction> notifies = new ArrayList<>();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    notifies.add(dataSnapshot.getValue(Transaction.class));
                }
                loadData.onDataLoaded(notifies);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled:"+error.toString());
                loadData.onDataLoaded(null);
            }
        });
    }

    /**
     * function to verify if the user has verified email
     * @param loadData to return boolean for user verified
     */
    public void isUserVerified(LoadData<Boolean> loadData){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!= null){

            mAuth.getCurrentUser().reload().addOnCompleteListener(task->{
                if (task.isSuccessful()){

                    if (mAuth.getCurrentUser().isEmailVerified()){
                        loadData.onDataLoaded(true);
                    }else{
                        loadData.onDataLoaded(false);
                    }
                }else{
                    Log.e(TAG, "isUserVerified:reload failed:"+task.getException());
                }
            });

        }else{
            loadData.onDataLoaded(false);
        }

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
