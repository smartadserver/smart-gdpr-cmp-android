package com.fidzup.android.cmp.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fidzup.android.cmp.R;
import com.fidzup.android.cmp.consentstring.ConsentString;
import com.fidzup.android.cmp.manager.ConsentManager;
import com.fidzup.android.cmp.model.ConsentToolConfiguration;
import com.fidzup.android.cmp.model.Purpose;
import com.fidzup.android.cmp.model.VendorList;

/**
 * Consent tool preferences activity.
 */

public class ConsentToolPreferencesActivity extends AppCompatActivity {

    private static final int PURPOSE_ACTIVITY_REQUEST_CODE = 0;
    private static final int VENDORS_LIST_ACTIVITY_REQUEST_CODE = 1;

    private ConsentString consentString;
    private VendorList vendorList;

    private ListLayoutAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consent_tool_preferences_activity_layout);

        ConsentToolConfiguration config = ConsentManager.getSharedInstance().getConsentToolConfiguration();

        // Setup the action bar if any.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(config.getConsentManagementScreenTitle());
            // Display the back button
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        consentString = getIntent().getParcelableExtra("consent_string");
        vendorList = getIntent().getParcelableExtra("vendor_list");

        bindViews();
    }

    private void bindViews() {
        // Setup the recycler view.
        RecyclerView recyclerView = findViewById(R.id.preferences_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListLayoutAdapter();
        recyclerView.setAdapter(adapter);

        // Setup the positive button
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setText(ConsentManager.getSharedInstance().getConsentToolConfiguration().getConsentManagementSaveButtonTitle());
        saveButton.getBackground().setColorFilter(getResources().getColor(R.color.actionButtonColor), PorterDuff.Mode.MULTIPLY);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent result = new Intent();
                result.putExtra("consent_string", consentString);
                setResult(RESULT_OK, result);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // catch the back button pressed event.
        // Show alert that warns the user, and force it to click on buttons to quit.
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case PURPOSE_ACTIVITY_REQUEST_CODE:
                Purpose purpose = data.getParcelableExtra("purpose");
                boolean purposeStatus = data.getBooleanExtra("purpose_status", false);

                // Update the ConsentString with the new purpose status.
                consentString = purposeStatus ? ConsentString.consentStringByAddingPurposeConsent(purpose.getId(), consentString) : ConsentString.consentStringByRemovingPurposeConsent(purpose.getId(), consentString);
                break;

            case VENDORS_LIST_ACTIVITY_REQUEST_CODE:
                // Retrieve the new consentString.
                ConsentString consentString = data.getParcelableExtra("consent_string");
                if (consentString != null) {
                    this.consentString = consentString;
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

        // Refresh the RecyclerView to handle possible changes.
        adapter.notifyDataSetChanged();
    }

    /**
     * Custom view holder for Purpose cells.
     */
    private class PreferencesViewHolder extends RecyclerView.ViewHolder {

        private TextView mainTextView;
        private TextView secondaryTextView;

        PreferencesViewHolder(View itemView, boolean isTitle) {
            super(itemView);

            if (isTitle) {
                mainTextView = itemView.findViewById(R.id.title_textview);
            } else {
                mainTextView = itemView.findViewById(R.id.main_textview);
                secondaryTextView = itemView.findViewById(R.id.secondary_textview);
            }
        }

        void setMainText(@NonNull String text) {
            mainTextView.setText(text);
        }

        void setSecondaryText(@NonNull String text) {
            secondaryTextView.setText(text);
        }

        void setOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
        }
    }

    /**
     * Custom layout adapter for the recycler view.
     */
    private class ListLayoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        final int VIEW_TYPE_PURPOSE_TITLE = 0;
        final int VIEW_TYPE_PURPOSE_CELL = 1;
        final int VIEW_TYPE_VENDOR_TITLE = 2;
        final int VIEW_TYPE_VENDOR_CELL = 3;

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return VIEW_TYPE_PURPOSE_TITLE;

            } else if (position <= vendorList.getPurposes().size()) {
                return VIEW_TYPE_PURPOSE_CELL;

            } else if (position == vendorList.getPurposes().size() + 1) {
                return VIEW_TYPE_VENDOR_TITLE;
            }

            return VIEW_TYPE_VENDOR_CELL;
        }

        @Override
        @NonNull
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v;
            boolean isTitle = true;

            if (viewType == VIEW_TYPE_PURPOSE_TITLE || viewType == VIEW_TYPE_VENDOR_TITLE) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.title_cell, parent, false);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.preferences_cell, parent, false);
                isTitle = false;
            }

            return new PreferencesViewHolder(v, isTitle);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            int ViewType = getItemViewType(position);
            PreferencesViewHolder holder = (PreferencesViewHolder) viewHolder;
            ConsentToolConfiguration config = ConsentManager.getSharedInstance().getConsentToolConfiguration();

            switch (ViewType) {
                case VIEW_TYPE_PURPOSE_TITLE:
                    holder.setMainText(config.getConsentManagementScreenPurposesSectionHeaderText());
                    break;

                case VIEW_TYPE_PURPOSE_CELL:
                    final Purpose purpose = vendorList.getPurposes().get(position - 1);
                    final boolean isPurposeEnable = consentString.isPurposeAllowed(purpose.getId());

                    holder.setMainText(purpose.getName());
                    holder.setSecondaryText(isPurposeEnable ? config.getConsentManagementActivatedText() : config.getConsentManagementDeactivatedText());

                    holder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), PurposeActivity.class);
                            intent.putExtra("purpose", purpose);
                            intent.putExtra("purpose_status", isPurposeEnable);
                            startActivityForResult(intent, PURPOSE_ACTIVITY_REQUEST_CODE);
                        }
                    });
                    break;

                case VIEW_TYPE_VENDOR_TITLE:
                    holder.setMainText(config.getConsentManagementScreenVendorsSectionHeaderText());
                    break;

                case VIEW_TYPE_VENDOR_CELL:
                    holder.setMainText(config.getConsentManagementVendorListActivityAccessText());
                    int nbVendors = vendorList.getActivatedVendor().size();
                    holder.setSecondaryText(consentString.getAllowedActivatedVendorsCount(vendorList) + "/" + nbVendors);

                    holder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Start the VendorListActivity
                            Intent intent = new Intent(getApplicationContext(), VendorListActivity.class);
                            intent.putExtra("vendor_list", vendorList);
                            intent.putExtra("consent_string", consentString);
                            startActivityForResult(intent, VENDORS_LIST_ACTIVITY_REQUEST_CODE);
                        }
                    });
                    break;
            }
        }

        @Override
        public int getItemCount() {
            // 2 Title cells for Purposes and Vendors, all purposes cells and the vendors summary cell.
            return vendorList.getPurposes().size() + 3;
        }
    }
}
