package com.jdasense.app;

@dagger.hilt.android.AndroidEntryPoint()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u000b\u001a\u00020\fH\u0002J\u0012\u0010\r\u001a\u00020\f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0014J\b\u0010\u0010\u001a\u00020\fH\u0002J\b\u0010\u0011\u001a\u00020\fH\u0002J\u0010\u0010\u0012\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\u0014H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0005\u001a\u00020\u00068\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\n\u00a8\u0006\u0015"}, d2 = {"Lcom/jdasense/app/QuickAuthActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/jdasense/app/databinding/ActivityPinBinding;", "tokenManager", "Lcom/jdasense/app/security/TokenManager;", "getTokenManager", "()Lcom/jdasense/app/security/TokenManager;", "setTokenManager", "(Lcom/jdasense/app/security/TokenManager;)V", "navigateToMain", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "setupUI", "showBiometricPrompt", "verifyPin", "enteredPin", "", "app_debug"})
public final class QuickAuthActivity extends androidx.appcompat.app.AppCompatActivity {
    @javax.inject.Inject()
    public com.jdasense.app.security.TokenManager tokenManager;
    private com.jdasense.app.databinding.ActivityPinBinding binding;
    
    public QuickAuthActivity() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.jdasense.app.security.TokenManager getTokenManager() {
        return null;
    }
    
    public final void setTokenManager(@org.jetbrains.annotations.NotNull()
    com.jdasense.app.security.TokenManager p0) {
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupUI() {
    }
    
    private final void verifyPin(java.lang.String enteredPin) {
    }
    
    private final void showBiometricPrompt() {
    }
    
    private final void navigateToMain() {
    }
}