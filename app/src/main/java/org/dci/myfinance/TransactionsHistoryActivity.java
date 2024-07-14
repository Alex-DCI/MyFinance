package org.dci.myfinance;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransactionsHistoryActivity extends AppCompatActivity {
    private List<Transaction> transactionList;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private FilesOperations filesOperations;
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
        TextView currentBalance = findViewById(R.id.currentBalance);
        filesOperations = FilesOperations.getInstance();
        transactionList = filesOperations.getTransactions(this);

        double amount = 0;

        for (Transaction transaction : transactionList) {
            amount += transaction.getAmount();
        }

        amount = Math.round(amount * 100.0) / 100.0;
        String balanceText = getResources().getString(R.string.balance) + (amount == Math.floor(amount) ? "" + (int) amount : amount) +
                getResources().getString(R.string.euro);
        currentBalance.setText(balanceText);

        findViewById(R.id.backImage).setOnClickListener(v -> this.finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setAdapter();

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
        }
        );

    }

    private List<Transaction> getCurrentList() {
        List<Transaction> currentList = new ArrayList<>();
        switch (tabLayout.getSelectedTabPosition()) {
            case 0: currentList = transactionList;
                break;
            case 1: for (Transaction transaction : transactionList) {
                if (!transaction.isIncome()) {
                    currentList.add(transaction);
                }
            }
                break;
            case 2: for (Transaction transaction : transactionList) {
                if (transaction.isIncome()) {
                    currentList.add(transaction);
                }
            }
                break;
        }
        return currentList;
    }

    public void setAdapter() {
        recyclerView.setAdapter(new TransactionsListAdapter(this, getCurrentList()));
    }

    public void editTransaction(int index) {
        Intent intent = new Intent(TransactionsHistoryActivity.this, EditTransactionActivity.class);
        intent.putExtra("transaction", getCurrentList().get(index));
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Transaction toReplace;
        Transaction newTransaction;
        try {
            assert data != null;
            toReplace = (Transaction) Objects.requireNonNull(data.getExtras()).getSerializable("toReplace");
            newTransaction = (Transaction) data.getExtras().getSerializable("newTransaction");
        } catch (NullPointerException e) {
            throw new RuntimeException(e);
        }

        transactionList.set(transactionList.indexOf(toReplace), newTransaction);
        filesOperations.sortTransactions(this);
        setAdapter();
    }


}