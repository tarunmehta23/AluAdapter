package com.charter.provisioning.voice.commercial.alu.model.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Builder
@Data
public class SubParty {

    private String displayName;

    private String category;

    private String partyId;

    private String primaryPUID;

    private boolean primaryPUIDDomainRequired;

    private int primaryPUIDCPEProfileNumber;

    private boolean primaryPUIDFlashable;

    private String assocOtasRealm;

    private String networkProfileName;

    private int networkProfileVersion;

    private String serviceProfileName;

    private int serviceProfileVersion;

    private boolean reducedServiceProfile;

    private int callLimit;

    private boolean serviceSuspension;

    private boolean originationSuspension;

    private boolean terminationSuspension;

    private boolean suspensionNotification;

    private boolean userOrigSuspension;

    private boolean userTermSuspension;

    private String assocWpifRealm;

    private String iddPrefix;

    private String alternateFsdbFqdn;

    private boolean sharedHssData;

    private String pin;

    private boolean msnCapability;

    private boolean videoProhibit;

    private long maxFwdHops;

    private String csdFlavor;

    private boolean csdDynamic;

    private boolean csdUnrestricted;

    private int sipErrorTableId;

    private int treatmentTableId;

    private String locale;

    private String cliPrefixList;

    private boolean groupCPE;

    private String receive181Mode;

    private int ccNdcLength;

    private int maxActiveCalls;

    private String callingPartyCategory;

    private String publicUID1;

    private String publicUID2;

    private String publicUID3;

    private String publicUID4;

    private String publicUID5;

    private String publicUID6;

    private String publicUID7;

    private String publicUID8;

    private String publicUID9;

    private boolean publicUID1DomainRequired;

    private boolean publicUID2DomainRequired;

    private boolean publicUID3DomainRequired;

    private boolean publicUID4DomainRequired;

    private boolean publicUID5DomainRequired;

    private boolean publicUID6DomainRequired;

    private boolean publicUID7DomainRequired;

    private boolean publicUID8DomainRequired;

    private boolean publicUID9DomainRequired;

    private String wildCardPUIDStr;

    private boolean allowCustomAnnouncement;

    private BigInteger ptySpareLong1;

    private String ptySpareString;

    private String ptySpareString2;

    private BigInteger ptySpareShort1;

    private BigInteger ptySpareShort2;

    private boolean ptySpareBool1;

    private boolean ptySpareBool2;

    private boolean ptySpareBool3;

    private boolean ptySpareBool4;

    private boolean ptySpareBool5;

    private boolean ptySpareBool6;

    private boolean ptySpareBool7;

    private boolean ptySpareBool8;

    private int terminatingTableId;

    private boolean allowNonSipTelUri;

    private String locationType;

    private String rncID;

    private String lteMcc;

    private String lteMnc;

    private String lteTac;

    private String marketSID;

    private String switchNumber;

    private boolean callsToWebUserProhibited;

    private String imsi;

    private boolean imsNotSupported;

    private boolean validateCellID;

    private int operatorID;

    private int homeMTA;

    private boolean forwardDenyNumbers;

    private boolean playAnnoFailNotForward;

    private int mrfPoolID;

    private boolean custom120x;

    private String dualPersonaType;

    private int whisperBargeInAttPercent;

    private String featureTags;

    private boolean proxyCCBS;

    private int countryCode;

    private boolean denyIntlFwdExceptHome;

    private boolean hlrProvisioned;

}
