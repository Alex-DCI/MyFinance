package org.dci.myfinance;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_transaction);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle bundle = this.getIntent().getExtras();
        assert bundle != null;
        transaction = (Transaction) bundle.getSerializable("transaction");
        filesOperations = FilesOperations.getInstance();

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

        dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

        assert transaction != null;
        double transactionAmount = transaction.isIncome() ? transaction.getAmount() : -transaction.getAmount();
        String amountString = transactionAmount + getResources().getString(R.string.euro);
        amountTextView.setText(amountString);

        descriptionEditText.setText(transaction.getDescription());

        if (transaction.isIncome()) {
            isIncomeRadioGroup.check(R.id.incomeRadio);
            setSpinnerAdapter(0);
        } else {
            isIncomeRadioGroup.check(R.id.expenseRadio);
            setSpinnerAdapter(1);
        }



        isIncomeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> setSpinnerAdapter(checkedId));

        //TODO - check if month is correct

        LocalDateTime dateTime = transaction.getDateTime();
        editDate.setText(dateTime.format(dateFormatter));
        editDate.setOnClickListener(v -> showDatePickerDialog(dateTime));

        editTime.setText(dateTime.format(timeFormatter));
        editTime.setOnClickListener(v -> showTimePickerDialog(dateTime));

        applyButton.setOnClickListener(v -> validateInput());
    }

    private void setSpinnerAdapter(int checkedID) {

        List<String> categories = filesOperations.getCategories(this, checkedID == 0);
        if (!categories.contains(transaction.getCategory())) {
            categories.add(transaction.getCategory());
        }
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, categories);
        spinner.setAdapter(adapter);
        spinner.setSelection(categories.indexOf(transaction.getCategory()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = adapter.getItem(position);
                Toast.makeText(EditTransactionActivity.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                Log.d("SpinnerSelection", "Selected item: " + selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("SpinnerSelection", "Nothing selected");
            }
        });
    }

    private void showDatePickerDialog(LocalDateTime localDateTime) {
        LocalDate oldDateValue = localDateTime.toLocalDate();
        int year = oldDateValue.getYear();
        int month = oldDateValue.getMonthValue();
        int day = oldDateValue.getDayOfMonth();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                EditTransactionActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    LocalDate newDateValue = LocalDate.of(year1, monthOfYear + 1, dayOfMonth);
                    editDate.setText(newDateValue.format(dateFormatter));
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog(LocalDateTime localDateTime) {
        LocalTime oldTimeValue = localDateTime.toLocalTime();

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
                false);

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

        Intent intent = new Intent();
        intent.putExtra("toReplace", transaction);

        Transaction newTransaction = new Transaction(
                amount,
                (String)spinner.getSelectedItem(),
                LocalDateTime.of(date, time),
                descriptionEditText.getText().toString(),
                incomeRadio.isChecked()
        );
        intent.putExtra("newTransaction", newTransaction);

        Toast.makeText(this, "Transaction saved.", Toast.LENGTH_SHORT).show();
        setResult(0, intent);
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
        if (amountString.endsWith("€")) {
            amountString = amountString.substring(0, amountString.lastIndexOf(' '));
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