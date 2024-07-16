package org.dci.myfinance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class ProfileManagementActivity extends AppCompatActivity {
    public static class Profile implements Serializable {
        private static Profile instance;
        private String name, email, picture;

        public static Profile getInstance(Context context) {
            if (instance == null) {
                JsonNode profileNode = FilesOperations.getInstance().readProfile(context);
                if (profileNode.asText().isEmpty()) {
                    instance = new Profile(context, null, null, null);
                } else {
                    instance = new Profile(
                            context,
                            profileNode.get("email").asText(null),
                            profileNode.get("name").asText(null),
                            profileNode.get("picture").asText(null));
                }
            }
            return instance;
        }

        public Bitmap getPicture() {
            return StringToBitMap(picture);
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public void setProfile(Context context, String name, String email, Bitmap picture) {
            this.name = name;
            this.email = email;
            this.picture = BitMapToString(picture);
            FilesOperations.getInstance().setProfile(context, this);
        }

        private Profile(Context context, String email, String name, String picture) {
            this.email = email;
            this.name = name;

            Bitmap bitmap;
            if (picture == null) {
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.default_profile_picture);
                this.picture = BitMapToString(bitmap);
            } else {
                this.picture = picture;
            }
        }

        private String BitMapToString(Bitmap bitmap) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            return Base64.encodeToString(b, Base64.DEFAULT);
        }

        private Bitmap StringToBitMap(String encodedString) {
            try {
                byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            } catch (Exception e) {
                return null;
            }
        }
    }

    Profile profile;
    ImageView profilePicture;
    TextInputEditText nameEdit;
    TextInputEditText emailEdit;
    Button applyButton;

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

        findViewById(R.id.backImage).setOnClickListener(v -> finish());
        findViewById(R.id.cancelButton).setOnClickListener(v -> finish());
        findViewById(R.id.editPictureIcon).setOnClickListener(v -> editPicture());

        applyButton = findViewById(R.id.applyButton);
        TextView activityView = findViewById(R.id.activityView);
        nameEdit = findViewById(R.id.nameEdit);
        emailEdit = findViewById(R.id.emailEdit);
        profilePicture = findViewById(R.id.profilePicture);

        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setApplyButtonAvailability();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        applyButton.setOnClickListener(v -> validateInput());

        profilePicture.setDrawingCacheEnabled(true);
        activityView.setText(getResources().getString(R.string.profileManagement));
        profile = Profile.getInstance(this);

        if (profile == null) {
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.default_profile_picture, null);
            profilePicture.setImageDrawable(drawable);
        } else {
            nameEdit.setText(profile.getName());
            emailEdit.setText(profile.getEmail());
            profilePicture.setImageBitmap(profile.getPicture());
        }
    }

    private void setApplyButtonAvailability() {
        applyButton.setEnabled(isNameValid(String.valueOf(nameEdit.getText()))
                && isEmailValid(String.valueOf(emailEdit.getText())));
    }

    private void editPicture() {


    }

    private void validateInput() {
        String name = String.valueOf(nameEdit.getText());
        String email = String.valueOf(emailEdit.getText());

        if (isEmailValid(email) && isNameValid(name)) {
            profile.setProfile(this,
                    String.valueOf(nameEdit.getText()),
                    String.valueOf(emailEdit.getText()),
                    profilePicture.getDrawingCache());
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
        boolean match = name.matches("\\w{3,15}");
        return match;
    }

    private boolean isEmailValid(String email) {
        boolean match = email.matches("[A-Za-z][\\w\\.-]{0,63}@[a-z]+\\.[a-z]{2}");
        return match;
    }
}