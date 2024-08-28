package com.ericsson.nms.rv.taf.test.apache.operators.dto;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class file is mapped to node property file.
 */
public class Node {
    private String managedElementId;
    private String managedElementModelVersion;
    private String eNodeBFunctionModelVersion;
    private String ossModelIdentity;
    private String erbsConnectivityInfoModelVersion;
    private String ipAddress;
    private String simulation;
    private String netsim;
    private String nodePoolGroup;
    private String secureUserName;
    private String secureUserPassword;
    private String normalUserName;
    private String normalUserPassword;

    public String getNetsim() {
        return netsim;
    }

    public void setNetsim(final String netsim) {
        this.netsim = netsim;
    }

    public String getManagedElementId() {
        return managedElementId;
    }

    public void setManagedElementId(final String managedElementId) {
        this.managedElementId = managedElementId;
    }

    public String getManagedElementModelVersion() {
        return managedElementModelVersion;
    }

    public void setManagedElementModelVersion(
            final String managedElementModelVersion) {
        this.managedElementModelVersion = managedElementModelVersion;
    }

    public String getENodeBFunctionModelVersion() {
        return eNodeBFunctionModelVersion;
    }

    public void setENodeBFunctionModelVersion(
            final String eNodeBFunctionModelVersion) {
        this.eNodeBFunctionModelVersion = eNodeBFunctionModelVersion;
    }

    public String getOssModelIdentity() {
        return ossModelIdentity;
    }

    public void setOssModelIdentity(final String ossModelIdentity) {
        this.ossModelIdentity = ossModelIdentity;
    }

    public String getERBSConnectivityInfoModelVersion() {
        return erbsConnectivityInfoModelVersion;
    }

    public void setERBSConnectivityInfoModelVersion(
            final String eRBSConnectivityInfoModelVersion) {
        this.erbsConnectivityInfoModelVersion = eRBSConnectivityInfoModelVersion;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getSimulation() {
        return simulation;
    }

    public void setSimulation(final String simulation) {
        this.simulation = simulation;
    }

    public String getNodePoolGroup() {
        return nodePoolGroup;
    }

    public void setNodePoolGroup(final String nodePoolGroup) {
        this.nodePoolGroup = nodePoolGroup;
    }

    public String getSecureUserName() {
        return secureUserName;
    }

    public void setSecureUserName(final String secureUserName) {
        this.secureUserName = secureUserName;
    }

    public String getSecureUserPassword() {
        return secureUserPassword;
    }

    public void setSecureUserPassword(final String secureUserPassword) {
        this.secureUserPassword = secureUserPassword;
    }

    public String getNormalUserName() {
        return normalUserName;
    }

    public void setNormalUserName(final String normalUserName) {
        this.normalUserName = normalUserName;
    }

    public String getNormalUserPassword() {
        return normalUserPassword;
    }

    public void setNormalUserPassword(final String normalUserPassword) {
        this.normalUserPassword = normalUserPassword;
    }

    public Object get(final String key) {
        final Class c = this.getClass();
        Method m = null;
        try {
            m = c.getMethod(getGetter(key));
            return m.invoke(this);
        } catch (final NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        } catch (final InvocationTargetException e) {
            e.printStackTrace();
            return null;
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getGetter(final String key) {
        return "get" + key.substring(0, 1).toUpperCase()
                + key.substring(1, key.length());
    }

    @Override
    public String toString() {
        return "[" + this.getManagedElementId() + "]";
    }
}
