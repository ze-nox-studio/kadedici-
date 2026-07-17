package com.example.ui.components

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.util.Log
import kotlinx.coroutines.*
import kotlin.random.Random

object AmbientSoundGenerator {
    private var audioTrack: AudioTrack? = null
    private var isPlaying = false
    private var playScope: CoroutineScope? = null
    private var currentType: SoundType = SoundType.NONE

    enum class SoundType {
        NONE, WHITE_NOISE, OCEAN_WAVES, FOCUS_BINAURAL
    }

    fun start(type: SoundType) {
        if (type == SoundType.NONE) {
            stop()
            return
        }
        if (isPlaying && currentType == type) return
        stop()

        currentType = type
        isPlaying = true
        playScope = CoroutineScope(Dispatchers.Default + Job())

        val sampleRate = 44100
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        try {
            audioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM
            )
            audioTrack?.play()
        } catch (e: Exception) {
            Log.e("AmbientSound", "Error initializing AudioTrack: ${e.message}")
            isPlaying = false
            return
        }

        playScope?.launch {
            val shortBuffer = ShortArray(bufferSize)
            var lastValueLeft = 0.0
            var lastValueRight = 0.0
            var phase = 0.0

            while (isPlaying && isActive) {
                for (i in shortBuffer.indices step 2) {
                    when (type) {
                        SoundType.WHITE_NOISE -> {
                            // Raw random noise scaled to 16-bit PCM range
                            val left = (Random.nextFloat() * 2f - 1f) * 12000
                            val right = (Random.nextFloat() * 2f - 1f) * 12000
                            shortBuffer[i] = left.toInt().toShort()
                            if (i + 1 < shortBuffer.size) {
                                shortBuffer[i + 1] = right.toInt().toShort()
                            }
                        }
                        SoundType.OCEAN_WAVES -> {
                            // Brownian noise (integrating white noise for deep rumble)
                            val leftNoise = (Random.nextFloat() * 2f - 1f) * 1500
                            val rightNoise = (Random.nextFloat() * 2f - 1f) * 1500
                            
                            // Simple low-pass filter
                            lastValueLeft = (lastValueLeft * 0.985) + leftNoise
                            lastValueRight = (lastValueRight * 0.985) + rightNoise
                            
                            // Add a low frequency oscillator (LFO) to simulate wave rolling
                            val lfo = Math.sin(phase) * 0.4 + 0.6
                            phase += 0.0001
                            if (phase > 2 * Math.PI) phase -= 2 * Math.PI

                            shortBuffer[i] = (lastValueLeft * lfo).toInt().coerceIn(-32768, 32767).toShort()
                            if (i + 1 < shortBuffer.size) {
                                shortBuffer[i + 1] = (lastValueRight * lfo).toInt().coerceIn(-32768, 32767).toShort()
                            }
                        }
                        SoundType.FOCUS_BINAURAL -> {
                            // Play 150Hz in left ear, 154Hz in right ear -> 4Hz binaural (theta focus)
                            val frequencyLeft = 150.0
                            val frequencyRight = 154.0
                            val sampleIndex = (i / 2) + phase
                            
                            val angleLeft = 2.0 * Math.PI * frequencyLeft * (sampleIndex / sampleRate.toDouble())
                            val angleRight = 2.0 * Math.PI * frequencyRight * (sampleIndex / sampleRate.toDouble())
                            
                            val left = Math.sin(angleLeft) * 12000
                            val right = Math.sin(angleRight) * 12000
                            
                            shortBuffer[i] = left.toInt().toShort()
                            if (i + 1 < shortBuffer.size) {
                                shortBuffer[i + 1] = right.toInt().toShort()
                            }
                        }
                        else -> {
                            shortBuffer[i] = 0
                            if (i + 1 < shortBuffer.size) shortBuffer[i + 1] = 0
                        }
                    }
                }
                
                // Keep phase variable from exploding
                if (type == SoundType.FOCUS_BINAURAL) {
                    phase += shortBuffer.size / 2
                    if (phase > sampleRate * 100) phase = 0.0
                }

                try {
                    audioTrack?.write(shortBuffer, 0, shortBuffer.size)
                } catch (e: Exception) {
                    break
                }
            }
        }
    }

    fun stop() {
        isPlaying = false
        currentType = SoundType.NONE
        playScope?.cancel()
        playScope = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            // Ignored
        }
        audioTrack = null
    }
}
