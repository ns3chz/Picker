package com.hzc.picker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hzc.widget.picker.file.FilePicker;
import com.hzc.widget.picker.file.FilePickerUiParams;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvResult = findViewById(R.id.tv_result);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilePicker.build(MainActivity.this, 1)
//                        .setOpenFile(new File("sdcard/123/"))
                        .setPickFileType(FilePickerUiParams.PickType.FILE)
//                        .setMultiPick(new FilePicker.OnMultiPickListener() {
//                            @Override
//                            public void pick(@NonNull List<File> pathList) {
//                                StringBuilder path = new StringBuilder("多选：\n");
//                                for (int i = 0; i < pathList.size(); i++) {
//                                    path.append(pathList.get(i).getAbsolutePath()).append("\n\n");
//                                }
//                                tvResult.setText(path.toString());
//                            }
//
//                            @Override
//                            public void cancel() {
//                                tvResult.setText("取消选择了");
//                            }
//                        })
                        .setSinglePick(new FilePicker.OnSinglePickListener() {
                            @Override
                            public void pick(@NonNull File path) {
                                tvResult.setText("单选 : \n" + path.getAbsolutePath());
                            }

                            @Override
                            public void cancel() {
                                tvResult.setText("取消选择了");
                            }
                        })
                        .open();
            }
        });

    }

    //                        .setMultiPick(new FilePicker.OnMultiPickListener() {
//                            @Override
//                            public void pick(@NonNull List<File> pathList) {
//                                StringBuilder path = new StringBuilder("多选：\n");
//                                for (int i = 0; i < pathList.size(); i++) {
//                                    path.append(pathList.get(i).getAbsolutePath()).append("\n\n");
//                                }
//                                tvResult.setText(path.toString());
//                            }
//
//                            @Override
//                            public void cancel() {
//                                tvResult.setText("取消选择了");
//                            }
//                        })
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FilePicker.onActivityResult(this, requestCode, resultCode, data);
    }

}
