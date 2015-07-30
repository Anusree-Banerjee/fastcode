package org.fastcode.popup.actions.snippet;

import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.LEFT_BRACKET;
import static org.fastcode.common.FastCodeConstants.LEFT_CURL;
import static org.fastcode.common.FastCodeConstants.RIGHT_BRACKET;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.fastcode.common.FastCodeField;
import org.fastcode.setting.TemplateSettings;
import org.fastcode.util.SourceUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CreateNewJsonFieldSelectionAction extends AbstractCreateNewSnippetAction {
	private final List<FastCodeField>	jsonFieldList	= new ArrayList<FastCodeField>();

	/**
	 * @param templateSettings
	 * @param placeHolders
	 * @param resourceFile
	 * @throws Exception
	 */
	public void getJsonFileElements(final TemplateSettings templateSettings, final Map<String, Object> placeHolders,
			final IFile resourceFile) throws Exception {
		final JSONParser jsonParser = new JSONParser();

		try {
			final String jsonFileContent = SourceUtil.getFileContents(resourceFile);
			final Object object = jsonParser.parse(jsonFileContent);
			parseJson(object, null);
		} catch (final ParseException ex) {
			ex.printStackTrace();
			MessageDialog.openError(new Shell(), "Parsing Error", "There was some error while parsing the file " + resourceFile.getName()
					+ COLON + ex);
			placeHolders.put("_exit", true);
			return;
			//throw new Exception(ex);
		}
		//final org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) object;

		final CheckedTreeSelectionDialog checkedTreeSelectionDialog = new CheckedTreeSelectionDialog(new Shell(), new JsonLevelProvider(),
				new JsonContentProvider());

		checkedTreeSelectionDialog.setTitle("Field Selection");
		checkedTreeSelectionDialog.setMessage("Select Json fields");

		checkedTreeSelectionDialog.setInput(filterJsonList(this.jsonFieldList));
		if (checkedTreeSelectionDialog.open() == Window.CANCEL) {
			this.jsonFieldList.clear();
			placeHolders.put("_exit", true);
			return;
		}
		placeHolders.put("fields", checkedTreeSelectionDialog.getResult());
		this.jsonFieldList.clear();
	}

	/**
	 * @param jsonFieldList
	 * @return
	 */
	private List<FastCodeField> filterJsonList(final List<FastCodeField> jsonFieldList) {
		final List<FastCodeField> initialList = new ArrayList<FastCodeField>();
		for (final FastCodeField fastCodeField : jsonFieldList) {
			if (fastCodeField.getParentField() == null) {
				initialList.add(fastCodeField);
			}
		}
		return initialList;
	}

	/**
	 * @param jsonObject
	 * @param parentField
	 * @throws ParseException
	 */

	private void parseJson(final Object object, final FastCodeField parentField) throws ParseException {
		final JSONObject jsonObject = (JSONObject) object;
		FastCodeField jsonField = null;
		//System.out.println(jsonObject);

		for (final Object objct : jsonObject.keySet()) {
			try {
				if (parentField != null) {

					jsonField = new FastCodeField(objct.toString(), jsonObject.get(objct).toString(), parentField);
				} else {
					jsonField = new FastCodeField(objct.toString(), jsonObject.get(objct).toString());
				}
				if (this.jsonFieldList == null) {
					this.jsonFieldList.add(jsonField);
				} else if (this.jsonFieldList != null && !this.jsonFieldList.contains(jsonField)) {
					this.jsonFieldList.add(jsonField);
				}
				if (jsonObject.get(objct) instanceof JSONArray) {
					getArray(jsonObject.get(objct), jsonField);

				} else {
					if (jsonObject.get(objct) instanceof JSONObject) {
						//parentJson = new FastCodeField(objct.toString(), jsonObject.get(objct).toString());
						parseJson(jsonObject.get(objct), jsonField);
					}
				}

				//this.jsonFieldList.add(jsonField);
			} catch (final Exception ex) {
				ex.printStackTrace();
			}

			/*final Set<Object> set = jsonObject.keySet();

			final Iterator iterator = set.iterator();

			while (iterator.hasNext()) {
				final Object obj = iterator.next();
				if (jsonObject.get(obj) instanceof JSONArray) {
					System.out.println(obj.toString());

					getArray(jsonObject.get(obj));
				} else {
					if (jsonObject.get(obj) instanceof JSONObject) {
						parseJson((JSONObject) jsonObject.get(obj));
					} else {
						System.out.println(obj.toString() + "\t" + jsonObject.get(obj));
					}
				}
			}*/
		}
	}

	/**
	 * @param object
	 * @param jsonField
	 * @throws ParseException
	 */
	private void getArray(final Object object, FastCodeField jsonField) {
		try {
			final JSONArray jsonArr = (JSONArray) object;

			for (int k = 0; k < jsonArr.size(); k++) {
				final String fieldName = jsonField.getName().contains("]") ? jsonField.getParentField().getName() + LEFT_BRACKET
						+ String.valueOf(k) + RIGHT_BRACKET : jsonField.getName() + LEFT_BRACKET + String.valueOf(k) + RIGHT_BRACKET;
				jsonField = new FastCodeField(fieldName, jsonArr.get(k).toString(),
						jsonField.getName().contains("]") ? jsonField.getParentField() : jsonField);
				if (this.jsonFieldList == null) {
					this.jsonFieldList.add(jsonField);
				} else if (this.jsonFieldList != null && !this.jsonFieldList.contains(jsonField)) {
					this.jsonFieldList.add(jsonField);
				}
				if (jsonArr.get(k) instanceof JSONObject) {

					parseJson(jsonArr.get(k), jsonField);//getChildrenForJson(jsonArr.get(k));
				} else {
					final FastCodeField field = new FastCodeField(jsonArr.toString(), jsonArr.get(k).toString(), jsonField);
					if (this.jsonFieldList == null) {
						this.jsonFieldList.add(field);
					} else if (this.jsonFieldList != null && !this.jsonFieldList.contains(field)) {
						this.jsonFieldList.add(field);
					}
				}
				//System.out.println(jsonArr.get(k));
			}
		} catch (final Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

	}

	private class JsonLevelProvider implements ILabelProvider {

		@Override
		public void addListener(final ILabelProviderListener arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isLabelProperty(final Object arg0, final String arg1) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeListener(final ILabelProviderListener arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public Image getImage(final Object arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getText(final Object obj) {
			if (obj instanceof FastCodeField) {
				//if (((FastCodeField) obj).getParentField() != null) {
				return ((FastCodeField) obj).getName();
				//}
			}
			return null;
		}

	}

	private class JsonContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public void inputChanged(final Viewer arg0, final Object arg1, final Object arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public Object[] getChildren(final Object input) {
			final List<FastCodeField> subJsonField = new ArrayList<FastCodeField>();
			if (input instanceof ArrayList<?>) {
				return ((ArrayList<FastCodeField>) input).toArray(new FastCodeField[0]);

			}
			if (input instanceof FastCodeField) {
				for (final FastCodeField fastCodeField : CreateNewJsonFieldSelectionAction.this.jsonFieldList) {
					if (fastCodeField.getParentField() != null && fastCodeField.getParentField().equals(input)) {
						subJsonField.add(fastCodeField);
					}
				}

				return subJsonField.toArray(new FastCodeField[0]);

			}
			return null;

		}

		@Override
		public Object[] getElements(final Object jsonObject) {
			return getChildren(jsonObject);
		}

		@Override
		public Object getParent(final Object obj) {
			return obj instanceof FastCodeField ? ((FastCodeField) obj).getParentField() : null;
		}

		@Override
		public boolean hasChildren(final Object obj) {
			if (obj instanceof FastCodeField) {
				if (((FastCodeField) obj).getValue().contains(LEFT_CURL) || ((FastCodeField) obj).getValue().contains("[")) {
					return true;
				}

			}

			return false;

		}
	}
}
