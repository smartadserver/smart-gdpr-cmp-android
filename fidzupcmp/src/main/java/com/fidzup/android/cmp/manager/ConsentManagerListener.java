package com.fidzup.android.cmp.manager;

import com.fidzup.android.cmp.consentstring.ConsentString;
import com.fidzup.android.cmp.model.VendorList;

/**
 * Listener of CMPManager. This listener is optional for the ConsentManager.
 * If the ConsentManager does not have any listener, it will handle the Consent Tool UI display on its own.
 */

public interface ConsentManagerListener {

    /**
     * Called when the consent manager found a reason to display the Consent Tool UI. The publisher
     * should display the consent tool as soon as possible.
     *
     * Note: This callback will never be called if 'showConsentToolWhenLimitedAdTracking = false' is used during the
     * ConsentManager configuration and if the user has enabled 'Limited Ad Tracking' in its device settings.
     * In this case, a consent string without any consent will be automatically generated and stored.
     */
    void onShowConsentToolRequest(ConsentString consentString, VendorList vendorList);

}
