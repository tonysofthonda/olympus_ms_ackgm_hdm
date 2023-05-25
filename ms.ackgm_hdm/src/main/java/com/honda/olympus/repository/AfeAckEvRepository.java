package com.honda.olympus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.honda.olympus.dao.AfeAckEvEntity;

@Repository
public interface AfeAckEvRepository extends JpaRepository<AfeAckEvEntity, Long>{
	
	// QUERY6
		@Query("SELECT o FROM AfeAckEvEntity o WHERE o.fixedOrderId = :fixedOrderId ")
		public List<AfeAckEvEntity> findAllByFixedOrderId(@Param("fixedOrderId") Long fixedOrderId);

}
