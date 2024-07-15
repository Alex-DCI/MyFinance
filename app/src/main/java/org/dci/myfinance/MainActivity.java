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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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

        TextView greetingsText = findViewById(R.id.greetingsText);
        TextView amountTextView = findViewById(R.id.amountTextView);
        ImageView addIncomeImage = findViewById(R.id.addCircleImage);
        ImageView addExpenseImage = findViewById(R.id.removeCircleImage);
        Button transactionsHistoryButton = findViewById(R.id.transactionsHistoryButton);
        Button categoriesManagemenButton = findViewById(R.id.categoriesManagementButton);
        Button profilesManagementButton = findViewById(R.id.profileManagementButton);
        Button supportButton = findViewById(R.id.supportButton);

        greetingsText.setText(R.string.welcome);

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

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(new Transaction(50, "Very very long category name, so that it doesn't fit one line", LocalDateTime.now(), "Test Description", false));
        transactionList.add(new Transaction(100.1, "Test Income", LocalDateTime.now(),"Let's see...", true));

        FilesOperations.getInstance().setTransactions(this, transactionList);
    }
}