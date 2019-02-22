package com.fidzup.android.cmp.activity.main;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.Button;

import com.fidzup.android.cmp.R;

public class MainActivityFooterLayout extends ConstraintLayout {

    public Button acceptAllBtn;
    public Button rejectAllBtn;
    public Button customChoicesBtn;

    public MainActivityFooterLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCustomActive(boolean active) {
        getCustomChoicesBtn().setEnabled(active);
    }

    public Button getAcceptAllBtn() {
        if (acceptAllBtn == null)
            acceptAllBtn = findViewById(R.id.button_accept);
        return acceptAllBtn;
    }

    public Button getCustomChoicesBtn() {
        if (customChoicesBtn == null)
            customChoicesBtn = findViewById(R.id.button_custom);
        return customChoicesBtn;
    }

    public Button getRejectAllBtn() {
        if (rejectAllBtn == null)
            rejectAllBtn = findViewById(R.id.button_reject);
        return rejectAllBtn;
    }
}
