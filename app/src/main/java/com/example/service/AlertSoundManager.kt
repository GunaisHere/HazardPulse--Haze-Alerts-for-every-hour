package com.example.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import com.example.data.local.HazardType
import com.example.data.model.VoicePersona
import com.example.data.model.VoiceSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class AlertSoundManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("AlertSoundManager", "Error initializing TTS", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            applyPersonaSettings(VoiceSettings())
        }
    }

    /**
     * Applies Voice settings (pitch, speed, and optimal locale/voice model for Jarvis or Friday).
     */
    fun applyPersonaSettings(settings: VoiceSettings) {
        val engine = tts ?: return
        if (!isTtsReady) return

        try {
            engine.setPitch(settings.pitch)
            engine.setSpeechRate(settings.speed)

            val availableVoices = try {
                engine.voices
            } catch (e: Exception) {
                null
            }

            when (settings.persona) {
                VoicePersona.JARVIS -> {
                    // Look for British English voice (Paul Bettany style)
                    var matchedVoice: Voice? = null
                    if (availableVoices != null) {
                        matchedVoice = availableVoices.firstOrNull { voice ->
                            val name = voice.name.lowercase()
                            (voice.locale.country.equals("GB", ignoreCase = true) ||
                                    voice.locale.language.equals("en", ignoreCase = true) && name.contains("gb")) &&
                                    (name.contains("male") || name.contains("rjs") || name.contains("gbc"))
                        } ?: availableVoices.firstOrNull {
                            it.locale.country.equals("GB", ignoreCase = true)
                        }
                    }

                    if (matchedVoice != null) {
                        engine.voice = matchedVoice
                    } else {
                        engine.setLanguage(Locale.UK)
                    }
                }

                VoicePersona.FRIDAY -> {
                    // Look for Irish or British crisp female voice (Kerry Condon style)
                    var matchedVoice: Voice? = null
                    if (availableVoices != null) {
                        matchedVoice = availableVoices.firstOrNull { voice ->
                            val name = voice.name.lowercase()
                            (voice.locale.country.equals("IE", ignoreCase = true) ||
                                    voice.locale.country.equals("GB", ignoreCase = true)) &&
                                    (name.contains("female") || name.contains("gba") || name.contains("gbf"))
                        } ?: availableVoices.firstOrNull {
                            it.locale.country.equals("IE", ignoreCase = true) || it.locale.country.equals("GB", ignoreCase = true)
                        }
                    }

                    if (matchedVoice != null) {
                        engine.voice = matchedVoice
                    } else {
                        val irishLocale = Locale("en", "IE")
                        if (engine.isLanguageAvailable(irishLocale) >= TextToSpeech.LANG_AVAILABLE) {
                            engine.setLanguage(irishLocale)
                        } else {
                            engine.setLanguage(Locale.UK)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AlertSoundManager", "Error configuring TTS persona", e)
        }
    }

    /**
     * Plays the holographic Iron Man HUD acoustic activation cue, then speaks the tactical alert.
     */
    fun triggerAlertSoundAndVoice(
        hazardType: HazardType,
        customMessage: String? = null,
        voiceSettings: VoiceSettings = VoiceSettings(),
        forceBypassDnd: Boolean = false
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            if (voiceSettings.playHudChime) {
                playStarkHudChime()
            }

            vibrateAlert(hazardType)

            // Small natural breath pause between the HUD chime and AI vocal response
            if (voiceSettings.playHudChime) {
                delay(220)
            }

            val spokenText = customMessage ?: generateCinematicDialogue(hazardType, voiceSettings)
            speakOut(spokenText, voiceSettings)
        }
    }

    fun speakOut(text: String, voiceSettings: VoiceSettings = VoiceSettings()) {
        if (!isTtsReady || tts == null) {
            try {
                tts = TextToSpeech(context.applicationContext) { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        isTtsReady = true
                        applyPersonaSettings(voiceSettings)
                        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "HAZARD_ALERT_${System.currentTimeMillis()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("AlertSoundManager", "TTS Speak error", e)
            }
            return
        }

        applyPersonaSettings(voiceSettings)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "HAZARD_ALERT_${System.currentTimeMillis()}")
    }

    fun generateCinematicDialogue(
        hazardType: HazardType,
        voiceSettings: VoiceSettings,
        primaryMetric: String? = null
    ): String {
        val callsign = voiceSettings.userCallsign.ifBlank {
            voiceSettings.persona.defaultCallsign
        }

        return when (voiceSettings.persona) {
            VoicePersona.JARVIS -> when (hazardType) {
                HazardType.HAZE -> {
                    "Pardon the intrusion, $callsign. Environmental telemetry from IQAir indicates atmospheric particulates have surged past safe limits into the hazardous tier. I strongly advise sealing all exterior ventilation and initiating indoor filtration protocols."
                }
                HazardType.RAIN -> {
                    "Warning, $callsign. Weather radar indicates an intense torrential cloudburst over your coordinates. Localized flash flooding is imminent; please exercise extreme caution."
                }
                HazardType.EARTHQUAKE -> {
                    "Priority seismic alert, $callsign. Sub-surface sensors report a significant tectonic disturbance near your quadrant. I recommend bracing for potential secondary tremors immediately."
                }
                HazardType.TSUNAMI -> {
                    "Emergency protocol activated, $callsign. A maritime tsunami warning has been declared for this coastal quadrant. Immediate relocation to higher elevation is imperative."
                }
            }

            VoicePersona.FRIDAY -> when (hazardType) {
                HazardType.HAZE -> {
                    "$callsign, heads up on the telemetry. IQAir air monitors are showing a critical haze spike. Particulate density has broken safety thresholds. Keep windows sealed and avoid outdoor exertion."
                }
                HazardType.RAIN -> {
                    "$callsign, torrential squall incoming right now. Torrential downpour is critical. Watch out for rapid water accumulation on roadways."
                }
                HazardType.EARTHQUAKE -> {
                    "Seismic disturbance detected, $callsign! Major tectonic event nearby. Drop, cover, and hold on immediately."
                }
                HazardType.TSUNAMI -> {
                    "Priority one alert, $callsign: coastal tsunami hazard. We need to move inland and seek higher ground right now."
                }
            }
        }
    }

    /**
     * Synthesizes a futuristic Stark Industries HUD chime:
     * Dual-frequency ascending harmonic sweep (523Hz -> 1046Hz -> 1568Hz)
     * with warm exponential resonance decay.
     */
    fun playStarkHudChime() {
        try {
            val sampleRate = 44100
            val durationMs = 380
            val numSamples = (durationMs * sampleRate) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                // Frequency sweeps upward smoothly like a holographic boot chime
                val baseFreq = 523.25 + (t / (durationMs / 1000.0)) * 780.0
                val harmonic = baseFreq * 2.0
                val subHarmonic = baseFreq * 0.5

                // Dual exponential envelope (sparkling attack, warm chime decay)
                val decay = exp(-t * 8.5)
                val attack = if (t < 0.02) t / 0.02 else 1.0
                val envelope = attack * decay

                // Polyphonic synthesis
                val sample = (
                        0.55 * sin(2.0 * PI * baseFreq * t) +
                                0.30 * sin(2.0 * PI * harmonic * t) +
                                0.15 * sin(2.0 * PI * subHarmonic * t)
                        ) * Short.MAX_VALUE * envelope

                buffer[i] = sample.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
        } catch (e: Exception) {
            Log.e("AlertSoundManager", "Error playing HUD chime", e)
        }
    }

    private fun vibrateAlert(hazardType: HazardType) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            vibrator?.let {
                val pattern = when (hazardType) {
                    HazardType.HAZE -> longArrayOf(0, 180, 80, 180)
                    HazardType.RAIN -> longArrayOf(0, 200, 100, 200)
                    HazardType.EARTHQUAKE -> longArrayOf(0, 350, 100, 350)
                    HazardType.TSUNAMI -> longArrayOf(0, 500, 150, 500)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createWaveform(pattern, -1))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(pattern, -1)
                }
            }
        } catch (e: Exception) {
            Log.e("AlertSoundManager", "Vibrate error", e)
        }
    }

    fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
