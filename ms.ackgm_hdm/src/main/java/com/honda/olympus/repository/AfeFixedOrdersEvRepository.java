package com.honda.olympus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.honda.olympus.dao.AfeFixedOrdersEvEntity;

@Repository
public interface AfeFixedOrdersEvRepository extends JpaRepository<AfeFixedOrdersEvEntity, Long> {

	// QUERY1
	@Query("SELECT o FROM AfeFixedOrdersEvEntity o WHERE o.id = :id ")
	List<AfeFixedOrdersEvEntity> findAllById(@Param("id") Long id);
}
