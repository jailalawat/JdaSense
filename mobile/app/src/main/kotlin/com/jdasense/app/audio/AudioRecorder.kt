package com.jdasense.app.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AudioRecorder(private val outputFile: File) {

    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var recordingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        private const val TAG = "AudioRecorder"
        private const val SAMPLE_RATE = 8000 // Optimized for heart sounds
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
    }

    @SuppressLint("MissingPermission")
    fun start() {
        if (isRecording) return

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            BUFFER_SIZE
        )

        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            Log.e(TAG, "AudioRecord initialization failed")
            return
        }

        audioRecord?.startRecording()
        isRecording = true

        recordingJob = scope.launch {
            writeAudioDataToFile()
        }
    }

    fun stop() {
        isRecording = false
        recordingJob?.cancel()
        audioRecord?.apply {
            stop()
            release()
        }
        audioRecord = null
        
        // After stopping, we should wrap the raw PCM into a WAV header
        writeWavHeader(outputFile)
    }

    private fun writeAudioDataToFile() {
        val data = ByteArray(BUFFER_SIZE)
        val fileOutputStream = FileOutputStream(outputFile)

        try {
            // Leave space for WAV header (44 bytes)
            fileOutputStream.write(ByteArray(44))

            while (isRecording) {
                val read = audioRecord?.read(data, 0, BUFFER_SIZE) ?: 0
                if (read > 0) {
                    fileOutputStream.write(data, 0, read)
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error writing audio data", e)
        } finally {
            fileOutputStream.close()
        }
    }

    private fun writeWavHeader(file: File) {
        val fileSize = file.length()
        val dataSize = fileSize - 44
        val header = createWavHeader(dataSize)

        val randomAccessFile = java.io.RandomAccessFile(file, "rw")
        randomAccessFile.seek(0)
        randomAccessFile.write(header)
        randomAccessFile.close()
    }

    private fun createWavHeader(dataSize: Long): ByteArray {
        val totalSize = dataSize + 36
        val sampleRate = SAMPLE_RATE.toLong()
        val channels = 1
        val byteRate = sampleRate * channels * 2 // 16-bit = 2 bytes

        val header = ByteArray(44)
        header[0] = 'R'.toByte()
        header[1] = 'I'.toByte()
        header[2] = 'F'.toByte()
        header[3] = 'F'.toByte()
        header[4] = (totalSize and 0xff).toByte()
        header[5] = (totalSize shr 8 and 0xff).toByte()
        header[6] = (totalSize shr 16 and 0xff).toByte()
        header[7] = (totalSize shr 24 and 0xff).toByte()
        header[8] = 'W'.toByte()
        header[9] = 'A'.toByte()
        header[10] = 'V'.toByte()
        header[11] = 'E'.toByte()
        header[12] = 'f'.toByte()
        header[13] = 'm'.toByte()
        header[14] = 't'.toByte()
        header[15] = ' '.toByte()
        header[16] = 16 // Subchunk1Size
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // AudioFormat (PCM)
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = (sampleRate shr 8 and 0xff).toByte()
        header[26] = (sampleRate shr 16 and 0xff).toByte()
        header[27] = (sampleRate shr 24 and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        header[32] = (channels * 2).toByte() // BlockAlign
        header[33] = 0
        header[34] = 16 // BitsPerSample
        header[35] = 0
        header[36] = 'd'.toByte()
        header[37] = 'a'.toByte()
        header[38] = 't'.toByte()
        header[39] = 'a'.toByte()
        header[40] = (dataSize and 0xff).toByte()
        header[41] = (dataSize shr 8 and 0xff).toByte()
        header[42] = (dataSize shr 16 and 0xff).toByte()
        header[43] = (dataSize shr 24 and 0xff).toByte()

        return header
    }
}
