package com.charter.provisioning.voice.commercial.alu.config;

/**
 * All OMC-P commands
 */
public interface OMCPCommands
{

	// This command is used to log onto the OMC-P Web API
	String ACT_USER = "act-user";

	// This command is used to log off the OMC-P Web API
	String CANC_USER = "canc-user";

	// This command is used to add an new subscriber to CTS
	String ENT_NGFS_SUBSCRIBER_V2 = "ent-ngfs-subscriber-v2";

	// This command is used to modify a subscriber to CTS
	String ED_NGFS_SUBSCRIBER_V2 = "ed-ngfs-subscriber-v2";

	// This command is used to delete an subscriber to CTS
	String DLT_NGFS_SUBSCRIBER_V2 = "dlt-ngfs-subscriber-v2";

	// This command is used to retrieve n subscriber on CTS
	String RTRV_NGFS_SUBSCRIBER_V2 = "rtrv-ngfs-subscriber-v2";

	// This command is used to retrieve remoteUser PIN
	String RTRV_NGFS_PSIUSER_V2 = "rtrv-ngfs-psiuser-v2";
	
	// This command is used to set remoteUser PIN
	String ENT_NGFS_PSIUSER_V2 = "ent-ngfs-psiuser-v2";
	
	// This command is used to update remoteUser PIN
	String ED_NGFS_PSIUSER_V2 = "ed-ngfs-psiuser-v2";
	
	// This command is used to delete remoteUser PIN
	String DLT_NGFS_PSIUSER_V2 = "dlt-ngfs-psiuser-v2";
	
	// The RTRV-QUEUE-INFO command returns the congestion state of the OMC-P
	// Servers southbound queues.
	String RTRV_QUEUE_INFO = "rtrv-queue-info";

	// This command is used to add a new subscriber on PCM
	String ENT_PCM_ENTUSER = "ent-pcm-entuser";

	// This command is used to modify a subscriber on PCM
	String ED_PCM_ENTUSER = "ed-pcm-entuser";

	// This command is used to retrieve a subscriber on PCM
	String RTRV_PCM_ENTUSER = "rtrv-pcm-entuser";

	// This command is used to delete a subscriber on PCM
	String DLT_PCM_ENTUSER = "dlt-pcm-entuser";

	// This command is used to create a enterprise partition on PCM
	String ENT_PCM_ENT_PARTITION = "entpcm-entpartition";

	// This command is used to add an new BG VPN Group to CTS
	String ENT_NGFS_VPNGROUP_V2 = "ent-ngfs-vpngroup-v2";

	// This command is used to delete BG VPN Group from CTS
	String DLT_NGFS_VPNGROUP_V2 = "dlt-ngfs-vpngroup-v2";

	// This command is used to query BG VPN Group from CTS
	String RTRV_NGFS_VPNGROUP_V2 = "rtrv-ngfs-vpngroup-v2";

	// This command is used to provision the Authorization Code table on the
	// 5420 CTS
	String ENT_NGFS_AUTH_CODES_V2 = "ent-ngfs-authorizationcodes-v2";

	// This command is used to modify the Authorization Code table on the 5420
	// CTS
	String ED_NGFS_AUTH_CODES_V2 = "ed-ngfs-authorizationcodes-v2";

	// This command is used to retrieve the Authorization Code table on the 5420
	// CTS
	String RTRV_NGFS_AUTH_CODES_V2 = "rtrv-ngfs-authorizationcodes-v2";

	// This command is used to delete the Authorization Code table on the 5420
	// CTS
	String DLT_NGFS_AUTH_CODES_V2 = "dlt-ngfs-authorizationcodes-v2";

	// This command is used to Query Subscriber by TN
	String RTRV_NGFS_SUBPARTY_V2 = "rtrv-ngfs-subparty-v2";

	String PUBLICUIDx = "PublicUIDx";

	// This command is used to keep the session alive (Ping command)
	String RTRV_NGFS_CPEPROFILETABLE_V2 = "rtrv-ngfs-cpeprofiletable-v2";

}
