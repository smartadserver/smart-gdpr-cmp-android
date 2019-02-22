package com.fidzup.android.cmp.activity.main;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.fidzup.android.cmp.R;
import com.fidzup.android.cmp.model.Purpose;

public class ExpandablePurposeLayout extends ConstraintLayout {

    public boolean expanded = false;

    public ExpandablePurposeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setExpanded(boolean big) {
        View v = findViewById(R.id.description);
        v.setVisibility(big ? View.VISIBLE : View.GONE);

        ImageView icon = findViewById(R.id.expand_icon);
        icon.setImageResource(big ? R.drawable.ic_expand_less_24dp : R.drawable.ic_expand_more_24dp);

        expanded = big;
    }

    public void setup(Purpose purpose, boolean on, boolean expanded) {
        // disable listener before setup
        setSwitchChangeListener(null);

        ((TextView)findViewById(R.id.description)).setText(purpose.getDescription());
        ((TextView)findViewById(R.id.title)).setText(purpose.getName());
        ((Switch)findViewById(R.id.enablePurpose)).setChecked(on);
        setExpanded(expanded);
    }

    public void setExpandButtonClickListener(View.OnClickListener ocl) {
        findViewById(R.id.expand_icon).setOnClickListener(ocl);
    }

    public void setSwitchChangeListener(CompoundButton.OnCheckedChangeListener occl) {
        ((Switch)findViewById(R.id.enablePurpose)).setOnCheckedChangeListener(occl);
    }
}