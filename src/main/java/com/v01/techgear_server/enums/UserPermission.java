package com.v01.techgear_server.enums;

public enum UserPermission {
    // Product Permissions
    PRODUCT_CREATE, // Permission to create new products
    PRODUCT_READ, // Permission to read/view product details
    PRODUCT_UPDATE, // Permission to update existing products
    PRODUCT_DELETE, // Permission to delete products

    // Order Permissions
    ORDER_PROCESS, // Permission to process orders (Admin/Manager)
    ORDER_READ, // Permission to read/view all orders
    ORDER_CREATE, // Permission to create new orders
    ORDER_UPDATE, // Permission to update existing orders
    ORDER_READ_OWN, // User can only read their own orders

    // User Permissions
    USER_READ, // Permission to read/view user details
    USER_CREATE, // Permission to create new user accounts
    USER_UPDATE, // Permission to update existing user accounts
    USER_DELETE, // Permission to delete user accounts

    // Profile Permissions
    PROFILE_READ_OWN, // User can only read their own profile
    PROFILE_UPDATE_OWN, // User can only update their own profile

    // Review Permissions
    REVIEW_CREATE, // Permission to create product reviews
    REVIEW_READ_OWN, // User can only read their own reviews

    // Report Permissions
    REPORT_GENERATE, // Permission to generate reports (Admin/Manager)

    // Admin Permissions
    ACCESS_ADMIN_PANEL, // Permission to access the admin panel
    USER_MANAGE, // Permission to manage users (create, update, delete)
    PRODUCT_MANAGE, // Permission to manage products (create, update, delete)
    ORDER_MANAGE, // Permission to manage orders (view, update, process)
    DISCOUNT_MANAGE, // Permission to create and manage discounts and promotions
    INVENTORY_MANAGE, // Permission to manage inventory levels

    // Customer Support Permissions
    CUSTOMER_SUPPORT_ACCESS, // Permission to access customer support tools
    CUSTOMER_SUPPORT_MANAGE, // Permission to manage customer inquiries and support tickets

    // Manager Permissions
    SALES_PERFORMANCE_VIEW, // Permission to view sales performance metrics
    STAFF_MANAGE, // Permission to manage staff and assign tasks
    REPORT_VIEW_ALL, // Permission to view all reports (not just own)
}
