package tech.aiflowy.ai.entity;

import tech.aiflowy.ai.entity.base.AiBotKnowledgeBase;
import com.mybatisflex.annotation.RelationOneToOne;
import com.mybatisflex.annotation.Table;

/**
 *  实体类。
 *
 * @author michael
 * @since 2024-08-28
 */

@Table("tb_ai_bot_knowledge")
public class AiBotKnowledge extends AiBotKnowledgeBase {

    @RelationOneToOne(selfField = "knowledgeId", targetField = "id")
    private AiKnowledge knowledge;

    public AiKnowledge getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(AiKnowledge knowledge) {
        this.knowledge = knowledge;
    }
}
