package com.sastellarsolutions.qaclothingfactory.model

enum class UserRole(
    val displayName: String,
    val apiName: String
) {

    ADMIN(
        displayName = "Admin",
        apiName = "Admin"
    ),

    PRODUCTION_MANAGER(
        displayName = "Production Manager",
        apiName = "ProductionManager"
    ),

    QUALITY_CONTROLLER(
        displayName = "Quality Controller",
        apiName = "QualityController"
    ),

    INVENTORY_CLERK(
        displayName = "Inventory Clerk",
        apiName = "InventoryClerk"
    ),

    SUPERVISOR(
        displayName = "Supervisor",
        apiName = "Supervisor"
    );


    companion object {

        fun fromString(
            role: String?
        ): UserRole? {

            if (role.isNullOrBlank()) {
                return null
            }

            return entries.firstOrNull {

                it.name.equals(
                    role,
                    ignoreCase = true
                ) ||

                        it.displayName.equals(
                            role,
                            ignoreCase = true
                        ) ||

                        it.apiName.equals(
                            role,
                            ignoreCase = true
                        )
            }
        }
    }
}