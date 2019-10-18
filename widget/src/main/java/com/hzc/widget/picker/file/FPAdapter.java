package com.hzc.widget.picker.file;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.zch.last.view.recycler.adapter.BaseRecyclerHFAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FPAdapter extends BaseRecyclerHFAdapter<FPAdapter.Holder, File> {
    @NonNull
    private final FilePickerUiParams uiParams;

    FPAdapter(Context mContext, @NonNull FilePickerUiParams params) {
        super(mContext);
        this.uiParams = params;
        setChoiceMode(uiParams.getChoiceMode());
        setChoiceState(uiParams.getChoiceState());
        setDataList(getSortData());
    }

    @Override
    public void onBindsViewHolder(Holder holder, int position) {
        File file = dataList.get(position);
        //
        if (holder.icon != null) {
            if (holder.icon instanceof ImageView) {
                ((ImageView) holder.icon).setImageResource(file.isFile() ?
                        uiParams.getRes_drawable_type_file() : uiParams.getRes_drawable_type_folder());
            } else {
                holder.icon.setBackgroundResource(file.isFile() ?
                        uiParams.getRes_drawable_type_file() : uiParams.getRes_drawable_type_folder());
            }
        }
        //
        if (holder.name != null) {
            if (holder.name instanceof TextView) {
                ((TextView) holder.name).setText(file.getName());
            }
        }
        //
        if (holder.desc != null) {
            if (holder.desc instanceof TextView) {
                if (file.isDirectory() && FilePickerUiParams.PickType.FILE.equals(uiParams.getPickType())) {
                    String[] list = file.list();
                    ((TextView) holder.desc).setText((list == null ? 0 : list.length) + " 项");
                } else {
                    ((TextView) holder.desc).setText(null);
                }
            }
        }
        //
    }

    @Override
    public void onBindChooseState(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull ChoiceState choiceState, boolean hadChoice) {
        if (holder instanceof Holder) {
            Holder vHolder = (Holder) holder;
            if (vHolder.select != null) {
                switch (choiceState) {
                    case SELECT:
                        setViewSelected(vHolder.select, hadChoice);
                        break;
                    case CHECKED:
                        setViewChecked(vHolder.select, hadChoice);
                        break;
                    case NONE:
                        setViewSelected(vHolder.select, false);
                        setViewChecked(vHolder.select, false);
                        break;
                }
            }
        }
    }

    @Override
    public Holder onCreateViewHolders(ViewGroup parent, View view, int viewType) {
        return new Holder(view);
    }

    @Override
    public int onCreateViewRes(@NonNull ViewGroup parent, int viewType) {
        return this.uiParams.getRes_file_item_layout();
    }

    void notifyQuery(String key) {
        if (key == null || key.length() == 0) {
            notifyFolderChanged();
            return;
        }
        List<File> sortData = getSortData();
        File file;
        for (int i = sortData.size() - 1; i >= 0; i--) {
            try {
                file = sortData.get(i);
                String fileName = file.getName();
                if (!fileName.contains(key)) {
                    sortData.remove(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setDataList(sortData);
        notifyOnUiThread();
    }

    void notifyFolderChanged() {
        setDataList(getSortData());
        resetSelected();
        notifyDataSetChanged();
    }

    @NonNull
    private List<File> getSortData() {
        File currentFile = uiParams.getCurrentFile();
        if (!currentFile.canRead()) {
            return new ArrayList<>();
        }
        List<File> fileList = new ArrayList<>(Arrays.asList(currentFile.listFiles()));
        switch (uiParams.getPickType()) {
            case FOLDER:
                for (int i = fileList.size() - 1; i >= 0; i--) {
                    try {
                        File file = fileList.get(i);
                        if (file.isFile()) {
                            fileList.remove(i);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case FILE:
                break;
        }
        //排序
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                boolean f1isDir = f1.isDirectory();
                boolean f2isDir = f2.isDirectory();
                if (f1isDir && !f2isDir) return -1;
                if (!f1isDir && f2isDir) return 1;
                boolean conP1 = f1.getName().startsWith(".");
                boolean conP2 = f2.getName().startsWith(".");
                if (!conP1 && conP2) return -1;
                if (conP1 && !conP2) return 1;

                return f1.compareTo(f2);
            }
        });
        return fileList;
    }


    class Holder extends RecyclerView.ViewHolder {
        @Nullable
        final View desc;
        @Nullable
        final View icon;
        @Nullable
        final View name;
        @Nullable
        final View select;

        Holder(@NonNull View itemView) {
            super(itemView);
            desc = itemView.findViewById(uiParams.getId_picker_recycler_item_desc());
            icon = itemView.findViewById(uiParams.getId_picker_recycler_item_icon());
            name = itemView.findViewById(uiParams.getId_picker_recycler_item_name());
            select = itemView.findViewById(uiParams.getId_picker_recycler_item_select());

        }
    }
}
