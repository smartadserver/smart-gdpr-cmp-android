package com.fidzup.android.cmp.editor;


import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import com.fidzup.android.cmp.Expectation;
import com.fidzup.android.cmp.model.Language;
import com.fidzup.android.cmp.model.Editor;
import com.fidzup.android.cmp.util.JSONAsyncTask;
import com.fidzup.android.cmp.util.JSONAsyncTaskListener;
import com.fidzup.android.cmp.editor.EditorManager;
import com.fidzup.android.cmp.editor.EditorManagerListener;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class EditorManagerTest {

    private JSONObject editorJSON;
    private JSONObject localizedEditorJSON;

    private JSONObject getEditorJSON() {
        if (editorJSON == null) {
            editorJSON = getJSON("editor.json");
        }

        return editorJSON;
    }

    private JSONObject getLocalizedVendorsJSON() {
        if (localizedEditorJSON == null) {
            localizedEditorJSON = getJSON("editor-fr.json");
        }

        return localizedEditorJSON;
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
            getEditorJSON();
            return null;
        }
    }

    @Test
    public void testEditorCanBeRetrieveManually() {
        final Expectation expectationEditorRetrieved = new Expectation("Editor retrieved");

        final EditorManagerListener mockListener = new EditorManagerListener() {
            @Override
            public void onEditorUpdateSuccess(@NonNull Editor editor) {
                Assert.assertEquals(6, editor.getVersion());
                Assert.assertEquals(6, editor.getPurposes().size());
                Assert.assertEquals(3, editor.getFeatures().size());

                expectationEditorRetrieved.fulfill();
            }

            @Override
            public void onEditorUpdateFail(@NonNull Exception e) {
            }
        };


        EditorManager vlManager = new EditorManager(mockListener, 100, 10, new Language("en")) {
            @Override
            protected JSONAsyncTask getNewJSONAsyncTaskForEditor(@NonNull JSONAsyncTaskListener listener) {
                return new MockJSONAsyncTask(listener) {
                    @Override
                    protected void onPostExecute(Object o) {
                        if (getEditorJSON() == null) {
                            listener.JSONAsyncTaskDidFailDownloadingJSONObject();
                        } else {
                            listener.JSONAsyncTaskDidSucceedDownloadingJSONObject(getEditorJSON());
                        }
                    }
                };
            }
        };
        vlManager.setEditorURLs("https://www.fidzup.com/editor/editor.json","https://www.fidzup.com/editor/editor-{language}.json");

        vlManager.refreshEditor();
        expectationEditorRetrieved.assertFulfilled(2000);
    }

    @Test
    public void testEditorCanBeRetrieveManuallyForCustomVersionOnce() {
        final Expectation expectationEditorRetrieved = new Expectation("Editor retrieved");

        EditorManagerListener mockListener = new EditorManagerListener() {
            @Override
            public void onEditorUpdateSuccess(@NonNull Editor editor) {
            }

            @Override
            public void onEditorUpdateFail(@NonNull Exception e) {
            }
        };

        EditorManager vlManager = new EditorManager(mockListener, 1000, 500, null) {
            @Override
            protected JSONAsyncTask getNewJSONAsyncTaskForEditor(@NonNull JSONAsyncTaskListener listener) {
                return new MockJSONAsyncTask(listener) {
                    @Override
                    protected void onPostExecute(Object o) {
                        listener.JSONAsyncTaskDidSucceedDownloadingJSONObject(getEditorJSON());
                    }

                    @Override
                    protected Object doInBackground(Object[] objects) {
                        if (objects.length > 0) {
                            // Retrieve the URL given in parameters and check its the right value
                            String rawJSONURL = (String) objects[0];
                            Assert.assertEquals("https://www.fidzup.com/editor/editor.json", rawJSONURL);
                        } else {
                            Assert.fail("There is no URL given to the JSONAsyncTask");
                        }

                        return super.doInBackground(objects);
                    }
                };
            }
        };
        vlManager.setEditorURLs("https://www.fidzup.com/editor/editor.json","https://www.fidzup.com/editor/editor-{language}.json");

        vlManager.getEditor(42, new EditorManagerListener() {
            @Override
            public void onEditorUpdateSuccess(@NonNull Editor editor) {
                Assert.assertEquals(6, editor.getVersion());
                Assert.assertEquals(6, editor.getPurposes().size());
                Assert.assertEquals(3, editor.getFeatures().size());

                expectationEditorRetrieved.fulfill();
            }

            @Override
            public void onEditorUpdateFail(@NonNull Exception e) {
                Assert.fail("Should not fail");
            }
        });

        expectationEditorRetrieved.assertFulfilled(2000);
    }

    @Test
    public void testEditorRefreshCanFailWithInvalidJSON() {
        final Expectation editorManagerShouldFail = new Expectation("EditorManager should failed with JSONException");

        EditorManagerListener mockListener = new EditorManagerListener() {
            @Override
            public void onEditorUpdateSuccess(@NonNull Editor editor) {
                Assert.fail("Should have failed.");
            }

            @Override
            public void onEditorUpdateFail(@NonNull Exception e) {
                Assert.assertTrue(e instanceof JSONException);

                editorManagerShouldFail.fulfill();
            }
        };

        EditorManager vlManager = new EditorManager(mockListener, 1000, 200, new Language("en")) {
            @Override
            protected JSONAsyncTask getNewJSONAsyncTaskForEditor(@NonNull JSONAsyncTaskListener listener) {
                return new MockJSONAsyncTask(listener) {
                    @Override
                    protected void onPostExecute(Object o) {
                        listener.JSONAsyncTaskDidSucceedDownloadingJSONObject(new JSONObject());
                    }
                };
            }
        };

        vlManager.setEditorURLs("https://www.fidzup.com/editor/editor.json","https://www.fidzup.com/editor/editor-{language}.json");

        vlManager.refreshEditor();

        editorManagerShouldFail.assertFulfilled(3000);
    }

    @Test
    public void testEditorCanBeRefreshedAutomatically() {
        final Expectation refreshCalledImmediatelyExpectation = new Expectation("Refresh is called automatically at start");
        final Expectation refreshCalledAfterIntervalExpectation = new Expectation("Refresh is called automatically when the refresh interval is elapsed");

        final EditorManagerListener mockListener = new EditorManagerListener() {
            boolean isFirstCall = true;

            @Override
            public void onEditorUpdateSuccess(@NonNull Editor editor) {
                if (isFirstCall) {
                    isFirstCall = false;
                    refreshCalledImmediatelyExpectation.fulfill();
                } else {
                    refreshCalledAfterIntervalExpectation.fulfill();
                }
            }

            @Override
            public void onEditorUpdateFail(@NonNull Exception e) {
                Assert.fail("This call should not fail.");
            }
        };

        EditorManager vlManager = new EditorManager(mockListener, 1000, 500, null) {
            @Override
            protected JSONAsyncTask getNewJSONAsyncTaskForEditor(@NonNull JSONAsyncTaskListener listener) {
                return new MockJSONAsyncTask(listener) {
                    @Override
                    protected void onPostExecute(Object o) {
                        listener.JSONAsyncTaskDidSucceedDownloadingJSONObject(getEditorJSON());
                    }
                };
            }
        };

        vlManager.setEditorURLs("https://www.fidzup.com/editor/editor.json","https://www.fidzup.com/editor/editor-{language}.json");

        vlManager.startAutomaticRefresh(true);

        refreshCalledImmediatelyExpectation.assertFulfilled(500);
        refreshCalledAfterIntervalExpectation.assertFulfilled(1500);
    }

    @Test
    public void testEditorCanBeTranslatedIfALanguageIsSetInTheURL() {
        final Expectation editorURLCalledExpectation = new Expectation("The editor URL has been called");
        final Expectation editorFRURLCalledExpectation = new Expectation("The FR editor URL has been called");

        final EditorManagerListener mockListener = new EditorManagerListener() {
            @Override
            public void onEditorUpdateSuccess(@NonNull Editor editor) {
                // Ok we successfully retrieve the vendor list.
                Assert.assertEquals(6, editor.getVersion());
                Assert.assertEquals(6, editor.getPurposes().size());
                Assert.assertEquals(3, editor.getFeatures().size());

                Assert.assertEquals("Finalité 3", editor.getPurposes().get(2).getName());
                Assert.assertEquals("Fonctionnalité 2", editor.getFeatures().get(1).getName());
            }

            @Override
            public void onEditorUpdateFail(@NonNull Exception e) {
                Assert.fail("This call should not fail.");
            }
        };

        EditorManager vlManager = new EditorManager(mockListener, 1000, 500, new Language("fr")) {
            @Override
            protected JSONAsyncTask getNewJSONAsyncTaskForEditor(@NonNull JSONAsyncTaskListener listener) {
                return new MockJSONAsyncTask(listener) {
                    @Override
                    protected void onPostExecute(Object o) {
                        listener.JSONAsyncTaskDidSucceedDownloadingJSONObject(getEditorJSON());
                        editorURLCalledExpectation.fulfill();
                    }
                };
            }

            @Override
            protected JSONAsyncTask getNewJSONAsyncTaskForLocalizedEditor(@NonNull JSONAsyncTaskListener listener) {
                return new MockJSONAsyncTask(listener) {
                    @Override
                    protected void onPostExecute(Object o) {
                        listener.JSONAsyncTaskDidSucceedDownloadingJSONObject(getLocalizedVendorsJSON());
                        editorFRURLCalledExpectation.fulfill();
                    }

                    @Override
                    protected Object doInBackground(Object[] objects) {
                        getLocalizedVendorsJSON();
                        return null;
                    }
                };
            }
        };

        vlManager.setEditorURLs("https://www.fidzup.com/editor/editor.json","https://www.fidzup.com/editor/editor-{language}.json");

        vlManager.refreshEditor();

        editorURLCalledExpectation.assertFulfilled(2000);
        editorFRURLCalledExpectation.assertFulfilled(2500);
    }

    @Test
    public void testInvalidTranslationFileWillNotPreventTheEditorFromBeingRetrieved() {
        final Expectation editorURLCalledExpectation = new Expectation("The editor URL has been called");
        final Expectation editorFRURLCalledExpectation = new Expectation("The FR editor URL has been called");

        final EditorManagerListener mockListener = new EditorManagerListener() {
            @Override
            public void onEditorUpdateSuccess(@NonNull Editor editor) {
                // Ok we successfully retrieve the vendor list.
                Assert.assertEquals(6, editor.getVersion());
                Assert.assertEquals(6, editor.getPurposes().size());
                Assert.assertEquals(3, editor.getFeatures().size());

                Assert.assertEquals("Ad selection, delivery, reporting", editor.getPurposes().get(2).getName());
                Assert.assertEquals("Linking Devices", editor.getFeatures().get(1).getName());
            }

            @Override
            public void onEditorUpdateFail(@NonNull Exception e) {
                Assert.fail("This call should not fail.");
            }
        };

        EditorManager vlManager = new EditorManager(mockListener, 1000, 500, new Language("fr")) {
            @Override
            protected JSONAsyncTask getNewJSONAsyncTaskForEditor(@NonNull JSONAsyncTaskListener listener) {
                return new MockJSONAsyncTask(listener) {
                    @Override
                    protected void onPostExecute(Object o) {
                        listener.JSONAsyncTaskDidSucceedDownloadingJSONObject(getEditorJSON());
                        editorURLCalledExpectation.fulfill();
                    }
                };
            }

            @Override
            protected JSONAsyncTask getNewJSONAsyncTaskForLocalizedEditor(@NonNull JSONAsyncTaskListener listener) {
                return new MockJSONAsyncTask(listener) {
                    @Override
                    protected void onPostExecute(Object o) {
                        // return a invalid JSONObject
                        listener.JSONAsyncTaskDidSucceedDownloadingJSONObject(new JSONObject());
                        editorFRURLCalledExpectation.fulfill();
                    }
                };
            }
        };

        vlManager.setEditorURLs("https://www.fidzup.com/editor/editor.json","https://www.fidzup.com/editor/editor-{language}.json");

        vlManager.refreshEditor();

        editorURLCalledExpectation.assertFulfilled(2000);
        editorFRURLCalledExpectation.assertFulfilled(2500);
    }
}
