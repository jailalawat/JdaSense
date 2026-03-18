package com.jdasense.app.security;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0007\b\u0007\u0018\u00002\u00020\u0001B\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0007\u001a\u00020\bJ\b\u0010\t\u001a\u0004\u0018\u00010\nJ\b\u0010\u000b\u001a\u0004\u0018\u00010\nJ\b\u0010\f\u001a\u0004\u0018\u00010\nJ\u0006\u0010\r\u001a\u00020\u000eJ\u000e\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0010\u001a\u00020\nJ\u000e\u0010\u0011\u001a\u00020\b2\u0006\u0010\u0012\u001a\u00020\nJ\u000e\u0010\u0013\u001a\u00020\b2\u0006\u0010\u0014\u001a\u00020\u000eR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/jdasense/app/security/TokenManager;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "sharedPreferences", "Landroid/content/SharedPreferences;", "clearToken", "", "getPin", "", "getRole", "getToken", "isBiometricEnabled", "", "savePin", "pin", "saveToken", "token", "setBiometricEnabled", "enabled", "app_debug"})
public final class TokenManager {
    @org.jetbrains.annotations.NotNull()
    private final android.content.SharedPreferences sharedPreferences = null;
    
    @javax.inject.Inject()
    public TokenManager(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    public final void saveToken(@org.jetbrains.annotations.NotNull()
    java.lang.String token) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getToken() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getRole() {
        return null;
    }
    
    public final void clearToken() {
    }
    
    public final boolean isBiometricEnabled() {
        return false;
    }
    
    public final void setBiometricEnabled(boolean enabled) {
    }
    
    public final void savePin(@org.jetbrains.annotations.NotNull()
    java.lang.String pin) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getPin() {
        return null;
    }
}