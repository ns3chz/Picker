package com.hzc.widget.picker.file;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zch.last.view.recycler.adapter.BaseRecyclerAdapter;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilePicker {
    @NonNull
    private final static Set<FilePicker> filePickerSet = new HashSet<>();

    public static FilePicker build(@NonNull Activity activity, int requestCode) {
        return new FilePicker(activity, requestCode);
    }

    public static FilePicker build(@NonNull androidx.fragment.app.Fragment fragmentX, int requestCode) {
        return new FilePicker(fragmentX, requestCode);
    }

    public static FilePicker build(@NonNull android.app.Fragment activity, int requestCode) {
        return new FilePicker(activity, requestCode);
    }

    public static void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
        onObjectTagResult(activity, requestCode, resultCode, data);
    }

    public static void onActivityResult(@NonNull androidx.fragment.app.Fragment fragmentX, int requestCode, int resultCode, @Nullable Intent data) {
        onObjectTagResult(fragmentX, requestCode, resultCode, data);
    }

    public static void onActivityResult(@NonNull android.app.Fragment fragment, int requestCode, int resultCode, @Nullable Intent data) {
        onObjectTagResult(fragment, requestCode, resultCode, data);
    }

    /**
     * @param objTag      {@link Activity}{@link androidx.fragment.app.Fragment}{@link android.app.Fragment}
     * @param requestCode 请求码
     * @param resultCode  返回码
     * @param data        返回数据
     */
    private static void onObjectTagResult(@NonNull Object objTag, int requestCode, int resultCode, Intent data) {
        for (FilePicker picker : filePickerSet) {
            if (picker == null) continue;
            if (requestCode != picker.requestCode) continue;//请求码
            if (objTag instanceof Activity) {
                if (!objTag.equals(picker.activity)) continue;
            } else if (objTag instanceof Fragment) {
                if (!objTag.equals(picker.fragmentX)) continue;
            } else if (objTag instanceof android.app.Fragment) {
                if (!objTag.equals(picker.fragment)) continue;
            } else {
                throw new RuntimeException("Tag : ( " + objTag.getClass().getCanonicalName() +
                        " ) is not activity,fragment or androidx.fragment.app.Fragment !!!");
            }
            switch (picker.uiParams.getChoiceMode()) {
                case SINGLE:
                    if (picker.onSinglePickListener != null) {
                        try {
                            if (resultCode == FilePicker_ViewModel.RESULT_OK) {
                                File result = (File) data.getSerializableExtra(FilePicker_ViewModel.RESULT_KEY);
                                picker.onSinglePickListener.pick(result);
                            } else if (resultCode == FilePicker_ViewModel.RESULT_CANCELED) {
                                picker.onSinglePickListener.cancel();
                            }
                        } catch (Exception e) {
                            picker.onSinglePickListener.exception(e);
                        }
                    }
                    break;
                case MULTI:
                    if (picker.onMultiPickListener != null) {
                        try {
                            if (resultCode == FilePicker_ViewModel.RESULT_OK) {
                                List<File> result = (List<File>) data.getSerializableExtra(FilePicker_ViewModel.RESULT_KEY);
                                picker.onMultiPickListener.pick(result);
                            } else if (resultCode == FilePicker_ViewModel.RESULT_CANCELED) {
                                picker.onMultiPickListener.cancel();
                            }
                        } catch (Exception e) {
                            picker.onMultiPickListener.exception(e);
                        }
                    }
                    break;
                case NONE:
                    break;
            }
            if (resultCode == FilePicker_ViewModel.RESULT_OK || resultCode == FilePicker_ViewModel.RESULT_CANCELED) {
                filePickerSet.remove(picker);
            }
            return;
        }
    }


    //------------------------------------------------------------------------------------

    @NonNull
    private final FilePickerUiParams uiParams;
    @Nullable
    private Activity activity;
    @Nullable
    private Fragment fragmentX;
    @Nullable
    private android.app.Fragment fragment;
    private final int requestCode;
    private OnSinglePickListener onSinglePickListener;
    private OnMultiPickListener onMultiPickListener;

    private FilePicker(@NonNull Activity activity, int requestCode) {
        this.activity = activity;
        this.requestCode = requestCode;
        this.uiParams = new FilePickerUiParams();
    }

    private FilePicker(@NonNull android.app.Fragment fragment, int requestCode) {
        this.fragment = fragment;
        this.requestCode = requestCode;
        this.uiParams = new FilePickerUiParams();
    }

    private FilePicker(@NonNull androidx.fragment.app.Fragment fragmentX, int requestCode) {
        this.fragmentX = fragmentX;
        this.requestCode = requestCode;
        this.uiParams = new FilePickerUiParams();
    }

    public FilePicker setItemFileLayoutRes(@LayoutRes int layout, @IdRes int icon, @IdRes int name,
                                           @IdRes int desc, @IdRes int select) {
        uiParams.setRes_file_item_layout(layout);
        uiParams.setId_picker_recycler_item_icon(icon);
        uiParams.setId_picker_recycler_item_name(name);
        uiParams.setId_picker_recycler_item_desc(desc);
        uiParams.setId_picker_recycler_item_select(select);
        return this;
    }

    public FilePicker setItemFileDrawable(@DrawableRes int file, @DrawableRes int folder) {
        uiParams.setRes_drawable_type_file(file);
        uiParams.setRes_drawable_type_folder(folder);
        return this;
    }

    public FilePicker setOpenFile(@Nullable File openFile) {
        if (openFile == null) return this;
        uiParams.setCurrentFile(openFile);
        return this;
    }

    public FilePicker setChoiceState(BaseRecyclerAdapter.ChoiceState state) {
        uiParams.setChoiceState(state);
        return this;
    }

    public FilePicker setSinglePick(OnSinglePickListener listener) {
        uiParams.setChoiceMode(BaseRecyclerAdapter.ChoiceMode.SINGLE);
        onSinglePickListener = listener;
        return this;
    }

    public FilePicker setMultiPick(OnMultiPickListener listener) {
        uiParams.setChoiceMode(BaseRecyclerAdapter.ChoiceMode.MULTI);
        onMultiPickListener = listener;
        return this;

    }

    public FilePicker setPickFileType(FilePickerUiParams.PickType type) {
        uiParams.setPickType(type);
        return this;
    }

    public void open() {
        check();
        Intent intent = getIntent();
        if (intent == null) {
            throw new RuntimeException("activity , fragment or fragmentX might be NULL !!!");
        }
        intent.putExtra(FilePicker_ViewModel.EXTRA_UI_PARAMS_KEY, uiParams);
        if (activity != null) {
            activity.startActivityForResult(intent, requestCode);
        } else if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
        } else if (fragmentX != null) {
            fragmentX.startActivityForResult(intent, requestCode);
        } else {
            throw new RuntimeException("activity , fragment or fragmentX might be NULL !!!");
        }
        filePickerSet.add(this);
    }

    private void check() {
        if (BaseRecyclerAdapter.ChoiceMode.MULTI.equals(uiParams.getChoiceMode())) {
            if (FilePickerUiParams.PickType.FOLDER.equals(uiParams.getPickType())) {
                throw new RuntimeException("Can Not Choose Multi Folders");
            }
        }
    }

    @Nullable
    private Intent getIntent() {
        Intent intent = null;
        if (activity != null) {
            intent = new Intent(activity, FilePickerActivity.class);
        } else if (fragment != null) {
            intent = new Intent(fragment.getActivity(), FilePickerActivity.class);
        } else if (fragmentX != null) {
            intent = new Intent(fragmentX.getActivity(), FilePickerActivity.class);
        }
        return intent;
    }

    public abstract static class OnSinglePickListener implements OnListener {
        public abstract void pick(@NonNull File file) throws Exception;

        @Override
        public void cancel() throws Exception {

        }

        @Override
        public void exception(@NonNull Exception e) {

        }
    }

    public abstract static class OnMultiPickListener implements OnListener {
        public abstract void pick(@NonNull List<File> fileList) throws Exception;

        @Override
        public void cancel() throws Exception {

        }

        @Override
        public void exception(@NonNull Exception e) {

        }
    }

    private interface OnListener {
        /**
         * 取消选择
         */
        void cancel() throws Exception;

        void exception(@NonNull Exception e);
    }

    public static void LOG_MSG() {
        Log.d(FilePicker.class.getName(), "SET SIZE : " + filePickerSet.size());
    }
}
