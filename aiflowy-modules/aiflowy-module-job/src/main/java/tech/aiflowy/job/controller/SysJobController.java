package tech.aiflowy.job.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.date.DateUtil;
import org.quartz.CronExpression;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.constant.enums.EnumJobStatus;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.job.entity.SysJob;
import tech.aiflowy.job.service.SysJobService;

import tech.aiflowy.common.entity.LoginAccount;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 系统任务表 控制层。
 *
 * @author xiaoma
 * @since 2025-05-20
 */
@RestController
@RequestMapping("/api/v1/sysJob")
public class SysJobController extends BaseCurdController<SysJobService, SysJob> {
    public SysJobController(SysJobService service) {
        super(service);
    }

    @GetMapping("/start")
    @SaCheckPermission("/api/v1/sysJob/save")
    public Result<Void> start(BigInteger id) {
        SysJob sysJob = service.getById(id);
        sysJob.setStatus(EnumJobStatus.RUNNING.getCode());
        service.addJob(sysJob);
        service.updateById(sysJob);
        return Result.ok();
    }

    @GetMapping("/stop")
    @SaCheckPermission("/api/v1/sysJob/save")
    public Result<Void> stop(BigInteger id) {
        SysJob sysJob = new SysJob();
        sysJob.setId(id);
        sysJob.setStatus(EnumJobStatus.STOP.getCode());
        ArrayList<Serializable> ids = new ArrayList<>();
        ids.add(id);
        service.deleteJob(ids);
        service.updateById(sysJob);
        return Result.ok();
    }

    @GetMapping("/getNextTimes")
    public Result<List<String>> getNextTimes(String cronExpression) throws  Exception{
        CronExpression ex = new CronExpression(cronExpression);
        List<String> times = new ArrayList<>();
        Date date = new Date();
        for (int i = 0; i < 5; i++) {
            Date next = ex.getNextValidTimeAfter(date);
            times.add(DateUtil.formatDateTime(next));
            date = next;
        }
        return Result.ok(times);
    }

    @Override
    protected Result onSaveOrUpdateBefore(SysJob entity, boolean isSave) {
        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
        if (isSave) {
            commonFiled(entity,loginUser.getId(),loginUser.getTenantId(), loginUser.getDeptId());
        } else {
            entity.setModified(new Date());
            entity.setModifiedBy(loginUser.getId());
        }
        return super.onSaveOrUpdateBefore(entity, isSave);
    }

    @Override
    protected Result onRemoveBefore(Collection<Serializable> ids) {
        service.deleteJob(ids);
        return super.onRemoveBefore(ids);
    }
}