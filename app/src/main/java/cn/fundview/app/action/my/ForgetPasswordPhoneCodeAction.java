package cn.fundview.app.action.my;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.fundview.app.action.ABaseAction;
import cn.fundview.app.domain.webservice.RService;
import cn.fundview.app.model.ResultBean;
import cn.fundview.app.tool.NetWorkConfig;
import cn.fundview.app.tool.ToastUtils;
import cn.fundview.app.tool.json.JSONTools;
import cn.fundview.app.view.ABaseWebView;

/**
 * 找回密码的手机验证码
 */
public class ForgetPasswordPhoneCodeAction extends ABaseAction {

    /**
     * 需要验证的手机号
     */
   private String username;

    /**
     * 处理结果
     **/
    private String message;// 发送的返回信息
    private String checkCode;//验证码
    private int status;// 返回的结果

    public ForgetPasswordPhoneCodeAction(Context context, ABaseWebView webView) {
        super(context, webView);
    }

    /**
     * 执行action流程
     */
    public void execute(String username) {

        this.username = username;

        handle(true);
    }

    /**
     * 执行异步处理
     **/
    protected void doAsynchHandle() {

        if (NetWorkConfig.checkNetwork(context)) {

            //网络连接正常
            Map<String, String> param = new HashMap<>();
            param.put("username", username);//登录用户id
            ResultBean resultBean;
            try {
                resultBean = JSONTools.parseResult(RService.doPostSync(param, cn.fundview.app.domain.webservice.util.Constants.FORGET_PASSWORD_SEND_CODE_URL));
                if(resultBean != null) {

                    message = resultBean.getMessage();
                    status = resultBean.getStatus();
                    if(cn.fundview.app.domain.webservice.util.Constants.REQUEST_SUCCESS == resultBean.getStatus()) {

                        checkCode = resultBean.getResult();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{

            message = "网络连接失败,请稍后再试...";
        }

    }

    /**
     * 处理结果
     **/
    @Override
    protected void doHandleResult() {

        if(message != null && !message.trim().equals(""))
        ToastUtils.show(context, message);

        if(status == 2){

            //验证码发送成功
            webView.loadUrl("javascript:Page.wait120Seconds('"+checkCode+"');");
        }else {

            webView.loadUrl("javascript:Page.sendFailed();");

        }

    }
}
