package tech.aiflowy.system.service.impl;

import tech.aiflowy.system.entity.SysLog;
import tech.aiflowy.system.mapper.SysLogMapper;
import tech.aiflowy.system.service.SysLogService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 操作日志表 服务层实现。
 *
 * @author michael
 * @since 2024-01-28
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

}
