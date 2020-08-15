package cf.projectspro.bank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SliderAdapterDemo extends SliderViewAdapter<SliderAdapterDemo.SliderVH> {


    private Context context;
    private List<SlideModel> mList;

    public SliderAdapterDemo(Context context, List<SlideModel> mList) {
        this.context = context;
        this.mList = mList;
    }

    @Override
    public SliderAdapterDemo.SliderVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.slider_image, parent, false);
        return new SliderVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterDemo.SliderVH viewHolder, int position) {

//        switch (position){
//
//            case 0:
//                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/bank-f7765.appspot.com/o/one.png?alt=media&token=e9f30af4-9450-4653-b0c0-667b946cb741").into(viewHolder.img);
//                break;
//
//            case 1:
//                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/bank-f7765.appspot.com/o/two.png?alt=media&token=2fb8125e-7ce4-4196-88f2-7bcb06164590").into(viewHolder.img);
//                break;
//
//            case 2:
//                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/bank-f7765.appspot.com/o/three.png?alt=media&token=86e3da4b-7258-4356-9939-aed3e631e9be").into(viewHolder.img);
//                break;
//
//        }

        viewHolder.setData(mList.get(position).getImage());

    }

    @Override
    public int getCount() {
        return 3;
    }


    class SliderVH extends SliderViewAdapter.ViewHolder {
        ImageView img;

        public SliderVH(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.slider_img);
        }

        private void setData(String image) {
            Picasso.get().load(image).placeholder(R.drawable.sliderplaceholder).into(img);
        }
    }


}


