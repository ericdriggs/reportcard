package io.github.ericdriggs.reportcard.model;

import java.util.HashMap;
import java.util.Map;

public enum FaultContext {
    ERROR((byte) 1, "error", FaultContextType.ERROR),
    FAILURE((byte) 2, "failure", FaultContextType.FAILURE),
    FLAKY_ERROR((byte)3, "flakyError", FaultContextType.ERROR),
    FLAKY_FAILURE((byte)4, "flakyFailure", FaultContextType.FAILURE),
    RERUN_ERROR((byte)5, "rerunError", FaultContextType.ERROR),
    RERUN_FAILURE((byte)6, "rerunFailure", FaultContextType.FAILURE),
    ;

    FaultContext(byte faultContextId, String xmlFaultContextName, FaultContextType faultContextType) {
        this.faultContextId = faultContextId;
        this.xmlFaultContextName = xmlFaultContextName;
        this.faultContextType = faultContextType;
    }

    private final byte faultContextId;
    private final String xmlFaultContextName;

    private final FaultContextType faultContextType;

    public byte getFaultContextId() {
        return faultContextId;
    }

    public String getXmlFaultContextName() {
        return xmlFaultContextName;
    }

    public FaultContextType getFaultContextType() {
        return faultContextType;
    }

    private static final Map<Byte, FaultContext> idContextMap = new HashMap<>();

    private static final Map<String, FaultContext> xmlNameContextMap = new HashMap<>();

    private static void initMaps() {
        if (idContextMap.isEmpty()) {
            synchronized ("testContextMap") {
                for (FaultContext faultContext : FaultContext.values()) {
                    idContextMap.put(faultContext.getFaultContextId(), faultContext);
                }
            }
        }

        if (xmlNameContextMap.isEmpty()) {
            synchronized ("nameContextMap") {
                for (FaultContext faultContext : FaultContext.values()) {
                    xmlNameContextMap.put(faultContext.getXmlFaultContextName(), faultContext);
                }
            }
        }
    }

    public static FaultContext fromFaultContextId(byte faultContextId) {
        initMaps();
        FaultContext faultContext = idContextMap.get(faultContextId);
        if (faultContext == null) {
            throw new IllegalArgumentException("faultContextId not found: " + faultContextId);
        }
        return faultContext;
    }

    public static FaultContext fromXmlFaultContextName(String xmlFaultContextName) {
        initMaps();
        FaultContext faultContext = xmlNameContextMap.get(xmlFaultContextName);
        if (faultContext == null) {
            throw new IllegalArgumentException("xmlFaultContextName not found: " + xmlFaultContextName);
        }
        return faultContext;
    }

}
