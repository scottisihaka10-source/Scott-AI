package com.scottai.assistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SettingsVoice
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.util.Locale

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        setContent { ScottScreen(onListen = { listen() }, onSpeak = { say(it) }) }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            ttsReady = true
            // Uses the best installed Swahili voice; Android/Huawei voice availability varies.
            tts?.language = Locale("sw", "TZ")
        }
    }

    private fun say(text: String) { if (ttsReady) tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "scott_reply") }

    private fun listen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 702)
            return
        }
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            say("Huduma ya kutambua sauti haipatikani kwenye simu hii.")
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "sw-TZ")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "sw-TZ")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Sema amri yako kwa Scott")
        }
        try {
            startActivityForResult(intent, 701)
        } catch (_: Exception) { say("Imeshindikana kuanza kusikiliza.") }
    }

    @Deprecated("Android permission callback")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 702 && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) listen()
        else if (requestCode == 702) say("Ruhusu microphone ili niweze kusikiliza.")
    }

    @Deprecated("Android speech activity result")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 701 && resultCode == RESULT_OK) {
            val spoken = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull().orEmpty()
            handleCommand(spoken)
        }
    }

    private fun handleCommand(raw: String) {
        val q = raw.lowercase(Locale.ROOT)
        when {
            q.contains("whatsapp") -> openApp("com.whatsapp", "Sijaweza kufungua WhatsApp.")
            q.contains("youtube") -> openApp("com.google.android.youtube", "YouTube haijaonekana kwenye simu.")
            q.contains("settings") || q.contains("mipangilio") -> {
                say("Nafungua mipangilio.")
                startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
            }
            q.contains("piga simu") || q.contains("call") -> {
                val number = Regex("[+0-9][0-9 ]{5,}").find(raw)?.value?.replace(" ", "")
                if (number != null) {
                    say("Ninafungua sehemu ya kupiga simu.")
                    startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))
                } else say("Taja namba ya simu unayotaka kupiga.")
            }
            q.contains("sms") || q.contains("ujumbe") || q.contains("message") -> {
                say("Ninafungua sehemu ya kuandika ujumbe.")
                startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:")))
            }
            q.contains("habari") || q.contains("hello") || q.contains("hi") -> say("Habari Scott! Nipo hapa kukusaidia.")
            else -> say("Nimekusikia: $raw. Kwa sasa naweza kufungua WhatsApp, YouTube, Settings, na sehemu ya simu au ujumbe.")
        }
    }

    private fun openApp(packageName: String, error: String) {
        val launch = packageManager.getLaunchIntentForPackage(packageName)
        if (launch != null) { say("Sawa, nafungua programu."); startActivity(launch) } else say(error)
    }

    override fun onDestroy() {
        tts?.stop(); tts?.shutdown(); super.onDestroy()
    }
}

@Composable
private fun ScottScreen(onListen: () -> Unit, onSpeak: (String) -> Unit) {
    val bg = Brush.verticalGradient(listOf(Color(0xFF080B18), Color(0xFF11152B), Color(0xFF080B18)))
    var status by remember { mutableStateOf("Your personal assistant is ready") }
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF080B18)) {
        Column(
            modifier = Modifier.fillMaxSize().background(bg).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("SCOTT AI", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                Text("● READY", color = Color(0xFF69E6C1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(218.dp)) {
                    Box(Modifier.size(210.dp).background(Color(0x222D83FF), CircleShape))
                    Box(Modifier.size(164.dp).background(Color(0x333F5BFF), CircleShape))
                    Box(Modifier.size(112.dp).background(Brush.linearGradient(listOf(Color(0xFF54D8FF), Color(0xFF7657FF))), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.SettingsVoice, contentDescription = "Scott AI", tint = Color.White, modifier = Modifier.size(52.dp))
                    }
                }
                Text("Hello, Scott!", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Bold)
                Text(status, color = Color(0xFFAAB5D5), textAlign = TextAlign.Center, fontSize = 14.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { status = "Listening… speak now"; onListen() },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF655BFF)),
                    modifier = Modifier.size(78.dp)
                ) { Icon(Icons.Default.Mic, contentDescription = "Speak", modifier = Modifier.size(34.dp)) }
                Text("TAP TO SPEAK", color = Color(0xFFB8C4E8), fontSize = 11.sp, letterSpacing = 2.sp)
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF171D31)), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("Try saying", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text("“Fungua WhatsApp”  •  “Open YouTube”  •  “Fungua settings”", color = Color(0xFFAAB5D5), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
