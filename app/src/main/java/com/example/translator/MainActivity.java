package com.example.translator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText enteredText, translatedText;
    Button translate;
    boolean isDownloaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enteredText = findViewById(R.id.editText);
        translatedText = findViewById(R.id.editText2);
        translate = findViewById(R.id.button);
        // Create an English-Hindi translator:
        FirebaseTranslatorOptions options =
                new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
                        .setTargetLanguage(FirebaseTranslateLanguage.HI)
                        .build();
        final FirebaseTranslator englishhindiTranslator =
                FirebaseNaturalLanguage.getInstance().getTranslator(options);


          //sure the required translation model has been downloaded to the device. Don't call translate() until you know the model is available.
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                //.requireDeviceIdle()//internet Data on
                .requireWifi()
                .build();

        englishhindiTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                // Model downloaded successfully. Okay to start translating.
                                // (Set a flag, unhide the translation UI, etc.)
                                isDownloaded = true;
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be downloaded or other internal error.
                                // ...
                                isDownloaded = false;
                            }
                        });

        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDownloaded) {
                    englishhindiTranslator.translate(enteredText.getText().toString())
                            .addOnSuccessListener(
                                    new OnSuccessListener<String>() {
                                        @Override
                                        public void onSuccess(@NonNull String text) {

                                            translatedText.setText(text);
                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error.

                                        }
                                    });
                } else {
                    Toast.makeText(MainActivity.this, "model is not downloaded", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
