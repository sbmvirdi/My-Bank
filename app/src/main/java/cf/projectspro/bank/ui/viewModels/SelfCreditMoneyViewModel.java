package cf.projectspro.bank.ui.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import cf.projectspro.bank.repository.MyBankRepo;

public class SelfCreditMoneyViewModel extends ViewModel {

    private MyBankRepo mRepo;
    private MutableLiveData<Integer> status = new MutableLiveData<>();

    public SelfCreditMoneyViewModel() {
        mRepo = MyBankRepo.getInstance();
    }

    public void initiateSelfCredit(String uid, long amount) {
        mRepo.creditSelfAccount(uid, amount, status);
    }

    public LiveData<Integer> getStatus() {
        return status;
    }
}
