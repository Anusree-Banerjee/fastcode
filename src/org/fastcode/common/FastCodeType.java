/**
 * This class has been generated by Fast Code Eclipse Plugin
 * For more information please go to http://fast-code.sourceforge.net/
 * @author : Gautam
 * Created : 07/15/2011
 */

package org.fastcode.common;

import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.UNDER_SCORE;
import static org.fastcode.util.SourceUtil.getFQNameFromFieldTypeName;
import static org.fastcode.util.StringUtil.changeFirstLetterToLowerCase;
import static org.fastcode.util.StringUtil.changeToCamelCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

public class FastCodeType extends FastCodeEntity {

	private String							fullyQualifiedName;
	// private String packageName;
	private String							defaultInstance;
	public static final Map<String, String>	defaultValues	= new HashMap<String, String>();
	private String							value;
	private boolean							defaultConstructor;
	private FastCodeType					superType;
	private final List<FastCodeType>		interfaces		= new ArrayList<FastCodeType>();
	// private String javaProject;
	final private List<FastCodeType>		parameters		= new ArrayList<FastCodeType>();
	private FastCodePackage					packge;
	private IType							iType;
	private boolean							isClass;
	private boolean							isInterface;
	private boolean							isEnum;
	private boolean							isAbstract;
	private final List<FastCodeType>		implementations	= new ArrayList<FastCodeType>();
	private List<FastCodeField>				fields			= new ArrayList<FastCodeField>();
	private List<FastCodeMethod>			methods			= new ArrayList<FastCodeMethod>();

	static {
		defaultValues.put("Boolean", "false");
		defaultValues.put("boolean", "false");
		defaultValues.put("Integer", "0");
		defaultValues.put("int", "0");
		defaultValues.put("float", "0");
		defaultValues.put("Float", "0");
		defaultValues.put("Double", "0");
		defaultValues.put("double", "0");
		defaultValues.put("byte", "0");
		defaultValues.put("char", "''");
		defaultValues.put("float", "0.0f");
		defaultValues.put("long", "0");
		defaultValues.put("short", "0");
	}

	/**
	 * @param type
	 * @throws Exception
	 */
	public FastCodeType(final IType type) {
		super();
		if (type == null || !type.exists()) {
			this.isEmpty = true;
			return;
		}
		final int off = type.getFullyQualifiedName().lastIndexOf(DOT);
		if (off > 0) {
			this.iType = type;
			this.name = type.getFullyQualifiedName().substring(off + 1);
			this.fullyQualifiedName = type.getFullyQualifiedName();
			// this.packageName = type.getFullyQualifiedName().substring(0,
			// off);
			this.defaultInstance = changeFirstLetterToLowerCase(this.name);
			// this.javaProject = type.getJavaProject().getElementName();
			this.packge = new FastCodePackage(type.getFullyQualifiedName().substring(0, off), type.getJavaProject().getElementName());

			try {
				this.isClass = type.isClass();
				this.isInterface = type.isInterface();
				this.isEnum = type.isEnum();
			} catch (final JavaModelException ex1) {
				ex1.printStackTrace();
			}

			final IJavaProject workingJavaProject = type.getJavaProject();
			if (this.isClass) {
				try {
					if (type.getSuperclassName() != null && type.getCompilationUnit() != null) {
						final IType superClassType = workingJavaProject.findType(getFQNameFromFieldTypeName(type.getSuperclassName(),
								type.getCompilationUnit()));
						if (superClassType != null && superClassType.exists()) {
							this.superType = new FastCodeType(superClassType);
						}
					}

					if (type.getSuperInterfaceNames() != null && type.getSuperInterfaceNames().length > 0) {
						for (final String intrface : type.getSuperInterfaceNames()) {
							if (intrface != null && type.getCompilationUnit() != null) {
								final IType interfaceType = workingJavaProject.findType(getFQNameFromFieldTypeName(intrface,
										type.getCompilationUnit()));
								if (interfaceType != null && interfaceType.exists()) {
									this.interfaces.add(new FastCodeType(interfaceType, this.isClass));
								}
							}
						}
					}
					//this.interfaces = this.interfaces.toArray(new FastCodeType[0]);
					final IMethod[] methods = type.getMethods();
					int count = 0;
					boolean isDefault = false;
					if (methods != null && methods.length > 0) {
						for (final IMethod method : methods) {
							if (method != null) {
								this.methods.add(new FastCodeMethod(method));
								if (method.isConstructor()) {
									count++;
									if (method.getParameterNames() == null || method.getParameterNames().length == 0) {
										isDefault = true;
									}
								}
							}
						}
					}
					if (count == 0) {
						this.defaultConstructor = true;
					} else if (count >= 1) {
						this.defaultConstructor = isDefault ? true : false;
					}
					for (IField field : type.getFields()) {
						if (field != null) {
							this.fields.add(new FastCodeField(field));
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			} else if (this.isInterface) {
				try {
					final ITypeHierarchy typeHierarchy = type.newTypeHierarchy(workingJavaProject, new NullProgressMonitor());
					for (final IType type2 : typeHierarchy.getAllClasses()) {
						if (type2 != null && type2.exists() && !type2.getElementName().equals("Object")) {
							this.implementations.add(new FastCodeType(type2, this.isInterface));
						}
					}
					for (IField field : type.getFields()) {
						if (field != null) {
							this.fields.add(new FastCodeField(field));
						}
					}
					for (IMethod method : type.getMethods()) {
						if (method != null) {
							this.methods.add(new FastCodeMethod(method));
						}
					}
				} catch (final JavaModelException ex) {
					ex.printStackTrace();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (this.isEnum) {
				this.name = type.getElementName();
				this.defaultInstance = changeToCamelCase(this.name, UNDER_SCORE);
			}
			this.value = defaultValues.containsKey(this.name) ? defaultValues.get(this.name) : "null";
		}
	}

	/**
	 * @param name
	 * @param fullyQualifiedName
	 * @param packageName
	 */
	public FastCodeType(final String fullyQualifiedName) {
		super();
		final int off = fullyQualifiedName.lastIndexOf(DOT);
		if (off > 0) {
			this.name = fullyQualifiedName.substring(off + 1);
			this.fullyQualifiedName = fullyQualifiedName;
			// this.packageName = fullyQualifiedName.substring(0, off);
			this.defaultInstance = changeFirstLetterToLowerCase(this.name);
			this.packge = new FastCodePackage(fullyQualifiedName.substring(0, off), null);
			this.value = defaultValues.containsKey(this.name) ? defaultValues.get(this.name) : "null";
		} else {
			this.name = fullyQualifiedName;
			this.fullyQualifiedName = fullyQualifiedName;
			this.defaultInstance = changeFirstLetterToLowerCase(this.name);
			this.value = defaultValues.containsKey(this.name) ? defaultValues.get(this.name) : "null";
		}
	}

	/**
	 * @param type
	 * @param isTrue
	 */
	public FastCodeType(final IType type, final boolean isClasOrIntrface) {
		if (isClasOrIntrface) {
			final int off = type.getFullyQualifiedName().lastIndexOf(DOT);
			if (off > 0) {
				this.iType = type;
				this.name = type.getFullyQualifiedName().substring(off + 1);
				this.fullyQualifiedName = type.getFullyQualifiedName();
				this.defaultInstance = changeFirstLetterToLowerCase(this.name);
				this.packge = new FastCodePackage(type.getFullyQualifiedName().substring(0, off), type.getJavaProject().getElementName());
			}
		}
	}

	/**
	 *
	 * getter method for fullyQualifiedName
	 *
	 * @return
	 *
	 */
	public String getFullyQualifiedName() {
		return this.fullyQualifiedName;
	}

	/**
	 *
	 * setter method for fullyQualifiedName
	 *
	 * @param fullyQualifiedName
	 *
	 */
	public void setFullyQualifiedName(final String fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
	}

	/*	*//**
			*
			* getter method for packageName
			*
			* @return
			*
			*/
	/*
	 * public String getPackageName() { return this.packageName; }
	 *//**
		*
		* setter method for packageName
		*
		* @param packageName
		*
		*/
	/*
	 * public void setPackageName(final String packageName) { this.packageName =
	 * packageName; }
	 */

	public String getDefaultInstance() {
		return this.defaultInstance;
	}

	public void setDefaultInstance(final String defaultInstance) {
		this.defaultInstance = defaultInstance;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public void setDefaultConstructor(final boolean defaultConstructor) {
		this.defaultConstructor = defaultConstructor;
	}

	public boolean isDefaultConstructor() {
		return this.defaultConstructor;
	}

	public FastCodeType getSuperType() {
		return this.superType;
	}

	public void setSuperType(final FastCodeType superType) {
		this.superType = superType;
	}

	/*
	 * public String getJavaProject() { return this.javaProject; }
	 *
	 * public void setJavaProject(final String javaProject) { this.javaProject =
	 * javaProject; }
	 */

	/**
	 * @return the params
	 */
	public List<FastCodeType> getParameters() {
		return this.parameters;
	}

	/**
	 *
	 * @param param
	 */
	public void addParameters(final FastCodeType fastCodeType) {
		this.parameters.add(fastCodeType);
	}

	public FastCodePackage getPackage() {
		return this.packge;
	}

	public void setPackage(final FastCodePackage fastCodePackage) {
		this.packge = fastCodePackage;
	}

	public IType getiType() {
		return this.iType;
	}

	public void setiType(final IType iType) {
		this.iType = iType;
	}

	public List<FastCodeType> getInterfaces() {
		return this.interfaces;
	}

	public boolean isClass() {
		return this.isClass;
	}

	public void setClass(final boolean isClass) {
		this.isClass = isClass;
	}

	public boolean isInterface() {
		return this.isInterface;
	}

	public void setInterface(final boolean isInterface) {
		this.isInterface = isInterface;
	}

	public boolean isEnum() {
		return this.isEnum;
	}

	public void setEnum(final boolean isEnum) {
		this.isEnum = isEnum;
	}

	public List<FastCodeType> getImplementations() {
		return this.implementations;
	}

	public List<FastCodeField> getFields() {
		return fields;
	}

	public void setFields(List<FastCodeField> fields) {
		this.fields = fields;
	}

	public List<FastCodeMethod> getMethods() {
		return methods;
	}

	public void setMethods(List<FastCodeMethod> methods) {
		this.methods = methods;
	}

}
