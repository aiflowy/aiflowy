package tech.aiflowy.ai.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import dev.tinyflow.core.chain.Chain;
import tech.aiflowy.common.constant.Constants;
import tech.aiflowy.common.entity.LoginAccount;

import java.math.BigInteger;

public class WorkFlowUtil {

    public final static String USER_KEY = "user";
    public final static String WORKFLOW_KEY = "workflow";

    public static String removeSensitiveInfo(String originJson) {
        JSONObject workflowInfo = JSON.parseObject(originJson);
        JSONArray nodes = workflowInfo.getJSONArray("nodes");
        for (Object node : nodes) {
            JSONObject nodeInfo = (JSONObject) node;
            JSONObject data = nodeInfo.getJSONObject("data");
            JSONObject newData = new JSONObject();
            newData.put("outputDefs", data.get("outputDefs"));
            newData.put("parameters", data.get("parameters"));
            newData.put("title", data.get("title"));
            newData.put("description", data.get("description"));
            nodeInfo.put("data", newData);
        }
        return workflowInfo.toJSONString();
    }

    public static LoginAccount getOperator(Chain chain) {
        Object cache = chain.getState().getMemory().get(Constants.LOGIN_USER_KEY);
        return cache == null ? defaultAccount() : (LoginAccount) cache;
    }

    public static LoginAccount defaultAccount() {
        LoginAccount account = new LoginAccount();
        account.setId(new BigInteger("0"));
        account.setDeptId(new BigInteger("0"));
        account.setTenantId(new BigInteger("0"));
        return account;
    }
}
