//package cf.projectspro.bank.ui.adapters;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//import com.smarteist.autoimageslider.SliderViewAdapter;
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
//import cf.projectspro.bank.R;
//import cf.projectspro.bank.ui.modelClasses.SlideModel;
//
//public class SliderIntroAdapter
//        extends SliderViewAdapter<SliderIntroAdapter.SliderVH>
//{
//
//
//    private Context context;
//    private List<SlideModel> mList;
//
//    public SliderIntroAdapter(Context context, List<SlideModel> mList) {
//        this.context = context;
//        this.mList = mList;
//    }
//
//    @Override
//    public SliderIntroAdapter.SliderVH onCreateViewHolder(ViewGroup parent) {
//        View inflate = LayoutInflater.from(context).inflate(R.layout.slider_image, parent, false);
//        return new SliderVH(inflate);
//    }
//
//    @Override
//    public void onBindViewHolder(SliderIntroAdapter.SliderVH viewHolder, int position) {
//        viewHolder.setData(mList.get(position).getImage());
//    }
//
//    @Override
//    public int getCount() {
//        return mList.size();
//    }
//
//
//    class SliderVH extends SliderViewAdapter.ViewHolder {
//        ImageView img;
//
//        public SliderVH(View itemView) {
//            super(itemView);
//            img = itemView.findViewById(R.id.slider_img);
//        }
//
//        private void setData(String image) {
//            Picasso.get().load(image).placeholder(R.drawable.sliderplaceholder).into(img);
//        }
//    }
//
//
//}
//
//
