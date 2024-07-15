package org.dci.myfinance;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;


public class AddTransaction extends AppCompatActivity {
    boolean isIncome;
    DateTimeFormatter dateFormatter;
    DateTimeFormatter timeFormatter;
    TextInputEditText dateEditText;
    TextInputEditText timeEditText;
    Spinner spinner;
    FilesOperations filesOperations;
    EditText amountTextView;
    EditText descriptionEditText;

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
        filesOperations = FilesOperations.getInstance();
        Bundle bundle = getIntent().getExtras();

        assert bundle != null;
        String bundleContent = (String)(bundle.get("isIncome"));

        assert bundleContent != null;
        isIncome = bundleContent.equals("true");

        findViewById(R.id.backImage).setOnClickListener(v -> finish());
        findViewById(R.id.cancelButton).setOnClickListener(v -> finish());
        findViewById(R.id.applyButton).setOnClickListener(v -> validateInput());
        TextView activityView = findViewById(R.id.activityView);
        ConstraintLayout viewGroup = findViewById(R.id.radioLayout);
        amountTextView = findViewById(R.id.amountTextView);
        dateEditText = findViewById(R.id.dateEditText);
        timeEditText = findViewById(R.id.timeEditText);
        spinner = findViewById(R.id.spinner);
        descriptionEditText = findViewById(R.id.descriptionEditText);

        LocalDateTime dateTime = LocalDateTime.now();
        dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        setSpinnerAdapter();
        viewGroup.removeAllViews();

        activityView.setText(isIncome ? R.string.addIncome : R.string.addExpense);
        dateEditText.setText(dateTime.format(dateFormatter));
        timeEditText.setText(dateTime.format(timeFormatter));

        dateEditText.setOnClickListener(v -> showDatePickerDialog());
        timeEditText.setOnClickListener(v -> showTimePickerDialog());
    }

    private void setSpinnerAdapter() {
        List<String> categories = filesOperations.getCategories(this, isIncome);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, R.layout.custom_spinner_item, categories);
        spinner.setAdapter(spinnerAdapter);
    }

    private void showTimePickerDialog() {
        LocalTime time = LocalTime.now();
        int hours = time.getHour();
        int minutes = time.getMinute();

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                AddTransaction.this,
                (view, hourOfDay, minute) -> {
                    LocalTime newTimeValue = LocalTime.of(hourOfDay, minute);
                    timeEditText.setText(newTimeValue.format(timeFormatter));
                },
                hours,
                minutes,
                true);

        timePickerDialog.show();
    }

    private void showDatePickerDialog() {
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        int month = date.getMonthValue() - 1;
        int day = date.getDayOfMonth();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddTransaction.this,
                android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    LocalDate newDateValue = LocalDate.of(year1, monthOfYear + 1, dayOfMonth);
                    dateEditText.setText(newDateValue.format(dateFormatter));
                },
                year, month, day);
        datePickerDialog
                .show();
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
            if (String.valueOf(dateEditText.getText()).isEmpty()) {
                date = LocalDate.now();
            } else {
                dateEditText.setError(getResources().getString(R.string.checkDate));
                return;
            }
        }

        time = checkTime();
        if (time == null) {
            if (String.valueOf(timeEditText.getText()).isEmpty()) {
                time = LocalTime.now();
            } else {
                timeEditText.setError(getResources().getString(R.string.checkTime));
                return;
            }
        }

        Transaction transaction = new Transaction(
                amount,
                String.valueOf(spinner.getSelectedItem()),
                LocalDateTime.of(date, time),
                String.valueOf(descriptionEditText.getText()),
                isIncome);

        List<Transaction> fullTransactionsList = filesOperations.getTransactions(this);
        for (int i = fullTransactionsList.size() - 1; i >= 0; i--) {
            if (transaction.getDateTime().isAfter(fullTransactionsList.get(i).getDateTime())) {
                fullTransactionsList.add(i, transaction);
                break;
            }
        }

        filesOperations.setTransactions(this, fullTransactionsList);

        Toast.makeText(this, "Transaction saved.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private LocalTime checkTime() {
        LocalTime time;
        try {
            time = LocalTime.parse(timeEditText.getText(), timeFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }
        return time;
    }

    private LocalDate checkDate() {
        LocalDate date;
        try {
            date = LocalDate.parse(dateEditText.getText(), dateFormatter);
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