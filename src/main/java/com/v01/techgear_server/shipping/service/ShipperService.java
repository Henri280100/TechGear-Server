package com.v01.techgear_server.shipping.service;

import java.util.concurrent.CompletableFuture;

import com.v01.techgear_server.shipping.dto.ShipperDTO;

public interface ShipperService {
    CompletableFuture<ShipperDTO> createShipper(ShipperDTO shipperDTO);
    CompletableFuture<ShipperDTO> updateShipper(ShipperDTO shipperDTO);
    CompletableFuture<ShipperDTO> deleteShipperById(Long shipperId);
    CompletableFuture<ShipperDTO> deleteShipperByUsername(Long shipperId, String username);
    
}
