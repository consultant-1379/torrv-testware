package com.ericsson.nms.rv.taf.test.arne.operator;

import java.util.*;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.rv.taf.test.arne.element.Entry;
import com.ericsson.nms.rv.taf.test.arne.element.ManagedElementXml;

/**
 * Created by ewandaf on 17/07/14.
 */
public class ManagedElement {
    public static final String MIM_TO_IDENTITY_MAPPINGS = "mim_to_identity_mappings";
    public static final String SEPARATOR = "||";
    public static final String ERBS_CONNECTIVITY_INFO_MODEL_VERSION = "erbs_connectivity_info_model_version";
    private static final String SECURITY_TYPE_NORMAL = "NORMAL";
    private static final String SECURITY_TYPE_SECURE = "SECURE";
    private String managedElementId;
    private String ipAddress;
    private String erbsConnectivityInfoModelVersion = (String) DataHandler
            .getAttribute(ERBS_CONNECTIVITY_INFO_MODEL_VERSION);
    private String neMIMversion;
    private String enodeBFunctionModelVersion;
    private String ossModelIdentity;
    private String simulation;
    private String netsimHost;
    private final Map<String, String> mimToIdMap = getMimMap(MIM_TO_IDENTITY_MAPPINGS);
    private String secureUserName;
    private String secureUserPassword;
    private String normalUserName;
    private String normalUserPassword;

    public ManagedElement(final ManagedElementXml model) {
        this.managedElementId = model.getManagedElementId().getString();

        this.neMIMversion = model.getNeMIMVersion().getString();
        if (this.neMIMversion.startsWith("v")) {
            this.neMIMversion = this.neMIMversion.substring(1);
        }

        this.ipAddress = model.getConnectivity().getaDefault().getIpAddress()
                .getIpAddress();
        if (this.ipAddress == null) {
            this.ipAddress = model.getConnectivity().getaDefault()
                    .getIpAddress().getIpv4Address();
        }

        this.enodeBFunctionModelVersion = convertNeMimVersionToENodeBFunctionModelVersion(this.neMIMversion);
        this.ossModelIdentity = mimToIdMap.get(this.neMIMversion);

        final List<Entry> tssEntries = model.getTss().geEntryList();
        for (final Entry entry : tssEntries) {
            final String type = entry.getType().getText().trim();
            final String user = entry.getUser().getText().trim();
            final String password = entry.getPassword().getText().trim();

            if (type.equals(SECURITY_TYPE_NORMAL)) {
                this.normalUserName = user;
                this.normalUserPassword = password;
            } else if (type.equals(SECURITY_TYPE_SECURE)) {
                this.secureUserName = user;
                this.secureUserPassword = password;
            }
        }
    }

    private String convertNeMimVersionToENodeBFunctionModelVersion(
            final String mim) {
        //convert vE.1.200 or E.1.200 to 5.1.200
        final int num = mim.charAt(0) - 'A' + 1;
        return num + mim.substring(1);
    }

    public String getManagedElementId() {
        return managedElementId;
    }

    public void setManagedElementId(final String managedElementId) {
        this.managedElementId = managedElementId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getErbsConnectivityInfoModelVersion() {
        return erbsConnectivityInfoModelVersion;
    }

    public void setErbsConnectivityInfoModelVersion(
            final String erbsConnectivityInfoModelVersion) {
        this.erbsConnectivityInfoModelVersion = erbsConnectivityInfoModelVersion;
    }

    public String getEnodeBFunctionModelVersion() {
        return enodeBFunctionModelVersion;
    }

    public void setEnodeBFunctionModelVersion(
            final String enodeBFunctionModelVersion) {
        this.enodeBFunctionModelVersion = enodeBFunctionModelVersion;
    }

    public String getOssModelIdentity() {
        return ossModelIdentity;
    }

    public void setOssModelIdentity(final String ossModelIdentity) {
        this.ossModelIdentity = ossModelIdentity;
    }

    public String getNeMIMversion() {
        return neMIMversion;
    }

    public void setNeMIMversion(final String neMIMversion) {
        this.neMIMversion = neMIMversion;
    }

    public void setSimulation(final String simulation) {
        this.simulation = simulation;
    }

    public String getSimulation() {
        return simulation;
    }

    public void setNetsimHost(final String netsimHost) {
        this.netsimHost = netsimHost;
    }

    public String getNetsimHost() {
        return netsimHost;
    }

    public static Map<String, String> getMimMap(final String mappingName) {
        final Object mapObject = DataHandler.getAttribute(mappingName);
        List<String> mapList = new ArrayList<String>();
        if (mapObject instanceof Collection) {
            mapList = (List<String>) mapObject;
        }
        if (mapObject instanceof String) {
            mapList.add(mapObject.toString());
        }
        final Map<String, String> toReturn = new HashMap<String, String>();
        for (final String item : mapList) {
            final StringTokenizer tokens = new StringTokenizer(item, "||");
            if (tokens.countTokens() == 2) {
                toReturn.put(tokens.nextToken().trim(), tokens.nextToken()
                        .trim());
            }
        }
        return toReturn;
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
}
