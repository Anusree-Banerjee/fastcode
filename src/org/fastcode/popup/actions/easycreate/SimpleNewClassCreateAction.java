/**
 * This class has been generated by Fast Code Eclipse Plugin
 * For more information please go to http://fast-code.sourceforge.net/
 * @author : Gautam
 * Created : 01/09/2012
 */

package org.fastcode.popup.actions.easycreate;

import static org.eclipse.jface.window.Window.CANCEL;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.GROOVY_EXTENSION;
import static org.fastcode.common.FastCodeConstants.JAVA_EXTENSION;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.fastcode.common.CreateVariableData;
import org.fastcode.common.ProjectSelectionDialog;
import org.fastcode.dialog.CreateClassDialog;

public class SimpleNewClassCreateAction extends NewMemberCreateActionSupport implements IActionDelegate, IWorkbenchWindowActionDelegate {

	/**
	 *
	 */
	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.easycreate.NewMemberCreateActionSupport#getCreateVariableData(org.eclipse.jdt.core.ICompilationUnit)
	 */
	@Override
	protected CreateVariableData getCreateVariableData(final ICompilationUnit compUnit) throws Exception {
		if (this.createVariableData == null || !this.createVariableData.isCreateClassDetailed()) {
			this.createVariableData = new CreateVariableData();
			this.createVariableData.setCreateClassSimple(true);
			this.createVariableData.setCreateClassDetailed(false);
		}
		if (this.editorPart != null) {

			final IFileEditorInput input = (IFileEditorInput) this.editorPart.getEditorInput();
			final IFile file = input.getFile();
			this.createVariableData.setJavaProject(JavaCore.create(file.getProject()));
			final String fileName = this.editorPart.getEditorInput().getName();
			final String type = fileName.substring(fileName.lastIndexOf(DOT) + 1, fileName.length());
			if (type.equalsIgnoreCase(JAVA_EXTENSION) || type.equalsIgnoreCase(GROOVY_EXTENSION)) {
				this.createVariableData.setCompUnitType(type);

			} else {
				this.createVariableData.setCompUnitType(EMPTY_STR);
			}
			if (getCompilationUnitFromEditor() != null) {
				this.createVariableData.setPackageFragment(getCompilationUnitFromEditor().getPrimary().findPrimaryType()
						.getPackageFragment());
			}
		}
		if (this.editorPart == null) {
			final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			final ISelection selection = window.getSelectionService().getSelection("org.eclipse.jdt.ui.PackageExplorer");
			final IStructuredSelection structuredSelection = (IStructuredSelection) selection;

			final Object firstElement = structuredSelection.getFirstElement();

			if (firstElement instanceof IPackageFragment) {
				IPackageFragment packageFragment;
				packageFragment = (IPackageFragment) firstElement;
				this.createVariableData.setJavaProject(packageFragment.getJavaProject());
				String type = "";
				if (packageFragment.getJavaProject().getProject().getNature("org.eclipse.jdt.groovy.core.groovyNature") == null) {
					type = JAVA_EXTENSION;
				} else {
					type = GROOVY_EXTENSION;
				}
				this.createVariableData.setCompUnitType(type);
				this.createVariableData.setPackageFragment(packageFragment);
			} else if (firstElement instanceof ICompilationUnit) {
				final ICompilationUnit compilationUnit = (ICompilationUnit) firstElement;
				this.createVariableData.setJavaProject(compilationUnit.getJavaProject());
				String type = "";
				if (compilationUnit.getJavaProject().getProject().getNature("org.eclipse.jdt.groovy.core.groovyNature") == null) {
					type = JAVA_EXTENSION;
				} else {
					type = GROOVY_EXTENSION;
				}
				this.createVariableData.setCompUnitType(type);
				this.createVariableData.setPackageFragment(compilationUnit.findPrimaryType().getPackageFragment());
			} else {
				final IProject project = getTypeOfProject(this.createVariableData);
				this.createVariableData.setJavaProject(JavaCore.create(project));
			}
		}

		final CreateClassDialog createClassDialog = new CreateClassDialog(new Shell(), this.createVariableData);
		if (createClassDialog.open() == Window.CANCEL) {
			return null;
		}
		return createClassDialog.getCreateVariableData();
	}

	@Override
	protected boolean isSimpleType() {
		return true;
	}

	@Override
	protected boolean doesRequireMoreInfoFromUser() {
		return false;
	}

	@Override
	protected boolean requireJavaClass() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean doesModify() {
		return false;
	}

	/**
	 * @return
	 */
	protected ICompilationUnit getCompilationUnitFromEditor() {
		final IWorkingCopyManager manager = JavaUI.getWorkingCopyManager();
		final ICompilationUnit compilationUnit = manager.getWorkingCopy(this.editorPart.getEditorInput());
		return compilationUnit != null && compilationUnit.exists() ? compilationUnit : null;
	}

	private IProject getTypeOfProject(final CreateVariableData createVariableData) throws CoreException {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject[] projects = root.getProjects();
		IProject workingJavaProject = null;
		final List<IProject> iProjects = new ArrayList<IProject>();
		String type = EMPTY_STR;
		for (final IProject prj : projects) {
			if (prj == null || !prj.exists() || !prj.isOpen()) {
				continue;
			}
			iProjects.add(prj);
		}
		final ProjectSelectionDialog projectSelectionDialog = new ProjectSelectionDialog(new Shell(), "Projects in Workspace",
				"Select a project you want to work with", iProjects.toArray(), -1);

		if (projectSelectionDialog.open() != CANCEL) {
			workingJavaProject = (IProject) projectSelectionDialog.getFirstResult();
		}
		if (workingJavaProject != null && workingJavaProject.getNature("org.eclipse.jdt.groovy.core.groovyNature") == null) {
			type = JAVA_EXTENSION;
		} else {
			type = GROOVY_EXTENSION;
		}
		createVariableData.setCompUnitType(type);
		return workingJavaProject;

	}
}