package com.charter.provisioning.voice.commercial.alu.model.response;

import lombok.Data;

@Data
public class CallForwardingBusy {

     private String forwardToDN;

    private boolean forwardToDNAllowed;

    private String forwardToType;

    private String editPermission;

    private boolean forwardVoiceCalls;

    private boolean forwardDataCalls;

    private boolean playAnnouncement;

    private boolean receiveNotify;

    private boolean pinRequired;

    private String send181Mode;

    private boolean restrictIdForward;

    private boolean restrictIdBackward;

    private String dataForwardToType;

    private String dataForwardToDN;

    private boolean activated;

    private String publicUID;

    private boolean assigned;

    private boolean perPuid;

}
