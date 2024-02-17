package com.example.extracttext.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.extracttext.R
import com.example.extracttext.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val scripts by lazy { resources.getStringArray(R.array.Scripts) }
    private var pos: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, scripts)
        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                pos = position
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.selected_script) + " " +
                            "" + scripts[position], Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.cameraBtn.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null) {
                Toast.makeText(this@MainActivity, "Opening Camera", Toast.LENGTH_SHORT).show()
                startActivityForResult(intent, 8) // request code can be any number
            } else {
                Snackbar.make(binding.root, "Something went wrong", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.editBtn.setOnClickListener {
            binding.extractedText.setText("")
            Toast.makeText(this@MainActivity, "Text Cleared", Toast.LENGTH_SHORT).show()
        }

        binding.copyBtn.setOnClickListener {
            val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("", binding.extractedText.text.toString())
            clipBoard.setPrimaryClip(clip)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 8 && resultCode == RESULT_OK) {
            val extras = data?.extras
            val bitmap = extras?.get("data") as Bitmap
            detectTextUsingML(bitmap, pos)
        }
    }

    private fun detectTextUsingML(bitmap: Bitmap, position: Int) {

        val image = InputImage.fromBitmap(bitmap, 0)
        var recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        when (position) {
            0 -> {
                // When using Devanagari script library
                recognizer =
                    TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())
            }

            1 -> {
                // When using Latin script library
                recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            }

            2 -> {
                // When using Chinese script library
                recognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())

            }

            3 -> {
                // When using Japanese script library
                recognizer = TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
            }

            4 -> {
                // When using Korean script library
                recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
            }
        }

        recognizer.process(image)
            .addOnSuccessListener {
                binding.extractedText.setText(it.text)
            }
    }
}