/**
 *
 */
package org.fastcode.preferences;

import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * @author Gautam
 *
 */
public class CreateNewPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 *
	 */
	public CreateNewPreferencePage() {
		super(GRID);
		final IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		this.setPreferenceStore(store);
		this.setDescription("Fast Code Create New Preference Page the plugin");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors
	 * ()
	 */
	@Override
	protected void createFieldEditors() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(final IWorkbench arg0) {
		// TODO Auto-generated method stub

	}

}
