package com.honda.olympus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.honda.olympus.dao.AfeAckMsgEntity;

@Repository
public interface AfeAckMsgEvRepository extends JpaRepository<AfeAckMsgEntity, Long> {

}
