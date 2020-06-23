/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.xuexiang.xutildemo.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.appcompat.widget.AppCompatImageView;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xuexiang.xaop.annotation.IOThread;
import com.xuexiang.xaop.annotation.MainThread;
import com.xuexiang.xaop.annotation.Permission;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xutil.app.IntentUtils;
import com.xuexiang.xutil.app.PathUtils;
import com.xuexiang.xutil.app.SAFUtils;
import com.xuexiang.xutil.common.StringUtils;
import com.xuexiang.xutil.data.DateUtils;
import com.xuexiang.xutil.display.ImageUtils;
import com.xuexiang.xutil.file.FileUtils;
import com.xuexiang.xutil.resource.ResourceUtils;
import com.xuexiang.xutil.tip.ToastUtils;
import com.xuexiang.xutildemo.R;

import java.io.File;
import java.io.InputStream;



import static android.app.Activity.RESULT_OK;
import static com.xuexiang.xaop.consts.PermissionConsts.STORAGE;

/**
 * SAFUtils工具测试
 *
 * @author xuexiang
 * @since 2020/5/14 11:47 PM
 */
@Page(name = "SAFUtils 功能测试")
public class SAFFragment extends XPageFragment {

    /**
     * 选择系统文件Request Code
     */
    public static final int REQUEST_FILE = 1000;

    TextView tvContent;

    TextView tvOperation;

    AppCompatImageView ivContent;

    private String mFilePath;
    private Uri mUri;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_saf_utils;
    }

    @Override
    protected void initViews() {
        tvOperation.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvContent  = findViewById(R.id.tv_content);
        tvOperation = findViewById(R.id.tv_operation);
        ivContent = findViewById(R.id.iv_content);
    }

    @Override
    protected void initListeners() {
        ((Button)findViewById(R.id.btn_select)).setOnClickListener(onViewClicked);
    }

    public View.OnClickListener onViewClicked = new View.OnClickListener()
    {
        @Override
        @SingleClick
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_select:
                    selectFile();
                    break;
                case R.id.btn_write:
                    writeFile();
                    break;
                case R.id.btn_read:
                    readFile();
                    break;
                case R.id.btn_delete:
                    break;
                case R.id.btn_read_image:
                    readImage();
                    break;
                case R.id.btn_write_image:
                    break;
                default:
                    break;
            }
        }
    };


    @IOThread
    @Permission(STORAGE)
    private void writeFile() {
        InputStream inputStream = ResourceUtils.openAssetsFile("test.txt");
        String path = "test" + File.separator;
        String name = DateUtils.getNowString(DateUtils.yyyyMMddHHmmssNoSep.get()) + ".txt";
        if (SAFUtils.writeFileToPublicDownloads(path, name, inputStream)) {
            ToastUtils.toast("写入成功");
        } else {
            ToastUtils.toast("写入失败");
        }
    }


    private boolean isSelectFile() {
        return mUri != null && FileUtils.isFileExists(mFilePath);
    }

    @IOThread
    private void readFile() {
        if (!isSelectFile()) {
            ToastUtils.toast("请先选择文件");
            return;
        }

        if (!mFilePath.endsWith(".txt")) {
            ToastUtils.toast("请选择txt文件");
            return;
        }


        String content = ResourceUtils.readInputStream(SAFUtils.openInputStream(mUri), true);
        showOperationResult(content);
    }

    @IOThread
    private void readImage() {
        if (!isSelectFile()) {
            ToastUtils.toast("请先选择文件");
            return;
        }

        if (!mFilePath.endsWith(".jpg")) {
            ToastUtils.toast("请选择jpg图片");
            return;
        }

        Bitmap bitmap = ImageUtils.getBitmap(SAFUtils.openInputStream(mUri));
        showImg(bitmap);
    }

    @Permission(STORAGE)
    private void selectFile() {
        startActivityForResult(IntentUtils.getDocumentPickerIntent(IntentUtils.DocumentType.ANY), REQUEST_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //选择系统文件并解析
        if (resultCode == RESULT_OK && requestCode == REQUEST_FILE) {
            if (data != null) {
                mUri = data.getData();
                mFilePath = PathUtils.getFilePathByUri(getContext(), mUri);
                String result = mFilePath;
                if (StringUtils.isEmptyTrim(result)) {
                    result = "未解析成功";
                } else {
                    result += "\n\n文件是否存在:" + FileUtils.isFileExists(result);
                }
                tvContent.setText(result);
            }
        }
    }

    /**
     * 显示操作结果
     */
    @MainThread
    private void showOperationResult(String msg) {
        tvOperation.setText(msg);
    }

    @MainThread
    private void showImg(Bitmap bitmap) {
        ivContent.setImageBitmap(bitmap);
    }
}
