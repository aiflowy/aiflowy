package tech.aiflowy.datacenter.service;

import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.service.IService;
import tech.aiflowy.common.entity.DatacenterQuery;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.datacenter.entity.DatacenterTable;
import tech.aiflowy.datacenter.entity.DatacenterTableFields;
import tech.aiflowy.datacenter.entity.vo.HeaderVo;

import java.math.BigInteger;
import java.util.List;

public interface DatacenterTableService extends IService<DatacenterTable> {

    void saveTable(DatacenterTable entity, LoginAccount loginUser);

    void removeTable(BigInteger tableId);

    Long getCount(DatacenterQuery where);

    List<Row> getListData(DatacenterQuery where);

    Page<Row> getPageData(DatacenterQuery where);

    List<HeaderVo> getHeaders(BigInteger tableId);

    void saveValue(BigInteger tableId, JSONObject object, LoginAccount account);

    void removeValue(BigInteger tableId, BigInteger id, LoginAccount account);

    List<DatacenterTableFields> getFields(BigInteger tableId);
}
