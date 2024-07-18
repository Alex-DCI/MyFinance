package org.dci.myfinance;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

public class EditTransactionActivity extends AppCompatActivity {

    private Spinner spinner;
    private FilesOperations filesOperations;
    private Transaction transaction;
    private EditText editDate;
    private EditText editTime;
    private EditText amountTextView;
    private DateTimeFormatter dateFormatter;
    private DateTimeFormatter timeFormatter;
    private EditText descriptionEditText;
    private RadioButton incomeRadio;
    private LocalDateTime dateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle bundle = this.getIntent().getExtras();
        assert bundle != null;
        transaction = (Transaction) bundle.getSerializable("transaction");
        filesOperations = FilesOperations.getInstance(this);

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> this.finish());
        ImageView backImage = findViewById(R.id.backImage);
        backImage.setOnClickListener(v -> this.finish());

        Button applyButton = findViewById(R.id.applyButton);
        RadioGroup isIncomeRadioGroup = findViewById(R.id.isIncomeRadioGroup);
        amountTextView = findViewById(R.id.amountTextView);
        editDate = findViewById(R.id.dateEditText);
        editTime = findViewById(R.id.timeEditText);
        spinner = findViewById(R.id.spinner);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        incomeRadio = findViewById(R.id.incomeRadio);
        TextView activityView = findViewById(R.id.activityView);

        dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        dateTime = transaction.getDateTime();

        assert transaction != null;
        double transactionAmount = transaction.getAmount();
        String amountString = transactionAmount + getResources().getString(R.string.euro);
        amountTextView.setText(amountString);

        isIncomeRadioGroup.check(transaction.isIncome() ? R.id.incomeRadio : R.id.expenseRadio);
        setSpinnerAdapter(transaction.isIncome());

        isIncomeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> setSpinnerAdapter(checkedId == 0));
        LocalDateTime dateTime = transaction.getDateTime();

        editDate.setOnClickListener(v -> showDatePickerDialog());
        editTime.setOnClickListener(v -> showTimePickerDialog());
        applyButton.setOnClickListener(v -> validateInput());

        activityView.setText(getResources().getString(R.string.editTransaction));
        editTime.setText(dateTime.format(timeFormatter));
        editDate.setText(dateTime.format(dateFormatter));
        descriptionEditText.setText(transaction.getDescription());
    }

    private void setSpinnerAdapter(boolean isIncome) {

        List<String> categories = filesOperations.getCategories(isIncome);
        if (!categories.contains(transaction.getCategory())) {
            categories.add(transaction.getCategory());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, R.layout.custom_spinner_item, categories);

        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(categories.indexOf(transaction.getCategory()));

    }

    private void showDatePickerDialog() {

        LocalDate oldDateValue = dateTime.toLocalDate();
        int year = oldDateValue.getYear();
        int month = oldDateValue.getMonthValue() - 1;
        int day = oldDateValue.getDayOfMonth();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                EditTransactionActivity.this,
                android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    LocalDate newDateValue = LocalDate.of(year1, monthOfYear + 1, dayOfMonth);
                    editDate.setText(newDateValue.format(dateFormatter));
                },
                year, month, day);
        datePickerDialog
                .show();
    }

    private void showTimePickerDialog() {

        LocalTime oldTimeValue = dateTime.toLocalTime();
        int oldHour = oldTimeValue.getHour();
        int oldMinute = oldTimeValue.getMinute();

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                EditTransactionActivity.this,
                (view, hourOfDay, minute) -> {
                    LocalTime newTimeValue = LocalTime.of(hourOfDay, minute);
                    editTime.setText(newTimeValue.format(timeFormatter));
                },
                oldHour,
                oldMinute,
                true);

        timePickerDialog.show();
    }

    private void validateInput() {
        double amount;
        LocalDate date;
        LocalTime time;

        amount = checkAmount();
        if (amount == 0) {
            amountTextView.setError(getResources().getString(R.string.checkAmount));
            return;
        }

        date = checkDate();
        if (date == null) {
            if (editDate.getText().toString().isEmpty()) {
                date = LocalDate.now();
            } else {
                editDate.setError(getResources().getString(R.string.checkDate));
                return;
            }
        }

        time = checkTime();
        if (time == null) {
            if (editTime.getText().toString().isEmpty()) {
                time = LocalTime.now();
            } else {
                editTime.setError(getResources().getString(R.string.checkTime));
                return;
            }
        }

        List<Transaction> fullTransactionsList = filesOperations.getTransactions();
        fullTransactionsList.remove(transaction);

        transaction.setAmount(amount);
        transaction.setCategory(String.valueOf(spinner.getSelectedItem()));
        transaction.setDateTime(LocalDateTime.of(date, time));
        transaction.setDescription(String.valueOf(descriptionEditText.getText()));
        transaction.setIncome(incomeRadio.isChecked());

        fullTransactionsList.add(0, transaction);
        Collections.sort(fullTransactionsList);
        filesOperations.setTransactions(this, fullTransactionsList);
        Toast.makeText(this, "Transaction saved.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private LocalTime checkTime() {
        LocalTime time;
        try {
            time = LocalTime.parse(editTime.getText(), timeFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }
        return time;
    }

    private LocalDate checkDate() {
        LocalDate date;
        try {
            date = LocalDate.parse(editDate.getText(), dateFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }
        return date;
    }

    private double checkAmount() {
        String amountString = amountTextView.getText().toString();
        if (!amountString.matches("\\d*\\.?\\d{1,2} *[€$]?")) {
            amountTextView.setError(getResources().getString(R.string.checkAmount));
            return 0;
        }
        if (amountString.endsWith("€") || amountString.endsWith("$")) {
            amountString = amountString.substring(0, amountString.length() - 1).trim();
        }
        double amount;
        try {
            amount = Double.parseDouble(amountString);
        } catch(NumberFormatException e) {
            return 0;
        }
        return amount;
    }
}