package com.charter.provisioning.voice.commercial.alu.model.response;

import lombok.Data;

@Data
public class CallBlocking {

    private boolean all;

    private boolean international;

    private boolean userCtrlAll;

    private boolean userCtrlInternational;

    private boolean receiveNotify;

    private boolean activated;

    private String publicUID;

    private boolean assigned;

    private boolean perPuid;

    private boolean blockRoamNtwk;

    private boolean userCtrlRoamNtwk;

    private boolean roamOutsideCountry;

    private boolean userCtrlRoamOutCtry;

}
