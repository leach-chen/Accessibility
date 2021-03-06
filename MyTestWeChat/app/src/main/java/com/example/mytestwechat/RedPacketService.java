package com.example.mytestwechat;

/**
 * ClassName:   RedPacketService.java
 * Description:
 * Author :     leach.chen
 * Date:        2017/2/4 10:47
 **/

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;

import static android.R.attr.name;
import static com.example.mytestwechat.MainActivity.CHECK_AUTOBACK;
import static com.example.mytestwechat.MainActivity.CHECK_AUTOSELF;
import static com.example.mytestwechat.MainActivity.RADIG_AUTO;
import static com.example.mytestwechat.MainActivity.RADIG_HAND;

/**
 * 抢红包Service,继承AccessibilityService
 */
public class RedPacketService extends AccessibilityService {
    /**
     * 微信几个页面的包名+地址。用于判断在哪个页面 LAUCHER-微信聊天界面，LUCKEY_MONEY_RECEIVER-点击红包弹出的界面
     */
    private String LAUCHER = "com.tencent.mm.ui.LauncherUI";
    private String LUCKEY_MONEY_DETAIL = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";
    private String LUCKEY_MONEY_RECEIVER = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";

    /**
     * 用于判断是否点击过红包了
     */
    private boolean isOpenRP;


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            int eventType = event.getEventType();
            //Log.d("chat", "eventType:" + eventType);
            switch (eventType) {
                //通知栏来信息，判断是否含有微信红包字样，是的话跳转
                case 2048:
                    AccessibilityNodeInfo rootNode1 = getRootInActiveWindow();
                    if(rootNode1 == null)return;
                    if ((rootNode1.findAccessibilityNodeInfosByText("领取红包") != null && rootNode1.findAccessibilityNodeInfosByText("领取红包").size() > 0) ) {
                        boolean isAuto = SharedPreferencesHelp.getInstance(this).getBooleanDefaultFalse(RADIG_AUTO);
                        if (isAuto) {
                            openRedLook(rootNode1);
                        }
                    }
                    /*else if ((rootNode1.findAccessibilityNodeInfosByText("查看红包") != null && rootNode1.findAccessibilityNodeInfosByText("查看红包").size() > 0) ) {
                        boolean isAutoSelf = SharedPreferencesHelp.getInstance(this).getBooleanDefaultFalse(CHECK_AUTOSELF);
                        if (isAutoSelf) {
                            openRedSelf(rootNode1);
                        }
                    }*/
                    break;
                case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                    List<CharSequence> texts = event.getText();
                    for (CharSequence text : texts) {
                        String content = text.toString();
                        if (!TextUtils.isEmpty(content)) {
                            //判断是否含有[微信红包]字样
                            if (content.contains("[微信红包]")) {
                                //如果有则打开微信红包页面
                                openWeChatPage(event);

                                isOpenRP = false;
                            }
                        }
                    }
                    break;
                //界面跳转的监听
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                    String className = event.getClassName().toString();
                    //判断是否是微信聊天界面
                    if (LAUCHER.equals(className)) {
                        //获取当前聊天页面的根布局
                        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                        //开始找红包
                        findRedPacket(rootNode);
                    }

                    //判断是否是显示‘开’的那个红包界面
                    if (LUCKEY_MONEY_RECEIVER.equals(className)) {
                        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                        //开始抢红包
                        openRedPacket(rootNode);
                    }

                    //判断是否是红包领取后的详情界面,是的话则退出详情界面
                    if (LUCKEY_MONEY_DETAIL.equals(className)) {
                        //返回桌面
                        //back2Home();
                        AccessibilityNodeInfo rootNode2 = getRootInActiveWindow();
                        if(rootNode2 == null)return;
                        boolean isAutoBack = SharedPreferencesHelp.getInstance(this).getBooleanDefaultTrue(CHECK_AUTOBACK);
                        if (isAutoBack) {
                            goBack(rootNode2);
                        }
                    }
                    break;
            }
        }catch (Exception e)
        {
            Log.e("mytest",e.toString());
        }
    }

    /**
     * 抢完红包点击返回
     */
    private void goBack(AccessibilityNodeInfo rootNode) {
        try {
            int count = rootNode.getChildCount();
            for (int i = count - 1; i >= 0; i--) {
                AccessibilityNodeInfo node = rootNode.getChild(i);
                if (node == null) continue;
                AccessibilityNodeInfo parent = node.getParent();
                while (parent != null) {
                    if (parent.isClickable()) {
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        break;
                    }
                    parent = parent.getParent();
                }

                goBack(node);
            }
        }catch (Exception e)
        {
            Log.e("mytest",e.toString());
        }
    }


    /**
     * 点击查看红包
     */
    private void openRedSelf(AccessibilityNodeInfo rootNode) {
        try {
            int count = rootNode.getChildCount();
            for (int i = 0; i <= count; i++) {
                AccessibilityNodeInfo node = rootNode.getChild(i);
                if (node == null) continue;
                AccessibilityNodeInfo parent = node.getParent();
                while (parent != null) {
                    if (parent.isClickable() && (!"android.widget.ImageView".equals(node.getClassName()))) {
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        break;
                    }
                    parent = parent.getParent();
                }
                openRedSelf(node);
            }
        }catch (Exception e)
        {
            Log.e("mytest",e.toString());
        }
    }



    /**
     * 点击查看红包
     */
    private void openRedLook(AccessibilityNodeInfo rootNode) {
        try {
            int count = rootNode.getChildCount();
            for (int i = count - 1; i >= 0; i--) {
                AccessibilityNodeInfo node = rootNode.getChild(i);
                if (node == null) continue;
                AccessibilityNodeInfo parent = node.getParent();
                while (parent != null) {
                    if (parent.isClickable() && (!"android.widget.ImageView".equals(node.getClassName()))) {
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        break;
                    }
                    parent = parent.getParent();
                }
                openRedLook(node);
            }
        }catch (Exception e)
        {
            Log.e("mytest",e.toString());
        }
    }

    /**
     * 点击打开红包
     */
    private void openRedPacket(AccessibilityNodeInfo rootNode) {
        try {
            for (int i = 0; i < rootNode.getChildCount(); i++) {
                AccessibilityNodeInfo node = rootNode.getChild(i);
                if ("android.widget.Button".equals(node.getClassName())) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                openRedPacket(node);
            }
        }catch (Exception e)
        {
            Log.e("mytest",e.toString());
        }
    }

    /**
     * 遍历查找红包
     */
    private void findRedPacket(AccessibilityNodeInfo rootNode) {
        try {
            if (rootNode != null) {
                //从最后一行开始找起
                for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {
                    AccessibilityNodeInfo node = rootNode.getChild(i);
                    //如果node为空则跳过该节点
                    if (node == null) {
                        continue;
                    }
                    CharSequence text = node.getText();
                    if (text != null && text.toString().equals("领取红包")) {
                        AccessibilityNodeInfo parent = node.getParent();
                        //while循环,遍历"领取红包"的各个父布局，直至找到可点击的为止
                        while (parent != null) {
                            if (parent.isClickable()) {
                                //模拟点击
                                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                //isOpenRP用于判断该红包是否点击过
                                isOpenRP = true;
                                break;
                            }
                            parent = parent.getParent();
                        }
                    }
                    //判断是否已经打开过那个最新的红包了，是的话就跳出for循环，不是的话继续遍历
                    if (isOpenRP) {
                        break;
                    } else {
                        findRedPacket(node);
                    }

                }
            }
        }catch (Exception e)
        {
            Log.e("mytest",e.toString());
        }
    }

    /**
     * 开启红包所在的聊天页面
     */
    private void openWeChatPage(AccessibilityEvent event) {
        try {
            //A instanceof B 用来判断内存中实际对象A是不是B类型，常用于强制转换前的判断
            if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
                Notification notification = (Notification) event.getParcelableData();
                //打开对应的聊天界面
                PendingIntent pendingIntent = notification.contentIntent;
                try {
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e)
        {
            Log.e("mytest",e.toString());
        }
    }


    /**
     * 服务连接
     */
    @Override
    protected void onServiceConnected() {
        Toast.makeText(this, "抢红包服务开启", Toast.LENGTH_SHORT).show();
        super.onServiceConnected();
    }

    /**
     * 必须重写的方法：系统要中断此service返回的响应时会调用。在整个生命周期会被调用多次。
     */
    @Override
    public void onInterrupt() {
        Toast.makeText(this, "我快被终结了啊-----", Toast.LENGTH_SHORT).show();
    }

    /**
     * 服务断开
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "抢红包服务已被关闭", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }

    /**
     * 返回桌面
     */
    private void back2Home() {
        Intent home=new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

}
