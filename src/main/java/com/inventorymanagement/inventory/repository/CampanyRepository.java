package com.inventorymanagement.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.inventorymanagement.inventory.model.Company;


public interface CampanyRepository  extends JpaRepository<Company, Long>{


    
}
