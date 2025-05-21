package tech.aiflowy.ai.entity.base;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import java.io.Serializable;
import java.sql.Timestamp;


public class AiPluginCategoriesBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto, value = "snowFlakeId")
    private Integer id;

    private String name;

    private Timestamp createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

}
