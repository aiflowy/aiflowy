package tech.aiflowy.system.service;

import com.mybatisflex.core.service.IService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.system.entity.SysToken;

import java.io.Serializable;

/**
 * iframe 嵌入用 Token 表 服务层。
 *
 * @author Administrator
 * @since 2025-05-26
 */
public interface SysTokenService extends IService<SysToken> {

    Result<Void> saveGenerateToken();

    Result<Void> updateToken(SysToken sysToken);

    void delete(Serializable id);
}
