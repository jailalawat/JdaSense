package com.jdasense.app;

@dagger.hilt.android.AndroidEntryPoint()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0013\u001a\u00020\u0014H\u0002J\u0012\u0010\u0015\u001a\u00020\u00142\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u0014J\b\u0010\u0018\u001a\u00020\u0014H\u0002J\b\u0010\u0019\u001a\u00020\u0014H\u0002J\b\u0010\u001a\u001a\u00020\u0014H\u0002J\b\u0010\u001b\u001a\u00020\u0014H\u0002R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\t\u001a\u0010\u0012\f\u0012\n \f*\u0004\u0018\u00010\u000b0\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\r\u001a\u00020\u000e8\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012\u00a8\u0006\u001c"}, d2 = {"Lcom/jdasense/app/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "audioRecorder", "Lcom/jdasense/app/audio/AudioRecorder;", "binding", "Lcom/jdasense/app/databinding/ActivityMainBinding;", "isRecording", "", "requestPermissionLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "", "kotlin.jvm.PlatformType", "tokenManager", "Lcom/jdasense/app/security/TokenManager;", "getTokenManager", "()Lcom/jdasense/app/security/TokenManager;", "setTokenManager", "(Lcom/jdasense/app/security/TokenManager;)V", "checkPermissionsAndStart", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "setupRoleBasedUI", "startRecordingProcess", "startTimer", "stopRecording", "app_debug"})
public final class MainActivity extends androidx.appcompat.app.AppCompatActivity {
    @javax.inject.Inject()
    public com.jdasense.app.security.TokenManager tokenManager;
    private com.jdasense.app.databinding.ActivityMainBinding binding;
    @org.jetbrains.annotations.Nullable()
    private com.jdasense.app.audio.AudioRecorder audioRecorder;
    private boolean isRecording = false;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String> requestPermissionLauncher = null;
    
    public MainActivity() {
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
    
    private final void setupRoleBasedUI() {
    }
    
    private final void checkPermissionsAndStart() {
    }
    
    private final void startRecordingProcess() {
    }
    
    private final void startTimer() {
    }
    
    private final void stopRecording() {
    }
}