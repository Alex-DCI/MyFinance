package org.dci.myfinance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class ProfileManagementActivity extends AppCompatActivity {
    public static class Profile implements Serializable {
        private String name;
        private String email;
        private String picturePath;
        private String pinCode;

        public Profile(String name, String email, String picturePath, String pinCode) {
            this.name = name;
            this.email = email;
            this.picturePath = picturePath;
            this.pinCode = pinCode;
        }
        public String getPinCode() {
            return pinCode;
        }
        public boolean checkPinCode(String pinCode) {
            return this.pinCode.equals(pinCode);
        }

        public void setPinCode(Context context, String pinCode) {
            this.pinCode = pinCode;
            FilesOperations.getInstance(context).setProfile(context, this);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPicturePath() {
            return picturePath;
        }

        public Bitmap getBitmap() {
            try {
                byte[] decodedByte = Base64.decode(picturePath, Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            } catch (Exception e) {
                e.getMessage();
                return null;
            }
        }

        public void setPicturePath(String picturePath) {
            this.picturePath = picturePath;
        }
    }

    Profile profile;
    ImageView profilePicture;
    TextInputEditText nameEdit;
    TextInputEditText emailEdit;
    Button applyButton;
    FilesOperations filesOperations;
    ImageView[] pins;
    EditText pinEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        filesOperations = FilesOperations.getInstance(this);
        profile = filesOperations.getProfile();

        findViewById(R.id.backImage).setOnClickListener(v -> finish());
        findViewById(R.id.cancelButton).setOnClickListener(v -> finish());
        findViewById(R.id.editPictureIcon).setOnClickListener(v -> editPicture());
        findViewById(R.id.editPinIcon).setOnClickListener(v -> {
            Intent intent = new Intent(this, EditPinActivity.class);
            intent.putExtra("profile", profile);
            startActivity(intent);
        });

        pins = new ImageView[4];
        pins[0] = findViewById(R.id.pin0);
        pins[1] = findViewById(R.id.pin1);
        pins[2] = findViewById(R.id.pin2);
        pins[3] = findViewById(R.id.pin3);

        applyButton = findViewById(R.id.applyButton);
        TextView activityView = findViewById(R.id.activityView);
        nameEdit = findViewById(R.id.nameEdit);
        emailEdit = findViewById(R.id.emailEdit);
        profilePicture = findViewById(R.id.profilePicture);
        pinEditText = findViewById(R.id.pinEditText);

        if (profile.checkPinCode("")) {
            findViewById(R.id.pins).setVisibility(View.INVISIBLE);
        }

        findViewById(R.id.pins).setOnClickListener(v -> {
            pinEditText.setVisibility(View.VISIBLE);
            pinEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(pinEditText, InputMethodManager.SHOW_IMPLICIT);
        });

        activityView.setText(getResources().getString(R.string.profileManagement));
        nameEdit.setText(profile.getName());
        emailEdit.setText(profile.getEmail());
        setPicture();

        applyButton.setOnClickListener(v -> validateInput());
        addTextChangedListener(nameEdit);
        addTextChangedListener(emailEdit);
        addTextChangedListener(pinEditText);

        pinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setPins();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void addTextChangedListener(EditText inputField) {
        inputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setApplyButtonAvailability();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    private void setApplyButtonAvailability() {
        applyButton.setEnabled((pinEditText.getText().toString().length() == 4 || profile.checkPinCode(""))
                &&isNameValid(String.valueOf(nameEdit.getText()))
                && isEmailValid(String.valueOf(emailEdit.getText())));
    }

    private void setPicture() {
        if (profile.picturePath != null) {
            profilePicture.setImageBitmap(profile.getBitmap());
        } else {
            profilePicture.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.default_profile_picture));
        }
    }

    private void editPicture() {
        String[] imageSource = {"Camera", "Gallery"};
        new AlertDialog.Builder(this)
                .setTitle(R.string.chooseAnImageSource)
                .setItems(imageSource, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else {
                        openGallery();
                    }
                }).show();
    }

    private void openGallery() {

    }

    private void openCamera() {

    }

    private void validateInput() {
        String name = String.valueOf(nameEdit.getText());
        String email = String.valueOf(emailEdit.getText());
        String pin = String.valueOf(pinEditText.getText());

        if (!profile.checkPinCode(pin)) {
            Toast.makeText(this, "The entered password is incorrect", Toast.LENGTH_SHORT).show();
            pinEditText.setText("");
            setPins();
        } else if (isEmailValid(email) && isNameValid(name)) {
            profile.setName(String.valueOf(nameEdit.getText()));
            profile.setEmail(String.valueOf(emailEdit.getText()));
            BitmapDrawable bitmapDrawable = (BitmapDrawable) profilePicture.getDrawable();
            profile.setPicturePath(bitmapDrawable.getBitmap());
            filesOperations.setProfile(this, profile);
            applyButton.setEnabled(false);
        } else {
            if (isNameValid(name)) {
                if (name.length() < 3 || name.length() > 15) {
                    nameEdit.setError(getResources().getString(R.string.lettersAmountError));
                } else {
                    nameEdit.setError(getResources().getString(R.string.charsSetError));
                }
            }
            if (!isEmailValid(email)) {
                emailEdit.setError(getResources().getString(R.string.emailIsInvalid));
            }
        }
    }

    private boolean isNameValid(String name) {
        return name.matches("\\w{3,15}");
    }

    private boolean isEmailValid(String email) {
        return email.matches("[A-Za-z][\\w\\.-]{0,63}@[a-z]+\\.[a-z]{2}");
    }

    private void setPins() {
        String input = pinEditText.getText().toString();
        if (input.length() > 4) {
            Toast.makeText(this, "Only 4-digit PIN is allowed.", Toast.LENGTH_SHORT).show();
            pinEditText.setText(input.substring(0, 4));
        }

        for (int i = 0; i < 4; i++) {
            if (i < input.length()) {
                pins[i].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.circle_filled));
            } else {
                pins[i].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.circle));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!profile.checkPinCode("")) {
            findViewById(R.id.pins).setVisibility(View.VISIBLE);
        }
    }
}