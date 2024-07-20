package org.dci.myfinance;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.io.FileInputStream;

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

        public void setPicturePath(String picturePath) {
            this.picturePath = picturePath;
        }
    }

    private Profile profile;
    private ImageView profilePicture;
    private TextInputEditText nameEdit;
    private TextInputEditText emailEdit;
    private Button applyButton;
    private FilesOperations filesOperations;
    private ImageView[] pins;
    private EditText pinEditText;
    private Bitmap bitmap;

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

        pinEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setOnFocusChangeListener(hasFocus);
            }
        });

        activityView.setText(getResources().getString(R.string.profileManagement));
        nameEdit.setText(profile.getName());
        emailEdit.setText(profile.getEmail());
        setPicture();

        applyButton.setOnClickListener(v -> validateInput());
        addTextChangedListener(nameEdit);
        addTextChangedListener(emailEdit);
        addTextChangedListener(pinEditText);
    }

    private void setOnFocusChangeListener(boolean hasFocus) {
        if (hasFocus) {
            pinEditText.setBackground(AppCompatResources.getDrawable(this, R.drawable.edit_text_style));
        } else {
            pinEditText.setBackground(AppCompatResources.getDrawable(this, R.drawable.edit_text_default));
        }
    }

    private void addTextChangedListener(EditText inputField) {
        inputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (inputField == pinEditText) {
                    setPins();
                }
                setApplyButtonAvailability();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setApplyButtonAvailability() {
        applyButton.setEnabled((pinEditText.getText().toString().length() == 4 || profile.checkPinCode(""))
                && isNameValid(String.valueOf(nameEdit.getText()))
                && isEmailValid(String.valueOf(emailEdit.getText())));
    }

    private void setPicture() {
        if (profile.getPicturePath() != null) {
            try (FileInputStream fis = openFileInput(profile.getPicturePath())){
                profilePicture.setImageBitmap(BitmapFactory.decodeStream(fis));
                System.out.println();
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "File not found. Default image is used instead", Toast.LENGTH_SHORT).show();
                profile.setPicturePath(null);
                setPicture();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Log.d("pictureTest", profilePicture.getDrawable().toString());
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
                        checkCamera();
                    } else {
                        openGallery();
                    }
                }).show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    private void checkCamera() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA}, 3);
        } else {
            startCamera();
        }

    }

    private void validateInput() {
        String name = String.valueOf(nameEdit.getText());
        String email = String.valueOf(emailEdit.getText());
        String pin = String.valueOf(pinEditText.getText());

        if (!profile.checkPinCode(pin)) {
            Toast.makeText(this, "The entered Pin is incorrect", Toast.LENGTH_SHORT).show();
            pinEditText.setText("");
            setPins();
        } else if (isEmailValid(email) && isNameValid(name)) {
            profile.setName(name);
            profile.setEmail(email);
            filesOperations.setProfile(this, profile);
            Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show();
            applyButton.setEnabled(false);
        } else {
            if (isNameValid(name)) {
                if (name.length() < 3 || name.length() > 15) {
                    nameEdit.setError(getResources().getString(R.string.nameLengthError));
                } else if (!name.matches("[a-zA-Z0-9]+")) {
                    nameEdit.setError(getResources().getString(R.string.nameValidationError));
                }
            }

            if (!isEmailValid(email)) {
                emailEdit.setError(getResources().getString(R.string.emailValidationError));
            }
        }
    }

    private boolean isEmailValid(String email) {
        return email.matches(".+@.+\\.[a-z]+");
    }

    private boolean isNameValid(String name) {
        return !name.equals(profile.getName())
                && name.length() >= 3 && name.length() <= 15
                && name.matches("[a-zA-Z0-9]+");
    }

    private void setPins() {
        int enteredPinLength = pinEditText.getText().toString().length();
        for (int i = 0; i < pins.length; i++) {
            if (i < enteredPinLength) {
                pins[i].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.circle_filled));
            } else {
                pins[i].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.circle));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1 && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    bitmap = (Bitmap) extras.get("data");
                }
                profilePicture.setImageBitmap(bitmap);
                filesOperations.setImage(this, bitmap, profile);
                setApplyButtonAvailability();
            } else if (requestCode == 2 && data != null) {
                Uri selectedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    profilePicture.setImageBitmap(bitmap);
                    filesOperations.setImage(this, bitmap, profile);
                    setApplyButtonAvailability();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 3) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to use the camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 1);
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }
}