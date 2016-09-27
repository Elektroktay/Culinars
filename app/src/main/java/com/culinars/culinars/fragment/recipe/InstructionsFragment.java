package com.culinars.culinars.fragment.recipe;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.culinars.culinars.R;
import com.culinars.culinars.Timer;
import com.culinars.culinars.data.FB;
import com.culinars.culinars.data.structure.Content;
import com.culinars.culinars.data.structure.Instruction;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A Fragment that holds an instruction in a ViewPager.
 */
public class InstructionsFragment extends Fragment {

    private Instruction currentInstruction;
/*    private Bitmap currentImage;
    private String videoUrl;
    private boolean downloadIsFailed;*/

    private boolean timerEnabled = false;
    private Timer timer;
    private PieChart timerView;
    private ProgressBar mediaLoading;
    private RoundedImageView mediaImage;

    public InstructionsFragment() {
    }

    /**
     * Creates a new InstructionsFragment. Use this instead of the constructor.
     */
    public static InstructionsFragment newInstance(Instruction currentInstruction) {
        final InstructionsFragment fragment = new InstructionsFragment();
        fragment.currentInstruction = currentInstruction;
//        fragment.downloadIsFailed = false;
        if (currentInstruction.content_id != null && currentInstruction.content_id.length() > 0)
            Content.load(currentInstruction.content_id).getOnce().onGet(new FB.GetListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Content content = Content.from(dataSnapshot);
                    if (content != null) {
                        if (content.type == Content.TYPE_IMAGE)
                            Picasso.with(fragment.getContext()).load(content.url).into(fragment.mediaImage);
                        else {
                            fragment.mediaImage.setImageResource(R.drawable.pizza);
                            fragment.mediaImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent videoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(content.url));
                                    videoIntent.setDataAndType(Uri.parse(content.url), "video/*");
                                    fragment.startActivity(videoIntent);
                                }
                            });
                        }
                        fragment.mediaLoading.setVisibility(View.GONE);
                    }
                }
            });
        return fragment;
    }

    /**
     * This method is called before xml is loaded onto the screen (inflating).
     * Inflation must be done here.
     * @return The view that was created as a result.
     */
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
        mediaImage = (RoundedImageView) rootView.findViewById(R.id.instructions_image);
        if (currentInstruction.content_id == null || currentInstruction.content_id.length() == 0)
            mediaContainer.setVisibility(View.GONE);
        else {
            mediaContainer.setVisibility(View.VISIBLE);
            mediaImage.setVisibility(View.INVISIBLE);
            mediaLoading.setVisibility(View.VISIBLE);
        }

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

/*    private void setLoadingEnabled(boolean enabled) {
        if (mediaLoading != null) {
            if (enabled)
                mediaLoading.setVisibility(View.VISIBLE);
            else
                mediaLoading.setVisibility(View.INVISIBLE);
        }
    }*/

    /**
     * Refreshes the pie chart (timer view) with the given parameters.
     * @param current Amount of time passed so far. (Unit independent.)
     * @param max Maximum amount of time. (Unit independent.)
     */
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