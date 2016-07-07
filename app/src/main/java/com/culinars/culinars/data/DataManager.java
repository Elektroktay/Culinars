package com.culinars.culinars.data;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.culinars.culinars.activity.MainActivity;
import com.culinars.culinars.data.structure.Comment;
import com.culinars.culinars.data.structure.Content;
import com.culinars.culinars.data.structure.Data;
import com.culinars.culinars.data.structure.Ingredient;
import com.culinars.culinars.data.structure.Instruction;
import com.culinars.culinars.data.structure.Recipe;
import com.culinars.culinars.data.structure.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class DataManager {

    public static final String USERS = "users";
    public static final String RECIPES = "recipes";
    public static final String INGREDIENTS = "ingredients";
    public static final String COMMENTS = "comments";
    public static final String RECOMMENDATIONS = "suggestions";
    public static final String SIMILAR_RECIPES = "similar_recipes";
    public static final String INSTRUCTIONS = "instructions";
    public static final String CONTENT = "contents";

    public static final String FAVORITES = "favorites";

    private static DataManager instance;
    private static Reference<User> currentUserInstance;

    private FirebaseApp firebase;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private Activity activity;

    private DataManager() {
        firebase = FirebaseApp.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(false);
    }

    public static DataManager getInstance() {
        if (instance == null)
            instance = new DataManager();
        return instance;
    }

    /*

                    BASIC GETTERS

     */

    public Reference<User> getUser(String id) {
        if (id.equals(getCurrentUser().getUid())) {
            if (currentUserInstance == null
                    || currentUserInstance.getValue() == null
                    || !currentUserInstance.getValue().uid.equals(id))
                currentUserInstance = getDynamicData(USERS, id, User.class);
            return currentUserInstance;
        }
        return getSingleData(USERS, id, User.class);
    }

    public Reference<User> getUser() {
        return getUser(getCurrentUser().getUid());
    }

    public Reference<Recipe> getRecipe(String id) {
        return getSingleData(RECIPES, id, Recipe.class);
    }

    public Reference<Comment> getComment(String id) {
        return getSingleData(COMMENTS, id, Comment.class);
    }

    public Reference<Ingredient> getIngredient(String id) {
        return getSingleData(INGREDIENTS, id, Ingredient.class);
    }

    public Reference<Instruction> getInstruction(String id) {
        return getSingleData(INSTRUCTIONS, id, Instruction.class);
    }

    public Reference<Content> getContent(String id) {
        return getSingleData(CONTENT, id, Content.class);
    }

    public ReferenceMultipleFromKeys<Recipe> getFavorites() {
        return getFavorites(getCurrentUser().getUid());
    }

    public ReferenceMultipleFromKeys<Recipe> getFavorites(String userId) {
        DatabaseReference ref = database.getReference(USERS).child(userId).child(FAVORITES);
        return new ReferenceMultipleFromKeys<>(ref, RECIPES, Recipe.class);
    }

    public ReferenceMultiple<Ingredient> getIngredients() {
        DatabaseReference ref = database.getReference(INGREDIENTS);
        return new ReferenceMultiple<>(ref, Ingredient.class);
    }

    public ReferenceMultipleFromKeys<Ingredient> getUserIngredients() {
        return getUserIngredients(getCurrentUser().getUid());
    }

    public ReferenceMultipleFromKeys<Ingredient> getUserIngredients(String userId) {
        DatabaseReference ref = database.getReference(USERS).child(userId).child(INGREDIENTS);
        return new ReferenceMultipleFromKeys<>(ref, INGREDIENTS, Ingredient.class);
    }

    public boolean hasIngredient(String ingredient) {
        return hasIngredient(getCurrentUser().getUid(), ingredient);
    }

    private boolean hasIngredient(String userId, String ingredient) {
        return getUser(userId).getValue() != null
                && getUser().getValue().ingredients != null
                && getUser().getValue().ingredients.containsKey(ingredient);
    }

    public ReferenceMultipleFromKeys<Recipe> getSimilarRecipes(String recipeId) {
        DatabaseReference ref = database.getReference(SIMILAR_RECIPES).child(recipeId);
        return new ReferenceMultipleFromKeys<>(ref, RECIPES, Recipe.class);
    }

    public ReferenceMultipleFromKeys<Instruction> getInstructions(String recipeId) {
        DatabaseReference ref = database.getReference(RECIPES).child(recipeId).child(INSTRUCTIONS);
        return new ReferenceMultipleFromKeys<>(ref, INSTRUCTIONS, Instruction.class);
    }

    public ReferenceMultipleFromKeys<Content> getRecipeContent(String recipeId) {
        DatabaseReference ref = database.getReference(RECIPES).child(recipeId).child(CONTENT);
        return new ReferenceMultipleFromKeys<>(ref, CONTENT, Content.class);
    }

    /*


                    BASIC PUTTERS


     */

    public void setFavorite(String recipeId, boolean isFavorite) {
        setFavorite(getCurrentUser().getUid(), recipeId, isFavorite);
    }

    public void setFavorite(String userId, String recipeId, boolean isFavorite) {
        if (recipeId == null) {
            recipeId = "";
        }
        if (userId == null) {
            userId = "";
        }
        DatabaseReference ref = database.getReference(USERS).child(userId).child(FAVORITES).child(recipeId);
        if (isFavorite)
            ref.setValue(true);
        else
            ref.removeValue();
    }

    public void setIngredient(String ingredient, boolean exists) {
        setIngredient(getCurrentUser().getUid(), ingredient, exists);
    }

    public void setIngredient(String userId, String ingredient, boolean exists) {
        if (ingredient == null) {
            ingredient = "";
        }
        if (userId == null) {
            userId = "";
        }
        DatabaseReference ref = database.getReference(USERS).child(userId).child(INGREDIENTS).child(ingredient);
        if (exists)
            ref.setValue(true);
        else
            ref.removeValue();
    }

    public String putInstruction(Instruction instruction) {
        DatabaseReference insRef = database.getReference(INSTRUCTIONS).push();
        instruction.uid = insRef.getKey();
        insRef.setValue(instruction);

        DatabaseReference recipeRef = database.getReference(RECIPES).child(instruction.recipe_id).child(INSTRUCTIONS);
        recipeRef.child(instruction.uid).setValue(true);

        return instruction.uid;
    }

    public String putComment(Comment comment) {
        DatabaseReference commentRef = database.getReference(COMMENTS).push();
        comment.uid = commentRef.getKey();
        commentRef.setValue(comment);

        DatabaseReference userRef = database.getReference(USERS).child(comment.user_id).child(COMMENTS);
        userRef.child(comment.uid).setValue(true);

        DatabaseReference recipeRef = database.getReference(RECIPES).child(comment.recipe_id).child(COMMENTS);
        recipeRef.child(comment.uid).setValue(comment.stars);

        return comment.uid;
    }

    public String putRecipe(Recipe recipe) {
        DatabaseReference recipeRef = database.getReference(RECIPES).push();
        recipe.uid = recipeRef.getKey();
        recipeRef.setValue(recipe);

        DatabaseReference userRef = database.getReference(USERS)
                .child(recipe.owner_id)
                .child("recipes");
        userRef.child(recipe.uid).setValue(true);

        return recipe.uid;
    }

    public void addNewUser(final String userId) {
        DatabaseReference ref = database.getReference(USERS).child(userId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(activity, "ADD_NEW_USER", Toast.LENGTH_SHORT).show();
                    User newUser = new User(userId, "", "",
                            new HashMap<String, Boolean>(), new HashMap<String, Boolean>(), new HashMap<String, Boolean>(),
                            new HashMap<String, Boolean>(), new HashMap<String, Boolean>());
                    dataSnapshot.getRef().setValue(newUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ADD_NEW_USER", "Error", databaseError.toException());
            }
        });
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public ReferenceMultipleFromKeys<Recipe> getRecommendations(String userId, int amount) {
        DatabaseReference ref = database.getReference(RECOMMENDATIONS).child(userId);
        Query query = ref.limitToLast(amount);
        return new ReferenceMultipleFromKeys<>(query, RECIPES, Recipe.class);
    }

    public <T extends Data> Reference<T> getSingleData(String parent, String child, Class<T> dataClass) {
        DatabaseReference ref = database.getReference(parent).child(child);
        return new Reference<>(ref, dataClass);
    }
    public <T extends Data> ReferenceDynamic<T> getDynamicData(String parent, String child, Class<T> dataClass) {
        DatabaseReference ref = database.getReference(parent).child(child);
        return new ReferenceDynamic<>(ref, dataClass);
    }

    public ReferenceMultiple<Ingredient> findIngredient(String completionText, int dataLimit) {
        DatabaseReference ref = database.getReference(INGREDIENTS);
        Query query = ref.limitToFirst(dataLimit).orderByKey().startAt(completionText).endAt(completionText+"zzzzzzzzzzzzzzz");
        return new ReferenceMultiple<>(query, Ingredient.class);
    }

    public ReferenceMultiple<Ingredient> getIngredients(int dataLimit) {
        DatabaseReference ref = database.getReference(INGREDIENTS);
        Query query = ref.limitToFirst(dataLimit);
        return new ReferenceMultiple<>(query, Ingredient.class);
    }

    public FirebaseAuth.AuthStateListener getAuthStateListener(@Nullable final Runnable loggedInAction, @Nullable final Runnable loggedOutAction) {
        return new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    //Logged in
                    if (loggedInAction != null)
                        loggedInAction.run();
                } else {
                    //Logged out
                    if (loggedOutAction != null)
                        loggedOutAction.run();
                }
            }
        };
    }

    public void init(final MainActivity activity) {
        this.activity = activity;
        auth.addAuthStateListener(getAuthStateListener(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "LOGGED IN: " + getCurrentUser().getUid(), Toast.LENGTH_LONG).show();
                addNewUser(getCurrentUser().getUid());
            }
        }, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "LOGGED OUT", Toast.LENGTH_SHORT).show();
                auth.signInAnonymously()
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isComplete()) {
                            Toast.makeText(activity, "ANONYMOUS LOGIN FAILURE", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(activity, "ANONYMOUS LOGIN SUCCESS", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }));
    }

    public void login() {
        if (getCurrentUser() == null) {
            auth.signInAnonymously();
        }
    }

    public void logout() {
        if (getCurrentUser() != null) {
            auth.signOut();
        }
    }

    public ReferenceMultiple<Recipe> searchRecipes(String searchQuery, final int searchMaxTime, final int searchMaxCalories, final ArrayList<String> searchIngredients, final String searchCuisine, boolean searchOnlyCurrentIngredients, int resultCount) {
        DatabaseReference ref = database.getReference(RECIPES);
        Query query = ref.limitToLast(100);
        if (searchQuery != null && searchQuery.length() > 0) {
            query = query.orderByChild("title");
            query = query.startAt(searchQuery);
            query = query.endAt(searchQuery + "zzzzzzzzzzzz");
        }
        final ReferenceMultiple<Recipe> result = new ReferenceMultiple<>(query, Recipe.class);
        result.addOnDataChangeListener(new ReferenceMultiple.OnDataChangeListener<Recipe>() {
            @Override
            public void onDataChange(Recipe newValue, int event) {
                //Filter time
                if (searchMaxTime > -1 && newValue.time > searchMaxTime)
                    result.getValues().remove(newValue);

                //Filter calories
                if (searchMaxCalories > -1 && newValue.calories > searchMaxCalories)
                    result.getValues().remove(newValue);

                //Filter Ingredients
                if (searchIngredients != null && searchIngredients.size() > 0) {
                    for (String i : searchIngredients) {
                        if (newValue.ingredients != null) {
                            if (!newValue.ingredients.containsKey(i)) {
                                result.getValues().remove(newValue);
                                break;
                            }
                        }
                    }
                }

                //Filter cuisine
                if (searchCuisine != null && searchCuisine.length() > 0
                        && !newValue.cuisine.equalsIgnoreCase(searchCuisine))
                    result.getValues().remove(newValue);

                //TODO: Filter only current
            }
        });
        return result;
    }

    public void downloadContent(final Content content, final OnDownloadFinishedListener listener, Activity currentActivity) {
        try {
            if (content != null && content.url != null && content.url.length() > 0) {
                if (content.type != Content.TYPE_VIDEO) {
                    StorageReference ref = storage.getReferenceFromUrl(content.url);
                    final File tempFile = File.createTempFile(content.uid, "cdownload");
                    ref.getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            if (content.type == Content.TYPE_IMAGE)
                                listener.onDownloadFinished(BitmapFactory.decodeFile(tempFile.getAbsolutePath()));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            listener.onDownloadFailed(e);
                        }
                    });
                } else {
                    listener.onDownloadFinished(content.url);
                }
            } else {
                listener.onDownloadFailed(new Exception("Content cannot be null."));
            }
        } catch (Exception e) {
            Log.w("DOWNLOAD_IMG", "Download failed!", e);
            listener.onDownloadFailed(e);
        }
    }

    public interface OnDownloadFinishedListener {
        void onDownloadFinished(Object result);
        void onDownloadFailed(Exception e);
    }
}
