package com.dnc.qrcodescanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.DecodeCallback;
import com.dnc.qrcodescanner.databinding.ActivityScanBinding;
import com.google.zxing.Result;


public class ScanActivity extends AppCompatActivity {

    private ActivityScanBinding binding;
    private CodeScanner codeScanner;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
        binding.resulttext.setText("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        codeScanner.releaseResources();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.scanback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanActivity.this.onBackPressed();
            }
        });

        codeScanner = new CodeScanner(this, binding.scannerView);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.resulttext.setText(result.getText());
                    }
                });
            }
        });

        binding.scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
                binding.resulttext.setText("");
            }
        });

        binding.scanActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(binding.resulttext.getText())) {
                    try {
                        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(binding.resulttext.getText().toString()));
                        startActivity(browse);
                    }catch (Exception exception){
                        Toast.makeText(ScanActivity.this, "This is not URL !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ScanActivity.this, "Empty !", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}