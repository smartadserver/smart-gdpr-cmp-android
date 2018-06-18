package com.smartadserver.android.smartcmp.vendorlist;


import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import com.smartadserver.android.smartcmp.Expectation;
import com.smartadserver.android.smartcmp.model.Language;
import com.smartadserver.android.smartcmp.model.VendorList;
import com.smartadserver.android.smartcmp.util.JSONAsyncTask;
import com.smartadserver.android.smartcmp.util.JSONAsyncTaskListener;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class VendorListManagerTest {

    private JSONObject vendorsJSON;
    private JSONObject localizedVendorsJSON;

    private JSONObject getVendorsJSON() {
        if (vendorsJSON == null) {
            vendorsJSON = getJSON("vendors.json");
        }

        return vendorsJSON;
    }

    private JSONObject getLocalizedVendorsJSON() {
        if (localizedVendorsJSON == null) {
            localizedVendorsJSON = getJSON("vendors_localized.json");
        }

        return localizedVendorsJSON;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private JSONObject getJSON(String fileName) {
        try {
            InputStream is = InstrumentationRegistry.getContext().getAssets().open(fileName);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            return new JSONObject(new String(buffer, "UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private class MockJSONAsyncTask extends JSONAsyncTask {

        MockJSONAsyncTask(@NonNull JSONAsyncTaskListener listener) {
            super(listener);
        }

        @Override
        protected void onPostExecute(Object o) {

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            getVendorsJSON();
            return null;
        }
    }

    @Test
    public void testVendorListCanBeRetrieveManually() {
        final Expectation expectationVendorListRetrieved = new Expectation("VendorList retrieved");

        final VendorListManagerListener mockListener = new VendorListManagerListener() {
            @Override
            public void onVendorListUpdateSuccess(@NonNull VendorList vendorList) {
                Assert.assertEquals(6, vendorList.getVersion());
                Assert.assertEquals(5, vendorList.getPurposes().size());
                Assert.assertEquals(3, vendorList.getFeatures().size());
                Assert.assertEquals(17, vendorList.getVendors().size());

                expectationVendorListRetrieved.fulfill();
            }

            @Override
            public void onVendorListUpdateFail(@NonNull Exception e) {
            }
        };


        VendorListManager vlManager = new VendorListManager(InstrumentationRegistry.getContext(), mockListener, 100, 10, new Language("en")) {
            @Override
            protected JSONAsyncTask getNewJSONAsyncTaskForVendorList(@NonNull JSONAsyncTaskListener listener) {
                return new MockJSONAsyncTask(listener) {
                    @Override
                    protected void onPostExecute(Object o) {
                        if (getVendorsJSON() == null) {
                            listener.JSONAsyncTaskDidFailDownloadingJSONObject();
                        } else {
                            listener.JSONAsyncTaskDidSucceedDownloadingJSONObject(getVendorsJSON());
                        }
                    }
                };
            }
        };

        vlManager.refreshVendorList();
        expectationVendorListRetrieved.assertFulfilled(2000);
    }

    @Test
    public void testVendorListCanBeRetrieveManuallyForCustomVersionOnce() {
        final Expectation expectationVendorListRetrieved = new Expectation("VendorList retrieved");

        VendorListManagerListener mockListener = new VendorListManagerListener() {
            @Override
            public void onVendorListUpdateSuccess(@NonNull VendorList vendorList) {
            }

            @Override
            public void onVendorListUpdateFail(@NonNull Exception e) {
            }
        };

        VendorListManager vlManager = new VendorListManager(InstrumentationRegistry.getContext(), mockListener, 1000, 500, null) {
            @Override
            protected JSONAsyncTask getNewJSONAsyncTaskForVendorList(@NonNull JSONAsyncTaskListener listener) {
                return new MockJSONAsyncTask(listener) {
                    @Override
                    protected void onPostExecute(Object o) {
                        listener.JSONAsyncTaskDidSucceedDownloadingJSONObject(getVendorsJSON());
                    }

                    @Override
                    protected Object doInBackground(Object[] objects) {
                        if (objects.length > 0) {
                            // Retrieve the URL given in parameters and check its the right value
                            String rawJSONURL = (String) objects[0];
                            Assert.assertEquals("https://vendorlist.consensu.org/v-42/vendorlist.json", rawJSONURL);
                        } else {
                            Assert.fail("There is no URL given to the JSONAsyncTask");
                        }

                        return super.doInBackground(objects);
                    }
                };
            }
        };

        vlManager.getVendorList(42, new VendorListManagerListener() {
            @Override
            public void onVendorListUpdateSuccess(@NonNull VendorList vendorList) {
                Assert.assertEquals(6, vendorList.getVersion());
                Assert.assertEquals(5, vendorList.getPurposes().size());
                Assert.assertEquals(3, vendorList.getFeatures().size());
                Assert.assertEquals(17, vendorList.getVendors().size());

                expectationVendorListRetrieved.fulfill();
            }

            @Override
            public void onVendorListUpdateFail(@NonNull Exception e) {
                Assert.fail("Should not fail");
            }
        });

        expectationVendorListRetrieved.assertFulfilled(2000);
    }

    @Test
    public void testVendorListRefreshCanFailWithInvalidJSON() {
        final Expectation vendorListManagerShouldFail = new Expectation("VendorListManager should failed with JSONException");

        VendorListManagerListener mockListener = new VendorListManagerListener() {
            @Override
            public void onVendorListUpdateSuccess(@NonNull VendorList vendorList) {
                Assert.fail("Should have failed.");
            }

            @Override
            public void onVendorListUpdateFail(@NonNull Exception e) {
                Assert.assertTrue(e instanceof JSONException);

                vendorListManagerShouldFail.fulfill();
            }
        };

        VendorListManager vlManager = new VendorListManager(InstrumentationRegistry.getContext(), mockListener, 1000, 200, new Language("en")) {
            @Override
            protected JSONAsyncTask getNewJSONAsyncTaskForVendorList(@NonNull JSONAsyncTaskListener listener) {
                return new MockJSONAsyncTask(listener) {
                    @Override
                    protected void onPostExecute(Object o) {
                        listener.JSONAsyncTaskDidSucceedDownloadingJSONObject(new JSONObject());
                    }
                };
            }
        };

        vlManager.refreshVendorList();

        vendorListManagerShouldFail.assertFulfilled(3000);
    }

    @Test
    public void testVendorListCanBeRefreshedAutomatically() {
        final Expectation refreshCalledImmediatelyExpectation = new Expectation("Refresh is called automatically at start");
        final Expectation refreshCalledAfterIntervalExpectation = new Expectation("Refresh is called automatically when the refresh interval is elapsed");

        final VendorListManagerListener mockListener = new VendorListManagerListener() {
            boolean isFirstCall = true;

            @Override
            public void onVendorListUpdateSuccess(@NonNull VendorList vendorList) {
                if (isFirstCall) {
                    isFirstCall = false;
                    refreshCalledImmediatelyExpectation.fulfill();
                } else {
                    refreshCalledAfterIntervalExpectation.fulfill();
                }
            }

            @Override
            public void onVendorListUpdateFail(@NonNull Exception e) {
                Assert.fail("This call should not fail.");
            }
        };

        VendorListManager vlManager = new VendorListManager(InstrumentationRegistry.getContext(), mockListener, 1000, 500, null) {
            @Override
            protected JSONAsyncTask getNewJSONAsyncTaskForVendorList(@NonNull JSONAsyncTaskListener listener) {
                return new MockJSONAsyncTask(listener) {
                    @Override
                    protected void onPostExecute(Object o) {
                        listener.JSONAsyncTaskDidSucceedDownloadingJSONObject(getVendorsJSON());
                    }
                };
            }
        };

        vlManager.startAutomaticRefresh(true);

        refreshCalledImmediatelyExpectation.assertFulfilled(500);
        refreshCalledAfterIntervalExpectation.assertFulfilled(1500);
    }

    @Test
    public void testVendorListCanBeTranslatedIfALanguageIsSetInTheURL() {
        final Expectation vendorListURLCalledExpectation = new Expectation("The vendor list URL has been called");
        final Expectation vendorListFRURLCalledExpectation = new Expectation("The FR vendor list URL has been called");

        final VendorListManagerListener mockListener = new VendorListManagerListener() {
            @Override
            public void onVendorListUpdateSuccess(@NonNull VendorList vendorList) {
                // Ok we successfully retrieve the vendor list.
                Assert.assertEquals(6, vendorList.getVersion());
                Assert.assertEquals(5, vendorList.getPurposes().size());
                Assert.assertEquals(3, vendorList.getFeatures().size());
                Assert.assertEquals(17, vendorList.getVendors().size());

                Assert.assertEquals("Purpose 3 name translated", vendorList.getPurposes().get(2).getName());
                Assert.assertEquals("Feature 2 name translated", vendorList.getFeatures().get(1).getName());
            }

            @Override
            public void onVendorListUpdateFail(@NonNull Exception e) {
                Assert.fail("This call should not fail.");
            }
        };

        VendorListManager vlManager = new VendorListManager(InstrumentationRegistry.getContext(), mockListener, 1000, 500, new Language("fr")) {
            @Override
            protected JSONAsyncTask getNewJSONAsyncTaskForVendorList(@NonNull JSONAsyncTaskListener listener) {
                return new MockJSONAsyncTask(listener) {
                    @Override
                    protected void onPostExecute(Object o) {
                        listener.JSONAsyncTaskDidSucceedDownloadingJSONObject(getVendorsJSON());
                        vendorListURLCalledExpectation.fulfill();
                    }
                };
            }

            @Override
            protected JSONAsyncTask getNewJSONAsyncTaskForLocalizedVendorList(@NonNull JSONAsyncTaskListener listener) {
                return new MockJSONAsyncTask(listener) {
                    @Override
                    protected void onPostExecute(Object o) {
                        listener.JSONAsyncTaskDidSucceedDownloadingJSONObject(getLocalizedVendorsJSON());
                        vendorListFRURLCalledExpectation.fulfill();
                    }

                    @Override
                    protected Object doInBackground(Object[] objects) {
                        getLocalizedVendorsJSON();
                        return null;
                    }
                };
            }
        };

        vlManager.refreshVendorList();

        vendorListURLCalledExpectation.assertFulfilled(2000);
        vendorListFRURLCalledExpectation.assertFulfilled(2500);
    }

    @Test
    public void testInvalidTranslationFileWillNotPreventTheVendorListFromBeingRetrieved() {
        final Expectation vendorListURLCalledExpectation = new Expectation("The vendor list URL has been called");
        final Expectation vendorListFRURLCalledExpectation = new Expectation("The FR vendor list URL has been called");

        final VendorListManagerListener mockListener = new VendorListManagerListener() {
            @Override
            public void onVendorListUpdateSuccess(@NonNull VendorList vendorList) {
                // Ok we successfully retrieve the vendor list.
                Assert.assertEquals(6, vendorList.getVersion());
                Assert.assertEquals(5, vendorList.getPurposes().size());
                Assert.assertEquals(3, vendorList.getFeatures().size());
                Assert.assertEquals(17, vendorList.getVendors().size());

                Assert.assertEquals("Ad selection, delivery, reporting", vendorList.getPurposes().get(2).getName());
                Assert.assertEquals("Linking Devices", vendorList.getFeatures().get(1).getName());
            }

            @Override
            public void onVendorListUpdateFail(@NonNull Exception e) {
                Assert.fail("This call should not fail.");
            }
        };

        VendorListManager vlManager = new VendorListManager(InstrumentationRegistry.getContext(), mockListener, 1000, 500, new Language("fr")) {
            @Override
            protected JSONAsyncTask getNewJSONAsyncTaskForVendorList(@NonNull JSONAsyncTaskListener listener) {
                return new MockJSONAsyncTask(listener) {
                    @Override
                    protected void onPostExecute(Object o) {
                        listener.JSONAsyncTaskDidSucceedDownloadingJSONObject(getVendorsJSON());
                        vendorListURLCalledExpectation.fulfill();
                    }
                };
            }

            @Override
            protected JSONAsyncTask getNewJSONAsyncTaskForLocalizedVendorList(@NonNull JSONAsyncTaskListener listener) {
                return new MockJSONAsyncTask(listener) {
                    @Override
                    protected void onPostExecute(Object o) {
                        // return a invalid JSONObject
                        listener.JSONAsyncTaskDidSucceedDownloadingJSONObject(new JSONObject());
                        vendorListFRURLCalledExpectation.fulfill();
                    }
                };
            }
        };

        vlManager.refreshVendorList();

        vendorListURLCalledExpectation.assertFulfilled(2000);
        vendorListFRURLCalledExpectation.assertFulfilled(2500);
    }
}
