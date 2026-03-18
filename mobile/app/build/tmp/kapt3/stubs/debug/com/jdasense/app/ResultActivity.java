package com.jdasense.app;

@dagger.hilt.android.AndroidEntryPoint()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0014J\u0010\u0010\u000f\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\u0010\u0010\u0012\u001a\u00020\f2\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u0018\u0010\u0015\u001a\u00020\f2\u0006\u0010\u0016\u001a\u00020\u00142\u0006\u0010\u0017\u001a\u00020\u0018H\u0002R\u001e\u0010\u0003\u001a\u00020\u00048\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/jdasense/app/ResultActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "apiService", "Lcom/jdasense/app/network/ApiService;", "getApiService", "()Lcom/jdasense/app/network/ApiService;", "setApiService", "(Lcom/jdasense/app/network/ApiService;)V", "binding", "Lcom/jdasense/app/databinding/ActivityResultBinding;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "performPrediction", "file", "Ljava/io/File;", "showError", "message", "", "showResult", "result", "isAnomaly", "", "app_debug"})
public final class ResultActivity extends androidx.appcompat.app.AppCompatActivity {
    @javax.inject.Inject()
    public com.jdasense.app.network.ApiService apiService;
    private com.jdasense.app.databinding.ActivityResultBinding binding;
    
    public ResultActivity() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.jdasense.app.network.ApiService getApiService() {
        return null;
    }
    
    public final void setApiService(@org.jetbrains.annotations.NotNull()
    com.jdasense.app.network.ApiService p0) {
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void performPrediction(java.io.File file) {
    }
    
    private final void showError(java.lang.String message) {
    }
    
    private final void showResult(java.lang.String result, boolean isAnomaly) {
    }
}