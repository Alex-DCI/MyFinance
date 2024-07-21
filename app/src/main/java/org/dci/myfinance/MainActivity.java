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

import java.time.LocalTime;
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
        int hour = LocalTime.now().getHour();
        String greeting;
        if (hour > 21 || hour < 6) {
            greeting = "Good night";
        } else if (hour < 12) {
            greeting = "Good morning";
        } else if (hour < 18) {
            greeting = "Good afternoon";
        } else {
            greeting = "Good evening";
        }
        String name = FilesOperations.getInstance(this).getProfile().getName();
        if (name != null) {
            greeting = greeting + ", " + name;
        }
        greeting += '!';
        greetingsText.setText(greeting);
    }

    private void setAmountValue() {
        List<Transaction> transactionsList = FilesOperations.getInstance(this).getTransactions();
        double amount = 0;
        for (Transaction transaction : transactionsList) {
            amount += transaction.isIncome() ? transaction.getAmount() : -transaction.getAmount();
        }
        String amountString = Math.round(amount * 100.0) / 100.0 + getResources().getString(R.string.euro);
        amountTextView.setText(amountString);
    }
}