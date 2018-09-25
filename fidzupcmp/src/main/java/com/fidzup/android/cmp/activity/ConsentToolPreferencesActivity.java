package com.fidzup.android.cmp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.fidzup.android.cmp.R;
import com.fidzup.android.cmp.consentstring.ConsentString;
import com.fidzup.android.cmp.manager.ConsentManager;
import com.fidzup.android.cmp.model.ConsentToolConfiguration;
import com.fidzup.android.cmp.model.Purpose;
import com.fidzup.android.cmp.model.Editor;
import com.fidzup.android.cmp.model.VendorList;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Consent tool preferences activity.
 */

public class ConsentToolPreferencesActivity extends AppCompatActivity {

    private static final int EDITOR_PURPOSE_ACTIVITY_REQUEST_CODE = 0;
    private static final int PURPOSE_ACTIVITY_REQUEST_CODE = 1;
    private static final int VENDORS_LIST_ACTIVITY_REQUEST_CODE = 2;

    private ConsentString consentString;
    private VendorList vendorList;
    private Editor editor;

    private ListLayoutAdapter adapter;

    private ArrayList<Boolean> expandedCells = null;

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
        editor = getIntent().getParcelableExtra("editor");

        bindViews();
    }

    private void bindViews() {
        // Setup the recycler view.
        RecyclerView recyclerView = findViewById(R.id.preferences_recycler_view);
        ConsentToolConfiguration config = ConsentManager.getSharedInstance().getConsentToolConfiguration();

        int cellCount = vendorList.getPurposes().size() + 3;
        if (config.isEditorConfigured() && editor!= null) {
            cellCount =  vendorList.getPurposes().size() + editor.getPurposes().size() + 4;
        }

        expandedCells = new ArrayList<>(cellCount);

        while(cellCount-- > 0) expandedCells.add(false);
        Log.d("dbg2", ""+expandedCells.size());

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
            case EDITOR_PURPOSE_ACTIVITY_REQUEST_CODE:
                Purpose editorPurpose = data.getParcelableExtra("editor_purpose");
                boolean editorPurposeStatus = data.getBooleanExtra("editor_purpose_status", false);

                // Update the ConsentString with the new purpose status.
                consentString = editorPurposeStatus ? ConsentString.consentStringByAddingEditorPurposeConsent(editorPurpose.getId(), consentString) : ConsentString.consentStringByRemovingEditorPurposeConsent(editorPurpose.getId(), consentString);
                break;

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
        private Switch purposeSwitch;
        boolean expanded = false;

        PreferencesViewHolder(View itemView, boolean isPurposeCell, boolean isTitle) {
            super(itemView);

            if (isTitle) {
                mainTextView = itemView.findViewById(R.id.title_textview);
            } else if (isPurposeCell) {
                mainTextView = itemView.findViewById(R.id.main_textview);
                secondaryTextView = itemView.findViewById(R.id.secondary_textview);
                purposeSwitch = itemView.findViewById(R.id.purpose_sw);
            } else {
                mainTextView = itemView.findViewById(R.id.main_textview);
                secondaryTextView = itemView.findViewById(R.id.secondary_textview);
            }
        }

        void toggleExpand() {
            expand(!expanded);
        }

        void expand(boolean e) {
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            params.height = e ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) (48.0 *
                    itemView.getContext().getResources().getDisplayMetrics().density);

            ImageView icon = (ImageView) itemView.findViewById(R.id.expand_icon);
            icon.setImageResource(e ? R.drawable.ic_remove_black_24dp : R.drawable.ic_add_black_24dp);

            this.expanded = e;
        }

        void setMainText(@NonNull String text) {
            mainTextView.setText(text);
        }

        void setSecondaryText(@NonNull String text) {
            secondaryTextView.setText(text);
        }

        void setActive(boolean active) {
            purposeSwitch.setChecked(active);
        }

        void setSwitchListener(CompoundButton.OnCheckedChangeListener listener) {
            purposeSwitch.setOnCheckedChangeListener(listener);
        }

        void setOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
        }
    }

    /**
     * Custom layout adapter for the recycler view.
     */
    private class ListLayoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        final int VIEW_TYPE_EDITOR_TITLE = 0;
        final int VIEW_TYPE_EDITOR_CELL = 1;
        final int VIEW_TYPE_PURPOSE_TITLE = 2;
        final int VIEW_TYPE_PURPOSE_CELL = 3;
        final int VIEW_TYPE_VENDOR_TITLE = 4;
        final int VIEW_TYPE_VENDOR_CELL = 5;

        @Override
        public int getItemViewType(int position) {
            ConsentToolConfiguration config = ConsentManager.getSharedInstance().getConsentToolConfiguration();
            // 3 Title cells for Editor Purposes and Vendors, all purposes cells and the vendors summary cell.
            int editorOffset = 0;
            if (config.isEditorConfigured()) {
                editorOffset = editor.getPurposes().size() + 1;
                if (position == 0) {
                    return VIEW_TYPE_EDITOR_TITLE;

                } else if (position < editorOffset) {
                    return VIEW_TYPE_EDITOR_CELL;

                }
            }

            if (position == editorOffset) {
                return VIEW_TYPE_PURPOSE_TITLE;

            } else if (position <= editorOffset + vendorList.getPurposes().size()) {
                return VIEW_TYPE_PURPOSE_CELL;

            } else if (position == editorOffset + vendorList.getPurposes().size() + 1) {
                return VIEW_TYPE_VENDOR_TITLE;
            }
            return VIEW_TYPE_VENDOR_CELL;
        }

        @Override
        @NonNull
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v;
            boolean isTitle = true;

            if (viewType == VIEW_TYPE_PURPOSE_TITLE || viewType == VIEW_TYPE_VENDOR_TITLE || viewType == VIEW_TYPE_EDITOR_TITLE) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.title_cell, parent, false);
            }
            else if (viewType == VIEW_TYPE_PURPOSE_CELL || viewType == VIEW_TYPE_EDITOR_CELL) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.purpose_cell, parent, false);
                isTitle = false;
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.preferences_cell, parent, false);
                isTitle = false;
            }

            return new PreferencesViewHolder(v, viewType == VIEW_TYPE_PURPOSE_CELL || viewType == VIEW_TYPE_EDITOR_CELL, isTitle);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
            int ViewType = getItemViewType(position);
            final PreferencesViewHolder holder = (PreferencesViewHolder) viewHolder;
            ConsentToolConfiguration config = ConsentManager.getSharedInstance().getConsentToolConfiguration();
            int editorOffset = 0;
            if (config.isEditorConfigured()) {
                editorOffset = editor.getPurposes().size() + 1;
            }

            switch (ViewType) {
                case VIEW_TYPE_EDITOR_TITLE:
                    holder.setMainText(config.getConsentManagementScreenEditorSectionHeaderText());
                    break;

                case VIEW_TYPE_PURPOSE_TITLE:
                    holder.setMainText(config.getConsentManagementScreenPurposesSectionHeaderText());
                    break;

                case VIEW_TYPE_EDITOR_CELL:
                    final Purpose purpose = editor.getPurposes().get(position - 1);
                    final boolean isPurposeEnable = consentString.isEditorPurposeAllowed(purpose.getId());

                    holder.setMainText(purpose.getName());
                    holder.setSecondaryText(purpose.getDescription());
                    holder.setActive(isPurposeEnable);
                    holder.expand(expandedCells.get(position));

                    holder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            expandedCells.set(position, !expandedCells.get(position));
                            holder.expand(expandedCells.get(position));
                            notifyItemChanged(position);
                            /*
                            Intent intent = new Intent(getApplicationContext(), PurposeActivity.class);
                            intent.putExtra("editor_purpose", purpose);
                            intent.putExtra("editor_purpose_status", isPurposeEnable);
                            startActivityForResult(intent, EDITOR_PURPOSE_ACTIVITY_REQUEST_CODE);
                            */
                        }
                    });

                    holder.setSwitchListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            consentString = b ?
                                    ConsentString.consentStringByAddingEditorPurposeConsent(purpose.getId(), consentString) :
                                    ConsentString.consentStringByRemovingEditorPurposeConsent(purpose.getId(), consentString);
                        }
                    });

                    break;

                case VIEW_TYPE_PURPOSE_CELL:
                    final Purpose purpose_v = vendorList.getPurposes().get(position - (editorOffset+1));
                    final boolean isPurposeEnable_v = consentString.isPurposeAllowed(purpose_v.getId());

                    holder.setMainText(purpose_v.getName());
                    holder.setSecondaryText(purpose_v.getDescription());
                    holder.setActive(isPurposeEnable_v);
                    holder.expand(expandedCells.get(position));

                    holder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            expandedCells.set(position, !expandedCells.get(position));
                            holder.expand(expandedCells.get(position));
                            notifyItemChanged(position);
                            /*
                            Intent intent = new Intent(getApplicationContext(), PurposeActivity.class);
                            intent.putExtra("purpose", purpose_v);
                            intent.putExtra("purpose_status", isPurposeEnable_v);
                            startActivityForResult(intent, PURPOSE_ACTIVITY_REQUEST_CODE);
                            */
                        }
                    });

                    holder.setSwitchListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            consentString = b ?
                                    ConsentString.consentStringByAddingPurposeConsent(purpose_v.getId(), consentString) :
                                    ConsentString.consentStringByRemovingPurposeConsent(purpose_v.getId(), consentString);
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
//                            intent.putExtra("editor", editor);
                            intent.putExtra(VendorListActivity.EXTRA_VENDORLIST, vendorList);
                            intent.putExtra(VendorListActivity.EXTRA_CONTENTSTRING, consentString);
                            startActivityForResult(intent, VENDORS_LIST_ACTIVITY_REQUEST_CODE);
                        }
                    });
                    break;
            }
        }

        @Override
        public int getItemCount() {
            ConsentToolConfiguration config = ConsentManager.getSharedInstance().getConsentToolConfiguration();
            // 3 Title cells for Editor Purposes and Vendors, all purposes cells and the vendors summary cell.
            if (config.isEditorConfigured()) {
                return vendorList.getPurposes().size() + editor.getPurposes().size() + 4;
            } else {
                return vendorList.getPurposes().size() + 3;
            }
        }
    }
}
