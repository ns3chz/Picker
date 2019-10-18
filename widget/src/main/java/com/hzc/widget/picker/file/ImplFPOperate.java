package com.hzc.widget.picker.file;

import androidx.annotation.NonNull;

import com.zch.last.view.recycler.model.ModelChoose;

import java.io.File;
import java.util.List;

public interface ImplFPOperate {

    void fileSelected(@NonNull List<ModelChoose<File>> chooseList, @NonNull ModelChoose<File> modelChoose, boolean isChoose);

}
