package net.ee.pfanalyzer.model;

public interface IDisplayConstants {

	public final static int DISPLAY_NAME = 2;
	public final static int DISPLAY_ADDITIONAL_INFO = 4;
	public final static int DISPLAY_ELEMENT_COUNT = 8;
	public final static int DISPLAY_DEFAULT = DISPLAY_NAME | DISPLAY_ADDITIONAL_INFO | DISPLAY_ELEMENT_COUNT;
	
	public final static int PARAMETER_DISPLAY_VALUE = 2;
	public final static int PARAMETER_DISPLAY_UNIT = 4;
	public final static int PARAMETER_DISPLAY_DEFAULT = PARAMETER_DISPLAY_VALUE | PARAMETER_DISPLAY_UNIT;
	
}
