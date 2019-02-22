package com.fidzup.android.cmp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fidzup.android.cmp.consentstring.ConsentString;
import com.fidzup.android.cmp.manager.ConsentManager;
import com.fidzup.android.cmp.model.Editor;
import com.fidzup.android.cmp.model.VendorList;

public abstract class ConsentActivity extends AppCompatActivity {

    public final static String EXTRA_CONTENTSTRING = "content_string";
    public final static String EXTRA_VENDORLIST = "vendor_list";
    public final static String EXTRA_EDITOR = "editor";

    public final static String EXTRA_ISROOTCONSENTACTIVITY = "is_root_consent_activity";

    public final static String EXTRA_CONTENTSTRING_RETURN = "content_string_return";

    protected ConsentString getConsentStringFromIntent() {
        return (ConsentString) getIntent().getParcelableExtra(ConsentActivity.EXTRA_CONTENTSTRING);
    }
    protected VendorList getVendorListFromIntent() {
        return (VendorList) getIntent().getParcelableExtra(ConsentActivity.EXTRA_VENDORLIST);
    }
    protected Editor getEditorFromIntent() {
        return (Editor) getIntent().getParcelableExtra(ConsentActivity.EXTRA_EDITOR);
    }

    protected void setResultConsentString(ConsentString cs) {
        Intent result = new Intent();
        result.putExtra(EXTRA_CONTENTSTRING_RETURN, cs);
        setResult(RESULT_OK, result);
    }

    protected ConsentString getResultConsentString(Intent data) {
        return data.getParcelableExtra(EXTRA_CONTENTSTRING_RETURN);
    }

    protected boolean isRootConsentActivity() {
        return getIntent().getBooleanExtra(EXTRA_ISROOTCONSENTACTIVITY, false);
    }

    @Override
    public void onBackPressed() {
        closeConsentActivity();
        super.onBackPressed();
    }

    @Override
    public void finish() {
        closeConsentActivity();
        super.finish();
    }

    protected void closeConsentActivity() {
        if(isRootConsentActivity()) {
            // this activity is at the root of the navigation
            // closing it should close the whole CMP
            // so we notify the manager
            ConsentManager.getSharedInstance().consentToolClosed();
        }
    }

    protected void storeConsentString(ConsentString contentString) {
        Log.d("storeConsentString", "storing consentString "+contentString.getConsentString());
        ConsentManager.getSharedInstance().setConsentString(contentString);
    }

    public Intent getIntentForConsentActivity (Class<? extends ConsentActivity> activityClass,
                                               Parcelable consentString,
                                               Parcelable vendorList,
                                               Parcelable editor) {
        return getIntentForConsentActivity(this, activityClass, consentString, vendorList, editor);
    }

    public static Intent getIntentForConsentActivity(Context ctx,
                                                     Class<? extends ConsentActivity> activityClass,
                                                     Parcelable consentString,
                                                     Parcelable vendorList,
                                                     Parcelable editor) {
        Intent intent = new Intent(ctx, activityClass);
        intent.putExtra(ConsentActivity.EXTRA_CONTENTSTRING, consentString);
        intent.putExtra(ConsentActivity.EXTRA_VENDORLIST, vendorList);
        intent.putExtra(ConsentActivity.EXTRA_EDITOR, editor);

        return intent;
    }
}
