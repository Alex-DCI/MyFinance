package org.dci.myfinance;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;

public class ProfileManagementActivity extends AppCompatActivity {
    public static class Profile implements Serializable {
        private String name;
        private String email;
        private String picture;
        private int pinCode;

        public Profile(String name, String email, String picture, int pinCode) {
            this.name = name;
            this.email = email;
            this.picture = picture;
            this.pinCode = pinCode;
        }

        public boolean checkPinCode(int pinCode) {
            return this.pinCode == pinCode;
        }

        public void setPinCode(int pinCode) {
            this.pinCode = pinCode;
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

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }
    }

    Profile profile;
    ImageView profilePicture;
    TextInputEditText nameEdit;
    TextInputEditText emailEdit;
    Button applyButton;
    FilesOperations filesOperations;

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

        applyButton = findViewById(R.id.applyButton);
        TextView activityView = findViewById(R.id.activityView);
        nameEdit = findViewById(R.id.nameEdit);
        emailEdit = findViewById(R.id.emailEdit);
        profilePicture = findViewById(R.id.profilePicture);

        activityView.setText(getResources().getString(R.string.profileManagement));

        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setApplyButtonAvailability();
            }

            @Override
            public void afterTextChanged(Editable s) {
                setApplyButtonAvailability();
            }
        });

        applyButton.setOnClickListener(v -> validateInput());
    }

    private void setApplyButtonAvailability() {
        applyButton.setEnabled(isNameValid(String.valueOf(nameEdit.getText()))
                && isEmailValid(String.valueOf(emailEdit.getText())));
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

        if (isEmailValid(email) && isNameValid(name)) {
            profile.setName(String.valueOf(nameEdit.getText()));
            profile.setEmail(String.valueOf(emailEdit.getText()));
//                    profilePicture.getDrawingCache());
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
}