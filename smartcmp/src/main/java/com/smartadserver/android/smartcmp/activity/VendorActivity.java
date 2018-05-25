package com.smartadserver.android.smartcmp.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.smartadserver.android.smartcmp.R;
import com.smartadserver.android.smartcmp.manager.ConsentManager;
import com.smartadserver.android.smartcmp.model.ConsentToolConfiguration;
import com.smartadserver.android.smartcmp.model.Feature;
import com.smartadserver.android.smartcmp.model.Purpose;
import com.smartadserver.android.smartcmp.model.Vendor;
import com.smartadserver.android.smartcmp.model.VendorList;

/**
 * Vendor activity.
 */

public class VendorActivity extends AppCompatActivity {

    private VendorList vendorList;
    private Vendor vendor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_activity_layout);

        // Setup the actionBar if any
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(ConsentManager.getSharedInstance().getConsentToolConfiguration().getConsentManagementVendorDetailTitle());
            // Display the back button
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        vendorList = getIntent().getParcelableExtra("vendor_list");
        vendor = getIntent().getParcelableExtra("vendor");

        bindViews();
    }

    private void bindViews() {
        ConsentToolConfiguration config = ConsentManager.getSharedInstance().getConsentToolConfiguration();

        // Setup the vendor name textview
        TextView vendorNameTextView = findViewById(R.id.vendor_name_textview);
        vendorNameTextView.setText(vendor.getName());

        // Setup the purposes section header textview
        TextView purposesSectionTitleTextView = findViewById(R.id.purposes_section_title_textview);
        purposesSectionTitleTextView.setText(config.getConsentManagementVendorDetailPurposesSectionHeaderText());

        TextView purposesTextView = findViewById(R.id.purposes_section_textview);

        // If no purpose, hide the purpose section.
        if (vendor.getPurposes().size() == 0) {
            purposesSectionTitleTextView.setVisibility(View.GONE);
            purposesTextView.setVisibility(View.GONE);
        } else {
            // else display all purposes names.
            for (int id : vendor.getPurposes()) {
                Purpose purpose = vendorList.getPurposeWithId(id);

                if (purpose != null) {
                    String tmp = "" + purposesTextView.getText();
                    tmp = tmp.length() != 0 ? tmp.concat("\n - " + purpose.getName()) : tmp.concat(" - " + purpose.getName());
                    purposesTextView.setText(tmp);
                }
            }
        }

        // Setup the features section textview
        TextView featuresSectionTitleTextView = findViewById(R.id.features_section_title_textview);
        featuresSectionTitleTextView.setText(config.getConsentManagementVendorDetailFeaturesSectionHeaderText());

        TextView featuresTextView = findViewById(R.id.features_section_textview);
        // If no feature, hide the feature section.
        if (vendor.getFeatures().size() == 0) {
            featuresSectionTitleTextView.setVisibility(View.GONE);
            featuresTextView.setVisibility(View.GONE);
        } else {
            // else display all features names.
            for (int id : vendor.getFeatures()) {
                Feature feature = vendorList.getFeatureWithId(id);

                if (feature != null) {
                    String tmp = "" + featuresTextView.getText();
                    tmp = tmp.length() != 0 ? tmp.concat("\n - " + feature.getName()) : tmp.concat(" - " + feature.getName());
                    featuresTextView.setText(tmp);
                }
            }
        }

        // Setup the privacy policy button
        Button privacyPolicyButton = findViewById(R.id.privacy_policy_button);
        privacyPolicyButton.getBackground().setColorFilter(getResources().getColor(R.color.actionButtonColor), PorterDuff.Mode.MULTIPLY);
        privacyPolicyButton.setText(config.getConsentManagementVendorDetailViewPrivacyButtonTitle());

        // Display the privacy policy button only if there is a privacy policy available.
        if (vendor.getPolicyURL() != null) {
            privacyPolicyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), PrivacyPolicyActivity.class);
                    intent.putExtra("privacy_policy_url", vendor.getPolicyURL().toString());
                    startActivity(intent);
                }
            });
        } else {
            privacyPolicyButton.setVisibility(View.GONE);
        }
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
