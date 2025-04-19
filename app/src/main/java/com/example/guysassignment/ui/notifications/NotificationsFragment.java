package com.example.guysassignment.ui.notifications;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.guysassignment.SharedViewModel;
import com.example.guysassignment.databinding.FragmentNotificationsBinding;
import com.example.guysassignment.ui.notifications.NotificationsViewModel;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;
    private SharedViewModel sharedVM;
    private NotificationsViewModel dashVM;

    @Override
    public void onCreate(Bundle saved) {
        super.onCreate(saved);
        sharedVM = new ViewModelProvider(requireActivity())
                .get(SharedViewModel.class);
        dashVM = new ViewModelProvider(this)
                .get(NotificationsViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle saved) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        TextView tv = binding.textNotifications;

        // Observe the old default text, too:
        dashVM.getText().observe(getViewLifecycleOwner(), defaultText -> {
            // if no name has been set yet, show the default
            if (sharedVM.getName().getValue().isEmpty()) {
                tv.setText(defaultText);
            }
        });

        // Observe the three shared values and rebuild the Notifications text
        Observer<Object> rebuild = o -> {
            String name = sharedVM.getName().getValue();
            String family = sharedVM.getFamilyName().getValue();
            int score = sharedVM.getBestScore().getValue();

            if (!name.isEmpty() || !family.isEmpty() || score != 0) {
                String combined = "Nofications Fragment:\n" +
                        "Name: " + name + "\n"
                        + "Family: " + family + "\n"
                        + "Best score: " + score;
                tv.setText(combined);
            }
        };
        sharedVM.getName().observe(getViewLifecycleOwner(), rebuild);
        sharedVM.getFamilyName().observe(getViewLifecycleOwner(), rebuild);
        sharedVM.getBestScore().observe(getViewLifecycleOwner(), rebuild);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}