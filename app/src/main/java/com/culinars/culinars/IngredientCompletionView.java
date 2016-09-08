package com.culinars.culinars;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.culinars.culinars.data.structure.Ingredient;
import com.tokenautocomplete.TokenCompleteTextView;

/**
 * Created by Oktayşen on 5/7/2016.
 */
public class IngredientCompletionView extends TokenCompleteTextView<Ingredient> {
    public IngredientCompletionView(Context context) {
        super(context);
    }

    @Override
    protected View getViewForObject(Ingredient ingredient) {
        View v = (LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_ingredient_autocomplete, (ViewGroup) getParent()));
        ((TextView) v.findViewById(R.id.autocomplete_ingredient_text)).setText(ingredient.name);
        return v;
    }



    @Override
    protected Ingredient defaultObject(String completionText) {
        return null;
    }
}
