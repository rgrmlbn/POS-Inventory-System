package com.spring.backend.module.product.inventory.service.impl;

import com.spring.backend.module.product.inventory.mapper.InventoryMapper;
import com.spring.backend.module.product.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InventoryServiceImplTest {

    @Test
    void countsInventoriesWithZeroProductQuantity() {
        InventoryRepository inventoryRepository = mock(InventoryRepository.class);
        InventoryServiceImpl inventoryService = new InventoryServiceImpl(
                inventoryRepository,
                mock(InventoryMapper.class)
        );
        when(inventoryRepository.countByProductQuantity(0)).thenReturn(3L);

        assertEquals(3L, inventoryService.getAllOutOfStockProductCount());
        verify(inventoryRepository).countByProductQuantity(0);
    }
}
