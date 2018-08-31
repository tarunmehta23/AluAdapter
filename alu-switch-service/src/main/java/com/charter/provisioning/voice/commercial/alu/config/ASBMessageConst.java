
package com.charter.provisioning.voice.commercial.alu.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class ASBMessageConst {

    public static final String	ASB_VERSION						= "2.0";
    //For namespace use below constants
    public static final String	ASB_ID_VALUE_TWC				= "TWC";

    //All namespaces goes here
    public static final String	ALU_NS							= "ALU";
    public static final String	TWC_NS							= "TWC";
    public static final String	DSB_NS							= "DSB";
    public static final String	PROP_NS							= TWC_NS;
    public static final String	PROP_TYPE_NS					= TWC_NS;

    public static final String	ASB_ID_RESPONSE					= "response";

    public static final String	ASB_ID_VALUE_ALU_ADAPTER		= "ALUAdapter";
    public static final String	ASB_ID_VALUE_BPS				= "BPS";
    //BCP.Portal is changed to CPP
    public static final String	ASB_ID_VALUE_CPP				= "CPP";

    public static final String	ASB_OPERATION_QUERY				= "query";
    public static final String	ASB_OPERATION_CREATE			= "create";
    public static final String	ASB_OPERATION_UPDATE			= "update";
    public static final String	ASB_OPERATION_DELETE			= "delete";
    public static final String  ASB_OPERATION_REFRESH           = "refresh";
    public static final String  ASB_OPERATION_CHANGETN          = "changetn";
    public static final String  ASB_OPERATION_ROLLBACK          = "rollback";

    public static final String	ASB_OK							= "OK";
    public static final String	ASB_ROLE						= "role";
    public static final String	ASB_CANCEL						= "Cancel";
    public static final String	ASB_ERROR						= "ERROR";
    public static final String	ASB_REQUEST						= "Request";

    public static final String	ASB_INACTIVE					= "inactive";
    public static final String	ASB_ACTIVE						= "active";

    public static final String	ASB_PRIVACY						= "PPS";
    public static final String	ASB_CALLER_ID					= "CNAM";
    public static final String  ASB_RCF							= "CallForward";
    public static final String	ASB_GRP_TIME_SLOTS				= "GroupTimeslots";
    public static final String	ASB_STATUS						= "status";
    public static final String	ASB_PHONE_NUMBER				= "PhoneNumber";
    public static final String	ASB_DIGEST_KEY					= "DigestKey";
    //Auth codes
    public static final String	ASB_GROUP_ID					= "GroupId";

    public static final String	ASB_PIC							= "PIC";
    public static final String	ASB_IPIC						= "IPIC";
    public static final String	ASB_LPIC						= "LPIC";
    public static final String	ASB_EMPTY						= "";

    // XStream Constants
    public static final String	XS_ASBMESSAGE					= "ASBMessage";
    public static final String	XS_HEADER						= "Header";
    public static final String	XS_BODY							= "Body";
    public static final String	XS_METADATA						= "Metadata";
    public static final String	XS_VERSION_ATTR					= "version";
    public static final String	XS_VALUE_ATTR					= "value";
    public static final String	XS_NUMRINGS_ATTR				= "rings";
    public static final String	XS_NUMRINGS_ATTR_VAL			= "numRing";
    public static final String	XS_ORDER_ATTR					= "order";
    public static final String	XS_ID_ATTR						= "id";
    public static final String	XS_TS_ATTR						= "timestamp";
    public static final String	XS_OPERATION_ATTR				= "operation";
    public static final String	XS_NS_ATTR						= "ns";
    public static final String	XS_TYPE_ATTR					= "type";
    public static final String	XS_UNIT_ATTR					= "unit";
    public static final String	XS_META_ATTR					= "meta";
    public static final String	XS_ITEMS_ATTR					= "items";
    public static final String	XS_MODE_ATTR					= "mode";
    public static final String	XS_ID							= "ID";
    public static final String	XS_CORRELATION					= "Correlation";
    public static final String	XS_PROPERTIES					= "Properties";
    public static final String	XS_PROPERTIES_TYPE				= "PropertiesType";
    public static final String	XS_ENDPOINT						= "Endpoint";
    public static final String	XS_EXCEPTION					= "Exception";
    public static final String	XS_EXPIRATION					= "Expiration";
    public static final String	XS_ITINERARY					= "Itinerary";
    public static final String	XS_MESSAGE						= "Message";
    public static final String	XS_PROPERTY_LIST				= "PropertyList";
    public static final String	XS_PROPERTY						= "Property";
    public static final String	XS_REPLY						= "Reply";
    public static final String	XS_REQUIRED						= "Required";
    public static final String	XS_STATUS						= "Status";
    public static final String	XS_VALUE						= "Value";
    public static final String	XS_URI							= "uri";
    public static final String	XS_SERVICE_PROPERTIES			= "ServiceProperties";
    public static final String	XS_ACCOUNT_SERVICE				= "AccountService";
    public static final String	XS_ACCOUNT_PROPERTIES			= "AccountProperties";
    public static final String	XS_QUERY						= "Query";
    public static final String	XS_TX_LOCAL						= "txLocal";
    public static final String	XS_DOCUMENT						= "Document";
    public static final String	XS_DATA						    = "Data";
    public static final String	XS_VXML_CONTENT                 = "VxmlContent";

    // Features
    public static final String	ASB_ACTION_CFNA					= "CFNA";
    public static final String	ASB_ACTION_CFB					= "CFB";
    public static final String	ASB_ACTION_SCF					= "SCF";
    public static final String	ASB_ACTION_ACR					= "ACR";
    public static final String	ASB_ACTION_SCR					= "SCR";
    public static final String	ASB_ACTION_SC1D					= "SC1D";
    public static final String	ASB_ACTION_CFU					= "CFU";
    public static final String	ASB_ACTION_CALLFORWARDING		= "CallForward";
    public static final String	ASB_ACTION_CALL_TRANSFER		= "CallTransfer";
    public static final String	ASB_ACTION_CWAIT				= "CWAIT";
    public static final String	ASB_ACTION_CIDCW				= "CIDCW";
    public static final String	ASB_ACTION_CALL_HOLD			= "CallHold";
    public static final String	ASB_ACTION_CALL_PARK_RTRV		= "CallParkRetrieve";
    public static final String	ASB_ACTION_DIRECTED_CALL_PICKUP	= "DPU";
    public static final String	ASB_ACTION_SIMUL_RING			= "SimultaneousRing";
    public static final String	ASB_ACTION_SEQ_RING				= "SequentialRing";
    public static final String	ASB_ACTION_REMOTE_OFFICE		= "RemoteOffice";
    public static final String	ASB_ACTION_PINSERVICE    		= "PinService";
    public static final String	ASB_ACTION_SUSPENDED_LINE		= "SuspendedLine";
    public static final String	ASB_ACTION_CALL_RETURN			= "CallReturn";
    public static final String	ASB_ACTION_AUTO_ATTND			= "AutoAttendant";
    public static final String	ASB_ACTION_CUSTOM_CNAM			= "CustomCNAM";
    public static final String	ASB_ACTION_LAST_NUM_REDIAL		= "LastNumberRedial"; //TODO AutomaticRedial (aka AutoMaticCallBack )
    public static final String	ASB_ACTION_PERSONAL_ANS			= "PersonalAnswering"; //TODO PersonalAnsweringService
    public static final String	ASB_ACTION_PROFILES				= "PROFILE";
    public static final String	ASB_ROLE_PROFILE				= "Profile";
    public static final String	ASB_ACTION_DISCNT_REFERRAL		= "DisconnectReferral";
    public static final String  ASB_ACTION_DO_NOT_DISTRUB       = "DoNotDisturb";
    public static final String  ASB_ACTION_DRING       			= "DistinctiveRing";
    public static final String  ASB_ACTION_MULP_RG_STD          =  "MultipleRingPattern-STANDARD";
    public static final String	ASB_ACTION_CFD					= "CFD";
    public static final String 	ASB_ACTION_SCHEDULE 			= "SCHEDULE";
    public static final String	ASB_ACTION_HOTLINE				= "HotLine";
    public static final String	ASB_ACTION_CALLLOG				= "BasicCallLog";
    public static final String	ASB_ACTION_ACCTCODES		    = "AccountCodeType";

    public static final String	META_OPERATION_DELETE			= "operation;delete";
    public static final String	META_OPERATION_ADD				= "operation;add";
    public static final String	META_OPERATION_UPDATE			= "operation;update";
    public static final String	META_OPERATION_REPLACE			= "operation;replace";
    public static final String	META_OPERATION_QUERY			= "operation;query";

    public static final String	ASB_HG_SCHEME					= "HuntingScheme";
    public static final String	ASB_HG_GRP_ID					= "HuntGroupId";
    public static final String	ASB_HG_PILOT					= "pilot";

    public static final String	ASB_BG_MDN						= "mdn";
    public static final String	ASB_BG_VPN_ID					= "VPNId";
    public static final String	ASB_ID_VALUE_MEMBER				= "member";

    public static final String	ASB_TRUNK_OVERFLOW				= "TrunkOverflow";
    public static final String	ASB_TRUNK_ALT_ROUTING			= "AlternateRouting";
    public static final String  ASB_OUTBOUND_PROXY              = "OutboundProxy";

    public static final String	ASB_SUBSCRIBER_ID				= "subscriberId";
    public static final String	ASB_SWITCH_NAME					= "SwitchName";
    public static final String	ASB_DIAL_PLAN_ID				= "DialPlanId";
    public static final String	ASB_BLOCKING_CODES				= "BlockingCodes";

    public static final String	ASB_IGNORED						= "ignored";

    public static final String	ASB_ID_VALUE_MAIN				= "main";
    public static final String	ASB_ID_VALUE_TN					= "tn";
    public static final String	ASB_ID_VALUE_SOURCE_TN			= "sourceTn";
    public static final String	ASB_ID_TIMESTAMP                = "timestamp";
    public static final String	ASB_ID_VALUE_STATUS				= "status";
    public static final String	ASB_ID_VALUE_STATUS_ACTIVE		= "active";
    public static final String	ASB_ID_VALUE_STATUS_INACTIVE	= "inactive";
    public static final String	ASB_ID_VALUE_STATUS_FULL		= "Full";
    public static final String	ASB_ID_VALUE_STATUS_NONE		= "None";
    public static final String  ASB_ID_VALUE_PUBLIC             = "PUBLIC";

    public static final String	ASB_ID_VALUE_ADM_STATUS			= "admStatus";
    public static final String	ASB_ID_VALUE_ADM_ENABLED		= "enabled";
    public static final String	ASB_ID_VALUE_ADM_DISABLED		= "disabled";
    public static final String	ASB_ID_VALUE_EXPIRY				= "expiry";

    public static final String	ASB_ID_VALUE_HG_CIRCULAR		= "Circular";
    public static final String	ASB_ID_VALUE_HG_SEQUENTIAL		= "Sequential";
    public static final String	ASB_ID_VALUE_HG_UCD				= "UCD";

    public static final String	ASB_ID_VALUE_ACCNTCD			= "AccountCode";
    public static final String	ASB_ID_VALUE_ACCNTCD_TYP		= "AccountCodeType";

    public static final String	ASB_VALUE_PIN				    = "pin";

    public static final String	ASB_ID_VALUE_LENGTH				= "length";
    public static final String	ASB_ID_VALUE_CODE				= "code";
    public static final String	ACT_CD_VERIFIED					= "verified";
    public static final String	ACT_CD_UNVERIFIED				= "unverified";
    public static final String	ACT_CD_INACTIVE					= "inactive";

    public static final String ASB_VALUE_FROM 					= "from";
    public static final String ASB_VALUE_TO 					= "to";
    public static final int DEFAULT_NUM_RINGS =  5;
    public static final int    DEFAULT_TIMEOUT                  =  30;
    public static final int    DEFAULT_SCF_DN_LIMIT             =  33;
    public static final int    DEFAULT_SIMRING_DN_LIMIT         =  10;
    public static final String SEQR_RING_PATTERN_NAME           = "PATTERN";
    public static final String D_RING_PATTERN_NAME              = "RINGPATTERN";
    public static final String D_RING_PATTERN1_NAME             = "RINGPATTERN1";
    public static final String STANDARD_RING_PATTERN_NAME       = "STANDARD";
    public static final String ASB_VALUE_TARGET                 = "target";
    public static final String ASB_VALUE_NUMRING                = "numRing";
    public static final String ASB_VALUE_NAME                   = "NAME";
    public static final String ASB_VALUE_DESCRIPTION            = "Description";
    public static final String ASB_VALUE_FILE                   = "File";

    public static final String ASB_ID_ADAPTER_ID				= "AdapterId";

    public static final String ASB_ID_DIALOG_URI                = "dialogUri";
    public static final String ASB_ID_VALUE_URI                 = "URI";

    public static final String ASB_ID_START_DT                  = "startDate";
    public static final String ASB_ID_END_DT                    = "endDate";
    public static final String ASB_ID_START_TIME                = "startTime";
    public static final String ASB_ID_END_TIME                  = "endTime";
    public static final String ASB_ID_OFFSET                    = "gmtOffset";
    public static final String ASB_ID_REPEAT                    = "repeat";
    public static final String ASB_ID_SINGLE                    = "single";

    //identifies which adapter
    public static final String	ASB_ADAPTER_ALU					= "ALU";

    // PropertiesType
    public static final String	ASB_PROP_TYPE_SERVICE_ADAPTER	= "ServiceAdapter";
    public static final String	ASB_PROP_TYPE_BCP_MNGDLINE		= "BCP.ManagedLine";
    public static final String	ASB_PROP_TYPE_BCP_HGROUP		= "BCP.HuntGroup";
    public static final String	ASB_PROP_TYPE_BCP_BGROUP		= "BCP.BusinessGroup";
    public static final String	ASB_PROP_TYPE_BCP_TRUNK			= "PRI.Trunk";
    public static final String	ASB_PROP_TYPE_BCP_AA			= "BCP.AutoAttendant";
    public static final String	ASB_PROP_TYPE_BCP_AA_LINE		= "BCP.AutoAttendant.Line";
    public static final String	ASB_PROP_TYPE_BCP_ACNTCODE		= "BCP.AccountCode";
    public static final String	ASB_PROP_TYPE_BCP_VOICEMAIL		= "BCP.Voicemail";
    public static final String	ASB_PROP_TYPE_BCP_RCF			= "BCP.RemoteCallForwarding";
    public static final String	ASB_PROP_TYPE_BCP_MNGDLINE_PROFILE			= "BCP.ManagedLine.Profile";
    public static final String	ASB_PROP_TYPE_BCP_MNGDLINE_SCHEDULE			= "BCP.ManagedLine.Schedule";
    public static final String	ASB_PROP_TYPE_RATECENTER 		= "RateCenter";

    public static final String  ASB_DOCUMENT_TYPE_CALLLOG       = "RetrieveCallLog";
    public static final String	ASB_ID_VALUE_CALLLOG_TYPE		= "CallLogType";

    public static final String  ASB_VALUE_MISSED	            = "Missed";
    public static final String  ASB_VALUE_RECEIVED	            = "Received";
    public static final String  ASB_VALUE_DIALED	            = "Dialed";
    public static final String  ASB_VALUE_ALL    	            = "All";
    public static final String  ASB_VALUE_INCOMING    	        = "incoming";
    public static final String  ASB_VALUE_OUTGOING    	        = "outgoing";

    public static final String  ASB_ID_DIALOG					= "Dialog";
    public static final String  ASB_ID_RECORDING                = "Recording";
    public static final String  ASB_ID_DIALOG_INFO				= "DialogInfo";

    public static final String PAS_CREATE_RECORDING = "PersonalAnswering.Recording.Create";
    public static final String PAS_CREATE_DIALOG = "PersonalAnswering.Dialog.Create";
    public static final String PAS_RETRIEVE_DIALOGS = "PersonalAnswering.Dialog.Retrieve";
    public static final String PAS_EXECUTE_DIALOG = "PersonalAnswering.Dialog.Execute";
    public static final String PAS_UPDATE_DIALOG = "PersonalAnswering.Dialog.Update";
    public static final String PAS_DELETE_DIALOG = "PersonalAnswering.Dialog.Delete";
    public static final String PAS_RETRIEVE_RECORDING = "PersonalAnswering.Recording.Query";
    public static final String PAS_TEST_RECORDING = "PersonalAnswering.Recording.Play";
    public static final String PAS_UPDATE_RECORDING = "PersonalAnswering.Recording.Update";
    public static final String PAS_DELETE_RECORDING = "PersonalAnswering.Recording.Delete";

    public static final Integer ASB_MAX_CALLS_ALL               = 60;
    public static final Integer ASB_MAX_CALLS_FOR_TYPE          = 20;

    public static final String ASB_ID_DAY                      = "day";
    public static final String ASB_SPECIFIC_DAYS_VALUE         = "SPECIFIC_DAYS";
    public static final String ASB_ID_TIMEZONE                 = "Timezone";
    public static final String ASB_ID_DEFAULT                  = "Default";
    public static final String DEFAULT_PROFILE                 = "DefaultProfile";
    public static final String PROFILE_OVERRIDE                = "ProfileOverride";
    public static final String DEFAULT_OFFSET                  = "-00:00";

    public static final String FORWARD_TO_DN                  = "FORWARD_TO_DN";
    public static final String FORWARD_TO_VM                  = "FORWARD_TO_VM";
    public static final String FORWARD_TO_VMS                 = "TO_VMS";

    public static final String ASB_PUID_PREFIX                 = "+1";
    public static final String	ASB_ID_VALUE_TRUE		= "true";
    public static final String	ASB_ID_VALUE_FALSE		= "false";

    public static final String	ASB_BLK_CODES_INTL		= "INTL";
    public static final String	ASB_BLK_CODES_PREM		= "900";
    public static final String	ASB_BLK_CODES_LOCAL		= "0+";
    public static final String	ASB_BLK_CODES_FGBD		= "CASUAL";
    public static final String	ASB_BLK_CODES_DIR_ASSIST	= "411";
    public static final String	ASB_BLK_CODES_IBR		= "IBR";
    public static final String	ASB_BLK_CODES_OBR		= "OBR";

    public static final String	DEFAULT_DOMAIN_NAME		= "ims.rr.com";
    public static final String	DEFAULT_SWITCH_ID		= "1";
    public static final String	MLHG_CONTROLLER_PREFIX		= "mlhg_";

    public enum HuntingScheme 	{ Circular, Sequential, UCD};

    public static final Set<String>	ASB_PIC_CODES = new HashSet<>(Arrays.asList(ASB_PIC.toLowerCase(), ASB_IPIC.toLowerCase(),ASB_LPIC.toLowerCase()));

    public static final Set<String> VALID_BLOCKING_CODES = new HashSet<>(Arrays.asList("900","976","INTL","0+","DA","NDA","CASUAL","411","IBR","OBR"));
    public static final Set<String> HG_CONFLICT_FEATURES = new HashSet<>(Arrays.asList("ACR","PersonalAnswering","SimultaneousRing","SequentialRing","DoNotDisturb"));

    //- Start - Constants for routing logic
    public static final Set<String> VALID_PROPERTIES_TYPES = new HashSet<>(Arrays.asList(ASB_PROP_TYPE_BCP_MNGDLINE,
            ASB_PROP_TYPE_BCP_HGROUP,
            ASB_PROP_TYPE_BCP_BGROUP,
            ASB_PROP_TYPE_BCP_TRUNK,
            ASB_PROP_TYPE_BCP_ACNTCODE,
            ASB_PROP_TYPE_BCP_RCF,
            ASB_PROP_TYPE_BCP_MNGDLINE_PROFILE,
            ASB_PROP_TYPE_BCP_MNGDLINE_SCHEDULE));

    public static final Set<String> SERVC_ADPTR_NOT_REQ4PROP_TYPES = new HashSet<>(Arrays.asList(ASB_PROP_TYPE_BCP_HGROUP,
            ASB_PROP_TYPE_BCP_BGROUP,
            ASB_PROP_TYPE_BCP_MNGDLINE_PROFILE,
            ASB_PROP_TYPE_BCP_MNGDLINE_SCHEDULE,
            ASB_PROP_TYPE_BCP_ACNTCODE));

    public static final Set<String> VALID_SERVICE_OPERATION_TYPES = new HashSet<>(Arrays.asList(ASB_OPERATION_CREATE,
            ASB_OPERATION_UPDATE,
            ASB_OPERATION_DELETE,
            ASB_OPERATION_QUERY));
    public static final Set<String> VALID_SOURCE_SYSTEMS = new HashSet<>(Arrays.asList(ASB_ID_VALUE_BPS,
            ASB_ID_VALUE_CPP));

}
