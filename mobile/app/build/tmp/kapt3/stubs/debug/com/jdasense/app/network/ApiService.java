package com.jdasense.app.network;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u001e\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u001e\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u00032\b\b\u0001\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u001a\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e0\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u001a\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u000e0\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u001e\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\u00032\b\b\u0001\u0010\u0014\u001a\u00020\u0015H\u00a7@\u00a2\u0006\u0002\u0010\u0016\u00a8\u0006\u0017"}, d2 = {"Lcom/jdasense/app/network/ApiService;", "", "createUser", "Lretrofit2/Response;", "Lcom/jdasense/app/network/User;", "request", "Lcom/jdasense/app/network/UserCreateRequest;", "(Lcom/jdasense/app/network/UserCreateRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteUser", "", "email", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAuditLogs", "", "Lcom/jdasense/app/network/AuditLog;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "listUsers", "predict", "Lcom/jdasense/app/network/PredictionResponse;", "audio", "Lokhttp3/MultipartBody$Part;", "(Lokhttp3/MultipartBody$Part;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface ApiService {
    
    @retrofit2.http.Multipart()
    @retrofit2.http.POST(value = "/predict")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object predict(@retrofit2.http.Part()
    @org.jetbrains.annotations.NotNull()
    okhttp3.MultipartBody.Part audio, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.jdasense.app.network.PredictionResponse>> $completion);
    
    @retrofit2.http.GET(value = "/users")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object listUsers(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.List<com.jdasense.app.network.User>>> $completion);
    
    @retrofit2.http.POST(value = "/users")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object createUser(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.jdasense.app.network.UserCreateRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.jdasense.app.network.User>> $completion);
    
    @retrofit2.http.DELETE(value = "/users/{email}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteUser(@retrofit2.http.Path(value = "email")
    @org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<kotlin.Unit>> $completion);
    
    @retrofit2.http.GET(value = "/audit")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAuditLogs(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<java.util.List<com.jdasense.app.network.AuditLog>>> $completion);
}