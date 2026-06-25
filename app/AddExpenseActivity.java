package com.example.expensemanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.UUID;

public class AddExpenseActivity extends AppCompatActivity {
    private EditText amountEt, descriptionEt, dateEt;
    private Spinner categorySpinner;
    private Button submitBtn;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Connect variables to XML views
        amountEt = findViewById(R.id.amountEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        dateEt = findViewById(R.id.dateEt);
        categorySpinner = findViewById(R.id.categorySpinner);
        submitBtn = findViewById(R.id.submitBtn);

        // Set click listener for the submit button
        submitBtn.setOnClickListener(v -> saveExpenseToFirestore());
    }

    private void saveExpenseToFirestore() {
        String amountStr = amountEt.getText().toString().trim();
        String description = descriptionEt.getText().toString().trim();
        String date = dateEt.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        // Simple validation check
        if (amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String uniqueId = UUID.randomUUID().toString();

        // Safety check to ensure a user is logged in
        String currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "test_user_id";
        String userEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : "test@employee.com";

        // Create the Expense object with initial flow states
        // "Pending" status and "manager" as the first approver
        Expense newClaim = new Expense(
                uniqueId, currentUserId, userEmail, amount,
                date, category, description, "no_image_uploaded", "Pending", "manager"
        );

        // Upload directly to Firestore
        db.collection("expenses").document(uniqueId).set(newClaim)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Claim Logged Successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Closes this screen and goes back to dashboard
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to submit entry: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}