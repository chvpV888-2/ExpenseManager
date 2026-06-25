package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button logoutBtn;
    private TextView totalSpentTv;
    private Button addExpenseBtn;
    private ListView expenseListView;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private List<Expense> expenseList = new ArrayList<>();
    private ArrayAdapter<Expense> adapter;
    private String currentUserRole = "employee"; // Standard safe fallback role

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect Firebase instances
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(MainActivity.this, "Logged Out!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        // Connect UI elements
        totalSpentTv = findViewById(R.id.totalSpentTv);
        addExpenseBtn = findViewById(R.id.addExpenseBtn);
        expenseListView = findViewById(R.id.expenseListView);

        // Open submission screen on click
        addExpenseBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddExpenseActivity.class)));

        setupListAdapter();
        fetchUserRoleAndData();
    }

    private void fetchUserRoleAndData() {
        if (mAuth.getCurrentUser() == null) {
            loadExpensesFromFirestore();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        // Fetch User profile metadata flag to parse operational views
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("role")) {
                        currentUserRole = documentSnapshot.getString("role");
                    }
                    loadExpensesFromFirestore();
                })
                .addOnFailureListener(e -> loadExpensesFromFirestore());
    }

    private void loadExpensesFromFirestore() {
        db.collection("expenses").addSnapshotListener((value, error) -> {
            if (value == null) return;

            expenseList.clear();
            double globalRunningSum = 0;

            for (DocumentSnapshot doc : value.getDocuments()) {
                Expense expense = doc.toObject(Expense.class);
                if (expense == null) continue;

                // 1. Calculate Reports Logic (Sum up all Paid items)
                if ("Paid".equals(expense.getStatus())) {
                    globalRunningSum += expense.getAmount();
                }

                // 2. Filter workflow items strictly matching user authorization level
                if ("manager".equals(currentUserRole) && "manager".equals(expense.getCurrentApprover())) {
                    expenseList.add(expense);
                } else if ("finance".equals(currentUserRole) && "finance".equals(expense.getCurrentApprover())) {
                    expenseList.add(expense);
                } else if ("employee".equals(currentUserRole)) {
                    // Employees can only track their own structural entries
                    if (mAuth.getCurrentUser() != null && expense.getEmployeeUid().equals(mAuth.getCurrentUser().getUid())) {
                        expenseList.add(expense);
                    }
                }
            }

            // Update simple analytical dashboard reporting component text
            totalSpentTv.setText(String.format("$%.2f", globalRunningSum));
            adapter.notifyDataSetChanged();
        });
    }

    private void setupListAdapter() {
        adapter = new ArrayAdapter<Expense>(this, R.layout.item_expense, expenseList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item_expense, parent, false);
                }

                Expense current = expenseList.get(position);

                TextView empTv = convertView.findViewById(R.id.itemEmployee);
                TextView detailsTv = convertView.findViewById(R.id.itemDetails);
                TextView statusTv = convertView.findViewById(R.id.itemStatus);
                View actions = convertView.findViewById(R.id.actionLayout);
                Button approve = convertView.findViewById(R.id.approveBtn);
                Button reject = convertView.findViewById(R.id.rejectBtn);

                empTv.setText(current.getEmployeeName());
                detailsTv.setText(String.format("$%.2f - %s (%s)", current.getAmount(), current.getCategory(), current.getDate()));
                statusTv.setText("Status: " + current.getStatus());

                // Hide direct manipulation layout links if observer profile is basic employee
                if ("employee".equals(currentUserRole)) {
                    actions.setVisibility(View.GONE);
                } else {
                    actions.setVisibility(View.VISIBLE);

                    approve.setOnClickListener(v -> processWorkflow(current, true));
                    reject.setOnClickListener(v -> processWorkflow(current, false));
                }

                return convertView;
            }
        };
        expenseListView.setAdapter(adapter);
    }

    private void processWorkflow(Expense target, boolean isApproved) {
        if (!isApproved) {
            db.collection("expenses").document(target.getExpenseId())
                    .update("status", "Rejected", "currentApprover", "completed");
            return;
        }

        // Direct Sequential Moving Steps: Manager -> Finance -> Paid State
        if ("manager".equals(currentUserRole)) {
            db.collection("expenses").document(target.getExpenseId())
                    .update("status", "Approved", "currentApprover", "finance");
            Toast.makeText(MainActivity.this, "Approved! Sent to Finance Team.", Toast.LENGTH_SHORT).show();
        } else if ("finance".equals(currentUserRole)) {
            db.collection("expenses").document(target.getExpenseId())
                    .update("status", "Paid", "currentApprover", "completed");
            Toast.makeText(MainActivity.this, "Expense Paid Out!", Toast.LENGTH_SHORT).show();
        }
    }
}