package cf.projectspro.bank.ui.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import cf.projectspro.bank.repository.MyBankRepo;

public class LoginViewModel extends ViewModel {

    private MyBankRepo mRepo;
    private MutableLiveData<Boolean> loginStatus = new MutableLiveData<>();

    public LoginViewModel() {
        mRepo = MyBankRepo.getInstance();
    }

    public void loginUserWithEmailAndPassword(String email,String password){
        mRepo.loginUserWithEmailAndPassword(email,password,status->{
            loginStatus.setValue(status);
        });
    }

    public LiveData<Boolean> getLoginStatus() {
        return loginStatus;
    }
}
