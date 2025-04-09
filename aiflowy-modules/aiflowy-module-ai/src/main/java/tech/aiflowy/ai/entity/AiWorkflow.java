package tech.aiflowy.ai.entity;

import tech.aiflowy.ai.entity.base.AiWorkflowBase;
import tech.aiflowy.common.util.StringUtil;
import com.agentsflex.core.llm.functions.Function;
import com.mybatisflex.annotation.Table;
import dev.tinyflow.core.Tinyflow;

/**
 * 实体类。
 *
 * @author michael
 * @since 2024-08-23
 */

@Table("tb_ai_workflow")
public class AiWorkflow extends AiWorkflowBase {


    public Tinyflow toTinyflow() {
        String jsonContent = this.getContent();
        if (StringUtil.noText(jsonContent)) {
            return null;
        }
        return new Tinyflow(jsonContent);
    }



    public Function toFunction() {
        return new AiWorkflowFunction(this);
    }
}
