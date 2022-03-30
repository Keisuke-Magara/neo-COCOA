package com.example.neo_cocoa.ui.hazard;

import android.content.Context;
import android.widget.TextView;

import com.example.neo_cocoa.R;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

public class GraphMarkerView extends com.github.mikephil.charting.components.MarkerView {

    private TextView marker;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public GraphMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        marker = findViewById(R.id.hazard_marker_view);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        marker.setText(String.valueOf((int)e.getY()) + "人");
        super.refreshContent(e, highlight);
    }
}
