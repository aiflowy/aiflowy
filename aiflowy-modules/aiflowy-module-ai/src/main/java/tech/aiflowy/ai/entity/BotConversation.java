package tech.aiflowy.ai.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.RelationOneToOne;
import com.mybatisflex.annotation.Table;
import tech.aiflowy.ai.entity.base.BotConversationBase;

import java.util.List;


/**
 *  实体类。
 *
 * @author Administrator
 * @since 2025-04-15
 */
@Table("tb_bot_conversation")
public class BotConversation extends BotConversationBase {
    @Column(ignore = true)
    private List<BotMessage> botMessageList;

    @RelationOneToOne(selfField = "botId", targetField = "id")
    private Bot bot;

    public List<BotMessage> getAiBotMessageList() {
        return botMessageList;
    }

    public void setAiBotMessageList(List<BotMessage> botMessageList) {
        this.botMessageList = botMessageList;
    }

    public Bot getBot() {
        return bot;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

}
