package com.fidzup.android.cmp.vendorlist;

import android.support.annotation.NonNull;

import com.fidzup.android.cmp.model.VendorList;

/**
 * Vendor List Manager Listener used by CMPManager.
 */

public interface VendorListManagerListener {

    /**
     * Warns that a vendor list has been fetched.
     *
     * @param vendorList The fetched vendor list.
     */
    void onVendorListUpdateSuccess(@NonNull VendorList vendorList);

    /**
     * Warns that VendorListManager did fail to fetch the vendor list.
     *
     * @param e The error that explains why the VendorListManager did fail.
     */
    void onVendorListUpdateFail(@NonNull Exception e);
}
