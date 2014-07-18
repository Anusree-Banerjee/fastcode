/**
 * This class has been generated by Fast Code Eclipse Plugin
 * For more information please go to http://fast-code.sourceforge.net/
 * @author : Gautam
 * Created : 09/24/2011
 */

package org.fastcode.popup.actions.snippet;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.fastcode.common.FastCodeProperty;

/**
 *
 * @author Gautam
 *
 */
public class PropertiesFileLoadingStrategy implements FileLoadingStrategy {

	/**
	 * @param inputStream
	 * @throws Exception
	 *
	 */
	public Object[] loadFileElementsFromInputStream(final InputStream inputStream, final Object configuration) throws Exception {
		final List<FastCodeProperty> fastCodeProperties = new ArrayList<FastCodeProperty>();

		final Properties properties = new Properties();
		properties.load(inputStream);
		for (final Object key : properties.keySet()) {
			if (key instanceof String) {
				fastCodeProperties.add(new FastCodeProperty((String) key, properties.getProperty((String) key)));
			}
		}

		if (fastCodeProperties.isEmpty()) {
			return null;
		}
		return fastCodeProperties.toArray(new FastCodeProperty[fastCodeProperties.size()]);
	}

}
