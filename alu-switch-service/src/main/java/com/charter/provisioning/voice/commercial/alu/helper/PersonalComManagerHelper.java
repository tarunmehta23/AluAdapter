package com.charter.provisioning.voice.commercial.alu.helper;

import com.alu.plexwebapi.api.PlexViewRequestType;
import com.charter.provisioning.voice.commercial.alu.config.CTSConfiguration;
import com.charter.provisioning.voice.commercial.alu.config.CTSSwitchConfiguration;
import com.charter.provisioning.voice.commercial.alu.config.OMCPCommands;
import com.charter.provisioning.voice.commercial.alu.exceptions.ProvisioningServiceException;
import com.charter.provisioning.voice.commercial.alu.model.BusinessClassPhone;
import com.charter.provisioning.voice.commercial.alu.model.RingPattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Map;

@Slf4j
@Component
public class PersonalComManagerHelper {

    private final CTSSwitchConfiguration ctsSwitchConfig;

    @Value("${PCMSwitchConfiguration.EnterpriseId}")
    private String enterpriseId;

    @Value("${PCMSwitchConfiguration.SwitchName}")
    private String pcmSwitchName;

    @Value("${PCMSwitchConfiguration.DefaultPassword}")
    private String password;

    @Value("${PCMSwitchConfiguration.PartitionId}")
    private String partitionedId;

    @Value("${PCMSwitchConfiguration.TemplateId}")
    private String templateId;

    @Autowired
    public PersonalComManagerHelper(CTSSwitchConfiguration ctsSwitchConfig) {
        this.ctsSwitchConfig = ctsSwitchConfig;
    }

    public PlexViewRequestType buildCreatePCMSubscriberRequest(String subscriberId, String correlationId) {
        PlexViewRequestType plexViewRequestType = getPlexViewRequestTemplate(OMCPCommands.RTRV_PCM_ENTUSER, pcmSwitchName, correlationId);
        plexViewRequestType.setAid(createAid(enterpriseId, subscriberId));
        return plexViewRequestType;
    }

    public PlexViewRequestType buildCreatePCMSubscriberRequest(BusinessClassPhone bcp, String requestId) {
        CTSConfiguration ctsConfiguration = ctsSwitchConfig.getCTSSwitchConfiguration(bcp.getSwitchName());
        PlexViewRequestType plexViewRequestType = getPlexViewRequestTemplate(OMCPCommands.ENT_PCM_ENTUSER, pcmSwitchName, requestId);

        String tnWithPlus1Prefix = String.format("+1%s", bcp.getPhoneNumber());
        plexViewRequestType.setPcmEntUserAid(createAid(enterpriseId, bcp.getSubscriberId()));
        plexViewRequestType.setUserId(bcp.getSubscriberId());
        plexViewRequestType.setCsServicePointID(bcp.getSubscriberId());
        plexViewRequestType.setPrimaryPublicId(tnWithPlus1Prefix);
        plexViewRequestType.setCallServerPublicIds(tnWithPlus1Prefix);

        String displayName = bcp.getDisplayName();

        if (StringUtils.isNotEmpty(displayName)) {
            String firstName = StringUtils.substringBefore(displayName, " ");
            String lastName = StringUtils.substringAfter(displayName, " ");
            lastName = StringUtils.isNotBlank(lastName) ? lastName : firstName;

            plexViewRequestType.setFirstName(firstName);
            plexViewRequestType.setLastName(lastName);
        }

        plexViewRequestType.setGroupId("");
        plexViewRequestType.setCsPrivilegeLevel("1");
        plexViewRequestType.setIsAssigned(Boolean.toString(true));

        plexViewRequestType.setPassword(password);
        plexViewRequestType.setPartitionId(partitionedId);
        plexViewRequestType.setTemplateId(templateId);
        plexViewRequestType.setTopologyId(ctsConfiguration.getTopologyId());

        populateCallServerPublicIDs(plexViewRequestType, bcp);

        return plexViewRequestType;
    }

    private PlexViewRequestType getPlexViewRequestTemplate(String command, String pcmSwitchName, String correlationId) {
        PlexViewRequestType plexViewRequestTemplate = PlexViewRequestType.Factory.newInstance();

        plexViewRequestTemplate.setCommand(command);
        plexViewRequestTemplate.setSwitchName(pcmSwitchName);
        plexViewRequestTemplate.setRequestId(correlationId);
        plexViewRequestTemplate.setMaxRows(new BigInteger("-1"));
        return plexViewRequestTemplate;
    }

    private String createAid(String enterpriseId, String subscriberId) {
        return String.format("%s-%s", enterpriseId, subscriberId);
    }

    private void populateCallServerPublicIDs(PlexViewRequestType plexViewRequestType, BusinessClassPhone dcp) {
        String delims = "^";
        String plus1 = "+1";
        String callServerPublicIds;

        if (dcp.getDistinctiveRingFeature() != null) {
            StringBuilder sbStandard = new StringBuilder();
            StringBuilder sbRingPattern = new StringBuilder();

            for (Map.Entry<RingPattern, String> entry : dcp.getDistinctiveRingFeature().getRingPatterns().entrySet()) {
                String patternName = entry.getKey().name();
                String mrpTN = entry.getValue();

                if (StringUtils.isNotEmpty(patternName) && StringUtils.isNotEmpty(mrpTN)) {
                    if (patternName.toUpperCase().startsWith(RingPattern.STANDARD.name())) {
                        sbStandard.append(delims);
                        sbStandard.append(plus1).append(mrpTN);
                    } else if (patternName.toUpperCase().startsWith("RINGPATTERN")) {
                        sbRingPattern.append(delims);
                        sbRingPattern.append(plus1).append(mrpTN);
                    }
                }
            }

            callServerPublicIds = String.format("%s%S", sbStandard.toString(), sbRingPattern.toString());
            String callServerPublicUIDs = formatCallServerPublicUIDs(callServerPublicIds);

            if (StringUtils.isNotEmpty(callServerPublicUIDs)) {
                plexViewRequestType.setCallServerPublicIds(callServerPublicUIDs);
            }
        }
    }

    private static String formatCallServerPublicUIDs(String callServerPublicUIDs) {
        String delims = "\\^";
        String delim = "^";
        String doubleDelims = "\\^\\^";

        if (callServerPublicUIDs != null) {
            callServerPublicUIDs = callServerPublicUIDs.replaceAll(doubleDelims, delims);

            if (callServerPublicUIDs.startsWith(delim)) {
                callServerPublicUIDs = callServerPublicUIDs.replaceFirst(delims, "");
            }

            if (callServerPublicUIDs.endsWith(delim)) {
                callServerPublicUIDs = callServerPublicUIDs.substring(0, callServerPublicUIDs.length() - 1) + ' ';
            }

            callServerPublicUIDs = callServerPublicUIDs.trim();
        }

        return callServerPublicUIDs;
    }
}