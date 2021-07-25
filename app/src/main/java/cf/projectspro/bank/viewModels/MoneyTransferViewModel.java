package cf.projectspro.bank.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import cf.projectspro.bank.repository.MyBankRepo;

public class MoneyTransferViewModel extends ViewModel {

    MyBankRepo myBankRepo;
    MutableLiveData<Integer> status = new MutableLiveData<>();

    public MoneyTransferViewModel(){
        myBankRepo = MyBankRepo.getInstance();
    }

    public void sendMoney(String senderUid,String receiverUid, int amount){
        myBankRepo.SendMoney(senderUid,receiverUid,amount,status);
    }

    public LiveData<Integer> getStatus() {
        return status;
    }
}
