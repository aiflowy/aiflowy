package tech.aiflowy.ai.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import tech.aiflowy.ai.entity.base.AiBotConversationMessageBase;

import java.math.BigInteger;
import java.util.List;


/**
 *  实体类。
 *
 * @author Administrator
 * @since 2025-04-15
 */
@Table("tb_ai_bot_conversation_message")
public class AiBotConversationMessage extends AiBotConversationMessageBase {
    @Column(ignore = true)
    private List<AiBotMessage> aiBotMessageList;

    public List<AiBotMessage> getAiBotMessageList() {
        return aiBotMessageList;
    }

    public void setAiBotMessageList(List<AiBotMessage> aiBotMessageList) {
        this.aiBotMessageList = aiBotMessageList;
    }

}
