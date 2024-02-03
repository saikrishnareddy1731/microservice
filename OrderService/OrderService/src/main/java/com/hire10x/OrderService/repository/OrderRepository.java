package com.hire10x.OrderService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hire10x.OrderService.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
}