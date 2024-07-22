package org.dci.myfinance;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import org.dci.myfinance.Activities.ProfileManagementActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FilesOperations {
    private static FilesOperations instance;

    private final File directory;
    private ProfileManagementActivity.Profile profile;
    private List<Transaction> transactions;
    private List<String> incomesCategories;
    private List<String> expensesCategories;
    private Bitmap image;

    private FilesOperations(Context context) {
        directory = new ContextWrapper(context).getDir(context.getFilesDir().getName(), MODE_PRIVATE);
        readProfile(context);

        incomesCategories = new ArrayList<>();
        expensesCategories = new ArrayList<>();
        readCategories(context);

        readTransactions(context);
    }

    public static synchronized FilesOperations getInstance(Context context) {
        if (instance == null) {
            instance = new FilesOperations(context);
        }
        return instance;
    }

    public List<String> getCategories(boolean isIncome) {
        return isIncome ? incomesCategories : expensesCategories;
    }

    public void setCategories(Context context, List<String> categories, boolean isIncome) {
        if (isIncome) {
            incomesCategories = categories;
        } else {
            expensesCategories = categories;
        }
        writeCategories(context);
    }

    public ProfileManagementActivity.Profile getProfile() {
        return profile;
    }

    public void setProfile(Context context, ProfileManagementActivity.Profile profile) {
        this.profile = profile;
        writeProfile(context);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Context context, List<Transaction> transactions) {
        this.transactions = transactions;
        writeTransactions(context);
    }

    public void setImage(Context context, Bitmap bitmap) {
        image = bitmap;
        File file = new File(directory, "profile_image.jpg");
        try (OutputStream stream = Files.newOutputStream(file.toPath())){
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        } catch (IOException e) {
            Toast.makeText(context, "Unable to save file", Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap getImage(Context context) {
        try (FileInputStream fileInputStream = new FileInputStream(new File(directory, "/profile_image.jpg"))){
            image = BitmapFactory.decodeStream(fileInputStream);
        } catch (IOException e) {
            image = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_profile_picture);
        }
        return image;
    }

    private void readTransactions(Context context) {
        File file = new File(directory, "transaction.json");
        transactions = new ArrayList<>();
        if (!file.exists()) {
            writeTransactions(context);
            return;
        }
        try (InputStream stream = Files.newInputStream(file.toPath())) {
            JsonNode rootNode = new ObjectMapper().readTree(stream);
            for (JsonNode transaction : rootNode) {
                transactions.add(new Transaction(
                        transaction.get("amount").asDouble(),
                        transaction.get("category").asText(),
                        LocalDateTime.parse(transaction.get("dateTime").asText()),
                        transaction.get("description").asText(),
                        transaction.get("income").asBoolean()
                ));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeTransactions(Context context) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JSR310Module());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(context.getFilesDir().getName(), MODE_PRIVATE);
        File file = new File(directory, "transaction.json");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(mapper.writeValueAsBytes(transactions));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readCategories(Context context) {
        File file = new File(directory, "categories.json");
        if (!file.exists()) {
            incomesCategories = List.of("Salary", "Bonus", "Others");
            expensesCategories = List.of("Food", "Transport", "Entertainment", "House", "Children", "Others");
            writeCategories(context);
            return;
        }

        try (InputStream stream = Files.newInputStream(file.toPath())) {
            JsonNode rootNode = new ObjectMapper().readTree(stream);
            JsonNode categoriesNode = rootNode.get("incomesCategories");
            for (JsonNode category : categoriesNode) {
                incomesCategories.add(category.asText());
            }
            categoriesNode = rootNode.get("expensesCategories");
            for (JsonNode category : categoriesNode) {
                expensesCategories.add(category.asText());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeCategories(Context context) {
        File file = new File(directory, "categories.json");
        try (FileWriter writer = new FileWriter(file)) {
            JSONObject rootNode = new JSONObject();
            rootNode.put("incomesCategories", new JSONArray(incomesCategories));
            rootNode.put("expensesCategories", new JSONArray(expensesCategories));
            writer.write(rootNode.toString());
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeProfile(Context context) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(directory, "profile.json");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(mapper.writeValueAsBytes(profile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readProfile(Context context) {
        File file = new File(directory, "profile.json");
        if (!file.exists()) {
            profile = new ProfileManagementActivity.Profile(null, null, "");
            writeProfile(context);
            return;
        }
        try (InputStream stream = Files.newInputStream(file.toPath())) {
            JsonNode rootNode = new ObjectMapper().readTree(stream);
            if (rootNode.isEmpty()) {
                profile = new ProfileManagementActivity.Profile(null, null, "");
            } else {
                profile = new ProfileManagementActivity.Profile(
                        rootNode.get("name").asText(null),
                        rootNode.get("email").asText(null),
                        rootNode.get("pinCode").asText("")
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}