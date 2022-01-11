package cf.projectspro.bank.ui.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import cf.projectspro.bank.repository.MyBankRepo;
import cf.projectspro.bank.ui.modelClasses.Transaction;

public class NotificationFragmentViewModel extends ViewModel {

    private MyBankRepo mRepo;
    private MutableLiveData<List<Transaction>> notification = new MutableLiveData<>();

    public NotificationFragmentViewModel() {
        mRepo = MyBankRepo.getInstance();
    }



    public void getNotificationListByUserUid(String uid){
        mRepo.getNotificationOfUserByUid(uid,notifications->{
            notification.setValue(notifications);
        });
    }

    public LiveData<List<Transaction>> getNotification() {
        return notification;
    }
}
