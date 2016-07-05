package com.culinars.culinars.fragment.recipe;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.culinars.culinars.R;
import com.culinars.culinars.data.structure.Instruction;
import com.culinars.culinars.data.structure.Recipe;

public class InstructionsFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private Instruction currentInstruction;

    public InstructionsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static InstructionsFragment newInstance(Instruction currentInstruction) {
        InstructionsFragment fragment = new InstructionsFragment();
        fragment.currentInstruction = currentInstruction;
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
        return rootView;
    }
}