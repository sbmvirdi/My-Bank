package cf.projectspro.bank.ui.viewModels;

import android.transition.Slide;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import cf.projectspro.bank.interfaces.LoadData;
import cf.projectspro.bank.repository.MyBankRepo;
import cf.projectspro.bank.ui.modelClasses.Promotion;
import cf.projectspro.bank.ui.modelClasses.SlideModel;
import cf.projectspro.bank.ui.modelClasses.User;

public class DashboardFragmentViewModel extends ViewModel {

    private MyBankRepo mRepo;
    private MutableLiveData<User> user = new MutableLiveData<>();
    private MutableLiveData<Boolean> userVerified = new MutableLiveData<>();
    private MutableLiveData<List<SlideModel>> slideModels = new MutableLiveData<>();
    private MutableLiveData<Promotion> promotionalData = new MutableLiveData<>();

    public DashboardFragmentViewModel(){
        mRepo = MyBankRepo.getInstance();
    }

    public void loadUserByUid(String uid){
        mRepo.getUserByUid(uid,user->{
            this.user.setValue(user);
        });
    }

    public void loadUserVerified(){
        mRepo.isUserVerified(verified->{
            userVerified.setValue(verified);
        });
    }

    public void loadPromotionalData(){
        mRepo.getPromotionData(promotionalData->{
            this.promotionalData.setValue(promotionalData);
        });
    }

    public void loadSlides(){
        mRepo.getSliderData(slides->{
            slideModels.setValue(slides);
        });
    }

    public LiveData<Boolean> getUserVerified() {
        return userVerified;
    }

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<List<SlideModel>> getSlideModels() {
        return slideModels;
    }

    public LiveData<Promotion> getPromotionalData() {
        return promotionalData;
    }
}
