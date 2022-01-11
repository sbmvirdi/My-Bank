package cf.projectspro.bank.ui.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import cf.projectspro.bank.databinding.FragmentNotificationsBinding;
import cf.projectspro.bank.ui.adapters.TransactionAdapter;
import cf.projectspro.bank.ui.activities.Login;
import cf.projectspro.bank.ui.viewModels.NotificationFragmentViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class Notifications extends Fragment {

    View layout;
    RecyclerView rec;
    private FirebaseAuth mAuth;
    private String uid;
    private FragmentNotificationsBinding binding;
    public static final String TAG = Notifications.class.getSimpleName();


    public Notifications() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(LayoutInflater.from(getContext()),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(getActivity(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        } else {
            uid = mAuth.getCurrentUser().getUid();
        }

        NotificationFragmentViewModel viewModel = new ViewModelProvider(this).get(NotificationFragmentViewModel.class);


        binding.notificationRec.setHasFixedSize(true);
        DividerItemDecoration dv = new DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL);
        binding.notificationRec.addItemDecoration(dv);
        binding.notificationRec.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getNotificationListByUserUid(uid);
        viewModel.getNotification().observe(getViewLifecycleOwner(),notifications -> {
            if (notifications==null || notifications.isEmpty()){
                binding.notificationRec.setVisibility(View.GONE);
                binding.noNotification.setVisibility(View.VISIBLE);
            }else {
                binding.notificationRec.setAdapter(new TransactionAdapter(notifications, getContext()));
                binding.notificationRec.setVisibility(View.VISIBLE);
                binding.noNotification.setVisibility(View.GONE);
            }
        });

    }
}
