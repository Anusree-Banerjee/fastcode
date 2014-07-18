package org.fastcode.util;

import static org.fastcode.common.FastCodeConstants.BACK_SLASH;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.LEFT_CURL;
import static org.fastcode.common.FastCodeConstants.LEFT_PAREN;
import static org.fastcode.common.FastCodeConstants.RIGHT_CURL;
import static org.fastcode.common.FastCodeConstants.RIGHT_PAREN;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.common.FastCodeConstants.DOLLAR;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_TEMPLATE_PREFIX;
import static org.fastcode.preferences.PreferenceConstants.P_FILE_TEMPLATE_PLACHOLDER_NAME;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.parseAdditonalParam;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;

import org.apache.commons.lang.ArrayUtils;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.GlobalTemplateVariables.Dollar;
import org.eclipse.swt.widgets.Shell;
import org.fastcode.Activator;
import org.fastcode.common.FastCodeAdditionalParams;
import org.fastcode.common.FastCodeConstants.FIRST_TEMPLATE;
import org.fastcode.common.FastCodeConstants.SECOND_TEMPLATE;
import org.fastcode.exception.FastCodeException;
import org.fastcode.preferences.TemplateVisitor;
import static org.fastcode.util.SourceUtil.openMessageDilalog;
import static org.fastcode.util.SourceUtil.showWarning;
import static org.fastcode.util.FastCodeUtil.getEmptyArrayForNull;

public class VelocityUtil {

	private Properties			properties;
	private static VelocityUtil	velocityUtil;
	final TemplateVisitor		visitor	= new TemplateVisitor();
	final String				msg2	= ".\nIt is recommended to correct the errors. Do u want to proceed or stay here and correct the errors?";

	/**
	 * @param templateBody
	 * @param templateDetailsForVelocity
	 * @return
	 */
	public boolean parseTemplateBody(final String templateBody, final TemplateDetailsForVelocity templateDetailsForVelocity) {
		VelocityEngine engine = null;
		boolean closeWindow = true;
		try {
			engine = getVelocityEngine(engine);
			final StringResourceRepository repository = StringResourceLoader.getRepository();

			repository.putStringResource("templateBody", templateBody);

			final Template t = engine.getTemplate("templateBody");

			final SimpleNode sn = (SimpleNode) t.getData();
			final Object o1 = templateDetailsForVelocity;

			sn.jjtAccept(this.visitor, o1);

			if (this.visitor.getInvalidVariable().replaceFirst(COMMA, EMPTY_STR).trim().length() > 0) {

				closeWindow = showWarning("Template " + templateDetailsForVelocity.getTemplateName() + " contains variables "
						+ this.visitor.getInvalidVariable().replaceFirst(COMMA, EMPTY_STR)
						+ " which are neither inbuilt nor locally defined" + this.msg2, "Proceed Anyway", "Cancel");
			}

		} catch (final Exception ex) {
			ex.printStackTrace();
		}

		return closeWindow;
	}

	public List<String> getVariablesList() {
		List<String> validVariables = null;
		validVariables = this.visitor.getVarList();
		return validVariables;
	}

	public List<String> getLocalVarlist() {
		List<String> localvars = null;
		localvars = this.visitor.getLocalVars();
		return localvars;
	}

	public List<String> getSetVarlist() {
		List<String> setvars = null;
		setvars = this.visitor.getSetVars();
		return setvars;
	}

	/**
	 * @param engine
	 * @return
	 * @throws Exception
	 */
	private static VelocityEngine getVelocityEngine(VelocityEngine engine) throws Exception {
		// Initializes the velocity engine with properties. We should specify    // the resource loader as string and the class for    // string.resource.loader in properties
		final Properties p = new Properties();
		p.setProperty("resource.loader", "string");
		p.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
		engine = new VelocityEngine();
		engine.init(p);
		return engine;
	}

	/**
	 *
	 */
	private void readProperties() {
		InputStream input = null;
		final String propertiesFile = "velocity-variables.properties";

		try {
			input = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/" + propertiesFile), false);
			this.properties = new Properties();
			this.properties.load(input);
		} catch (final Exception ex) {
			ex.printStackTrace();
		} finally {
			FastCodeUtil.closeInputStream(input);
		}
	}

	/**
	 *
	 * @param property
	 * @return
	 */
	public String getPropertyValue(final String property, final String defaultValue) {
		if (this.properties == null) {
			readProperties();
		}
		return this.properties.getProperty(property, defaultValue);
	}

	public final Map<String, String> getPluralMap() {
		final String prefix = "templateitem" + UNDERSCORE;
		final Map<String, String> pluralMap = new HashMap<String, String>();
		for (final SECOND_TEMPLATE secondTemplateItem : SECOND_TEMPLATE.values()) {
			pluralMap.put(secondTemplateItem.getValue(), getPropertyValue(prefix + secondTemplateItem.getValue(), EMPTY_STR));
		}

		return pluralMap;
	}

	public final Map<String, ArrayList<String>> getAdditionalParameterAttributeMap(final SortedSet<String> paramTypes) {
		final String prefix = "param" + UNDERSCORE;
		final Map<String, ArrayList<String>> attributesMap = new HashMap<String, ArrayList<String>>();
		for (final String type : paramTypes) {
			final ArrayList<String> attributes = new ArrayList(Arrays.asList(getPropertyValue(prefix + type, EMPTY_STR).split(COMMA)));
			attributesMap.put(type, attributes);
		}

		return attributesMap;
	}

	public static VelocityUtil getInstance() {
		if (velocityUtil == null) {
			velocityUtil = new VelocityUtil();
		}
		velocityUtil.readProperties();
		return velocityUtil;
	}

	public void reset() {
		velocityUtil = null;
	}

	/**
	 * @param templateBody
	 * @param currentFirstTemplateItemValue
	 * @param currentSecondTemplateItemValue
	 * @param additionalParam
	 * @param templateName
	 * @param templatePrefix
	 * @param preferenceStore
	 * @return
	 */
	public boolean validateVariablesAndMethods(final String templateBody, final String currentFirstTemplateItemValue,
			final String currentSecondTemplateItemValue, final String additionalParam, final String templateName,
			final String templatePrefix, final IPreferenceStore preferenceStore) {
		List<FastCodeAdditionalParams> fcAdditnlParamList = new ArrayList<FastCodeAdditionalParams>();
		try {
			if (!isEmpty(additionalParam)) {
				fcAdditnlParamList = parseAdditonalParam(additionalParam);
			}
		} catch (final FastCodeException ex1) {
			MessageDialog.openError(new Shell(), "Error",
					"Error in parsing additional parameter in template " + templateName + SPACE + ex1.getMessage());
			return false;
		}
		String tmpTemplateName = null;
		if (templateName == null) {
			tmpTemplateName = EMPTY_STR;
		} else {
			tmpTemplateName = templateName + SPACE;
		}
		String key = EMPTY_STR;
		if (templatePrefix.equals(P_DATABASE_TEMPLATE_PREFIX)) {
			key = P_DATABASE_TEMPLATE_PREFIX;
		} /*else if (templatePrefix.equals(P_FILE_TEMPLATE_PREFIX)) {//commented dont know is this required or not
			key = P_FILE_TEMPLATE_PREFIX;
			} */else {

			key = currentFirstTemplateItemValue + UNDERSCORE + currentSecondTemplateItemValue;
		}
		final TemplateDetailsForVelocity templateDetailsForVelocity = new TemplateDetailsForVelocity();
		templateDetailsForVelocity.setAdditionalPram(fcAdditnlParamList);
		templateDetailsForVelocity.setBuiltInVariables(getPropertyValue(key.toLowerCase(), EMPTY_STR));
		templateDetailsForVelocity.setKey(key.toLowerCase());
		templateDetailsForVelocity.setTemplateName(tmpTemplateName);
		templateDetailsForVelocity.setTemplatePrefix(templatePrefix);
		templateDetailsForVelocity.setFtPlaceholdeName(preferenceStore.getString(P_FILE_TEMPLATE_PLACHOLDER_NAME));
		templateDetailsForVelocity.setFirstTemplateItem(currentFirstTemplateItemValue);
		templateDetailsForVelocity.setSecondtemplateItem(currentSecondTemplateItemValue);
		final boolean returnValue = parseTemplateBody(templateBody, templateDetailsForVelocity);
		final List<String> validVariables = getVariablesList();
		final List<String> localVariables = getLocalVarlist();
		final List<String> setVariables = getSetVarlist();
		String invalidMethods = EMPTY_STR;
		String invalidMethodsStr = EMPTY_STR;
		boolean hasMethods;
		String[] methods = {};
		final String otherLocalVars = getPropertyValue("other_local_vars", EMPTY_STR);
		String dbcollection = EMPTY_STR;
		if (templatePrefix.equals(P_DATABASE_TEMPLATE_PREFIX)) {
			dbcollection = getPropertyValue("db_collection", EMPTY_STR);
		}
		for (final String velVar : validVariables) {
			hasMethods = false;
			String varName = EMPTY_STR;

			if (velVar.contains(DOT)) {
				int index = 0;
				final String tmpVelVar = velVar.substring(0, velVar.indexOf(DOT));
				if (tmpVelVar.contains("${")) {
					index = tmpVelVar.indexOf("${") + 2;
				} else if (tmpVelVar.contains("{")) {
					index = tmpVelVar.indexOf("{") + 1;
				} else if (tmpVelVar.contains("$")) {
					index = tmpVelVar.indexOf("$") + 1;
				}
				varName = velVar.substring(index, velVar.indexOf(DOT));
				if (velVar.contains(RIGHT_CURL)) {
					methods = velVar.substring(2, velVar.indexOf(RIGHT_CURL)).replaceAll("\\([^\\(]*\\)", EMPTY_STR)
							.split(BACK_SLASH + DOT);
					hasMethods = true;
					if (templatePrefix.equals(P_DATABASE_TEMPLATE_PREFIX)) {
						if (dbcollection.contains(varName) && velVar.contains("get")) {
							methods = (String[]) ArrayUtils.addAll(velVar.substring(2, velVar.indexOf(LEFT_PAREN)).split(BACK_SLASH + DOT),
									velVar.substring(velVar.indexOf(LEFT_PAREN) + 3, velVar.indexOf(RIGHT_PAREN)).split(BACK_SLASH + DOT));
							methods = (String[]) ArrayUtils.addAll(methods,
									velVar.substring(velVar.indexOf(RIGHT_PAREN) + 1, velVar.indexOf(RIGHT_CURL)).split(BACK_SLASH + DOT));
							hasMethods = true;
						}
					}

				}
			}
			if (currentFirstTemplateItemValue.equals(FIRST_TEMPLATE.File.getValue())
					&& currentSecondTemplateItemValue.equals(SECOND_TEMPLATE.data.getValue())) {
				hasMethods = false;
			}
			int startIndex = 0;
			String prefix = EMPTY_STR;
			String builtInMethods = EMPTY_STR;
			if (templatePrefix.equals(P_DATABASE_TEMPLATE_PREFIX)) {
				prefix = "db_";
			}
			if (hasMethods) {
				/*if (currentFirstTemplateItemValue.equals(FIRST_TEMPLATE.File.getValue())
						&& currentSecondTemplateItemValue.equals(SECOND_TEMPLATE.data.getValue())) {
					varName = "file_template_placholder_name";
				}*/
				builtInMethods = getPropertyValue(prefix + varName.toLowerCase(), EMPTY_STR);
				//final String[] additionlParamArr = additionalParamValue.split(SPACE);
				if (isEmpty(builtInMethods)) {
					for (final FastCodeAdditionalParams additionlParam : fcAdditnlParamList) {
						if (additionlParam.getName().equals(varName)) {
							builtInMethods = getPropertyValue(additionlParam.getReturnTypes().getValue(), EMPTY_STR);
							break;

						}
					}
				}
				if (isEmpty(builtInMethods)) {
					if (localVariables.size() > 0) {
						for (int i = 0; i < localVariables.size(); i++) {
							final String tmpString = localVariables.get(i).substring(1);
							if (tmpString.equalsIgnoreCase(varName)) {
								String tempVar = localVariables.get(i + 1).substring(localVariables.get(i + 1).indexOf(LEFT_CURL) + 1,
										localVariables.get(i + 1).indexOf(RIGHT_CURL));
								if (tempVar.indexOf(DOT) > -1) {
									tempVar = tempVar.substring(tempVar.indexOf(DOT) + 1);
								}
								builtInMethods = getPropertyValue(tempVar.toLowerCase(), EMPTY_STR);
								break;
							}
						}
					}
					if (dbcollection.contains(varName) && velVar.contains("get")) {
						startIndex = 2;
						builtInMethods = getPropertyValue(prefix + methods[startIndex], EMPTY_STR);

					}
				}

				String similarBuiltInMeth = getPropertyValue(builtInMethods.toLowerCase(), EMPTY_STR);
				if (!isEmpty(similarBuiltInMeth)) {
					builtInMethods = similarBuiltInMeth;
				}
				for (int i = startIndex + 1; i < methods.length; i++) {
					String tmpMeth = methods[i];
					if (methods[i].contains(LEFT_PAREN)) {
						tmpMeth = methods[i].substring(0, methods[i].indexOf(LEFT_PAREN));
					}
					if (tmpMeth.equals("get")) {
						continue;
					}
					if (templatePrefix.equals(P_DATABASE_TEMPLATE_PREFIX)) {
						if (tmpMeth.contains("get")) {
							tmpMeth = tmpMeth.replace("get", EMPTY_STR);
						}
					}
					if (!builtInMethods.toLowerCase().contains(tmpMeth.toLowerCase())) {
						invalidMethods = invalidMethods + COMMA + tmpMeth;
						invalidMethodsStr = invalidMethodsStr + COMMA + methods[i - 1] + DOT + tmpMeth;
						break;
					}
					if (templatePrefix.equals(P_DATABASE_TEMPLATE_PREFIX)) {
						builtInMethods = getPropertyValue(prefix + tmpMeth.toLowerCase(), EMPTY_STR);
					} else {
						builtInMethods = getPropertyValue(prefix + methods[i], EMPTY_STR);
					}

					similarBuiltInMeth = getPropertyValue(builtInMethods.toLowerCase(), EMPTY_STR);
					if (!isEmpty(similarBuiltInMeth)) {
						builtInMethods = similarBuiltInMeth;
					}

				}

			}
		}
		final GenericTypeCache genericTypeCache = GenericTypeCache.getInstance();

		invalidMethods = invalidMethods.replaceFirst(COMMA, EMPTY_STR);
		final String[] invalidMethArr = invalidMethods.split(COMMA);
		IMethod[] strmeths = null;
		IMethod[] colnmeths = null;
		if (genericTypeCache.getStringMethodsArr() != null && genericTypeCache.getStringMethodsArr().length > 0) {
			strmeths = genericTypeCache.getStringMethodsArr();
			colnmeths = genericTypeCache.getCollectionMethodsArr();
		} else {
			final IType listItype = new SearchUtil().searchForItype("java.util.List", IJavaSearchConstants.TYPE,
					SearchEngine.createWorkspaceScope());
			final IType stringItype = new SearchUtil().searchForItype("java.lang.String", IJavaSearchConstants.TYPE,
					SearchEngine.createWorkspaceScope());
			final IType mapIType = new SearchUtil().searchForItype("java.util.Map", IJavaSearchConstants.TYPE,
					SearchEngine.createWorkspaceScope());

			try {
				strmeths = stringItype != null ? stringItype.getMethods() : null;
				colnmeths = listItype != null ? listItype.getMethods() : null;
				colnmeths = mapIType != null ? (IMethod[]) ArrayUtils.addAll(colnmeths, mapIType.getMethods()) : colnmeths;
				genericTypeCache.setStringMethodsArr(strmeths);
				genericTypeCache.setCollectionMethodsArr(colnmeths);
			} catch (final JavaModelException ex) {
				ex.printStackTrace();
			}
		}
		invalidMethods = EMPTY_STR;
		invalidMethodsStr = invalidMethodsStr.replaceFirst(COMMA, EMPTY_STR);
		final String[] invalidMethStrArr = invalidMethodsStr.split(COMMA);
		invalidMethodsStr = EMPTY_STR;
		for (int i = 0; i < invalidMethArr.length; i++) {
			boolean isValidMeth = false;
			final String tmpMeth = invalidMethArr[i];
			for (int j = 0; j < getEmptyArrayForNull(strmeths).length; j++) {
				if (tmpMeth.equals(strmeths[j].getElementName())) {
					isValidMeth = true;
					break;
				}
			}
			if (!isValidMeth) {
				for (int j = 0; j < getEmptyArrayForNull(colnmeths).length; j++) {
					if (tmpMeth.equals(colnmeths[j].getElementName())) {
						isValidMeth = true;
						break;
					}
				}
			}
			if (!isValidMeth) {
				if (otherLocalVars.contains(invalidMethArr[i])) {
					isValidMeth = true;
				}
			}
			if (!isValidMeth) {
				invalidMethods = invalidMethods + COMMA + invalidMethArr[i];
				invalidMethodsStr = invalidMethodsStr + COMMA + invalidMethStrArr[i];
			}
		}
		invalidMethodsStr = invalidMethodsStr.replaceFirst(COMMA, EMPTY_STR);
		if (invalidMethodsStr.trim().length() > 0) {
			final int closeWindow = openMessageDilalog("Template " + templateDetailsForVelocity.getTemplateName()
					+ " contains invalid attributes or methods-- " + invalidMethodsStr + this.msg2, "Proceed Anyway", "Cancel");

			if (closeWindow != -1) {
				if (closeWindow == 0) {

					return true;

				} else {
					return false;
				}
			}

		}
		boolean areAddnlParamUsed = false;
		String unUsedAdditionalParam = EMPTY_STR;
		final String other_additional_param = getPropertyValue("other_additional_param", EMPTY_STR);
		for (final FastCodeAdditionalParams additionlParam : fcAdditnlParamList) {
			for (final String s : validVariables) {
				if (additionlParam.getName().equalsIgnoreCase(getVariableName(s))) {
					areAddnlParamUsed = true;
					break;
				} else if (additionlParam.getName().equalsIgnoreCase(other_additional_param)) {
					areAddnlParamUsed = true;
					break;
				}

			}
			if (!areAddnlParamUsed) {
				unUsedAdditionalParam = unUsedAdditionalParam + COMMA + additionlParam.getName();
			}
			areAddnlParamUsed = false;
		}
		unUsedAdditionalParam = unUsedAdditionalParam.replaceFirst(COMMA, EMPTY_STR);
		if (unUsedAdditionalParam.trim().length() > 0) {
			final int closeWindow = openMessageDilalog("Template " + templateDetailsForVelocity.getTemplateName()
					+ " contains additional parmeters which are not used - " + unUsedAdditionalParam + this.msg2, "Proceed Anyway",
					"Cancel");

			if (closeWindow != -1) {
				if (closeWindow == 0) {

					return true;

				} else {
					return false;
				}
			}

		}
		boolean areSetVarsUsed = false;
		String unUsedSetVars = EMPTY_STR;
		for (final String tmpSetVar : setVariables) {
			for (final String s : validVariables) {
				if (tmpSetVar.equalsIgnoreCase(getVariableName(s))) {
					areSetVarsUsed = true;
					break;
				}

			}
			if (!areSetVarsUsed) {
				unUsedSetVars = unUsedSetVars + COMMA + tmpSetVar;
			}
		}
		unUsedSetVars = unUsedSetVars.replaceFirst(COMMA, EMPTY_STR);
		if (unUsedSetVars.trim().length() > 0) {
			final int closeWindow = openMessageDilalog("Template " + templateDetailsForVelocity.getTemplateName()
					+ " contains unused set variables " + unUsedSetVars + this.msg2, "Proceed Anyway", "Cancel");

			if (closeWindow != -1) {
				if (closeWindow == 0) {

					return true;

				} else {
					return false;
				}
			}

		}
		/*boolean isFtiUsed = false;
		if (currentFirstTemplateItemValue.equals("class")) {
			isFtiUsed = arePlaceHolderUsed("class", "classes", validVariables);
		} else if (currentFirstTemplateItemValue.equals("field")) {
			isFtiUsed = arePlaceHolderUsed("field", "fields", validVariables);
		} else if (currentFirstTemplateItemValue.equals("package")) {
			isFtiUsed = arePlaceHolderUsed("package", "", validVariables);
		} else if (currentFirstTemplateItemValue.equals("folder")) {
			isFtiUsed = arePlaceHolderUsed("folder", "", validVariables);
		} else if (currentFirstTemplateItemValue.equals("enum")) {
			isFtiUsed = arePlaceHolderUsed("enum_constant", "", validVariables);
		} else if (currentFirstTemplateItemValue.equals("file")) {
			isFtiUsed = arePlaceHolderUsed("file", "files", validVariables);
		}
		if (!isFtiUsed && !currentFirstTemplateItemValue.equals("none")) {
			final int closeWindow = openMessageDilalog("Template " + templateDetailsForVelocity.getTemplateName()
					+ " is not using the first template item placeholder ");

			if (closeWindow != -1) {
				if (closeWindow == 0) {

					return true;

				} else {
					return false;
				}
			}

		}*/
		boolean isStiUsed = false;
		if (currentSecondTemplateItemValue.equals("class")) {
			isStiUsed = arePlaceHolderUsed("class", "classes", validVariables);
		} else if (currentSecondTemplateItemValue.equals("field")) {
			isStiUsed = arePlaceHolderUsed("field", "fields", validVariables);
		} else if (currentSecondTemplateItemValue.equals("method")) {
			isStiUsed = arePlaceHolderUsed("methods", "", validVariables);
		} else if (currentSecondTemplateItemValue.equals("both") || currentSecondTemplateItemValue.equals("custom")) {
			isStiUsed = arePlaceHolderUsed("fields", "methods", validVariables);
		} else if (currentSecondTemplateItemValue.equals("file")) {
			isStiUsed = arePlaceHolderUsed("file", "files", validVariables);
		} else if (currentSecondTemplateItemValue.equals("property")) {
			isStiUsed = arePlaceHolderUsed("property", "", validVariables);
		}
		if (!isStiUsed && !currentSecondTemplateItemValue.equals("none") && !currentSecondTemplateItemValue.equals("data")) {
			final int closeWindow = openMessageDilalog("Template " + templateDetailsForVelocity.getTemplateName()
					+ " is not using the second template item placeholder " + this.msg2, "Proceed Anyway", "Cancel");

			if (closeWindow != -1) {
				if (closeWindow == 0) {

					return true;

				} else {
					return false;
				}
			}

		}
		return returnValue;
	}

	/**
	 * @param varName
	 * @param isForDirective
	 * @param setVarLst
	 * @param isMacrodirective
	 * @param forLoopLocalVar
	 * @param o
	 * @return
	 */
	public boolean validateVariables(final String varName, final boolean isForDirective, final List<String> setVarLst,
			final boolean isMacrodirective, final String forLoopLocalVar, final Object o) {
		final TemplateDetailsForVelocity details = (TemplateDetailsForVelocity) o;
		if (details.getFirstTemplateItem().equals(FIRST_TEMPLATE.File.getValue())
				&& details.getSecondtemplateItem().equals(SECOND_TEMPLATE.data.getValue())) {
			details.setBuiltInVariables(details.getFtPlaceholdeName());
			details.setBuiltInVariables(details.getFtPlaceholdeName());

		}
		final String[] builtInvarArr = details.getBuiltInVariables().split(COMMA);
		//final String[] additionlParamArr = details.getAdditionalPram().split(SPACE);
		final List<FastCodeAdditionalParams> fcAdditnlParamList = details.getAdditionalPram();
		final String otherLocalVars = getPropertyValue("other_local_vars", EMPTY_STR);
		boolean isValidVar = false;

		for (int i = 0; i < builtInvarArr.length; i++) {
			if (varName.equalsIgnoreCase(builtInvarArr[i].trim())) {
				isValidVar = true;
				break;
			}
		}
		if (!isValidVar) {
			for (final FastCodeAdditionalParams additionlParam : fcAdditnlParamList) {
				if (additionlParam.getName().trim().equals(varName)) {
					isValidVar = true;
					break;
				}
			}
		}
		if (!isValidVar) {

			if (isForDirective && varName.equalsIgnoreCase(forLoopLocalVar)) {
				isValidVar = true;

			} else if (isMacrodirective) {
				isValidVar = true;
			}
			for (final String local : setVarLst) {
				if (local.contains(varName)) {
					isValidVar = true;
					break;
				}
			}
		}

		if (!isValidVar) {
			final String[] otherLocalVarsArr = otherLocalVars.split(COMMA);
			for (int i = 0; i < otherLocalVarsArr.length; i++) {
				if (otherLocalVarsArr[i].equalsIgnoreCase(varName)) {
					isValidVar = true;
					break;

				}
			}

		}

		return isValidVar;
	}

	/**
	 * @param msg
	 * @return
	 */
	/*
	public boolean showWarning(final String msg) {
	final int closeWindow = openMessageDilalog(msg);

	if (closeWindow != -1) {
		if (closeWindow == 0) {

			return true;

		} else {
			return false;
		}
	}
	return true;
	}

	*//**
		* @param msg
		* @return
		*/
	/*
	private int openMessageDilalog(final String msg) {

	final MessageDialog dialog = new MessageDialog(new Shell(), "Warning", null, msg
			+ ".\nIt is recommended to correct the errors. Do u want to proceed or stay here and correct the errors",
			MessageDialog.WARNING, new String[] { "Proceed Anyway", "Cancel" }, 0) {

		@Override
		protected void buttonPressed(final int buttonId) {
			setReturnCode(buttonId);
			close();

		}
	};

	dialog.open();

	return dialog.getReturnCode();

	}*/

	/**
	 * @param name
	 * @return
	 */
	public String getVariableName(final String name) {
		String varName = EMPTY_STR;
		if (name.contains(DOT)) {
			if (name.contains(LEFT_CURL)) {
				varName = name.substring(2, name.indexOf(DOT));
			} else {
				varName = name.substring(1, name.indexOf(DOT));
			}
		} else if (name.contains(RIGHT_CURL)) {
			varName = name.substring(2, name.indexOf(RIGHT_CURL));
		} else if (name.contains("$")) {
			varName = name.substring(1);
		} else {
			varName = name;
		}
		return varName;
	}

	/**
	 * @param placeholder1
	 * @param placeHolder2
	 * @param validVariables
	 * @return
	 */
	public boolean arePlaceHolderUsed(final String placeholder1, final String placeHolder2, final List<String> validVariables) {
		boolean isPlaceHolderUsed = false;
		for (final String s : validVariables) {
			if (placeholder1.contains(getVariableName(s))) {
				isPlaceHolderUsed = true;
				break;
			} else if (placeHolder2.contains(getVariableName(s))) {
				isPlaceHolderUsed = true;
				break;
			}

		}
		return isPlaceHolderUsed;

	}

	public List<String> getListofHeaders(final String placeholderName, final String templateBody) throws Exception {
		final List<String> lstColmnHeader = new ArrayList<String>();
		final TemplateDetailsForVelocity detailsForVelocity = new TemplateDetailsForVelocity();
		detailsForVelocity.setDoValidation(false);
		VelocityEngine engine = null;

		engine = getVelocityEngine(engine);
		final StringResourceRepository repository = StringResourceLoader.getRepository();

		repository.putStringResource("templateBody", templateBody);

		final Template t = engine.getTemplate("templateBody");

		final SimpleNode sn = (SimpleNode) t.getData();
		final Object o1 = detailsForVelocity;

		sn.jjtAccept(this.visitor, o1);
		final List<String> lstVaraibles = this.visitor.getVarList();
		final List<String> lstlocalVar = this.visitor.getLocalVars();
		String localVarName = EMPTY_STR;
		if (lstlocalVar.size() > 0) {
			final String tmpVar = lstlocalVar.get(0);
			localVarName = tmpVar.substring(tmpVar.indexOf("$") + 1, tmpVar.length());
		}
		for (final String velVar : lstVaraibles) {
			int index = 0;
			if (velVar.contains(DOT)) {
				final String tmpVelVar = velVar.substring(0, velVar.indexOf(DOT));
				if (tmpVelVar.contains("${")) {
					index = tmpVelVar.indexOf("${") + 2;
				} else if (tmpVelVar.contains("{")) {
					index = tmpVelVar.indexOf("{") + 1;
				} else if (tmpVelVar.contains("$")) {
					index = tmpVelVar.indexOf("$") + 1;
				}
				final String varName = velVar.substring(index, velVar.indexOf(DOT));
				if (varName.equals(placeholderName) || varName.equals(localVarName)) {
					final String method = velVar.substring(velVar.indexOf(DOT) + 1, velVar.indexOf(RIGHT_CURL)).replaceAll("\\([^\\(]*\\)",
							EMPTY_STR);
					lstColmnHeader.add(method);
				}
			}
		}
		return lstColmnHeader;

	}

	public List<String> getListofSetVariables(final String templateBody) throws Exception {
		final TemplateDetailsForVelocity detailsForVelocity = new TemplateDetailsForVelocity();
		detailsForVelocity.setDoValidation(false);
		VelocityEngine engine = null;

		engine = getVelocityEngine(engine);
		final StringResourceRepository repository = StringResourceLoader.getRepository();

		repository.putStringResource("templateBody", templateBody);

		final Template t = engine.getTemplate("templateBody");

		final SimpleNode sn = (SimpleNode) t.getData();
		final Object o1 = detailsForVelocity;

		sn.jjtAccept(this.visitor, o1);
		return this.visitor.getSetVars();
	}
}
