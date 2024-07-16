package org.dci.myfinance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;


public class ProfileManagementActivity extends AppCompatActivity {
    public static class Profile {
        private String name, email, picture;

        public Bitmap getPicture() {
            return StringToBitMap(picture);
        }

        public void setPicture(Bitmap picture) {
            this.picture = BitMapToString(picture);
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

        public Profile(Context context, String email, String name, String picture) {
            this.email = email;
            this.name = name;

            Bitmap bitmap;
            if (picture == null) {
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        context.getResources().getIdentifier("default-profile-picture.png",
                                "drawable", context.getPackageName()));
                this.picture = BitMapToString(bitmap);
            } else {
                this.picture = picture;
            }
        }

        public String BitMapToString(Bitmap bitmap){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
            byte [] b = baos.toByteArray();
            return Base64.encodeToString(b, Base64.DEFAULT);
        }

        public Bitmap StringToBitMap(String encodedString){
            try {
                byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            } catch(Exception e) {
                return null;
            }
        }
    }

    Profile profile;
    ImageView profilePicture;

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

        TextView activityView = findViewById(R.id.activityView);
        profilePicture = findViewById(R.id.profilePicture);

        FilesOperations filesOperations = FilesOperations.getInstance();
        profile = filesOperations.getProfile(this);

        if (profile == null) {
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.default_profile_picture, null);
            profilePicture.setImageDrawable(drawable);
        }
        activityView.setText(getResources().getString(R.string.profileManagement));
    }
}