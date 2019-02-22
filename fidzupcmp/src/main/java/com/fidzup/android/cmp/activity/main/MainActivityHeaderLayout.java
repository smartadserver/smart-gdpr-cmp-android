package com.fidzup.android.cmp.activity.main;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.fidzup.android.cmp.R;

public class MainActivityHeaderLayout extends ConstraintLayout {

    private ImageView headerLogo;
    private TextView headerTitle;
    private TextView disclaimer;
    private TextView vendorsLink;

    public MainActivityHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageView getLogo() {
        if (headerLogo == null)
            headerLogo = findViewById(R.id.logo);
        return headerLogo;
    }

    public TextView getTitle() {
        if (headerTitle == null)
            headerTitle = findViewById(R.id.title);
        return headerTitle;
    }

    public TextView getVendorsLink() {
        if (vendorsLink == null)
            vendorsLink = findViewById(R.id.viewVendors);
        return vendorsLink;
    }

    public TextView getDisclaimer() {
        if (disclaimer == null)
            disclaimer = findViewById(R.id.disclaimer);
        return disclaimer;
    }
}
