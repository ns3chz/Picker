package com.hzc.widget.picker.file;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.RecyclerView;

import com.hzc.widget.R;
import com.zch.last.utils.UtilCom;
import com.zch.last.utils.UtilFiles;
import com.zch.last.utils.UtilToast;
import com.zch.last.view.recycler.adapter.listener.OnItemClickListener;
import com.zch.last.view.recycler.adapter.listener.OnRecyclerItemSelectedListener;
import com.zch.last.view.recycler.decoration.VerticalRecyclerItemDecoration;
import com.zch.last.view.recycler.layout_manager.OpenLinearLayoutManager;
import com.zch.last.view.recycler.model.ModelChoose;
import com.zch.last.vmodel.BaseViewModel;
import com.zch.last.widget.dialog.DetailsDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilePicker_ViewModel extends BaseViewModel {
    static final String EXTRA_UI_PARAMS_KEY = "extra_ui_params_key";
    static final String RESULT_KEY = "result_pick";
    static final int RESULT_OK = 100;
    static final int RESULT_CANCELED = 101;

    @NonNull
    private final FilePickerUiParams uiParams;
    private FPAdapter fpAdapter;
    private ImplFPOperate mImplFPOperate;

    FilePicker_ViewModel(@NonNull Activity activity, @NonNull FilePickerUiParams params) {
        super(activity);
        this.uiParams = params;
    }

    void setRecyclerView(@NonNull RecyclerView recyclerView) {
        Activity activity = wrActivity.get();
        recyclerView.setLayoutManager(new OpenLinearLayoutManager(activity));
        fpAdapter = new FPAdapter(activity, uiParams);
        recyclerView.setAdapter(fpAdapter);
        recyclerView.addItemDecoration(
                new VerticalRecyclerItemDecoration(UtilCom.getColor(activity, R.color.line_gray), 1));

        initListener();
    }

    private void initListener() {
        fpAdapter.setOnItemClickListener(new OnItemClickListener<RecyclerView.ViewHolder, File>() {
            @Override
            public boolean onItemClick(RecyclerView.ViewHolder viewholder, File file, int position) {
                if (file.isFile()) {
                    fpAdapter.select(position, true);
                } else {
                    uiParams.setCurrentFile(file);
                    refreshFolder();
                    if (mImplFPOperate != null && fpAdapter.getChooseList().size() == 0) {
                        mImplFPOperate.setPickedDesc(null);
                    }
                }
                return true;
            }
        });
        fpAdapter.setOnRecyclerItemSelectedListener(new OnRecyclerItemSelectedListener<RecyclerView.ViewHolder, File>() {
            @Override
            public void selected(@Nullable RecyclerView.ViewHolder holder, @NonNull ModelChoose<File> modelChoose, boolean isChoose) {
                List<ModelChoose<File>> chooseList = fpAdapter.getChooseList();
                if (mImplFPOperate != null) {
                    mImplFPOperate.fileSelected(chooseList, modelChoose, isChoose);
                }
            }
        });
    }

    void notifyQuery(String key) {
        if (fpAdapter != null) {
            fpAdapter.notifyQuery(key);
        }
    }

    /**
     * 取消选择
     */
    public void cancelPick() {
        Activity activity = wrActivity.get();
        activity.setResult(RESULT_CANCELED);
        activity.finish();
    }

    /**
     * 返回上级目录
     */
    public void backFolder() {
        File lastFile = uiParams.getCurrentFile();
        File currentFile = lastFile.getParentFile();

        if (currentFile == null) {
            currentFile = Environment.getExternalStorageDirectory();
        }
        if (!uiParams.setCurrentFile(currentFile)) {
            return;
        }
        refreshFolder();
        if (mImplFPOperate != null) {
            mImplFPOperate.setPickedDesc(null);
        }
    }

    public void clickExtra(View view) {
        switch (uiParams.getPickType()) {
            case FILE:
                break;
            case FOLDER:
            case FILE_OR_FOLDER:
                getFolderCreateDialog().show();
                break;
        }
    }

    /**
     * 返回结果
     */
    public void sendResult() {
        Intent intent = new Intent();
        switch (uiParams.getPickType()) {
            case FILE_OR_FOLDER:
                List<ModelChoose<File>> chooseList = fpAdapter.getChooseList();
                if (chooseList.size() == 0) {
                    intent.putExtra(RESULT_KEY, uiParams.getCurrentFile());
                    break;
                }
            case FILE:
                chooseList = fpAdapter.getChooseList();
                if (chooseList.size() == 0) {
                    Toast.makeText(wrActivity.get(), "请选择文件", Toast.LENGTH_LONG).show();
                    return;
                }
                ModelChoose<File> fileModelChoose;
                switch (uiParams.getChoiceMode()) {
                    case SINGLE:
                        fileModelChoose = chooseList.get(0);
                        intent.putExtra(RESULT_KEY, fileModelChoose.getData());

                        break;
                    case MULTI:
                        ArrayList<File> pathList = new ArrayList<>();
                        for (int i = 0; i < chooseList.size(); i++) {
                            fileModelChoose = chooseList.get(i);
                            pathList.add(fileModelChoose.getData());
                        }
                        intent.putExtra(RESULT_KEY, pathList);
                        break;
                    case NONE:
                        break;
                }

                break;
            case FOLDER:
                File currentFile = uiParams.getCurrentFile();
                intent.putExtra(RESULT_KEY, currentFile);
                break;
        }
        Activity activity = wrActivity.get();
        activity.setResult(RESULT_OK, intent);
        activity.finish();
    }

    private DetailsDialog folderCreateDialog;

    private DetailsDialog getFolderCreateDialog() {
        if (folderCreateDialog == null) {
            folderCreateDialog = new DetailsDialog(wrActivity.get())
                    .setDialogBackgroundRes(R.color.trans_dark_00)
                    .setBodyBackgroundRes(R.color.white_teeth)
                    .setTitleImg(R.mipmap.icon_setting)
                    .setTitleImgDimenWH(R.dimen.dialog_icon_width, R.dimen.dialog_icon_height)
                    .setTitleText("新建文件夹")
                    .setTitleTextColor(Color.BLACK)
                    .setTitleBackgroundRes(R.drawable.bg_dialog_ask_title)
                    .hasEdit(true)
                    .setDialogWH(0.7f, -2)
                    .createButtons()
                    .setButtons("取消", "创建")
                    .setButtonTextColorRes(R.color.selector_press_black_2_white)
                    .setButtonTextSizeRes(R.dimen.text_size_normal)
                    .setButtonBackground(R.drawable.selector_bg_dialog_ask_left_button, R.drawable.selector_bg_dialog_ask_right_button)
                    .setOnButtonClickListener(new DetailsDialog.OnButtonClickListener() {
                        @Override
                        public void onClick(View v, int position) {
                            switch (position) {
                                case 0:
                                    folderCreateDialog.dissmiss();
                                    folderCreateDialog.contentEdit.setText(null);
                                    break;
                                case 1:
                                    String editContent = folderCreateDialog.getEditContent();
                                    if (editContent == null || editContent.length() == 0) {
                                        UtilToast.toast("请填写文件夹名");
                                        return;
                                    }
                                    File currentFile = uiParams.getCurrentFile();
                                    if (!UtilFiles.createDirs(currentFile.getAbsolutePath() + File.separator + editContent)) {
                                        UtilToast.toast("创建文件夹失败");
                                        return;
                                    }
                                    folderCreateDialog.dissmiss();
                                    UtilToast.toast("创建文件夹成功");
                                    refreshFolder();
                                    break;
                            }
                        }
                    })
                    .build();
        }
        return folderCreateDialog;
    }

    private void refreshFolder() {
        if (PermissionChecker.PERMISSION_GRANTED != PermissionChecker.checkSelfPermission(wrActivity.get(), Manifest.permission_group.STORAGE)) {
            ActivityCompat.requestPermissions(wrActivity.get(), new String[]{Manifest.permission_group.STORAGE}, 0);
        }
        fpAdapter.notifyFolderChanged();
    }

    void setFPOperateImpl(ImplFPOperate operate) {
        this.mImplFPOperate = operate;
    }
}
