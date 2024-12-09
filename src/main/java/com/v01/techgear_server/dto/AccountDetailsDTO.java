package com.v01.techgear_server.dto;

import java.util.*;
import java.time.LocalDateTime;

import com.v01.techgear_server.enums.UserGenders;
import com.v01.techgear_server.enums.UserTypes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Account Details Data Transfer Object")
public class AccountDetailsDTO {
    @Schema(description = "Account ID")
    @Positive(message = "Account ID must be a positive number")
    private Long accountDetailsId;

    @Schema(description = "First name")
    private String firstName;

    @Schema(description = "Last name")
    private String lastName;

    @Schema(description = "User phone number associated with account details")
    private UserPhoneNoDTO userPhoneNo;

    @Schema(description = "User address associated with account details")
    private UserAddressDTO userAddress;

    @Schema(description = "Image associated with account details")
    private ImageDTO image;

    @Schema(description = "Email verified")
    private boolean isEmailVerified;

    @Schema(description = "Phone number verified")
    private boolean isPhoneNumberVerified;

    @Schema(description = "Account creation timestamp")
    private LocalDateTime registrationDate;

    @Schema(description = "Account last updated timestamp")
    private LocalDateTime accountUpdatedTime;

    @Schema(description = "last_login_timestamp")
    private LocalDateTime lastLoginTimeStamp;

    @Schema(description = "User Types")
    private UserTypes userTypes;

    @Schema(description = "User associated with the account")
    private UserDTO user;

    @Schema(description = "Wishlist associated with account details")
    private List<WishlistDTO> wishlists;

    @Schema(description = "Order associated with account details")
    private List<OrderDTO> orders;

    @Schema(description = "Invoice associated with account details")
    private List<InvoiceDTO> invoices;

    @Schema(description = "Payment method associated with account details")
    private List<PaymentMethodDTO> paymentMethods;

    @Schema(description = "Total reviews")
    private int totalReview;

    @Schema(description = "Account age")
    private Long accountAgeDays;

    @Schema(description = "User genders")
    private UserGenders genders;

    @Schema(description = "User date of birth")
    private LocalDateTime dateOfBirth;

    @Schema(description = "Product ratings associated with account details")
    private List<ProductRatingDTO> productRatings;

    @Schema(description = "Shipper ratings associated with account details")
    private List<ShipperRatingDTO> shipperRatings;

    @Schema(description = "Order summary associated with account details")
    private List<OrderSummaryDTO> orderSummary;
}