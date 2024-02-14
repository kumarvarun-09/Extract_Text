package com.example.extracttext.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.extracttext.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.cameraBtn.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(intent.resolveActivity(packageManager) != null){
                startActivityForResult(intent, 8) // request code can be any number
            }
            else{
                Snackbar.make(binding.root, "Something went wrong", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.editBtn.setOnClickListener {
            binding.extractedText.setText("")
        }

        binding.copyBtn.setOnClickListener {
            val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("", binding.extractedText.text.toString())
            clipBoard.setPrimaryClip(clip)
            Toast.makeText(this@MainActivity, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 8 && resultCode == RESULT_OK)
        {
            val extras = data?.extras
            val bitmap = extras?.get("data") as Bitmap
            detectTextUsingML(bitmap)
        }
    }

    private fun detectTextUsingML(bitmap: Bitmap){
        // When using Latin script library
        val recognizerLatin = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        // When using Devanagari script library
        val recognizerDevanagari = TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())

        val image = InputImage.fromBitmap(bitmap, 0)

        recognizerLatin.process(image)
            .addOnSuccessListener {
                binding.extractedText.setText(it.text)
            }
//        recognizerDevanagari.process(image)
//            .addOnSuccessListener {
//                binding.extractedText.editText!!.setText(it.text)
//            }
    }
}