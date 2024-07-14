package org.dci.myfinance;


import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionsListAdapter extends RecyclerView.Adapter<TransactionsListAdapter.TransactionsListViewHolder> {

    public static class TransactionsListViewHolder extends RecyclerView.ViewHolder {
        TextView amountTextView;
        TextView categoryTextView;
        TextView dateTextView;
        ImageView deleteTransactionImage;
        ImageView editTransactionImage;

        public TransactionsListViewHolder(@NonNull View itemView) {
            super(itemView);

            amountTextView = itemView.findViewById(R.id.amountTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
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
        filesOperations = FilesOperations.getInstance();
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsListViewHolder holder, int position) {
        Transaction currentTransaction = transactionsList.get(position);
        holder.categoryTextView.setText(currentTransaction.getCategory());
        holder.categoryTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.categoryTextView.setSingleLine(true);
        holder.categoryTextView.setMarqueeRepeatLimit(10);
        holder.categoryTextView.setSelected(true);

        double transactionAmount = currentTransaction.isIncome() ?
                currentTransaction.getAmount() : -currentTransaction.getAmount();
        String amountString = transactionAmount + context.getResources().getString(R.string.euro);
        holder.amountTextView.setText(amountString);
        holder.amountTextView.setTextColor(context.getResources().getColor(
                currentTransaction.isIncome() ? R.color.green : R.color.red
        ));
        holder.categoryTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.categoryTextView.setSingleLine(true);
        holder.categoryTextView.setMarqueeRepeatLimit(10);
        holder.categoryTextView.setSelected(true);

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


}
