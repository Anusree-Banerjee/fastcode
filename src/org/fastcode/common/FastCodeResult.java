/**
 * This class has been generated by Fast Code Eclipse Plugin
 * For more information please go to http://fast-code.sourceforge.net/
 * @author : Gautam
 * Created : 04/02/2012
 */

package org.fastcode.common;

public class FastCodeResult {

	private final String		name;
	private final FastCodeType	type;

	/**
	 * @param name
	 * @param type
	 */
	public FastCodeResult(final String name, final FastCodeType type) {
		super();
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public FastCodeType getType() {
		return this.type;
	}

}
