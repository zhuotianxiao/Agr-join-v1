package cn.fundview.app.action.my;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.fundview.app.action.ABaseAction;
import cn.fundview.app.domain.dao.DaoFactory;
import cn.fundview.app.domain.model.UserInfor;
import cn.fundview.app.domain.webservice.RService;
import cn.fundview.app.model.ResultBean;
import cn.fundview.app.tool.Constants;
import cn.fundview.app.tool.JsMethod;
import cn.fundview.app.tool.file.PreferencesUtils;
import cn.fundview.app.tool.json.JSONTools;
import cn.fundview.app.view.ABaseWebView;

/**
 * 修改企业类型
 */
public class SaveProfileCompTypeAction extends ABaseAction {

    /**
     * 参数
     **/
    private int compType;
    private String callback;
    private int uid;

    /**
     * 处理结果
     ***/
    private boolean result;

    public SaveProfileCompTypeAction(Context context, ABaseWebView webView) {
        super(context, webView);
    }

    public void execute(int compType, String callback, int uid) {

        this.compType = compType;
        this.callback = callback;
        this.uid = uid;
        handle(true);
    }

    /**
     * 执行同步处理
     **/
    @Override
    protected void doAsynchHandle() {

        if (compType != 0) {
            // 上传到网络
            Map<String, String> param = new HashMap<>();
            param.put("accountId", PreferencesUtils.getInt(context, Constants.ACCOUNT_ID) + "");
            param.put("attName", UserInfor.SERVER_COMP_TYPE);
            param.put("attValue", compType+"");
            String jsonReturn = RService.doPostSync(param, cn.fundview.app.domain.webservice.util.Constants.UPDATE_USERINFOR_ATTR_URL);

            try {
                ResultBean resultBean = JSONTools.parseResult(jsonReturn);

                if (resultBean != null && resultBean.getStatus() == cn.fundview.app.domain.webservice.util.Constants.REQUEST_SUCCESS) {

                    UserInfor userInfor = new UserInfor();
                    userInfor.setCompType(compType);
                    userInfor.setId(uid);
                    result = DaoFactory.getInstance(context).getUserInforDao().saveOrUpdate(userInfor);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理结果
     **/
    @Override
    protected void doHandleResult() {

        String js = JsMethod.createJs("javascript:" + callback + "(${result},${compType});", result?1:0, compType);
        webView.loadUrl(js);
    }

}
