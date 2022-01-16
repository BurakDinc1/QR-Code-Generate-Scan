package com.dnc.qrcodescanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.dnc.qrcodescanner.databinding.ActivityGenerateBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class GenerateActivity extends AppCompatActivity {

    private ActivityGenerateBinding binding;
    private static int PERMISSION_STORAGE_CODE = 3;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(GenerateActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission required !")
                    .setMessage("This permission is required to save images on your device.")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(GenerateActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
                        }
                    }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setIcon(R.drawable.ic_folder).create().show();
        } else {
            ActivityCompat.requestPermissions(GenerateActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGenerateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.generateback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenerateActivity.this.onBackPressed();
            }
        });

        binding.generateClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.generateImage.setBackgroundResource(R.color.beyaz);
                binding.generateImage.setImageResource(R.color.beyaz);
                binding.generateText.setText("");
                binding.visibleItem.setVisibility(View.GONE);
            }
        });

        binding.generateSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getApplicationContext()),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    BitmapDrawable draw = (BitmapDrawable) binding.generateImage.getDrawable();
                    Bitmap bitmap = draw.getBitmap();

                    File sdCard = Environment.getExternalStorageDirectory();
                    File dir = new File(sdCard.getAbsolutePath() + "/QRCode");
                    //noinspection ResultOfMethodCallIgnored
                    dir.mkdirs();
                    String fileName = System.currentTimeMillis() + ".jpg";
                    File outFile = new File(dir, fileName);
                    FileOutputStream outStream = null;
                    try {
                        outStream = new FileOutputStream(outFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        outStream.flush();
                        outStream.close();

                        Toast.makeText(GenerateActivity.this, "The photo has been saved to the gallery.", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        intent.setData(Uri.fromFile(outFile));
                        GenerateActivity.this.sendBroadcast(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                } else {
                    //dosya izni runtime permission
                    requestStoragePermission();
                }
            }
        });

        binding.generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(binding.generateText.getText())) {
                    Toast.makeText(GenerateActivity.this, "Please enter a value !", Toast.LENGTH_SHORT).show();
                } else {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                    try {
                        BitMatrix bitMatrix = multiFormatWriter.encode(binding.generateText.getText().toString(), BarcodeFormat.QR_CODE, 250, 250);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        binding.generateImage.setImageBitmap(bitmap);
                        binding.visibleItem.setVisibility(View.VISIBLE);
                        Toast.makeText(GenerateActivity.this, "Successful !", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(GenerateActivity.this, "Error !", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}