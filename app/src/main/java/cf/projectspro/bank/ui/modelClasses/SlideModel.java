package cf.projectspro.bank.ui.modelClasses;

import androidx.annotation.Keep;

@Keep
public class SlideModel {
    public String image;

    public SlideModel(String image) {
        this.image = image;
    }

    public SlideModel() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
