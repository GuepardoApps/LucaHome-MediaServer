package guepardoapps.mediamirror.common;

import java.io.Serializable;

import guepardoapps.mediamirror.common.constants.Enables;
import guepardoapps.toolset.common.Logger;

public class SmartMirrorLogger extends Logger implements Serializable {

	private static final long serialVersionUID = 2275691547746095158L;

	public SmartMirrorLogger(String tag) {
		super(tag, Enables.DEBUGGING);
	}
}
