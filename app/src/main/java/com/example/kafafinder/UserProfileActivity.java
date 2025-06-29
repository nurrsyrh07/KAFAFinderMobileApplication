package com.example.kafafinder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import de.hdodenhof.circleimageview.CircleImageView;



import java.io.IOException;

public class UserProfileActivity extends AppCompatActivity {

    ImageView profileImage;
    Button btnSelectImage, btnLogout;
    TextView tvUserName, tvUserEmail;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    ActivityResultLauncher<Intent> imagePickerLauncher;
    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // View IDs
        profileImage = findViewById(R.id.profileImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnLogout = findViewById(R.id.btnLogout);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }

        // Dapatkan nama dari Intent
        String nameFromIntent = getIntent().getStringExtra("full_name");
        if (nameFromIntent != null && !nameFromIntent.isEmpty()) {
            tvUserName.setText(nameFromIntent);
        } else {
            tvUserName.setText("No Name Found");
        }

        // Dapatkan email dari Firebase Auth
        String email = firebaseUser.getEmail();
        tvUserEmail.setText(email);

        // Select Image dari galeri
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            profileImage.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(UserProfileActivity.this, SignInActivity.class));
            finish();
        });
    }
}
