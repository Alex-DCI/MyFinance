package org.dci.myfinance.Activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import org.dci.myfinance.FilesOperations;
import org.dci.myfinance.R;

import java.util.List;
import java.util.Objects;

public class Feedback extends AppCompatActivity {
    private EditText nameEditText;
    private EditText emailEditText;
    private Spinner reasonSpinner;
    private TextInputEditText feedback;
    private ImageView spinnerError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_feedback);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.backImage).setOnClickListener(v -> finish());
        findViewById(R.id.cancelButton).setOnClickListener(v -> finish());

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        feedback = findViewById(R.id.feedback);
        spinnerError = findViewById(R.id.spinnerError);

        reasonSpinner = findViewById(R.id.reasonSpinner);
        reasonSpinner.setAdapter(new ArrayAdapter<>(
                this, R.layout.custom_spinner_item, List.of("Choose the category:", "Bug", "Add new feature", "Question")));
        reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerError.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ProfileManagementActivity.Profile profile = FilesOperations.getInstance(this).getProfile();
        nameEditText.setText(profile.getName());
        emailEditText.setText(profile.getEmail());

        findViewById(R.id.applyButton).setOnClickListener(v -> validateInput());
    }

    private void validateInput() {
        if (isNameValid() && isEmailValid() && isCategoryValid() && isFeedbackValid()) {
            new AlertDialog.Builder(this)
                    .setTitle("Your request has been received")
                    .setMessage("You will soon receive an email with your request number.")
                    .setNegativeButton(android.R.string.ok, ((dialog, which) -> {
                        finish();
                    }))
                    .show();
        }
    }

    private boolean isNameValid() {
        String name = nameEditText.getText().toString();
        if (name.matches("[\\w\\.]{3,15}")) {
            return true;
        }
        if (name.length() < 3 || name.length() > 15) {
            nameEditText.setError(getResources().getString(R.string.nameLengthError));
        } else {
            nameEditText.setError(getResources().getString(R.string.nameValidationError));
        }
        return false;
    }

    private boolean isEmailValid() {
        String email = emailEditText.getText().toString();
        if (email.matches("^[a-zA-Z0-9].[a-zA-Z0-9\\._%\\+\\-]{0,63}@[a-zA-Z0-9\\.\\-]+\\.[a-zA-Z]{2,30}$")) {
            return true;
        }
        emailEditText.setError(getResources().getString(R.string.emailValidationError));
        return false;
    }

    private boolean isCategoryValid() {
        if (reasonSpinner.getSelectedItemId() == 0) {
            spinnerError.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    private boolean isFeedbackValid() {
        String message = Objects.requireNonNull(feedback.getText()).toString();
        if (message.isEmpty()) {
            feedback.setError("Type your message here");
            return false;
        }
        return true;
    }
}