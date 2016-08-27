package com.epages.microservice.handson.shared.jpa;

import static com.google.common.base.MoreObjects.toStringHelper;
import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ENTITY_AUDIT")
public class EntityAudit {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    
    @Column(name="ENTITY_NAME", updatable = false, nullable = false)
    private String entityName;
    
    @Column(name="FIELD_NAME", updatable = false, nullable = false)
    private String fieldName;
    
    @Column(name="ENTITY_ID", updatable = false, nullable = false)
    private long entityId;
    
    @Column(name="STATE", updatable = false)
    private String state;
    
    @Column(name="EVENT_TIME", updatable = false, nullable = false)
    private Date eventTime;

    public EntityAudit() {
    }

    public EntityAudit(String entityName, String fieldName, long entityId, String state) {
        this.entityName = entityName;
        this.fieldName = fieldName;
        this.entityId = entityId;
        this.state = state;
        this.eventTime = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EntityAudit that = (EntityAudit) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    @Override
    public String toString() {
        return toStringHelper(this).add("id", id)
                                   .add("entityName", entityName)
                                   .add("fieldName", fieldName)
                                   .add("entityId", entityId)
                                   .add("state", state)
                                   .add("eventTime", eventTime)
                                   .toString();
    }

}
