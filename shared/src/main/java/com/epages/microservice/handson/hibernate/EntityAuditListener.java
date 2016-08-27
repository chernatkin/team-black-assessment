package com.epages.microservice.handson.hibernate;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Table;

import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;

import com.epages.microservice.handson.shared.jpa.Audited;
import com.epages.microservice.handson.shared.jpa.EntityAudit;

public class EntityAuditListener implements PreUpdateEventListener, PreDeleteEventListener, PostInsertEventListener {

    private static final long serialVersionUID = 9120073823777698089L;

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        auditEntityChanges(event.getPersister(), 
                           event.getSession(), 
                           event.getEntity(), 
                           Optional.ofNullable(event.getDeletedState()), 
                           Optional.empty());
        return false;
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        auditEntityChanges(event.getPersister(), 
                           event.getSession(), 
                           event.getEntity(), 
                           Optional.ofNullable(event.getOldState()), 
                           Optional.ofNullable(event.getState()));
        return false;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        auditEntityChanges(event.getPersister(), 
                           event.getSession(), 
                           event.getEntity(), 
                           Optional.empty(), 
                           Optional.ofNullable(event.getState()));
    }
    
    private void auditEntityChanges(EntityPersister persister,
                                    EventSource session,
                                    Object entity,
                                    Optional<Object[]> oldState, 
                                    Optional<Object[]> newState){
        
        String[] fieldNames = persister.getEntityMetamodel().getPropertyNames();
        Class<?> clazz = persister.getClassMetadata().getMappedClass();
        Serializable id = persister.getIdentifier(entity, session);
        
        if(fieldNames == null || fieldNames.length == 0){
            throw new IllegalStateException(String.format("Entity %s has no fields", clazz));
        }
        
        for(int i = 0; i < fieldNames.length; i++){
            final int fieldIndex = i;
            Object oldValue = oldState.map(state -> state[fieldIndex]).orElse(null);
            Object newValue = newState.map(state -> state[fieldIndex]).orElse(null);
            
            try {
                auditFieldChangeIfNeed(id, clazz, fieldNames[fieldIndex], oldValue, newValue, session);
            } catch (NoSuchFieldException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private void auditFieldChangeIfNeed(Serializable id,
                                         Class<?> clazz,
                                         String fieldName,
                                         Object oldValue, 
                                         Object newValue,
                                         EventSource session) throws NoSuchFieldException {

        Field field = clazz.getDeclaredField(fieldName);

        Audited audited = field.getAnnotation(Audited.class);
        if(audited == null || Objects.equals(oldValue, newValue)){
            return;
        }

        if(id == null){
            throw new IllegalStateException(String.format("Audited entity %s has empty id", clazz));
        }

        if(!Number.class.isAssignableFrom(id.getClass())){
            throw new IllegalStateException(String.format("Audited entity %s should have id which is instance of Number", clazz));
        }

        String entityName = Optional.ofNullable(clazz.getAnnotation(Table.class)).map(t -> t.name()).orElse(clazz.getName());
        String auditedFieldName = Optional.ofNullable(field.getAnnotation(Column.class)).map(c -> c.name()).orElse(fieldName);
        long auditedId = ((Number)id).longValue();

        session.save(new EntityAudit(entityName, auditedFieldName, auditedId, Objects.toString(newValue, null)));
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }
}
