package com.benny.openlauncher.core.interfaces;

public interface DialogListener {

    public interface OnAddAppDrawerItemListener {
        void onAdd();
    }

    public interface OnEditDialogListener {
        void onRename(String str);
    }
}
