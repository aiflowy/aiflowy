package tech.aiflowy.admin.controller.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import tech.aiflowy.ai.entity.Model;
import tech.aiflowy.ai.entity.ModelProvider;
import tech.aiflowy.ai.entity.table.ModelTableDef;
import tech.aiflowy.ai.mapper.ModelMapper;
import tech.aiflowy.ai.service.ModelService;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.tree.Tree;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 控制层。
 *
 * @author michael
 * @since 2024-08-23
 */
@RestController
@RequestMapping("/api/v1/model")
public class ModelController extends BaseCurdController<ModelService, Model> {

    public ModelController(ModelService service) {
        super(service);
    }

    @Autowired
    ModelService modelService;

    @Resource
    ModelMapper modelMapper;

    @GetMapping("list")
    @SaCheckPermission("/api/v1/model/query")
    public Result<List<Model>> list(Model entity, Boolean asTree, String sortKey, String sortType) {
        QueryWrapper queryWrapper = QueryWrapper.create(entity, buildOperators(entity));
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));
        List<Model> list = Tree.tryToTree(modelMapper.selectListWithRelationsByQuery(queryWrapper), asTree);
        list.forEach(item -> {
            String providerName = Optional.ofNullable(item.getModelProvider())
                    .map(ModelProvider::getProviderName)
                    .orElse("-");
            item.setTitle(providerName + "/" + item.getTitle());
        });
        return Result.ok(list);
    }

    @GetMapping("getList")
    @SaCheckPermission("/api/v1/model/query")
    public Result<Map<String, Map<String, List<Model>>>> getList(Model entity) {
        return Result.ok(modelService.getList(entity));
    }

    @PostMapping("/addAiLlm")
    @SaCheckPermission("/api/v1/model/save")
    public Result<Boolean> addAiLlm(Model entity) {
        LoginAccount account = SaTokenUtil.getLoginAccount();
        commonFiled(entity, account.getId(), account.getTenantId(), account.getDeptId());
        return Result.ok(modelService.addAiLlm(entity));
    }


    @GetMapping("verifyLlmConfig")
    @SaCheckPermission("/api/v1/model/save")
    public Result<?> verifyLlmConfig(@RequestParam BigInteger id) {
        Model model = service.getModelInstance(id);
        return Result.ok(modelService.verifyModelConfig(model));
    }

    @PostMapping("/removeByEntity")
    @SaCheckPermission("/api/v1/model/remove")
    public Result<?> removeByEntity(@RequestBody Model entity) {
        modelService.removeByEntity(entity);
        return Result.ok();
    }

    @PostMapping("/updateByEntity")
    @SaCheckPermission("/api/v1/model/save")
    public Result<?> updateByEntity(@RequestBody Model entity) {
        modelService.updateByEntity(entity);
        return Result.ok();
    }

    @GetMapping("/selectLlmByProviderCategory")
    @SaCheckPermission("/api/v1/model/query")
    public Result<Map<String, List<Model>>> selectLlmByProviderCategory(Model entity, String sortKey, String sortType) {
        QueryWrapper queryWrapper = QueryWrapper.create(entity, buildOperators(entity));
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));
        BaseMapper<Model> mapper = service.getMapper();
        List<Model> totalList = mapper.selectListWithRelationsByQuery(queryWrapper);
        Map<String, List<Model>> groupList = totalList.stream().collect(Collectors.groupingBy(Model::getGroupName));
        return Result.ok(groupList);
    }

    @GetMapping("/selectLlmByProviderAndModelType")
    @SaCheckPermission("/api/v1/model/query")
    public Result<Map<String, List<Model>>> selectLlmByProviderAndModelType(
            @RequestParam String modelType,
            @RequestParam BigInteger providerId,
            @RequestParam(required = false) String selectText
    ) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq(Model::getProviderId, providerId);
        queryWrapper.eq(Model::getModelType, modelType);
        if (StringUtils.hasLength(selectText)) {
            queryWrapper.and(ModelTableDef.MODEL.TITLE.like(selectText).or(ModelTableDef.MODEL.MODEL_NAME.like(selectText)));
        }
        List<Model> totalList = service.getMapper().selectListWithRelationsByQuery(queryWrapper);
        Map<String, List<Model>> groupList = totalList.stream().collect(Collectors.groupingBy(Model::getGroupName));
        return Result.ok(groupList);
    }


    /**
     * 添加所有模型
     *
     * @param entity
     * @return
     */
    @PostMapping("addAllLlm")
    public Result<?> addAllLlm(@JsonBody Model entity) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq(Model::getProviderId, entity.getProviderId());
        service.update(entity, queryWrapper);
        return Result.ok();
    }

    @GetMapping("/selectLlmList")
    @SaCheckPermission("/api/v1/model/query")
    public Result<List<Model>> selectLlmList(Model entity, Boolean asTree, String sortKey, String sortType) {
        QueryWrapper queryWrapper = QueryWrapper.create(entity, buildOperators(entity));
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));
        List<Model> totalList = Tree.tryToTree(modelMapper.selectListWithRelationsByQuery(queryWrapper), asTree);
        totalList.forEach(aiLlm -> {
            aiLlm.setTitle(aiLlm.getModelProvider().getProviderName() + "/" + aiLlm.getTitle());
        });
        return Result.ok(totalList);
    }

    @Override
    protected Result<?> onSaveOrUpdateBefore(Model entity, boolean isSave) {
        if (isSave) {
            entity.setWithUsed(true);
        }
        return super.onSaveOrUpdateBefore(entity, isSave);
    }


    @PostMapping("removeLlmByIds")
    @Transactional
    public Result<?> removeLlm(@JsonBody(value = "id", required = true) Serializable id) {
        List<Serializable> ids = Collections.singletonList(id);
        QueryWrapper queryWrapper = QueryWrapper.create().in(Model::getId, ids);
        service.remove(queryWrapper);
        return Result.ok();
    }
}