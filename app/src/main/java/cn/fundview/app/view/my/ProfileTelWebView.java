package cn.fundview.app.view.my;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;

import cn.fundview.app.action.my.LoadProfileAction;
import cn.fundview.app.action.my.SaveProfileTelAction;
import cn.fundview.app.domain.model.UserInfor;
import cn.fundview.app.tool.Constants;
import cn.fundview.app.tool.file.PreferencesUtils;
import cn.fundview.app.view.ABaseWebView;

public class ProfileTelWebView extends ABaseWebView {

    public ProfileTelWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.loadUrl("file:///android_asset/page/my/profile-tel.html");
    }

    @Override
    protected void init() {
        // TODO Auto-generated method stub

        LoadProfileAction action = new LoadProfileAction(context, this);
        action.execute(UserInfor.TEL, "Page.initPage", PreferencesUtils.getInt(context, Constants.ACCOUNT_ID), ((Activity) context).getIntent().getBooleanExtra("editable", true));
    }

    @JavascriptInterface
    public void save(String name) {

        SaveProfileTelAction action = new SaveProfileTelAction(context, this);
        action.execute(name, "Page.showHintDialog", PreferencesUtils.getInt(context, Constants.ACCOUNT_ID));
    }

    @JavascriptInterface
    public void closePage() {

        ((Activity) context).finish();
    }

    @Override
    public void onClickRight() {
        super.onClickRight();

        this.loadUrl("javascript:Page.saveTel();");
    }
}
