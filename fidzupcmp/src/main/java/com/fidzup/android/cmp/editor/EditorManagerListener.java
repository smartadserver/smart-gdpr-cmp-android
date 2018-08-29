package com.fidzup.android.cmp.editor;

import android.support.annotation.NonNull;

import com.fidzup.android.cmp.model.Editor;

/**
 * Editor Manager Listener used by CMPManager.
 */

public interface EditorManagerListener {

    /**
     * Warns that an editor has been fetched.
     *
     * @param editor The fetched editor.
     */
    void onEditorUpdateSuccess(@NonNull Editor editor);

    /**
     * Warns that EditorManager did fail to fetch the editor.
     *
     * @param e The error that explains why the EditorManager did fail.
     */
    void onEditorUpdateFail(@NonNull Exception e);
}
