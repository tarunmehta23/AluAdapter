package com.charter.provisioning.voice.commercial.alu.model.response;

import lombok.Data;

@Data
public class SeqRinging {

    private String ringingList;

    private int defaultAnswerTimeout;

    private boolean playAnnouncement;

    private String send181Mode;

    private boolean restrictIdForward;

    private boolean restrictIdBackward;

    private boolean activated;

    private String publicUID;

    private boolean assigned;

    private boolean PerPuid;

}
