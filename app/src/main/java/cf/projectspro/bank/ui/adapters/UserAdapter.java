package cf.projectspro.bank.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import cf.projectspro.bank.R;
import cf.projectspro.bank.databinding.UserDetailBinding;
import cf.projectspro.bank.ui.activities.totransfer;
import cf.projectspro.bank.ui.modelClasses.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> mList;
    private Context mContext;

    public UserAdapter(List<User> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(UserDetailBinding.inflate(LayoutInflater.from(mContext)));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        private UserDetailBinding binding;
        public UserViewHolder(@NonNull UserDetailBinding binding) {
            super(binding.getRoot());
            this.binding= binding;
        }

        public void bind(User user) {

            binding.userName.setText(user.getname());
            String name = user.name.toLowerCase();
            char _name = name.charAt(0);
            if (Character.isAlphabetic(_name)) {
                int res = mContext.getResources().getIdentifier("@drawable/" + _name, null, mContext.getPackageName());
                binding.user.setImageResource(res);
            } else {
                Picasso.get().load(R.drawable.user).into(binding.user);
            }
            if (binding.user.getDrawable() == null) {
                Picasso.get().load(R.drawable.user).into(binding.user);
            }


            binding.getRoot().setOnClickListener(view -> {
                Intent intent = new Intent(mContext, totransfer.class);
                intent.putExtra("to_user_uid", user.uid);
                intent.putExtra("to_user_amount", user.amount);
                intent.putExtra("name", user.name);
                mContext.startActivity(intent);
            });
        }
    }
}
