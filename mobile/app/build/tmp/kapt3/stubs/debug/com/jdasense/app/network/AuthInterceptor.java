package com.jdasense.app.network;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/jdasense/app/network/AuthInterceptor;", "Lokhttp3/Interceptor;", "tokenManager", "Lcom/jdasense/app/security/TokenManager;", "(Lcom/jdasense/app/security/TokenManager;)V", "API_KEY", "", "intercept", "Lokhttp3/Response;", "chain", "Lokhttp3/Interceptor$Chain;", "app_debug"})
public final class AuthInterceptor implements okhttp3.Interceptor {
    @org.jetbrains.annotations.NotNull()
    private final com.jdasense.app.security.TokenManager tokenManager = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String API_KEY = "HxuBPWNWqxaZO6MPmwGYB93UnIqhk0W34DWlJcWY";
    
    @javax.inject.Inject()
    public AuthInterceptor(@org.jetbrains.annotations.NotNull()
    com.jdasense.app.security.TokenManager tokenManager) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public okhttp3.Response intercept(@org.jetbrains.annotations.NotNull()
    okhttp3.Interceptor.Chain chain) {
        return null;
    }
}