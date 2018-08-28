package com.fidzup.android.cmp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fidzup.android.cmp.R;
import com.fidzup.android.cmp.consentstring.ConsentString;
import com.fidzup.android.cmp.manager.ConsentManager;
import com.fidzup.android.cmp.model.ConsentToolConfiguration;

/**
 * Consent tool activity.
 */

public class ConsentToolActivity extends AppCompatActivity {

    static private final int PREFERENCES_REQUEST_CODE = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consent_tool_activity_layout);

        ConsentToolConfiguration config = ConsentManager.getSharedInstance().getConsentToolConfiguration();

        // Setup main logo
        ImageView mainLogoImageView = findViewById(R.id.fidzup_logo);
        mainLogoImageView.setImageResource(config.getHomeScreenLogoDrawableRes());

        // Setup main text
        TextView mainTextView = findViewById(R.id.main_textview);
        mainTextView.setText(config.getHomeScreenText());

        // Setup the accept and close button
        Button closeButton = findViewById(R.id.close_button);
        closeButton.setText(config.getHomeScreenCloseButtonTitle());
        closeButton.getBackground().setColorFilter(getResources().getColor(R.color.actionButtonColor), PorterDuff.Mode.MULTIPLY);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close UI.
                // Accept all new vendors or purposes.
                ConsentString consentString = getIntent().getParcelableExtra("consent_string");
                ConsentManager.getSharedInstance().consentToolClosedWithConsentString(consentString.getConsentString());
                finish();
            }
        });

        // Setup the refuse & close button
        Button closeRefuseButton = findViewById(R.id.close_refuse_button);
        closeRefuseButton.setText(config.getHomeScreenCloseRefuseButtonTitle());
        closeRefuseButton.getBackground().setColorFilter(getResources().getColor(R.color.actionButtonColor), PorterDuff.Mode.MULTIPLY);
        closeRefuseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Close UI.
                // Refuse all new vendors or purposes.
                ConsentString consentString = getIntent().getParcelableExtra("consent_string");
                ConsentManager.getSharedInstance().consentToolClosedWithConsentString(consentString.getConsentString());
                ConsentManager.getSharedInstance().revokeAllPurposes();
                finish();
            }
        });

        // Setup the manage consent button
        Button manageButton = findViewById(R.id.manage_button);
        manageButton.setText(config.getHomeScreenManageConsentButtonTitle());
        manageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the ConsentToolPreferencesActivity
                Intent intent = new Intent(getApplicationContext(), ConsentToolPreferencesActivity.class);
                intent.putExtra("consent_string", getIntent().getParcelableExtra("consent_string"));
                intent.putExtra("vendor_list", getIntent().getParcelableExtra("vendor_list"));
                startActivityForResult(intent, PREFERENCES_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        // Return the new consent string.
        ConsentString consentString = data.getParcelableExtra("consent_string");

        ConsentManager.getSharedInstance().consentToolClosedWithConsentString(consentString.getConsentString());

        finish();
    }

    @Override
    public void onBackPressed() {
        // catch the back button pressed event.
        // Show alert that warns the user, and force it to click on buttons to quit.
        ConsentToolConfiguration config = ConsentManager.getSharedInstance().getConsentToolConfiguration();
        new AlertDialog.Builder(this)
                .setTitle(config.getConsentManagementAlertDialogTitle())
                .setMessage(config.getConsentManagementAlertDialogText())
                .setPositiveButton(config.getConsentManagementAlertDialogPositiveButtonTitle(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Return the initial consent string.
                        ConsentString consentString = getIntent().getParcelableExtra("consent_string");
                        ConsentManager.getSharedInstance().consentToolClosedWithConsentString(consentString.getConsentString());

                        ConsentToolActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(config.getConsentManagementAlertDialogNegativeButtonTitle(), null)
                .show();
    }
}
