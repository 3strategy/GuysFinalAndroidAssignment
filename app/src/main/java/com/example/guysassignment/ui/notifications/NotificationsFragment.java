package com.example.guysassignment.ui.notifications;


import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.Locale;

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

        // Observe the three shared values and rebuild the dashboard text
        Observer<Integer> rebuild = scoreX10 -> {
            String name   = sharedVM.getName().getValue();
            String family = sharedVM.getFamilyName().getValue();
            // 2) unwrap the ×10 value and divide by 10.0 for one decimal
            double displayScore = (scoreX10 != null ? scoreX10 / 10.0 : 0.0);

            // only show when something’s been set
            if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(family) || displayScore != 0.0) {
                String combined = "Dashboard Fragment:\n" +
                        "Name: "   + (name   == null ? "" : name)   + "\n" +
                        "Family: " + (family == null ? "" : family) + "\n" +
                        String.format(Locale.getDefault(),
                                "Best score: %.1f", displayScore);
                binding.textNotifications.setText(combined);
            }
        };

        // 3) hook it to the new LiveData
        sharedVM.getBestScoreX10().observe(getViewLifecycleOwner(), rebuild);
        sharedVM.getName()      .observe(getViewLifecycleOwner(), s -> rebuild.onChanged(sharedVM.getBestScoreX10().getValue()));
        sharedVM.getFamilyName().observe(getViewLifecycleOwner(), s -> rebuild.onChanged(sharedVM.getBestScoreX10().getValue()));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}