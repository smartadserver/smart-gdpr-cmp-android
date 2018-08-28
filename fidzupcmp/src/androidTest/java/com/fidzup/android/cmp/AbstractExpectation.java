package com.fidzup.android.cmp;

import android.support.annotation.NonNull;



abstract class AbstractExpectation {

    // Monitor the fulfilled status of the expectation
    boolean fulfilled = false;

    @NonNull
    String description;

    /**
     * Initialize an expectation.
     *
     * @param description a description of the expectation (that will be displayed in case of failure).
     */
    AbstractExpectation(@NonNull String description) {
        this.description = description;
    }

    /**
     * Considers the expectation as fulfilled.
     */
    public void fulfill() {
        synchronized (this) {
            fulfilled = true;
            notify();
        }
    }

    /**
     * Returns the description of the expectation.
     *
     * @return the description of the expectation.
     */
    @SuppressWarnings("unused")
    @NonNull
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether the expectation is fulfilled or not.
     *
     * @return true if the expectation is fulfilled, false otherwise.
     */
    @SuppressWarnings("unused")
    public boolean isFulfilled() {
        return fulfilled;
    }

}
