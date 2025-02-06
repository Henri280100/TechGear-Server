package com.v01.techgear_server.service.ShippingServices;

import java.util.concurrent.CompletableFuture;

import com.v01.techgear_server.dto.ShipperDTO;

public interface ShipperService {
    CompletableFuture<ShipperDTO> createShipper(ShipperDTO shipperDTO);
    CompletableFuture<ShipperDTO> updateShipper(ShipperDTO shipperDTO);
    CompletableFuture<ShipperDTO> deleteShipperById(Long shipperId);
    CompletableFuture<ShipperDTO> deleteShipperByUsername(Long shipperId, String username);
    
}
