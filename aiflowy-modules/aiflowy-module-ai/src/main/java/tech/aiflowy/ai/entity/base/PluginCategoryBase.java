package tech.aiflowy.ai.entity.base;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;


public class PluginCategoryBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto, value = "snowFlakeId")
    private BigInteger id;

    private String name;

    private Date createdAt;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}
