package com.futureai.editor

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Transformer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FutureEditorScreen() }
    }

    @Composable
    private fun FutureEditorScreen() {
        var selectedVideo by remember { mutableStateOf<Uri?>(null) }
        var script by remember { mutableStateOf("") }
        var status by remember { mutableStateOf("প্রস্তুত — একটি ভিডিও নির্বাচন করুন") }
        var ratio by remember { mutableStateOf("9:16") }
        val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedVideo = uri
            status = if (uri != null) "ভিডিও নির্বাচন হয়েছে" else "কোনো ভিডিও নির্বাচন করা হয়নি"
        }

        MaterialTheme(colorScheme = darkColorScheme(
            primary = Color(0xFF8B80F9),
            background = Color(0xFF10111A),
            surface = Color(0xFF1B1D2A)
        )) {
            Column(
                Modifier.fillMaxSize().background(Color(0xFF10111A))
                    .verticalScroll(rememberScrollState()).padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("FUTURE AI EDITOR", color = Color(0xFFB8B1FF), fontSize = 13.sp)
                Text("তোমার ভিডিও, তোমার স্টাইল", color = Color.White, fontSize = 25.sp)
                Text("মোবাইল-ফ্রেন্ডলি প্রাথমিক ভিডিও এডিটর", color = Color.LightGray)
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1D2A))) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("১. ভিডিও নির্বাচন", color = Color.White)
                        Button(onClick = { picker.launch("video/*") }) { Text("গ্যালারি থেকে ভিডিও নাও") }
                        Text(if (selectedVideo == null) "এখনও ভিডিও নেওয়া হয়নি" else "ভিডিও প্রস্তুত", color = Color.LightGray)
                    }
                }
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1D2A))) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("২. স্ক্রিপ্ট / দৃশ্য পরিকল্পনা", color = Color.White)
                        OutlinedTextField(
                            value = script, onValueChange = { script = it },
                            modifier = Modifier.fillMaxWidth(), minLines = 4,
                            placeholder = { Text("এখানে বাংলা বা ইংরেজি স্ক্রিপ্ট লিখো…") }
                        )
                        Button(onClick = {
                            val scenes = script.split(Regex("[.!?।\\n]+"))
                                .map { it.trim() }.filter { it.isNotEmpty() }
                            status = if (scenes.isEmpty()) "আগে স্ক্রিপ্ট লিখো" else
                                "দৃশ্য পরিকল্পনা: ${scenes.size}টি দৃশ্য\n" +
                                scenes.mapIndexed { i, s -> "${i + 1}. $s" }.joinToString("\n")
                        }) { Text("দৃশ্য পরিকল্পনা তৈরি") }
                    }
                }
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1D2A))) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("৩. ভিডিও অনুপাত", color = Color.White)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("9:16", "16:9", "1:1").forEach {
                                FilterChip(selected = ratio == it, onClick = { ratio = it }, label = { Text(it) })
                            }
                        }
                        Text("নির্বাচিত: $ratio (এই প্রাথমিক সংস্করণে রপ্তানিতে অনুপাত প্রয়োগ হয় না)", color = Color.LightGray, fontSize = 12.sp)
                    }
                }
                Button(
                    onClick = { exportVideo(selectedVideo) { status = it } },
                    enabled = selectedVideo != null,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("MP4 ভিডিও এক্সপোর্ট") }
                Text(status, color = Color(0xFFD8D5FF), fontSize = 13.sp)
                Text(
                    "নোট: এটি প্রাথমিক ভিত্তি। AI voice, AI scene generation, music mixing, subtitle timing, filters ও transitions এখনো যুক্ত নয়।",
                    color = Color.Gray, fontSize = 12.sp
                )
            }
        }
    }

    private fun exportVideo(uri: Uri?, onStatus: (String) -> Unit) {
        if (uri == null) return
        try {
            val name = "FutureAI_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.mp4"
            val values = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, name)
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/FutureAIEditor")
            }
            val output = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw IllegalStateException("আউটপুট ফাইল তৈরি করা যায়নি")
            val pfd = contentResolver.openFileDescriptor(output, "w")
                ?: throw IllegalStateException("ফাইল খোলা যায়নি")
            val edited = EditedMediaItem.Builder(MediaItem.fromUri(uri)).build()
            val transformer = Transformer.Builder(this)
                .addListener(object : Transformer.Listener {
                    override fun onCompleted(composition: Composition, exportResult: androidx.media3.transformer.ExportResult) {
                        pfd.close()
                        onStatus("এক্সপোর্ট সম্পন্ন: Movies/FutureAIEditor/$name")
                    }
                    override fun onError(
                        composition: Composition,
                        exportResult: androidx.media3.transformer.ExportResult,
                        exportException: androidx.media3.transformer.ExportException
                    ) {
                        try { pfd.close() } catch (_: Exception) {}
                        onStatus("এক্সপোর্ট ব্যর্থ: ${exportException.message}")
                    }
                })
                .build()
            transformer.start(edited, pfd.fileDescriptor)
            onStatus("ভিডিও এক্সপোর্ট হচ্ছে…")
        } catch (e: Exception) {
            onStatus("সমস্যা: ${e.message}")
        }
    }
}
