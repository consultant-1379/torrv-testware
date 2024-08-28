package com.ericsson.nms.rv.taf.test.shm.operators;

/**
 * Created by ewandaf on 09/06/14.
 */
public interface ShmOperator {
    String HW_INVENTORY_LINK="/oss/shm/rest/inventory/hw/subracks";
    String SW_INVENTORY_LINK="oss/shm/rest/inventory/sw/upgradepackages";
    String LICENSE_INVENTORY_LINK="/oss/shm/rest/inventory/license/licensesummary";
    ShmResponse getHWInventoryFromCLI(String node);
    ShmResponse getHWInventory(String collectionName);
    ShmResponse getSWInventory(String collectionName);
    ShmResponse getLicenseInventory(String collectionName);
}
