package com.smartadserver.android.smartcmp;


import android.support.annotation.NonNull;

import junit.framework.Assert;

/**
 * Assert that a particular section of the code is reached within a given timeout.
 */
@SuppressWarnings("unused")
public class Expectation extends AbstractExpectation {

    private boolean unique = false;

    /**
     * Initialize an expectation.
     *
     * @param description a description of the expectation (that will be displayed in case of failure).
     */
    public Expectation(@NonNull String description) {
        super(description);
    }

    /**
     * Initialize an expectation that can't be fulfilled more than once.
     *
     * @param description a description of the expectation (that will be displayed in case of failure).
     * @param unique      true if the expectation can't be fulfilled more than once.
     */
    public Expectation(@NonNull String description, boolean unique) {
        super(description);
        this.unique = unique;
    }

    @Override
    public void fulfill() {
        synchronized (this) {
            if (unique && fulfilled) {
                Assert.fail("Expectation '" + description + "' has been fulfilled more than once");
            }
        }
        super.fulfill();
    }

    /**
     * Wait until the expectation is fulfilled or until the timeout is reached.
     * <p>
     * If the timeout is reached, an assert failure will be triggered. If the method is called
     * on an already fulfilled expectation, it will return immediately without failure.
     *
     */
    public void assertFulfilled(@SuppressWarnings("SameParameterValue") long timeout) {
        synchronized (this) {
            if (!fulfilled) {
                try {
                    wait(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!fulfilled) {
                    Assert.fail("Expectation '" + description + "' not fulfilled after " + timeout + "ms");
                }
            }
        }
    }
}
