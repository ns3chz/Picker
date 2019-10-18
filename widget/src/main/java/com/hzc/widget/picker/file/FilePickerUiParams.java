package com.hzc.widget.picker.file;

import android.os.Environment;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.hzc.widget.R;
import com.zch.last.view.recycler.adapter.BaseRecyclerAdapter;

import java.io.File;
import java.io.Serializable;

public class FilePickerUiParams extends BaseObservable implements Serializable {
    public enum PickType {
        FILE,
        FOLDER
    }

    @LayoutRes
    private int res_file_item_layout = R.layout.recycler_item_file_picker;//列表item布局id
    @IdRes
    private int id_picker_recycler_item_icon = R.id.picker_recycler_item_icon;//item文件图标id
    @IdRes
    private int id_picker_recycler_item_name = R.id.picker_recycler_item_name;//item文件名称文本框id
    @IdRes
    private int id_picker_recycler_item_desc = R.id.picker_recycler_item_desc;//item文件描述文本框id
    @IdRes
    private int id_picker_recycler_item_select = R.id.picker_recycler_item_select;//item文件选择框id
    //
    @DrawableRes
    private int res_drawable_type_file = R.mipmap.icon_file;//文件类型icon
    @DrawableRes
    private int res_drawable_type_folder = R.mipmap.icon_folder;//文件夹类型icon
    //
    @NonNull
    private File currentFile = Environment.getExternalStorageDirectory();
    @NonNull
    private BaseRecyclerAdapter.ChoiceMode choiceMode = BaseRecyclerAdapter.ChoiceMode.SINGLE;
    @NonNull
    private BaseRecyclerAdapter.ChoiceState choiceState = BaseRecyclerAdapter.ChoiceState.SELECT;
    @NonNull
    private PickType pickType = PickType.FILE;


    @Bindable
    @NonNull
    public File getCurrentFile() {
        return currentFile;
    }

    public boolean setCurrentFile(@NonNull File currentFile) {
        if (!currentFile.canRead()) {
            return false;
        }
        this.currentFile = currentFile;
        notifyPropertyChanged(com.hzc.widget.BR.currentFile);
        return true;
    }

    @NonNull
    public BaseRecyclerAdapter.ChoiceMode getChoiceMode() {
        return choiceMode;
    }

    public void setChoiceMode(@NonNull BaseRecyclerAdapter.ChoiceMode choiceMode) {
        this.choiceMode = choiceMode;
    }

    @NonNull
    public BaseRecyclerAdapter.ChoiceState getChoiceState() {
        return choiceState;
    }

    public void setChoiceState(@NonNull BaseRecyclerAdapter.ChoiceState choiceState) {
        this.choiceState = choiceState;
    }

    @NonNull
    public PickType getPickType() {
        return pickType;
    }

    public void setPickType(@NonNull PickType pickType) {
        this.pickType = pickType;
    }

    public FilePickerUiParams setRes_file_item_layout(@LayoutRes int res_file_item_layout) {
        this.res_file_item_layout = res_file_item_layout;
        return this;
    }


    public FilePickerUiParams setId_picker_recycler_item_icon(@IdRes int id) {
        this.id_picker_recycler_item_icon = id;
        return this;
    }

    public FilePickerUiParams setId_picker_recycler_item_name(@IdRes int id) {
        this.id_picker_recycler_item_name = id;
        return this;
    }

    public FilePickerUiParams setId_picker_recycler_item_desc(@IdRes int id) {
        this.id_picker_recycler_item_desc = id;
        return this;
    }

    public FilePickerUiParams setId_picker_recycler_item_select(@IdRes int id) {
        this.id_picker_recycler_item_select = id;
        return this;
    }

    public void setRes_drawable_type_file(@DrawableRes int res_drawable_type_file) {
        this.res_drawable_type_file = res_drawable_type_file;
    }

    public void setRes_drawable_type_folder(@DrawableRes int res_drawable_type_folder) {
        this.res_drawable_type_folder = res_drawable_type_folder;
    }
    //-----------------------------------

    @LayoutRes
    public int getRes_file_item_layout() {
        return res_file_item_layout;
    }

    @IdRes
    public int getId_picker_recycler_item_icon() {
        return id_picker_recycler_item_icon;
    }

    @IdRes
    public int getId_picker_recycler_item_name() {
        return id_picker_recycler_item_name;
    }

    @IdRes
    public int getId_picker_recycler_item_desc() {
        return id_picker_recycler_item_desc;
    }

    @IdRes
    public int getId_picker_recycler_item_select() {
        return id_picker_recycler_item_select;
    }

    @DrawableRes
    public int getRes_drawable_type_file() {
        return res_drawable_type_file;
    }

    @DrawableRes
    public int getRes_drawable_type_folder() {
        return res_drawable_type_folder;
    }
}
