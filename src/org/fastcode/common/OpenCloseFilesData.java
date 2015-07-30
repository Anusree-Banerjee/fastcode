package org.fastcode.common;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.fastcode.common.FastCodePackage;
import org.eclipse.ui.part.EditorPart;
import org.fastcode.common.FastCodeFolder;

public class OpenCloseFilesData {

	private IProject							project;
	FastCodeProject selectedProject;
	ICompilationUnit compUnit;
	private String pattern;
	private boolean closeOthers;
	private FastCodePackage fastCodePackage;
	private EditorPart editorPart;
	private FastCodeFolder fastCodeFolder;


	public ICompilationUnit getCompUnit() {
		return this.compUnit;
	}

	public void setCompUnit(final ICompilationUnit compUnit) {
		this.compUnit = compUnit;
	}

	public IProject getProject() {
		return this.project;
	}

	public void setProject(final IProject project) {
		this.project = project;
	}

	public FastCodeProject getSelectedProject() {
		return this.selectedProject;
	}

	public void setSelectedProject(final FastCodeProject selectedProject) {
		this.selectedProject = selectedProject;
	}

	/**
	 *
	 * getter method for pattern
	 * @return
	 *
	 */
	public String getPattern() {
		return this.pattern;
	}

	/**
	 *
	 * setter method for pattern
	 * @param pattern
	 *
	 */
	public void setPattern(final String pattern) {
		this.pattern = pattern;
	}

	/**
	 *
	 * getter method for closeOthers
	 * @return
	 *
	 */
	public boolean isCloseOthers() {
		return this.closeOthers;
	}

	/**
	 *
	 * setter method for closeOthers
	 * @param closeOthers
	 *
	 */
	public void setCloseOthers(final boolean closeOthers) {
		this.closeOthers = closeOthers;
	}

	/**
	 *
	 * getter method for fastCodePackage
	 * @return
	 *
	 */
	public FastCodePackage getFastCodePackage() {
		return this.fastCodePackage;
	}

	/**
	 *
	 * setter method for fastCodePackage
	 * @param fastCodePackage
	 *
	 */
	public void setFastCodePackage(final FastCodePackage fastCodePackage) {
		this.fastCodePackage = fastCodePackage;
	}

	/**
	 *
	 * getter method for editorPart
	 * @return
	 *
	 */
	public EditorPart getEditorPart() {
		return this.editorPart;
	}

	/**
	 *
	 * setter method for editorPart
	 * @param editorPart
	 *
	 */
	public void setEditorPart(final EditorPart editorPart) {
		this.editorPart = editorPart;
	}

	/**
	 *
	 * getter method for fastCodeFolder
	 * @return
	 *
	 */
	public FastCodeFolder getFastCodeFolder() {
		return this.fastCodeFolder;
	}

	/**
	 *
	 * setter method for fastCodeFolder
	 * @param fastCodeFolder
	 *
	 */
	public void setFastCodeFolder(final FastCodeFolder fastCodeFolder) {
		this.fastCodeFolder = fastCodeFolder;
	}


}
