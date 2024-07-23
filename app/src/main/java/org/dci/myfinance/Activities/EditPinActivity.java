package org.dci.myfinance.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.dci.myfinance.R;

import java.util.ArrayList;
import java.util.List;

public class EditPinActivity extends AppCompatActivity {
    private List<List<ImageView>> pins;
    private List<EditText> editTexts;
    private Button applyButton;
    private ProfileManagementActivity.Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_pin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pins = new ArrayList<>(3);
        pins.add(List.of(findViewById(R.id.oldPin0),
                findViewById(R.id.oldPin1),
                findViewById(R.id.oldPin2),
                findViewById(R.id.oldPin3)));
        pins.add(List.of(findViewById(R.id.newPin0),
                findViewById(R.id.newPin1),
                findViewById(R.id.newPin2),
                findViewById(R.id.newPin3)));
        pins.add(List.of(findViewById(R.id.repeatPin0),
                findViewById(R.id.repeatPin1),
                findViewById(R.id.repeatPin2),
                findViewById(R.id.repeatPin3)));

        applyButton = findViewById(R.id.applyButton);

        editTexts = new ArrayList<>(3);
        editTexts.add(findViewById(R.id.oldPinEditText));
        editTexts.add(findViewById(R.id.newPinEditText));
        editTexts.add(findViewById(R.id.repeatPinEditText));

        findViewById(R.id.oldPins).setOnClickListener(v -> activateInput(0));
        findViewById(R.id.newPins).setOnClickListener(v -> activateInput(1));
        findViewById(R.id.repeatPins).setOnClickListener(v -> activateInput(2));

        setOldPinDisabled();
        setTextChangedListener();
        setOnFocusChangeListener();

        findViewById(R.id.backImage).setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED, new Intent());
            finish();
        });
        findViewById(R.id.cancelButton).setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED, new Intent());
            finish();
        });
        findViewById(R.id.applyButton).setOnClickListener(v -> validateInput());

        editTexts.get(0).setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_TAB && event.getAction() == KeyEvent.ACTION_DOWN) {
                editTexts.get(1).setVisibility(View.VISIBLE);
                editTexts.get(1).requestFocus();
                return true;
            }
            return false;
        });

        editTexts.get(1).setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_TAB && event.getAction() == KeyEvent.ACTION_DOWN) {
                editTexts.get(2).setVisibility(View.VISIBLE);
                editTexts.get(2).requestFocus();
                return true;
            }
            return false;
        });
    }

    private void setOldPinDisabled() {
        profile = (ProfileManagementActivity.Profile) getIntent().getSerializableExtra("profile");

        assert profile != null;
        if (profile.checkPinCode("")) {
            findViewById(R.id.oldPinText).setVisibility(View.INVISIBLE);
            findViewById(R.id.oldPins).setVisibility(View.INVISIBLE);
        }
    }

    private void setOnFocusChangeListener() {
        for (EditText editText : editTexts) {
            editText.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    editText.setVisibility(View.VISIBLE);
                    editText.setCursorVisible(false);
                    editText.setBackgroundResource(R.drawable.edit_text_style);
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    editText.setVisibility(View.GONE);
                    editText.setBackgroundResource(android.R.color.transparent);
                }
            });
        }
    }

    private void setTextChangedListener() {
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            editTexts.get(i).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    setPins(finalI);
                    if (finalI == 0) {
                        applyButton.setEnabled(editTexts.get(0).getText().toString().length() == 4);
                    } else {
                        applyButton.setEnabled((profile.checkPinCode("")
                                || editTexts.get(0).getText().toString().length() == 4)
                                && editTexts.get(1).getText().toString().length() == 4
                                && editTexts.get(2).getText().toString().length() == 4);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void setPins(int index) {
        String input = editTexts.get(index).getText().toString();
        if (input.length() > 4) {
            editTexts.get(index).setText(input.substring(0, 4));
        }
        for (int i = 0; i < pins.get(index).size(); i++) {
            if (i < input.length()) {
                pins.get(index).get(i).setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.circle_filled));
            } else {
                pins.get(index).get(i).setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.circle));
            }
        }
    }

    private void activateInput(int index) {
        editTexts.get(index).setVisibility(View.VISIBLE);
        editTexts.get(index).requestFocus();
        editTexts.get(index).setCursorVisible(false);
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTexts.get(index), InputMethodManager.SHOW_IMPLICIT);
    }

    private void validateInput() {
        String oldPin = editTexts.get(0).getText().toString();
        String newPin = editTexts.get(1).getText().toString();
        String repeatPin = editTexts.get(2).getText().toString();

        if (!newPin.equals(repeatPin)) {
            Toast.makeText(this, "PIN codes do not match.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!profile.checkPinCode(oldPin)) {
            Toast.makeText(this, "The current pin does not match the provided one", Toast.LENGTH_LONG).show();
            return;
        }

        if (!newPin.isEmpty() && newPin.length() != 4) {
            Toast.makeText(this, "Enter a 4-digit PIN or leave the field blank to disable it", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("pinString", newPin);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
