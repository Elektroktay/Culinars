package com.culinars.culinars.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import com.culinars.culinars.data.structure.Content;
import com.culinars.culinars.data.structure.Recipe;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static com.culinars.culinars.data.FB.fb;

/**
 * Used to be the class that handled all data connections. Now FB is used instead.
 */
public class DM {

    /**
     * Searches for recipes that fit the given parameters. When results arrive, the listener will be invoked.
     * @param searchQuery Query text to be searched for. Set to null or "" if not a parameter.
     * @param searchMaxTime Maximum amount of time the recipes can take in minutes. -1 if not a parameter.
     * @param searchMaxCalories Maximum amount of calories recipes can have. -1 if not a parameter.
     * @param searchIngredients List of ingredients recipes should contain. null or empty list if not a parameter. (Not implemented)
     * @param searchCuisine The cuisine recipes should be a part of. null or "" if not a parameter.
     * @param searchOnlyCurrentIngredients true if recipes should only contain ingredients that are in the fridge. (Not implemented.)
     * @param resultCount Max number of results this should return.
     * @param listener Listener to be invoked after search is complete.
     */
    public static void searchRecipes(String searchQuery, final int searchMaxTime, final int searchMaxCalories, final ArrayList<String> searchIngredients, final String searchCuisine, boolean searchOnlyCurrentIngredients, int resultCount,
                              final FB.CompleteListener listener) {
        DatabaseReference ref = (DatabaseReference) fb().recipe().ref();
        Query query = ref.limitToLast(100);
        if (searchQuery != null && searchQuery.length() > 0) {
            query = query.orderByChild("title");
            query = query.startAt(searchQuery);
            query = query.endAt(searchQuery + "zzzzzzzzzzzz");
        }

        FB.Result request = fb().withQuery(query).getOnce();
        request.onComplete(new FB.CompleteListener() {
            ArrayList<DataSnapshot> results = new ArrayList<>();
            @Override
            public void onComplete(List<DataSnapshot> res) {
                for (DataSnapshot s : res) {
                    Recipe curr = Recipe.from(s);
                    if (
                            (searchMaxTime == -1 || curr.time < searchMaxTime)
                                    && (searchMaxCalories == -1 || curr.calories < searchMaxCalories)
                                    && (searchCuisine == null || searchCuisine.length() == 0 || searchCuisine.equalsIgnoreCase(curr.cuisine))) {
                        if (searchIngredients != null && searchIngredients.size() > 0) {
                            if (curr.ingredients != null) {
                                boolean willAdd = true;
                                for (String ing : searchIngredients) {
                                    if (!curr.ingredients.containsKey(ing)) {
                                        willAdd = false;
                                        break;
                                    }
                                }
                                if (willAdd) results.add(s);
                            }
                        } else {
                            results.add(s);
                        }
                    }
                }
                if (listener != null) listener.onComplete(results);
            }
        });
    }

    @Deprecated
    public void downloadContent(final Content content, final OnDownloadFinishedListener listener) {
        try {
            if (content != null && content.url != null && content.url.length() > 0) {
                if (content.type != Content.TYPE_VIDEO) {
                    StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(content.url);
                    //final File tempFile = File.createTempFile(content.uid, "cdownload");
                    ref.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap result = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            if (result == null || result.getByteCount() < 100) {
                                Log.w("FUCK", "ME");
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inSampleSize = 5;
                                result = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                            }
                            listener.onDownloadFinished(result);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            listener.onDownloadFailed(e);
                        }
                    });
/*                    ref.getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            if (content.type == Content.TYPE_IMAGE) {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inSampleSize = 2;
                                listener.onDownloadFinished(BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options));
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            listener.onDownloadFailed(e);
                        }
                    });*/
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
