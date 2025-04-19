package com.example.guysassignment.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.guysassignment.SharedViewModel;
import com.example.guysassignment.databinding.FragmentHomeBinding;

import java.util.Locale;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private SharedViewModel sharedVM;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedVM = new ViewModelProvider(requireActivity())
                .get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Initialize fields from saved prefs
        binding.editName.setText(sharedVM.getName().getValue());
        binding.editFamily.setText(sharedVM.getFamilyName().getValue());

        // Observe bestScoreX10 and update the label
        sharedVM.getBestScoreX10().observe(getViewLifecycleOwner(), scoreX10 -> {
            double displayScore = scoreX10 / 10.0;
            binding.textBestScore.setText(
                    String.format(Locale.getDefault(),
                            "Best score: %.1f", displayScore)
            );
        });

        // Push user changes back to SharedViewModel
        binding.editName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                sharedVM.setName(s.toString());
            }
            @Override public void afterTextChanged(Editable e) {}
        });

        binding.editFamily.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                sharedVM.setFamilyName(s.toString());
            }
            @Override public void afterTextChanged(Editable e) {}
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}