package com.v01.techgear_server.enums;

public enum Category {
    // PcCategories
    INTEL(PcCategories.INTEL),
    AMD(PcCategories.AMD),
    BRAND(PcCategories.BRAND),

    // AccessoriesCategories
    GPU(AccessoriesCategories.GPU),
    CPU(AccessoriesCategories.CPU),
    MOTHERBOARD(AccessoriesCategories.Motherboard),
    RAM(AccessoriesCategories.RAM),
    SSD(AccessoriesCategories.SSD);

    private final Enum<?> subCategory;

    Category(Enum<?> subCategory) {
        this.subCategory = subCategory;
    }

    public Enum<?> getSubCategory() {
        return subCategory;
    }

}
