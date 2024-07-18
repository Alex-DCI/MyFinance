package org.dci.myfinance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class TransactionsHistoryActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    TextView currentBalance;
    List<Transaction> transactionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transactions_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.recyclerView);
        currentBalance = findViewById(R.id.currentBalance);
        findViewById(R.id.backImage).setOnClickListener(v -> this.finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setAdapter();
        setCurrentBalance();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tabLayout.getSelectedTabPosition()) {
                    case 0: tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.yellow));
                            break;
                    case 1: tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.red));
                            break;
                    case 2: tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.green));
                            break;
                }
                setAdapter();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        setAdapter();
        setCurrentBalance();
    }

    private List<Transaction> getCurrentList() {
        transactionsList = new ArrayList<>(FilesOperations.getInstance(this).getTransactions());
        switch (tabLayout.getSelectedTabPosition()) {
            case 0: break;
            case 1: transactionsList.removeIf(Transaction::isIncome);
                        break;
            case 2: transactionsList.removeIf(transaction -> !transaction.isIncome());
                        break;
        }
        return transactionsList;
    }

    public void setAdapter() {
        recyclerView.setAdapter(new TransactionsListAdapter(this, getCurrentList()));
    }

    public void editTransaction(int index) {
        Intent intent = new Intent(TransactionsHistoryActivity.this, EditTransactionActivity.class);
        intent.putExtra("transaction", getCurrentList().get(index));
        startActivity(intent);
    }

    private void setCurrentBalance() {
        double amount = 0;
        for (Transaction transaction : transactionsList) {
            if (transaction.isIncome()) {
                amount += transaction.getAmount();
            } else {
                amount -= transaction.getAmount();
            }
        }
        String balanceText = getResources().getString(R.string.balance) +
                Math.round(amount * 100.0) / 100.0 + getResources().getString(R.string.euro);
        currentBalance.setText(balanceText);
    }
}