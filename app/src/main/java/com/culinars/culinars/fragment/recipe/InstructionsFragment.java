package com.culinars.culinars.fragment.recipe;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.culinars.culinars.R;
import com.culinars.culinars.Timer;
import com.culinars.culinars.data.DataManager;
import com.culinars.culinars.data.OnDataChangeListener;
import com.culinars.culinars.data.Reference;
import com.culinars.culinars.data.structure.Content;
import com.culinars.culinars.data.structure.Instruction;
import com.culinars.culinars.data.structure.Recipe;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class InstructionsFragment extends Fragment {

    private Instruction currentInstruction;
    private Bitmap currentImage;
    private String videoUrl;
    private boolean downloadIsFailed;

    private boolean timerEnabled = false;
    private Timer timer;
    private PieChart timerView;
    private ProgressBar mediaLoading;

    public InstructionsFragment() {
    }

    public static InstructionsFragment newInstance(Instruction currentInstruction) {
        final InstructionsFragment fragment = new InstructionsFragment();
        fragment.currentInstruction = currentInstruction;
        fragment.downloadIsFailed = false;
        if (currentInstruction.content_id != null && currentInstruction.content_id.length() > 0)
            DataManager.getInstance().getContent(currentInstruction.content_id)
                .addOnDataReadyListener(new OnDataChangeListener<Content>() {
                    @Override
                    public void onDataChange(Content newValue, int event) {
                        DataManager.getInstance().downloadContent(newValue, new DataManager.OnDownloadFinishedListener() {
                            @Override
                            public void onDownloadFinished(Object result) {
                                if (result instanceof Bitmap) {
                                    //It's an image
                                    fragment.currentImage = (Bitmap) result;
                                } else if (result instanceof String) {
                                    //It's a video
                                    fragment.currentImage = BitmapFactory.decodeResource(fragment.getResources(), R.drawable.pizza);
                                    fragment.videoUrl = (String) result;
                                }
                                fragment.getFragmentManager().beginTransaction()
                                        .detach(fragment).attach(fragment).commit(); //Refreshes fragment.
                            }

                            @Override
                            public void onDownloadFailed(Exception e) {
                                Log.w("DOWNLOAD", "Download failed.", e);
                                fragment.setLoadingEnabled(false);
                            }
                        });
                    }
                });
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_instructions, container, false);
        TextView numberView = (TextView) rootView.findViewById(R.id.instructions_number);
        numberView.setText("" + currentInstruction.position);

        TextView textView = (TextView) rootView.findViewById(R.id.instructions_text);
        textView.setText(currentInstruction.text);

        FrameLayout mediaContainer = (FrameLayout) rootView.findViewById(R.id.instructions_media_container);
        mediaLoading = (ProgressBar) rootView.findViewById(R.id.instructions_loading);
        RoundedImageView mediaImage = (RoundedImageView) rootView.findViewById(R.id.instructions_image);
        if (currentInstruction.content_id == null || currentInstruction.content_id.length() == 0)
            mediaContainer.setVisibility(View.GONE);
        else if (currentImage == null) {
            mediaContainer.setVisibility(View.VISIBLE);
            mediaImage.setVisibility(View.INVISIBLE);
            mediaLoading.setVisibility(View.VISIBLE);
        } else {
            mediaContainer.setVisibility(View.VISIBLE);
            mediaImage.setVisibility(View.VISIBLE);
            if (videoUrl != null || videoUrl.length() > 0) {
                mediaImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent videoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                        videoIntent.setDataAndType(Uri.parse(videoUrl), "video/*");
                        startActivity(videoIntent);
                    }
                });
            }
            mediaLoading.setVisibility(View.INVISIBLE);
            mediaImage.setImageBitmap(currentImage);
        }
        if (downloadIsFailed)
            mediaLoading.setVisibility(View.GONE);


        timerView = (PieChart) rootView.findViewById(R.id.instructions_timer);
        timerView.setUsePercentValues(false);
        timerView.setDescription("");

        timerView.setDragDecelerationFrictionCoef(0.95f);
        timerView.setDrawCenterText(false);
        //timerView.setCenterText("");

        timerView.setDrawHoleEnabled(true);
        timerView.setHoleColor(Color.parseColor("#33FFFFFF"));

        timerView.setTransparentCircleColor(Color.GREEN);
        timerView.setTransparentCircleAlpha(255);

        timerView.setHoleRadius(90f);
        timerView.setTransparentCircleRadius(61f);

        timerView.setRotationAngle(270f);
        timerView.setRotationEnabled(false);
        timerView.setHighlightPerTapEnabled(false);
        timerView.getLegend().setEnabled(false);
        timerView.setDrawEntryLabels(false);
        timerView.setDrawSlicesUnderHole(false);

        timerView.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        refreshData(60, 60);

        timer = new Timer(60, true);
        timer.addOnTimeChangedListener(new Timer.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(final int currentSecs) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshData(currentSecs, 60);
                    }
                });
            }
        });
        //timer.startTimer();


        return rootView;
    }

    @Override
    public void onDetach() {
        timer.removeListeners();
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        timer.removeListeners();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        timer.removeListeners();
        super.onStop();
    }

    private void setLoadingEnabled(boolean enabled) {
        if (mediaLoading != null) {
            if (enabled)
                mediaLoading.setVisibility(View.VISIBLE);
            else
                mediaLoading.setVisibility(View.INVISIBLE);
        }
    }

    private void refreshData(int current, int max) {
        Log.w("REFRESH_DATA", "current:" + current + " max:" + max);
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(max-current));
        entries.add(new PieEntry(current));

        PieDataSet dataSet = new PieDataSet(entries, null);
        dataSet.setColors(ColorTemplate.createColors(new int[]{
                Color.TRANSPARENT,
                Color.WHITE
        }));
        dataSet.setDrawValues(false);

        PieData data = new PieData(dataSet);
        data.setHighlightEnabled(false);
        data.setDrawValues(false);
        data.setValueTextColor(Color.BLUE);
        timerView.setData(data);
        timerView.invalidate();
        timerView.setDrawEntryLabels(false);
    }


}