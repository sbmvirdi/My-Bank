package cf.projectspro.bank.ui.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import cf.projectspro.bank.repository.MyBankRepo;
import cf.projectspro.bank.ui.modelClasses.User;

public class SendMoneyViewModel extends ViewModel {
    private MyBankRepo mRepo;
    private MutableLiveData<List<User>> userList = new MutableLiveData<>();

    public SendMoneyViewModel() {
        mRepo = MyBankRepo.getInstance();
    }


    public void getUsers(String uid,String searchKeyword){
        mRepo.getUserList(uid,searchKeyword,users->{
            userList.setValue(users);
        });
    }

    public LiveData<List<User>> getUserList() {
        return userList;
    }
}
