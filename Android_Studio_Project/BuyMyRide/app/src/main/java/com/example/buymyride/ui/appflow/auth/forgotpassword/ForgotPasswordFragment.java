package com.example.buymyride.ui.appflow.auth.forgotpassword;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.buymyride.R;
import com.example.buymyride.databinding.FragmentForgotPasswordBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ForgotPasswordFragment extends Fragment {

    private MaterialToolbar topAppBar;
    private NavController navController;
    private ForgotPasswordViewModel viewModel;
    private FragmentForgotPasswordBinding binding;


    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        topAppBar = view.findViewById(R.id.topAppBar);
        viewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);

        setListeners();
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            String message = event.getContentIfNotHandled();
            if (message != null) {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.getIsEmailSent().observe(getViewLifecycleOwner(), event -> {
            if (event == null) return;
            Boolean isSent = event.getContentIfNotHandled();
            if (Boolean.TRUE.equals(isSent)) {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), "Сообщение для сброса пароля отправлено. Проверьте вашу почту.", Snackbar.LENGTH_LONG)
                        .show();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.buttonResetPassword.setEnabled(!isLoading);
        });

        viewModel.getNavigateEvent().observe(getViewLifecycleOwner(), event -> {
            ForgotPasswordNavigationDestination destination = event.getContentIfNotHandled();
            if (destination == null) return;

            switch (destination) {
                case GO_BACK -> navController.navigateUp();
            }
        });
    }

    private void setListeners() {
        topAppBar.setNavigationOnClickListener(v -> {
            viewModel.navigateBack();
        });

        binding.buttonResetPassword.setOnClickListener(v -> {
            String email = binding.inputEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), "Пожалуйста, введите email.", Snackbar.LENGTH_SHORT).show();
                return;
            }
            viewModel.resetPassword(email);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; //  avoid memory leak
    }
}