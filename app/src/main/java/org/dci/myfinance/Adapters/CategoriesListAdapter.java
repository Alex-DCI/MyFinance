package org.dci.myfinance.Adapters;

import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.dci.myfinance.Activities.CategoriesManagementActivity;
import org.dci.myfinance.FilesOperations;
import org.dci.myfinance.R;

import java.util.List;

public class CategoriesListAdapter extends RecyclerView.Adapter<CategoriesListAdapter.CategoriesListViewHolder> {

    public static class CategoriesListViewHolder extends RecyclerView.ViewHolder {

        private final TextView categoryTextView;
        private final ImageView deleteTransactionImage;
        private final ImageView editTransactionImage;

        public CategoriesListViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryTextView = itemView.findViewById(R.id.amountTextView);
            deleteTransactionImage = itemView.findViewById(R.id.deleteTransactionImage);
            editTransactionImage = itemView.findViewById(R.id.editTransactionImage);
        }
    }

    private final CategoriesManagementActivity context;
    private final FilesOperations filesOperations;
    private final boolean isIncome;
    private final List<String> categoriesList;

    public CategoriesListAdapter(CategoriesManagementActivity context, boolean isIncome) {
        this.context = context;
        this.isIncome = isIncome;
        filesOperations = FilesOperations.getInstance(context);
        categoriesList = filesOperations.getCategories(isIncome);
    }

    @Override
    public int getItemCount() {
        return filesOperations.getCategories(isIncome).size();
    }

    @NonNull
    @Override
    public CategoriesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.categories_recyclerview_item, parent, false);

        return new CategoriesListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesListViewHolder holder, int position) {
        holder.categoryTextView.setText(categoriesList.get(position));
        holder.categoryTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.categoryTextView.setSingleLine(true);
        holder.categoryTextView.setMarqueeRepeatLimit(10);
        holder.categoryTextView.setSelected(true);

        holder.deleteTransactionImage.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    deleteCategory(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, categoriesList.size());
                })
                .setNegativeButton(android.R.string.no, null).show());

        holder.editTransactionImage.setOnClickListener(v -> {
            EditText input = new EditText(context);

            input.setHint(categoriesList.get(position));
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle("Edit Category name")
                    .setView(input)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        editCategory(position, input.getText().toString());
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, categoriesList.size());
                    })
                    .setNegativeButton(android.R.string.no, null).show();
            alertDialog.show();
        });
    }

    private void editCategory(int position, String newValue) {
        categoriesList.set(position, newValue);
        filesOperations.setCategories(categoriesList, isIncome);
        context.setAdapter();
    }

    private void deleteCategory(int index) {
        categoriesList.remove(index);
        filesOperations.setCategories(categoriesList, isIncome);

    }
}
