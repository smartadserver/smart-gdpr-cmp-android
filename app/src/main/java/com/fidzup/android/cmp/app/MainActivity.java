package com.fidzup.android.cmp.app;

import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fidzup.android.cmp.manager.ConsentManager;

public class MainActivity extends AppCompatActivity {

    static private final String IAB_CONSENT_STRING_KEY = "IABConsent_ConsentString";
    static private final String FULL_CONSENT_STRING_KEY = "FidzupCMP_ConsentString";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Do any additional setup.

        Button displayCMPButton = findViewById(R.id.display_cmp_button);
        displayCMPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ConsentManager.getSharedInstance().showConsentTool()) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Warning")
                            .setMessage("The consent tool UI can not be displayed yet. Please try again later.")
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            })
                            .show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Read GDPR ConsentString value from SharedPreferences and display it on the screen.
        String iabConsentString = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(IAB_CONSENT_STRING_KEY, "No consent string stored yet.");
        String consentString = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(FULL_CONSENT_STRING_KEY, "No consent string stored yet.");
        TextView consentStringTextView = findViewById(R.id.consentstring_textview);
        consentStringTextView.setText("IAB:\n" + iabConsentString + "\n\nFull:\n" + consentString);
    }
}
