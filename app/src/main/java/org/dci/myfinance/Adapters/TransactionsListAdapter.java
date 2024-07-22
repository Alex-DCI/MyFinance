package org.dci.myfinance.Adapters;


import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.dci.myfinance.FilesOperations;
import org.dci.myfinance.R;
import org.dci.myfinance.Transaction;
import org.dci.myfinance.Activities.TransactionsHistoryActivity;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionsListAdapter extends RecyclerView.Adapter<TransactionsListAdapter.TransactionsListViewHolder> {

    public static class TransactionsListViewHolder extends RecyclerView.ViewHolder {
        private TextView amountTextView;
        private TextView infoTextView;
        private TextView dateTextView;
        private ImageView deleteTransactionImage;
        private ImageView editTransactionImage;

        public TransactionsListViewHolder(@NonNull View itemView) {
            super(itemView);

            amountTextView = itemView.findViewById(R.id.amountTextView);
            infoTextView = itemView.findViewById(R.id.infoTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            deleteTransactionImage = itemView.findViewById(R.id.deleteTransactionImage);
            editTransactionImage = itemView.findViewById(R.id.editTransactionImage);
        }
    }

    @NonNull
    @Override
    public TransactionsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.history_recycler_view_item, parent, false);

        return new TransactionsListViewHolder(itemView);
    }

    private final TransactionsHistoryActivity context;
    private final List<Transaction> transactionsList;
    private final FilesOperations filesOperations;

    public TransactionsListAdapter(TransactionsHistoryActivity context, List<Transaction> transactionsList) {
        this.context = context;
        this.transactionsList = transactionsList;
        filesOperations = FilesOperations.getInstance(context);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsListViewHolder holder, int position) {
        Transaction currentTransaction = transactionsList.get(position);
        holder.infoTextView.setText(currentTransaction.getCategory());
        holder.infoTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.infoTextView.setSingleLine(true);
        holder.infoTextView.setMarqueeRepeatLimit(10);
        holder.infoTextView.setSelected(true);

        setListener(holder.infoTextView, holder.infoTextView, position);
        setListener(holder.amountTextView, holder.infoTextView, position);
        setListener(holder.dateTextView, holder.infoTextView, position);

        double transactionAmount = currentTransaction.getAmount();
        String amountString = transactionAmount + context.getResources().getString(R.string.euro);
        holder.amountTextView.setText(amountString);
        holder.amountTextView.setTextColor(context.getResources().getColor(
                currentTransaction.isIncome() ? R.color.green : R.color.red
        ));
        holder.infoTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.infoTextView.setSingleLine(true);
        holder.infoTextView.setMarqueeRepeatLimit(10);
        holder.infoTextView.setSelected(true);

        DateTimeFormatter to = DateTimeFormatter.ofPattern("dd MMM yyy");
        String date = currentTransaction.getDateTime().format(to);
        holder.dateTextView.setText(date);

        holder.deleteTransactionImage.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    deleteTransaction(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, transactionsList.size());
                })
                .setNegativeButton(android.R.string.no, null).show());

        holder.editTransactionImage.setOnClickListener(v -> context.editTransaction(position));
    }

    private void deleteTransaction(int position) {
        transactionsList.remove(position);
        filesOperations.setTransactions(context, transactionsList);
    }

    @Override
    public int getItemCount() {
        return transactionsList.size();
    }

    private void setListener(TextView view, TextView infoView, int position) {
        view.setOnClickListener(v -> {
            if (!transactionsList.get(position).getDescription().isEmpty()
                    && infoView.getText().toString().equals(transactionsList.get(position).getCategory())) {
                infoView.setText(transactionsList.get(position).getDescription());
            } else {
                infoView.setText(transactionsList.get(position).getCategory());
            }
        });
    }
}
