package cf.projectspro.bank.ui.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import cf.projectspro.bank.repository.MyBankRepo;

public class SignUpViewModel extends ViewModel {

    private MyBankRepo mRepo;
    private MutableLiveData<Boolean> status = new MutableLiveData<>();

    public SignUpViewModel() {
        mRepo = MyBankRepo.getInstance();
    }


    public void signUpUserWithEmailAndPassword(String name, String email, String password){
        mRepo.signUpUserWithEmailAndPassword(name,email,password,success->{
            status.setValue(success);
        });
    }

    public LiveData<Boolean> getStatus() {
        return status;
    }
}
