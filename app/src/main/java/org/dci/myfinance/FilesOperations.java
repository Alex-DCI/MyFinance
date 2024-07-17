package org.dci.myfinance;

import android.content.Context;
import android.content.ContextWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FilesOperations {
    private static FilesOperations instance;

    private FilesOperations(Context context) {
        readProfile(context);
        incomesCategories = new ArrayList<>();
        expensesCategories = new ArrayList<>();
        readCategories(context);
    }

    public static synchronized FilesOperations getInstance(Context context) {
        if (instance == null) {
            instance = new FilesOperations(context);
        }
        return instance;
    }

    private ProfileManagementActivity.Profile profile;
    private List<Transaction> transactions;

    private List<String> incomesCategories;
    private List<String> expensesCategories;

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

    public void setProfile(ProfileManagementActivity.Profile profile) {
        this.profile = profile;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    private List<Transaction> readTransactions(Context context) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(context.getFilesDir().getName(), Context.MODE_PRIVATE);
        File file = new File(directory, "transaction.json");
        List<Transaction> transactionsList = new ArrayList<>();
        try (InputStream stream = Files.newInputStream(file.toPath())) {
            JsonNode rootNode = new ObjectMapper().readTree(stream);

            for (JsonNode transaction : rootNode) {
                transactionsList.add(new Transaction(
                        transaction.get("amount").asDouble(),
                        transaction.get("category").asText(),
                        LocalDateTime.parse(transaction.get("dateTime").asText()),
                        transaction.get("description").asText(),
                        transaction.get("income").asBoolean()
                ));
            }
        } catch (IOException e) {
            return transactionsList;
        }
        return transactionsList;
    }

    private void writeTransactions(Context context, List<Transaction> transactionsList) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JSR310Module());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(context.getFilesDir().getName(), Context.MODE_PRIVATE);
        File file = new File(directory, "transaction.json");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(mapper.writeValueAsBytes(transactionsList));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readCategories(Context context) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(context.getFilesDir().getName(), Context.MODE_PRIVATE);
        File file = new File(directory, "categories.json");

        if (!file.exists()) {
            createCategoriesFile(context);
            incomesCategories = List.of("Salary", "Bonus", "Others");
            expensesCategories = List.of("Food", "Transport", "Entertainment", "House", "Children", "Others");
            writeCategories(context);
        }

        try (InputStream stream = Files.newInputStream(file.toPath())) {
            JsonNode categoriesNode = new ObjectMapper().readTree(stream).get("incomesCategories");
            for (JsonNode category : categoriesNode) {
                incomesCategories.add(category.asText());
            }
            categoriesNode = new ObjectMapper().readTree(stream).get("expensesCategories");
            for (JsonNode category : categoriesNode) {
                expensesCategories.add(category.asText());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read categories file");
        }
    }

    private void writeCategories(Context context) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(context.getFilesDir().getName(), Context.MODE_PRIVATE);
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

    private void createCategoriesFile(Context context) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(context.getFilesDir().getName(), Context.MODE_PRIVATE);
        File file = new File(directory, "categories.json");

        try (FileWriter writer = new FileWriter(file)) {
            JSONObject rootNode = new JSONObject();
            rootNode.put("incomesCategories", new JSONArray(List.of("Salary", "Bonus", "Others")));
            rootNode.put("expensesCategories", new JSONArray(List.of("Food", "Transport", "Entertainment",
                    "House", "Children", "Others")));
            writer.write(rootNode.toString());
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeProfile(Context context) {
        ObjectMapper mapper = new ObjectMapper();
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(context.getFilesDir().getName(), Context.MODE_PRIVATE);
        File file = new File(directory, "profiles.json");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(mapper.writeValueAsBytes(profile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readProfile(Context context) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(context.getFilesDir().getName(), Context.MODE_PRIVATE);
        File file = new File(directory, "profiles.json");

        try (InputStream stream = Files.newInputStream(file.toPath())) {
            JsonNode rootNode = new ObjectMapper().readTree(stream);

            profile = new ProfileManagementActivity.Profile(
                    rootNode.get("name").asText(),
                    rootNode.get("email").asText(),
                    rootNode.get("picture").asText(),
                    rootNode.get("pinCode").asInt()
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
