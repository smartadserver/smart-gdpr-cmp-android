package com.smartadserver.android.smartcmp.activity;

import android.drm.DrmStore;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;

import com.smartadserver.android.smartcmp.R;
import com.smartadserver.android.smartcmp.manager.ConsentManager;

/**
 * Privacy policy activity.
 */

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_policy_activity_layout);

        // Setup the actionBar if any
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(ConsentManager.getSharedInstance().getConsentToolConfiguration().getConsentManagementPrivacyPolicyTitle());
            // Display the back button
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        String url = getIntent().getStringExtra("privacy_policy_url");

        WebView webview = findViewById(R.id.webview);
        webview.loadUrl(url);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
