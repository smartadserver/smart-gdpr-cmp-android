package com.fidzup.android.cmp.activity.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.fidzup.android.cmp.R;
import com.fidzup.android.cmp.activity.ConsentActivity;
import com.fidzup.android.cmp.activity.VendorListActivity;
import com.fidzup.android.cmp.consentstring.ConsentString;
import com.fidzup.android.cmp.manager.ConsentManager;
import com.fidzup.android.cmp.model.ConsentToolConfiguration;
import com.fidzup.android.cmp.model.Purpose;

import java.util.ArrayList;

public class MainConsentActivity extends ConsentActivity {

    ConsentString consentString;
    ConsentToolConfiguration cmpConfig;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cmpConfig = ConsentManager.getSharedInstance().getConsentToolConfiguration();
        consentString = getConsentStringFromIntent();

        setContentView(R.layout.main_activity_layout);

        RecyclerView table = findViewById(R.id.table);
        table.setLayoutManager(new LinearLayoutManager(this));
        table.setAdapter(new MyAdapter(getVendorListFromIntent().getPurposes()));

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(table.getContext(),
                DividerItemDecoration.VERTICAL);
        table.addItemDecoration(mDividerItemDecoration);
        table.setItemAnimator(null);
    }

    @Override
    public void onBackPressed() {
        if(isRootConsentActivity()) { // this activity should always be root but checking anyway

            // catch the back button pressed event.
            // Show alert that warns the user, and force it to click on buttons to quit.
            new AlertDialog.Builder(this)
                    .setTitle(cmpConfig.getConsentManagementAlertDialogTitle())
                    .setMessage(cmpConfig.getConsentManagementAlertDialogText())
                    .setPositiveButton(cmpConfig.getConsentManagementAlertDialogPositiveButtonTitle(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Return the initial consent string.
                            // MainConsentActivity.this.storeConsentString(getConsentStringFromIntent());
                            // accept all purposes
                            ConsentManager.getSharedInstance().allowAllPurposes();
                            MainConsentActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(cmpConfig.getConsentManagementAlertDialogNegativeButtonTitle(), null)
                    .show();
        }
        else
            super.onBackPressed();
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private final int TYPE_HEADER = 0;
        private final int TYPE_PURPOSE = 1;
        private final int TYPE_FOOTER = 2;

        ArrayList<PurposeState> dataset;
        boolean switchTouched;

        MyAdapter(ArrayList<Purpose> purposes) {
            super();

            dataset = new ArrayList<>();
            int sum = 0;
            for (Purpose p : purposes) {
                sum += consentString.isPurposeAllowed(p.getId()) ? 1 : 0;
                dataset.add(new PurposeState(p, false));
            }

            switchTouched = (sum != 0 && sum != dataset.size());
        }

        // Create new views (invoked by the layout manager)
        @NonNull
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            int res;

            if (viewType == TYPE_HEADER)
                res = R.layout.main_activity_header;
            else if (viewType == TYPE_FOOTER)
                res = R.layout.main_activity_footer;
            else
                res = R.layout.purpose_cell;

            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(res, parent, false));
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

            if (holder.getItemViewType() == TYPE_HEADER) {
                MainActivityHeaderLayout header = (MainActivityHeaderLayout)holder.itemView;
                header.getLogo().setImageResource(cmpConfig.getHomeScreenLogoDrawableRes());
                header.getTitle().setText(cmpConfig.getConsentManagementScreenTitle());
                header.getDisclaimer().setText(cmpConfig.getHomeScreenText());
                header.getVendorsLink().setText(cmpConfig.getConsentManagementVendorListActivityAccessText());
                header.getVendorsLink().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Start the VendorListActivity
                        Intent intent = getIntentForConsentActivity(VendorListActivity.class,
                                getConsentStringFromIntent(),
                                getVendorListFromIntent(),
                                getEditorFromIntent());
                        intent.putExtra(VendorListActivity.EXTRA_READONLY, true);
                        startActivityForResult(intent, 0);
                    }
                });
            }
            else if (holder.getItemViewType() == TYPE_FOOTER) {
                MainActivityFooterLayout footer = (MainActivityFooterLayout)holder.itemView;
                footer.getAcceptAllBtn().setText(cmpConfig.getHomeScreenCloseButtonTitle());
                footer.getAcceptAllBtn().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ConsentManager.getSharedInstance().allowAllPurposes();
                        MainConsentActivity.this.finish();
                    }
                });
                footer.getRejectAllBtn().setText(cmpConfig.getHomeScreenCloseRefuseButtonTitle());
                footer.getRejectAllBtn().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ConsentManager.getSharedInstance().revokeAllPurposes();
                        MainConsentActivity.this.finish();
                    }
                });
                footer.getCustomChoicesBtn().setText(cmpConfig.getHomeScreenManageConsentButtonTitle());
                footer.getCustomChoicesBtn().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isRootConsentActivity())
                            storeConsentString(consentString);
                        else
                            setResultConsentString(consentString);
                        finish();
                    }
                });
                footer.setCustomActive(switchTouched);
            }
            else {
                final ExpandablePurposeLayout exLayout = (ExpandablePurposeLayout)holder.itemView;
                final PurposeState purposeState = dataset.get(position-1);
                final Purpose purpose = purposeState.purpose;

                exLayout.setup(purpose, consentString.isPurposeAllowed(purpose.getId()), purposeState.expanded);
                exLayout.setExpandButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean newExpanded = !exLayout.expanded;
                        exLayout.setExpanded(newExpanded);
                        purposeState.expanded = newExpanded;
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                });
                exLayout.setSwitchChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (!switchTouched) {
                            switchTouched = true;
                            notifyItemChanged(getItemCount() - 1);
                        }
                        consentString = b ?
                                ConsentString.consentStringByAddingPurposeConsent(purpose.getId(), consentString) :
                                ConsentString.consentStringByRemovingPurposeConsent(purpose.getId(), consentString);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return dataset.size() + 2;
        }

        @Override
        public int getItemViewType (int position) {
            if(position == 0)
                return TYPE_HEADER;
            else if(position == getItemCount()-1)
                return TYPE_FOOTER;
            else
                return TYPE_PURPOSE;
        }

        private class PurposeState {
            public Purpose purpose;
            boolean expanded;
            PurposeState(Purpose p, boolean e) {
                purpose = p;
                expanded = e;
            }
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            MyViewHolder(View view) {
                super(view);
            }
        }
    }
}
