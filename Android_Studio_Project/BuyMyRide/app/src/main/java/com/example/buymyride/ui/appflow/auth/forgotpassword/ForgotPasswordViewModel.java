package com.example.buymyride.ui.appflow.auth.forgotpassword;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.buymyride.data.repositories.AuthRepository;
import com.example.buymyride.ui.util.OneTimeEvent;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ForgotPasswordViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private MutableLiveData<OneTimeEvent<String>> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<OneTimeEvent<Boolean>> isEmailSent = new MutableLiveData<>();
    private final MutableLiveData<OneTimeEvent<ForgotPasswordNavigationDestination>> navigateEvent = new MutableLiveData<>();

    public LiveData<OneTimeEvent<String>> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<OneTimeEvent<Boolean>> getIsEmailSent() {
        return isEmailSent;
    }

    public LiveData<OneTimeEvent<ForgotPasswordNavigationDestination>> getNavigateEvent() {
        return navigateEvent;
    }


    @Inject
    public ForgotPasswordViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void navigateBack() {
        navigateEvent.setValue(new OneTimeEvent<>(ForgotPasswordNavigationDestination.GO_BACK));
    }

    public void resetPassword(String email) {
        isLoading.setValue(true);
        authRepository.resetPassword(email)
                .thenAccept(result -> {
                    isLoading.postValue(false);
                    isEmailSent.postValue(new OneTimeEvent<>(true));
                    navigateEvent.postValue(new OneTimeEvent<>(ForgotPasswordNavigationDestination.GO_BACK));
                })
                .exceptionally(throwable -> {
                    isLoading.postValue(false);
                    errorMessage.postValue(new OneTimeEvent<>(throwable.getMessage()));
                    return null;
                });
    }

}
