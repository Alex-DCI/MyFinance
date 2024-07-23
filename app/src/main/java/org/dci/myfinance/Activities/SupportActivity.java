package org.dci.myfinance.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.dci.myfinance.Adapters.FAQAdapter;
import org.dci.myfinance.R;

import java.util.ArrayList;
import java.util.List;

public class SupportActivity extends AppCompatActivity {
    private List<FAQ> questions;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_support);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.backImage).setOnClickListener(v -> finish());
        findViewById(R.id.feedback).setOnClickListener(v -> startActivity(new Intent(this, Feedback.class)));

        findViewById(R.id.phoneTextView).setOnClickListener(v -> {
            String phone = "+490909399997";
            intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phone));
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE}, 3);
            } else {
                startActivity(intent);
            }
        });

        findViewById(R.id.emailTextView).setOnClickListener( v -> {
            String email = "best.support@ever.com";
            intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, email);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Problem with the MyFinance app");
            startActivity(intent);
        });


        RecyclerView faqList = findViewById(R.id.faqList);
        faqList.setLayoutManager(new LinearLayoutManager(this));
        questions = new ArrayList<>();

        addQuestions();

        faqList.setAdapter(new FAQAdapter(questions, this));
    }

    private void addQuestions() {
        questions.add(new FAQ("How to set a PIN code to log into the application?",
                "You need to go to the Profile menu and click the edit button in the PIN " +
                        "section to open the pin setup dialog. You can always change it there."));
        questions.add(new FAQ("How to remove a PIN code?",
                "You need to go to the change pin code menu, enter your current pin in the " +
                        "old pin field, and leave the other two fields empty. Once saved, you will " +
                        "no longer have to enter your PIN code when logging into the application."));
        questions.add(new FAQ("How can I view a transaction description in the transaction " +
                "history? Is it necessary to go to the transaction editing menu?",
                "no, to view a description of a transaction, just click on the transaction " +
                        "itself. If the category does not change to the transaction description, " +
                        "then the transaction description is empty. Clicking on the transaction " +
                        "again will return the category name"));
        questions.add(new FAQ("The Apply button in profile management is inactive.",
                "The Apply button in profile management becomes active when all entered data " +
                        "is correct. Check that the following information is correct: \n" +
                        "    - The profile name must be between 3 and 15 characters and contain only" +
                        " letters, numbers and underscores. \n" +
                        "    - The email address must be valid.\n" +
                        "    - If you have a PIN code set, you must enter it to make changes to your " +
                        "profile"));
        questions.add(new FAQ("The greeting in the main menu wishes good morning to evenings," +
                " and vice versa",
                "This can happen if your device's time is set incorrectly (for example, " +
                        "if you have the time set to 12-hour format and AM and PM are mixed up). " +
                        "Go to the settings of your device and set the correct time, " +
                        "or enable automatic time correction."
        ));
        questions.add(new FAQ("I don't find the answer to my question here. How do I report " +
                "a problem?",
                "If you have any questions, you can contact us using the details indicated " +
                        "above (phone or email), or write to us directly through the feedback form."));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 3) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Call permission is required to use the phone call", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class FAQ {
        private final String question;
        private final String answer;
        private boolean isAnswerVisible;

        public FAQ(String question, String answer) {
            this.question = question;
            this.answer = answer;
            isAnswerVisible = false;
        }

        public String getQuestion() {
            return question;
        }

        public String getAnswer() {
            return answer;
        }

        public boolean isAnswerVisible() {
            return isAnswerVisible;
        }

        public void setAnswerVisible(boolean answerVisible) {
            isAnswerVisible = answerVisible;
        }
    }
}