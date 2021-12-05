package cf.projectspro.bank.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import cf.projectspro.bank.repository.MyBankRepo;
import cf.projectspro.bank.ui.activities.ForgotPassword;

public class ForgotPasswordViewModel extends ViewModel {

    private MyBankRepo mRepo;
    private MutableLiveData<Boolean> status = new MutableLiveData<>();

    public ForgotPasswordViewModel(){
        mRepo = MyBankRepo.getInstance();
    }


    public void sendResetPasswordEmail(String userEmail){
        mRepo.sendUserPasswordResetEmail(userEmail,status->{
            this.status.setValue(status);
        });
    }

    public LiveData<Boolean> getStatus() {
        return status;
    }

}
