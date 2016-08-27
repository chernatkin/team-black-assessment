package com.epages.microservice.handson.shared.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityAuditRepository extends JpaRepository<EntityAudit, Long>{

}
