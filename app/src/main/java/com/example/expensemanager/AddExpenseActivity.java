package com.example.expensemanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.UUID;

public class AddExpenseActivity extends AppCompatActivity {
    private EditText amountEt, descriptionEt, dateEt;
    private Spinner categorySpinner;
    private Button submitBtn, selectReceiptBtn;
    private TextView receiptStatusTv;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String localSelectedPhotoUri = "no_image_uploaded";

    // Modern Android Studio API to open gallery picker cleanly
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        localSelectedPhotoUri = selectedImageUri.toString();
                        receiptStatusTv.setText("Receipt Image Attached Successfully!");
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        amountEt = findViewById(R.id.amountEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        dateEt = findViewById(R.id.dateEt);
        categorySpinner = findViewById(R.id.categorySpinner);
        submitBtn = findViewById(R.id.submitBtn);
        selectReceiptBtn = findViewById(R.id.selectReceiptBtn);
        receiptStatusTv = findViewById(R.id.receiptStatusTv);

        selectReceiptBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            galleryLauncher.launch(intent);
        });

        submitBtn.setOnClickListener(v -> saveExpenseToFirestore());
    }

    private void saveExpenseToFirestore() {
        String amountStr = amountEt.getText().toString().trim();
        String description = descriptionEt.getText().toString().trim();
        String date = dateEt.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if (amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String uniqueId = UUID.randomUUID().toString();
        String currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "test_id";
        String userEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : "test@employee.com";

        Expense newClaim = new Expense(
                uniqueId, currentUserId, userEmail, amount,
                date, category, description, localSelectedPhotoUri, "Pending", "manager"
        );

        db.collection("expenses").document(uniqueId).set(newClaim)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Claim Logged Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to submit entry", Toast.LENGTH_SHORT).show());
    }
}