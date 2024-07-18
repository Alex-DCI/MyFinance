package org.dci.myfinance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView amountTextView;
    TextView greetingsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        greetingsText = findViewById(R.id.greetingsText);
        amountTextView = findViewById(R.id.amountTextView);
        ImageView addIncomeImage = findViewById(R.id.addCircleImage);
        ImageView addExpenseImage = findViewById(R.id.removeCircleImage);
        Button transactionsHistoryButton = findViewById(R.id.transactionsHistoryButton);
        Button categoriesManagemenButton = findViewById(R.id.categoriesManagementButton);
        Button profilesManagementButton = findViewById(R.id.profileManagementButton);
        Button supportButton = findViewById(R.id.supportButton);

        setGreeting();
        setAmountValue();

        categoriesManagemenButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CategoriesManagementActivity.class);
            startActivity(intent);
        });

        transactionsHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TransactionsHistoryActivity.class);
            startActivity(intent);
        });

        addExpenseImage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransaction.class);
            intent.putExtra("isIncome", "false");
            startActivity(intent);
        });

        addIncomeImage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransaction.class);
            intent.putExtra("isIncome", "true");
            startActivity(intent);
        });

        profilesManagementButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileManagementActivity.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setAmountValue();
        setGreeting();
    }

    private void setGreeting() {

    }

    private void setAmountValue() {
        List<Transaction> transactionsList = FilesOperations.getInstance(this).getTransactions();
        double amount = 0;
        for (Transaction transaction : transactionsList) {
            amount += transaction.isIncome() ? transaction.getAmount() : -transaction.getAmount();
        }
        String amountString = getResources().getString(R.string.balance) +
                Math.round(amount * 100.0) / 100.0 + getResources().getString(R.string.euro);
        amountTextView.setText(amountString);
    }
}