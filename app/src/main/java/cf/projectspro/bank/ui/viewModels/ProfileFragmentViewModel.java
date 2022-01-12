package cf.projectspro.bank.ui.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import cf.projectspro.bank.interfaces.LoadData;
import cf.projectspro.bank.repository.MyBankRepo;

public class ProfileFragmentViewModel extends ViewModel {

    private final MyBankRepo mRepo;
    private MutableLiveData<Boolean> isUserVerified = new MutableLiveData<>();
    private MutableLiveData<String> userName = new MutableLiveData<>();

    public ProfileFragmentViewModel(){
        mRepo = MyBankRepo.getInstance();
    }

    public void loadNameOfUser(String uid){
        mRepo.getUserNameByUid(uid, name->{
            userName.setValue(name);
        });
    }

    public void loadUserVerified(){
        mRepo.isUserVerified(verified->{
            isUserVerified.setValue(verified);
        });
    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<Boolean> getIsUserVerified() {
        return isUserVerified;
    }
}
