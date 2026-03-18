package com.jdasense.app.audio;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000l\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u0017\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\t\n\u0002\b\u0007\u0018\u0000 *2\u00020\u0001:\u0001*B#\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0014\b\u0002\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u00a2\u0006\u0002\u0010\bJ\u0018\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 H\u0002J\u0010\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020$H\u0002J\b\u0010%\u001a\u00020\u0007H\u0007J\u0006\u0010&\u001a\u00020\u0007J\b\u0010\'\u001a\u00020\u0007H\u0002J\u0010\u0010(\u001a\u00020\u00072\u0006\u0010)\u001a\u00020\u0003H\u0002R\u000e\u0010\t\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u0016X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0017\u001a\u0004\u0018\u00010\u0018X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u001aX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006+"}, d2 = {"Lcom/jdasense/app/audio/AudioRecorder;", "", "outputFile", "Ljava/io/File;", "onAmplitudeUpdate", "Lkotlin/Function1;", "", "", "(Ljava/io/File;Lkotlin/jvm/functions/Function1;)V", "alphaHighPass", "alphaLowPass", "audioRecord", "Landroid/media/AudioRecord;", "echoCanceler", "Landroid/media/audiofx/AcousticEchoCanceler;", "gainControl", "Landroid/media/audiofx/AutomaticGainControl;", "isRecording", "", "lastHighPassValue", "lastLowPassValue", "noiseSuppressor", "Landroid/media/audiofx/NoiseSuppressor;", "recordingJob", "Lkotlinx/coroutines/Job;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "calculateRMS", "", "data", "", "size", "", "createWavHeader", "", "dataSize", "", "start", "stop", "writeAudioDataToFile", "writeWavHeader", "file", "Companion", "app_debug"})
public final class AudioRecorder {
    @org.jetbrains.annotations.NotNull()
    private final java.io.File outputFile = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function1<java.lang.Float, kotlin.Unit> onAmplitudeUpdate = null;
    @org.jetbrains.annotations.Nullable()
    private android.media.AudioRecord audioRecord;
    @org.jetbrains.annotations.Nullable()
    private android.media.audiofx.NoiseSuppressor noiseSuppressor;
    @org.jetbrains.annotations.Nullable()
    private android.media.audiofx.AcousticEchoCanceler echoCanceler;
    @org.jetbrains.annotations.Nullable()
    private android.media.audiofx.AutomaticGainControl gainControl;
    private boolean isRecording = false;
    private float lastLowPassValue = 0.0F;
    private float lastHighPassValue = 0.0F;
    private final float alphaLowPass = 0.3F;
    private final float alphaHighPass = 0.9F;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job recordingJob;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "AudioRecorder";
    private static final int SAMPLE_RATE = 8000;
    private static final int CHANNEL_CONFIG = android.media.AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = android.media.AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = 0;
    private static final int NOISE_THRESHOLD = 500;
    @org.jetbrains.annotations.NotNull()
    public static final com.jdasense.app.audio.AudioRecorder.Companion Companion = null;
    
    public AudioRecorder(@org.jetbrains.annotations.NotNull()
    java.io.File outputFile, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Float, kotlin.Unit> onAmplitudeUpdate) {
        super();
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final void start() {
    }
    
    public final void stop() {
    }
    
    private final void writeAudioDataToFile() {
    }
    
    private final double calculateRMS(short[] data, int size) {
        return 0.0;
    }
    
    private final void writeWavHeader(java.io.File file) {
    }
    
    private final byte[] createWavHeader(long dataSize) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/jdasense/app/audio/AudioRecorder$Companion;", "", "()V", "AUDIO_FORMAT", "", "BUFFER_SIZE", "CHANNEL_CONFIG", "NOISE_THRESHOLD", "SAMPLE_RATE", "TAG", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}