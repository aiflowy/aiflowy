package tech.aiflowy.admin.controller.datacenter;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import cn.idev.excel.EasyExcel;
import cn.idev.excel.FastExcel;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.DatacenterQuery;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.datacenter.entity.DatacenterTable;
import tech.aiflowy.datacenter.entity.DatacenterTableFields;
import tech.aiflowy.datacenter.entity.vo.HeaderVo;
import tech.aiflowy.datacenter.excel.ReadDataListener;
import tech.aiflowy.datacenter.excel.ReadResVo;
import tech.aiflowy.datacenter.service.DatacenterTableFieldsService;
import tech.aiflowy.datacenter.service.DatacenterTableService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 数据中枢表 控制层。
 *
 * @author ArkLight
 * @since 2025-07-10
 */
@RestController
@RequestMapping("/api/v1/datacenterTable")
public class DatacenterTableController extends BaseCurdController<DatacenterTableService, DatacenterTable> {

    @Resource
    private DatacenterTableFieldsService fieldsService;

    public DatacenterTableController(DatacenterTableService service) {
        super(service);
    }

    @Override
    protected Result onSaveOrUpdateBefore(DatacenterTable entity, boolean isSave) {
        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
        if (isSave) {
            commonFiled(entity, loginUser.getId(), loginUser.getTenantId(), loginUser.getDeptId());
        } else {
            entity.setModifiedBy(loginUser.getId());
        }
        return super.onSaveOrUpdateBefore(entity, isSave);
    }

    @PostMapping("/saveTable")
    @SaCheckPermission("/api/v1/datacenterTable/save")
    public Result<Void> saveTable(@RequestBody DatacenterTable entity) {
        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
        List<DatacenterTableFields> fields = entity.getFields();
        if (CollectionUtil.isEmpty(fields)) {
            return Result.fail(99, "字段不能为空");
        }
        BigInteger id = entity.getId();
        if (id == null) {
            commonFiled(entity, loginUser.getId(), loginUser.getTenantId(), loginUser.getDeptId());
        } else {
            entity.setModified(new Date());
            entity.setModifiedBy(loginUser.getId());
        }
        service.saveTable(entity, loginUser);
        return Result.ok();
    }

    @GetMapping("/detailInfo")
    @SaCheckPermission("/api/v1/datacenterTable/query")
    public Result<DatacenterTable> detailInfo(BigInteger tableId) {
        DatacenterTable table = service.getById(tableId);
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq(DatacenterTableFields::getTableId, tableId);
        wrapper.orderBy("id");
        List<DatacenterTableFields> fields = fieldsService.list(wrapper);
        table.setFields(fields);
        return Result.ok(table);
    }

    @GetMapping("/removeTable")
    @SaCheckPermission("/api/v1/datacenterTable/remove")
    public Result<Void> removeTable(BigInteger tableId) {
        service.removeTable(tableId);
        return Result.ok();
    }

    @GetMapping("/getHeaders")
    @SaCheckPermission("/api/v1/datacenterTable/query")
    public Result<List<HeaderVo>> getHeaders(BigInteger tableId) {
        List<HeaderVo> res = service.getHeaders(tableId);
        return Result.ok(res);
    }

    @GetMapping("/getPageData")
    @SaCheckPermission("/api/v1/datacenterTable/query")
    public Result<Page<Row>> getPageData(DatacenterQuery where) {
        Page<Row> res = service.getPageData(where);
        return Result.ok(res);
    }

    @PostMapping("/saveValue")
    @SaCheckPermission("/api/v1/datacenterTable/save")
    public Result<Void> saveValue(@RequestParam Map<String, Object> map) {
        JSONObject object = new JSONObject(map);
        BigInteger tableId = object.getBigInteger("tableId");
        LoginAccount account = SaTokenUtil.getLoginAccount();
        if (tableId == null) {
            return Result.fail(99, "参数错误");
        }
        service.saveValue(tableId, object, account);
        return Result.ok();
    }

    @PostMapping("/removeValue")
    @SaCheckPermission("/api/v1/datacenterTable/remove")
    public Result<Void> removeValue(@RequestParam Map<String, Object> map) {
        JSONObject object = new JSONObject(map);
        BigInteger tableId = object.getBigInteger("tableId");
        BigInteger id = object.getBigInteger("id");
        if (tableId == null || id == null) {
            return Result.fail(99, "参数错误");
        }
        LoginAccount account = SaTokenUtil.getLoginAccount();
        service.removeValue(tableId, id, account);
        return Result.ok();
    }

    /**
     * 导入数据
     */
    @PostMapping("/importData")
    @SaCheckPermission("/api/v1/datacenterTable/save")
    public Result<ReadResVo> importData(MultipartFile file, @RequestParam Map<String, Object> map) throws Exception {
        Object tableId = map.get("tableId");
        DatacenterTable record = service.getById(tableId.toString());
        if (record == null) {
            throw new RuntimeException("数据表不存在");
        }
        InputStream is = file.getInputStream();
        List<DatacenterTableFields> fields = service.getFields(record.getId());

        LoginAccount account = SaTokenUtil.getLoginAccount();
        ReadDataListener listener = new ReadDataListener(record.getId(), fields, account);
        FastExcel.read(is, listener)
                .sheet()
                .doRead();
        int totalCount = listener.getTotalCount();
        int errorCount = listener.getErrorCount();
        int successCount = listener.getSuccessCount();
        List<JSONObject> errorRows = listener.getErrorRows();
        return Result.ok(new ReadResVo(successCount, errorCount, totalCount, errorRows));
    }

    @GetMapping("/getTemplate")
    public void getTemplate(BigInteger tableId, HttpServletResponse response) throws Exception {
        List<DatacenterTableFields> fields = service.getFields(tableId);
        // 设置响应内容类型
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");

        // 设置文件名
        String fileName = URLEncoder.encode("导入模板", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        // 动态表头数据
        List<List<String>> headList = new ArrayList<>();

        for (DatacenterTableFields field : fields) {
            List<String> head = new ArrayList<>();
            head.add(field.getFieldName());
            headList.add(head);
        }

        // 写入Excel
        EasyExcel.write(response.getOutputStream())
                .head(headList)
                .sheet("模板")
                .doWrite(new ArrayList<>()); // 写入空数据，只生成模板
    }
}