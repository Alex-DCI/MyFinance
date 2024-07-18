package org.dci.myfinance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StartActivity extends AppCompatActivity {
    private Button enterButton;
    private String input = "";
    private ImageView[] pins;
    private ProfileManagementActivity.Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ProfileManagementActivity.Profile profile = FilesOperations.getInstance(this).getProfile();

        if (profile == null || profile.checkPinCode("")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        Button[] numberButtons = new Button[10];
        numberButtons[0] = findViewById(R.id.button0);
        numberButtons[1] = findViewById(R.id.button1);
        numberButtons[2] = findViewById(R.id.button2);
        numberButtons[3] = findViewById(R.id.button3);
        numberButtons[4] = findViewById(R.id.button4);
        numberButtons[5] = findViewById(R.id.button5);
        numberButtons[6] = findViewById(R.id.button6);
        numberButtons[7] = findViewById(R.id.button7);
        numberButtons[8] = findViewById(R.id.button8);
        numberButtons[9] = findViewById(R.id.button9);
        Button clearButton = findViewById(R.id.clearButton);
        enterButton = findViewById(R.id.enterButton);
        Button backspaceButton = findViewById(R.id.backSpaceButton);

        pins = new ImageView[4];
        pins[0] = findViewById(R.id.newPin0);
        pins[1] = findViewById(R.id.newPin1);
        pins[2] = findViewById(R.id.newPin2);
        pins[3] = findViewById(R.id.newPin3);

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            numberButtons[i].setOnClickListener(v -> {
                addNumber(finalI);
            });
        }
        clearButton.setOnClickListener(v -> {
            input = "";
            setPins();
        });
        backspaceButton.setOnClickListener(v -> {
            input = input.substring(0, 4);
            setPins();
        });
        enterButton.setOnClickListener(v -> {

            if (profile.checkPinCode(input)) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "The entered password is incorrect", Toast.LENGTH_SHORT).show();
                input = "";
                setPins();
            }
        });
    }

    private void addNumber(int i) {
        if (input.length() < 4) {
            input += i;
        } else {
            Toast.makeText(this, "Only 4-digit PIN is allowed.", Toast.LENGTH_SHORT).show();
        }
        setPins();
    }

    private void setPins() {
        for (int i = 0; i < 4; i++) {
            if (i < input.length()) {
                pins[i].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.circle_filled));
            } else {
                pins[i].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.circle));
            }
        }
        if (input.length() == 4) {
            enterButton.setVisibility(View.VISIBLE);
        } else {
            enterButton.setVisibility(View.INVISIBLE);
        }
    }
}