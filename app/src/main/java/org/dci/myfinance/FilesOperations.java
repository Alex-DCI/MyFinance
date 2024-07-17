package org.dci.myfinance;

import android.content.Context;
import android.content.ContextWrapper;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
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

    private FilesOperations() {

    }

    public static synchronized FilesOperations getInstance() {
        if (instance == null) {
            instance = new FilesOperations();
        }
        return instance;
    }

    public List<Transaction> getTransactions(Context context) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(context.getFilesDir().getName(), Context.MODE_PRIVATE);
        File file =  new File(directory, "transaction.json");
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

    public void setTransactions(Context context, List<Transaction> transactionsList) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JSR310Module());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(context.getFilesDir().getName(), Context.MODE_PRIVATE);
        File file =  new File(directory, "transaction.json");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(mapper.writeValueAsBytes(transactionsList));
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getCategories(Context context, boolean isIncome) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(context.getFilesDir().getName(), Context.MODE_PRIVATE);
        File file = new File(directory, "categories.json");

        if (!file.exists()) {
            createCategoriesFile(context);
            if (isIncome) {
                return List.of("Salary", "Bonus", "Others");
            } else {
                return List.of("Food", "Transport", "Entertainment", "House", "Children", "Others");
            }
        }

        List<String> categories = new ArrayList<>();
        try (InputStream stream = Files.newInputStream(file.toPath())) {
            JsonNode categoriesNode;
            if (isIncome) {
                categoriesNode = new ObjectMapper().readTree(stream).get("incomesCategories");
            } else {
                categoriesNode = new ObjectMapper().readTree(stream).get("expensesCategories");
            }

            for (JsonNode category : categoriesNode) {
                categories.add(category.asText());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read categories file");
        }
        return categories;
    }

    public void setCategories(Context context, List<String> transactionsList, boolean isIncome) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(context.getFilesDir().getName(), Context.MODE_PRIVATE);
        File file = new File(directory, "categories.json");

        List<String> secondCategoryList = getCategories(context, !isIncome);
        try (FileWriter writer = new FileWriter(file)) {
            JSONObject rootNode = new JSONObject();
            if (isIncome) {
                rootNode.put("incomesCategories", new JSONArray(transactionsList));
                rootNode.put("expensesCategories", new JSONArray(secondCategoryList));
            } else {
                rootNode.put("incomesCategories", new JSONArray(secondCategoryList));
                rootNode.put("expensesCategories", new JSONArray(transactionsList));
            }
            writer.write(rootNode.toString());
        }  catch (IOException | JSONException e) {
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
        }  catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveProfile(Context context, List<ProfileManagementActivity.Profile> profilesList) {
        ObjectMapper mapper = new ObjectMapper();
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(context.getFilesDir().getName(), Context.MODE_PRIVATE);
        File file =  new File(directory, "profiles.json");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(mapper.writeValueAsBytes(profilesList));
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ProfileManagementActivity.Profile> readProfiles(Context context) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(context.getFilesDir().getName(), Context.MODE_PRIVATE);
        File file =  new File(directory, "profiles.json");
        List<ProfileManagementActivity.Profile> profilesList = new ArrayList<>();

        try (InputStream stream = Files.newInputStream(file.toPath())) {
            JsonNode rootNode = new ObjectMapper().readTree(stream);

            for (JsonNode profile : rootNode) {
                profilesList.add(new ProfileManagementActivity.Profile(
                        profile.get("name").asText(),
                        profile.get("email").asText(),
                        profile.get("picture").asText(),
                        profile.get("pinCode").asInt()
                ));
            }
        } catch (IOException e) {
            return profilesList;
        }
        return profilesList;
    }
}
