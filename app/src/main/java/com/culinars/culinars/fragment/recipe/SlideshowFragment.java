package com.culinars.culinars.fragment.recipe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.culinars.culinars.R;
import com.culinars.culinars.data.DataManager;
import com.culinars.culinars.data.OnDataChangeListener;
import com.culinars.culinars.data.Reference;
import com.culinars.culinars.data.ReferenceMultipleFromKeys;
import com.culinars.culinars.data.structure.Content;
import com.culinars.culinars.data.structure.Data;
import com.culinars.culinars.data.structure.Recipe;


public class SlideshowFragment extends Fragment {

    private Content currentContent;
    private Bitmap currentImage;
    private String videoUrl;

    ProgressBar loadingBar;
    ImageView slideshowImage;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_slideshow, container, false);
        slideshowImage = (ImageView) rootView.findViewById(R.id.slideshow_image);
        loadingBar = (ProgressBar) rootView.findViewById(R.id.slideshow_loading);
        if (currentContent == null || currentImage == null) {
            slideshowImage.setVisibility(View.INVISIBLE);
        } else {
            slideshowImage.setImageBitmap(currentImage);
            if (videoUrl != null && videoUrl.length() > 0) {
                slideshowImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent videoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                        videoIntent.setDataAndType(Uri.parse(videoUrl), "video/*");
                        startActivity(videoIntent);
                    }
                });
            }
            setLoadingEnabled(false);
        }
        return rootView;
    }

    public static SlideshowFragment newInstance(Content content) {
        final SlideshowFragment fragment = new SlideshowFragment();
        fragment.currentContent = content;
        if (content != null) {
            DataManager.getInstance().downloadContent(content, new DataManager.OnDownloadFinishedListener() {
                @Override
                public void onDownloadFinished(Object result) {
                    if (result instanceof Bitmap) {
                        //It's an image
                        fragment.currentImage = (Bitmap) result;
                        fragment.slideshowImage.setImageBitmap(fragment.currentImage);
                        fragment.slideshowImage.setVisibility(View.VISIBLE);
                        fragment.setLoadingEnabled(false);
                    } else if (result instanceof String) {
                        //It's a video
                        fragment.currentImage = BitmapFactory.decodeResource(fragment.getResources(), R.drawable.pizza);
                        fragment.slideshowImage.setImageBitmap(fragment.currentImage);
                        fragment.slideshowImage.setVisibility(View.VISIBLE);
                        fragment.setLoadingEnabled(false);
                        fragment.videoUrl = (String) result;
                    }
                    if (fragment.getFragmentManager() != null)
                        fragment.getFragmentManager().beginTransaction()
                                .detach(fragment).attach(fragment).commit(); //Refreshes fragment.
                }

                @Override
                public void onDownloadFailed(Exception e) {
                    Log.w("DOWNLOAD", "Download failed.", e);
                    fragment.setLoadingEnabled(false);
                }
            });
        } else {
            Log.w("WTF", "WTF");
        }

        return fragment;
    }

    public void setLoadingEnabled(boolean enabled) {
        if (loadingBar != null) {
            if (enabled)
                loadingBar.setVisibility(View.VISIBLE);
            else
                loadingBar.setVisibility(View.INVISIBLE);
        }
    }

    public static class PagerAdapter extends FragmentPagerAdapter {

        ReferenceMultipleFromKeys<Content> data;
        public PagerAdapter(FragmentManager fm, Recipe currentRecipe) {
            super(fm);
            data = DataManager.getInstance().getRecipeContent(currentRecipe.uid);
            data.addOnDataChangeListener(new OnDataChangeListener<Content>() {
                @Override
                public void onDataChange(Content newValue, int event) {
                    Log.w("DATA_REC", "DATA_RECEIVED:" + newValue.url);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public Fragment getItem(int position) {
            return SlideshowFragment.newInstance(data.getValueAt(position));
        }

        @Override
        public int getCount() {
            if (data != null)
                return data.getValues().size();
            else
                return 1;
        }
    }
}
