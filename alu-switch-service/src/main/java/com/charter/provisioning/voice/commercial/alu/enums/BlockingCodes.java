package com.charter.provisioning.voice.commercial.alu.enums;
public enum BlockingCodes
{
	CODE_ACC("ACC"),
	CODE_CASUAL("CASUAL"),
	CODE_DA("DA"),
	CODE_IBR("IBR"),
	CODE_INTL("INTL"),
	CODE_NDA("NDA"),
	CODE_OBR("OBR"),
	CODE_411("411"),
	CODE_900("900"),
	CODE_976("976"),
	CODE_ZERO_PLUS("0+");

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	private String code;

	BlockingCodes(String code)
	{
		this.code = code;
	}
}