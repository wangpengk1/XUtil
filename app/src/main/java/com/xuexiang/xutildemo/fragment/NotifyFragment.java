/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
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
 */

package com.xuexiang.xutildemo.fragment;

import android.os.Handler;
import androidx.core.app.NotificationCompat;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageSimpleListFragment;
import com.xuexiang.xutil.app.PendingIntentUtils;
import com.xuexiang.xutil.app.notify.NotificationUtils;
import com.xuexiang.xutil.resource.ResourceUtils;
import com.xuexiang.xutildemo.R;
import com.xuexiang.xutildemo.activity.TestRouterActivity;
import com.xuexiang.xutildemo.receiver.NotifyBroadCastReceiver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuexiang
 * @date 2018/4/14 上午1:49
 */
@Page(name = "NotificationUtils使用")
public class NotifyFragment extends XPageSimpleListFragment {

    private int progresses = 0;

    private final Handler mHandler = new Handler();

    /**
     * 初始化例子
     *
     * @param lists
     * @return
     */
    @Override
    protected List<String> initSimpleData(List<String> lists) {
        lists.add("简单通知");
        lists.add("图片通知");
        lists.add("多文本通知");
        lists.add("消息盒通知");
        lists.add("进度条通知");
        lists.add("无进度条通知");
        lists.add("自定义通知");
        return lists;
    }

    /**
     * 条目点击
     *
     * @param position
     */
    @Override
    protected void onItemClick(int position) {
        switch(position) {
            case 0:
            NotificationUtils.buildSimple(1000, R.mipmap.ic_launcher,"我是通知的标题","我是通知的内容",null)
                    .setHeadUp(true)
                    .addAction(R.mipmap.ic_launcher, "确定",  PendingIntentUtils.buildBroadcastIntent(NotifyBroadCastReceiver.class, NotifyBroadCastReceiver.ACTION_SUBMIT, 0))
                    .addAction(R.mipmap.ic_launcher, "取消",  PendingIntentUtils.buildBroadcastIntent(NotifyBroadCastReceiver.class, NotifyBroadCastReceiver.ACTION_CANCEL, 0))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setIsPolling(true)
                    .show();
                break;
            case 1:
                Map<String, Object> params = new HashMap<>();
                params.put("param1", "我是参数1");
                params.put("param2", 123);
                NotificationUtils.buildBigPic(1001, R.mipmap.ic_launcher,"我是通知的标题","我是图片的概要信息")
                        .setPicRes(R.mipmap.timg2)
                        .setContentIntent(PendingIntentUtils.buildActivityIntent(TestRouterActivity.class, params))
                        .show();
                break;
            case 2:
                NotificationUtils.buildBigText(1002, R.mipmap.ic_launcher,"我是通知的标题", ResourceUtils.getFileFromRaw(R.raw.test))
                        .show();
                break;
            case 3:
                NotificationUtils.buildMailBox(1003, R.mipmap.ic_launcher,"我是通知的标题")
                        .addMsg("11111111111")
                        .addMsg("22222222222")
                        .addMsg("33333333333")
                        .setForegroundService()
                        .show();
                break;
            case 4:
                progresses = 0;
                mHandler.removeCallbacksAndMessages(null);
                showProgress();
                break;
            case 5:
                NotificationUtils.buildProgress(1005, R.mipmap.ic_launcher, "正在下载").show();
                break;
            case 6:
                NotificationUtils.buildCustomView(1006, R.mipmap.ic_launcher, "自定义通知", getActivity().getPackageName(), R.layout.layout_notification_custom_view)
                        .setImageViewResource(R.id.iv_icon, R.mipmap.ic_launcher)
                        .setTextViewText(R.id.tv_title, "我是自定义通知的标题")
                        .setTextViewText(R.id.tv_content, "我是自定义通知的内容")
                        .setOnClickPendingIntent(R.id.btn_reply, PendingIntentUtils.buildBroadcastIntent(NotifyBroadCastReceiver.class, NotifyBroadCastReceiver.ACTION_REPLY, 0))
                        .show();
                break;
            default:
                break;
        }
    }

    private void showProgress() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progresses >= 100){
                    return;
                }
                progresses++;

                NotificationUtils.buildProgress(1004, R.mipmap.ic_launcher, "正在下载", 100, progresses).show();
                showProgress();

            }
        },100);

    }
}
