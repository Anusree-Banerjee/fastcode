/*
 * Fast Code Plugin for Eclipse
 *
 * Copyright (C) 2008  Gautam Dev
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA.
 *
 * Plugin Home Page: http://fast-code.sourceforge.net/
 */

package org.fastcode.preferences;

import static org.fastcode.preferences.PreferenceConstants.CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID;
import static org.fastcode.preferences.PreferenceConstants.P_NEXT_DESCRIPTOR;
import static org.fastcode.preferences.PreferenceUtil.getPreferenceLabel;

import org.eclipse.jface.preference.BooleanFieldEditor;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class CreateSimilarDAOServicePreferencePage extends CreateSimilarClassPreferencePage {

	public CreateSimilarDAOServicePreferencePage() {
		super();
		this.preferenceId = CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID;
		this.setDescription("Create similar preference page For Dao to Service");
	}

	@Override
	public void createFieldEditors() {
		super.createFieldEditors();
		final BooleanFieldEditor afterFinish = new BooleanFieldEditor(getPreferenceLabel(P_NEXT_DESCRIPTOR, this.preferenceId),
				"After completion, apply the service to GUI action :", this.getFieldEditorParent());
		this.addField(afterFinish);
		// implSubPkg.setEnabled(false, getFieldEditorParent());
		// assignReturnCheckBox.setEnabled(false, getFieldEditorParent());
	}
}