package tech.aiflowy.system.service.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import tech.aiflowy.common.ai.util.UUIDGenerator;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.util.DateUtil;
import tech.aiflowy.system.entity.SysToken;
import tech.aiflowy.system.mapper.SysTokenMapper;
import tech.aiflowy.system.service.SysTokenService;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * iframe 嵌入用 Token 表 服务层实现。
 *
 * @author Administrator
 * @since 2025-05-26
 */
@Service
public class SysTokenServiceImpl extends ServiceImpl<SysTokenMapper, SysToken>  implements SysTokenService{

    @Override
    public Result<Void> saveGenerateToken() {
        long loginId = StpUtil.getLoginIdAsLong();             // 这是要绑定的账号ID
                QueryWrapper queryWrapper = QueryWrapper.create()
                .select("*")
                .from("tb_sys_token")
                        .where("user_id = ? ", loginId);
        SysToken sysToken = this.getMapper().selectOneByQuery(queryWrapper);
        if (sysToken != null){
            return Result.fail(1, "已经存在 token， 无需生成");
        }
        String customToken = UUIDGenerator.generateUUID();; // 自定义的 Token 字符串
        SaLoginModel saLoginModel = new SaLoginModel();
        saLoginModel.setToken(customToken);
        // 默认30天过期
//        int timeout = 24*60*60*30;
        int timeout = 120;
        // 默认30天过期
        saLoginModel.setTimeout(timeout);
        // 将 loginId 与 customToken 关联，并设置有效期
        StpUtil.createLoginSession(loginId, saLoginModel);


        SysToken sysToken1 = new SysToken();
        sysToken1.setToken(customToken);
        sysToken1.setUserId(StpUtil.getLoginIdAsLong());
        sysToken1.setCreatedAt(LocalDateTime.now());
        // 计算过期时间（当前时间加上30天）
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(timeout);
        sysToken1.setExpireTime(expireTime);
        int insert = this.getMapper().insert(sysToken1);
        if (insert <= 0){
            return Result.fail(2, "新增失败");
        }
        return Result.ok();
    }

    @Override
    public Result<Void> updateToken(SysToken sysToken) {
        SysToken sysToken1 = this.getMapper().selectOneById(sysToken.getId());
        if (sysToken1 == null) {
            return Result.fail(1, "该 token 不存在，修改失败");
        }

        LocalDateTime newExpireTime = sysToken.getExpireTime();
        if (newExpireTime == null) {
            return Result.fail(3, "失效时间不能为空");
        }
        // 3. 计算新的过期时间（秒数）
        long timeoutInSeconds = Duration.between(LocalDateTime.now(), newExpireTime).getSeconds();
        if (timeoutInSeconds <= 0) {
            return Result.fail(4, "失效时间必须大于当前时间");
        }
        SaLoginModel saLoginModel = new SaLoginModel();
        saLoginModel.setToken(sysToken1.getToken());
        int timeout = (int) timeoutInSeconds;
        saLoginModel.setTimeout(timeout);
        StpUtil.createLoginSession(StpUtil.getLoginId(), saLoginModel);



        // 5. 更新数据库中的 expireTime
        sysToken1.setExpireTime(newExpireTime);
        int updateResult = this.getMapper().update(sysToken1);
        if (updateResult <= 0) {
            return Result.fail(5, "更新失败");
        }

        return Result.ok();
    }

    @Override
    public void delete(Serializable id) {
        SysToken sysToken = this.getMapper().selectOneById(id);
        StpUtil.renewTimeout(sysToken.getToken(), 0);
    }
}
