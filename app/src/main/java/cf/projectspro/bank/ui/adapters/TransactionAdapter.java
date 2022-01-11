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
import cf.projectspro.bank.databinding.NotificationPostBinding;
import cf.projectspro.bank.ui.activities.TransactionDetail;
import cf.projectspro.bank.ui.modelClasses.Transaction;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.NotificationViewHolder> {

    private List<Transaction> mList;
    private Context mContext;

    public TransactionAdapter(List<Transaction> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationViewHolder(NotificationPostBinding.inflate(LayoutInflater.from(mContext),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder{
        private NotificationPostBinding binding;

        public NotificationViewHolder(@NonNull NotificationPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Transaction model) {


            binding.notifyAmount.setText(String.valueOf(model.amount));
            Picasso.get().load(model.src).into(binding.notifyImage);
            binding.notifyName.setText(model.getTo());
            binding.notifyTransId.setText(String.valueOf(model.gettrans_id()));
            if (model.isStatus()) {
                binding.notifyAmount.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                binding.INR.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            } else {
                binding.notifyAmount.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                binding.INR.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
            }

            if (model.isFrom()){
                binding.fromText.setText("From:");
            }


            binding.getRoot().setOnClickListener(view -> {

                Intent intent = new Intent(mContext, TransactionDetail.class);
                intent.putExtra("to", model.to);
                intent.putExtra("trans_id", String.valueOf(model.trans_id));
                intent.putExtra("amount", String.valueOf(model.amount));
                intent.putExtra("image", model.src);
                intent.putExtra("status", model.status);
                intent.putExtra("from_status", model.from);
                mContext.startActivity(intent);
            });

        }
    }
}
