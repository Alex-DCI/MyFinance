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

import java.util.ArrayList;

public class StartActivity extends AppCompatActivity {
    private Button enterButton;
    private ArrayList<Integer> input;
    private ImageView[] pinChars;

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

        pinChars = new ImageView[4];
        pinChars[0] = findViewById(R.id.pinChar0);
        pinChars[1] = findViewById(R.id.pinChar1);
        pinChars[2] = findViewById(R.id.pinChar2);
        pinChars[3] = findViewById(R.id.pinChar3);

        input = new ArrayList<>(4);

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            numberButtons[i].setOnClickListener(v -> {
                addNumber(finalI);
            });
        }
        clearButton.setOnClickListener(v -> {
            input.clear();
            setPinChars();
        });
        backspaceButton.setOnClickListener(v -> {
            input.remove(input.size() - 1);
            setPinChars();
        });
        enterButton.setOnClickListener(v -> {
            //TODO implement after profile is complete
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void addNumber(int i) {
        if (input.size() < 4) {
            input.add(i);
        } else {
            Toast.makeText(this, "Only 4-digit PIN is allowed.", Toast.LENGTH_SHORT).show();
        }
        setPinChars();
    }

    private void setPinChars() {
        for (int i = 0; i < 4; i++) {
            if (i < input.size()) {
                pinChars[i].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.circle_filled));
            } else {
                pinChars[i].setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.circle));
            }
        }
        if (input.size() == 4) {
            enterButton.setVisibility(View.VISIBLE);
        } else {
            enterButton.setVisibility(View.INVISIBLE);
        }
    }
}