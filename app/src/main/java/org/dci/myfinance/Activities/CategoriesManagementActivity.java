package org.dci.myfinance.Activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import org.dci.myfinance.Adapters.CategoriesListAdapter;
import org.dci.myfinance.FilesOperations;
import org.dci.myfinance.R;

import java.util.List;

public class CategoriesManagementActivity extends AppCompatActivity {

    private RecyclerView categoriesRecyclerView;
    private TabLayout tabLayout;
    private EditText addNewCategoryInput;
    private FilesOperations filesOperations;
    private List<String> currentCategoriesList;
    private boolean isIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_categories_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addNewCategoryInput = findViewById(R.id.addNewCategoryInput);
        ImageView addNewCategoryImage = findViewById(R.id.addNewCategoryImage);
        tabLayout = findViewById(R.id.tabLayout);
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        ImageView backButton = findViewById(R.id.backImage);

        backButton.setOnClickListener(v -> finish());
        filesOperations = FilesOperations.getInstance(this);
        isIncome = false;
        currentCategoriesList = filesOperations.getCategories(false);

        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        setAdapter();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isIncome = tabLayout.getSelectedTabPosition() == 1;
                currentCategoriesList = filesOperations.getCategories(isIncome);
                if (isIncome) {
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.green, null));
                } else {
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.red, null));
                }
                setAdapter();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        findViewById(R.id.backImage).setOnClickListener(v -> this.finish());

        addNewCategoryImage.setOnClickListener(v -> addCategory());
    }

    private void addCategory() {
        String newValue = addNewCategoryInput.getText().toString();
        if (newValue.length() < 3) {
            addNewCategoryInput.setError(getResources().getString(R.string.enterAValidCategoryName));
            return;
        }
        if (currentCategoriesList.contains(newValue)) {
            addNewCategoryInput.setError(getResources().getString(R.string.thisCategoryAlreadyExists));
            return;
        }

        addNewCategoryInput.setText("");
        addNewCategoryInput.setHint(getResources().getString(R.string.addANewCategory));
        currentCategoriesList.add(newValue);
        filesOperations.setCategories(currentCategoriesList, isIncome);
        setAdapter();
    }

    public void setAdapter() {
        currentCategoriesList = filesOperations.getCategories(isIncome);
        categoriesRecyclerView.setAdapter(new CategoriesListAdapter(this, isIncome));
    }
}