package tech.aiflowy.common.entity;

import java.util.Date;

public abstract class DateEntity {

    public abstract Date getCreated();

    public abstract void setCreated(Date created);

    public abstract Date getModified();

    public abstract void setModified(Date modified);
}
