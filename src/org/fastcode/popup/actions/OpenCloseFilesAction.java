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

package org.fastcode.popup.actions;

import static org.eclipse.jdt.ui.JavaUI.openInEditor;
import static org.eclipse.jdt.ui.JavaUI.revealInEditor;
import static org.fastcode.common.FastCodeConstants.ASTERISK;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.fastcode.common.OpenCloseFilesData;
import org.fastcode.dialog.OpenCloseFilesDialog;
import org.fastcode.util.MessageUtil;

/**
 * @author Gautam
 *
 */
public class OpenCloseFilesAction implements IActionDelegate, IWorkbenchWindowActionDelegate {

	protected IWorkbenchWindow	window;
	protected IWorkbenchPage	page;
	protected IEditorPart		editorPart;
	IWorkingCopyManager			manager;
	OpenCloseFilesData			openCloseFilesData;

	@Override
	public void run(final IAction arg0) {

		this.manager = JavaUI.getWorkingCopyManager();
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		if (this.manager == null) {
			return;
		}
		if (this.window != null) {
			this.editorPart = this.window.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		} else {
			this.editorPart = null;
		}

		final Shell parentShell = MessageUtil.getParentShell();
		//final Shell shell = parentShell == null ? new Shell() : parentShell;
		this.openCloseFilesData = getOpenCloseFiles();

		if (this.openCloseFilesData == null) {
			return;
		}

		if (this.openCloseFilesData.isCloseOthers()) {
			for (final IWorkbenchPage workbenchPage : this.window.getWorkbench().getActiveWorkbenchWindow().getPages()) {
				workbenchPage.closeAllEditors(false);

			}
		}
		try {
			//System.out.println(this.openCloseFilesData.getFastCodePackage().getPackageFragment());
			//System.out.println(this.openRequiredClassesData.getFastCodePackage().getPackageFragment().getChildren());
			//System.out.println(this.openCloseFilesData.getFastCodeFolder().name);
			for (final IResource resource : this.openCloseFilesData.getFastCodeFolder().getFolder().members()) {
				if (matchPattern(resource, this.openCloseFilesData.getPattern())) {
					/*System.out.println(this.openCloseFilesData.getFastCodeFolder().name);
					System.out.println("ABC" + resource.getClass());*/
					if (resource instanceof IFile) {

						final IWorkbench wb = PlatformUI.getWorkbench();
						final IWorkbenchPage page = wb.getActiveWorkbenchWindow().getActivePage();
						IDE.openEditor(page, (IFile) resource);
					} else if (resource instanceof IJavaElement) {
						final IEditorPart javaEditor = openInEditor((IJavaElement) resource);
						revealInEditor(javaEditor, (IJavaElement) resource);
					}
				}
			}
		} catch (final JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	private boolean matchPattern(final IResource resource, final String pattern) {
		/*if (isEmpty(pattern)) {
			pattern = "\\*";
		}*/
		//final Pattern pattrn = Pattern.compile(pattern);
		//System.out.println(resource.getName());
		final Pattern pattrn = Pattern.compile(pattern.replace(ASTERISK, ".*"), Pattern.CASE_INSENSITIVE);
		final Matcher matcher = pattrn.matcher(resource.getName());
		return matcher.matches();

		//return javaElement.getElementName().matches(pattern);
	}

	private OpenCloseFilesData getOpenCloseFiles() {

		final OpenCloseFilesData openRequiredClassesData = new OpenCloseFilesData();

		IProject project = null;
		final ICompilationUnit compUnit;
		if (this.editorPart != null) {
			compUnit = this.manager.getWorkingCopy(this.editorPart.getEditorInput());
			if (compUnit != null) {
				project = compUnit.getJavaProject().getProject();
				openRequiredClassesData.setCompUnit(compUnit);
			} else {
				final IFile file = (IFile) this.editorPart.getEditorInput().getAdapter(IFile.class);
				project = file.getProject(); //JavaCore.create(file.getProject());
			}
		}

		openRequiredClassesData.setProject(project);

		final OpenCloseFilesDialog openRequiredClassesDialog = new OpenCloseFilesDialog(new Shell(), openRequiredClassesData);
		if (openRequiredClassesDialog.open() == Window.CANCEL) {
			return null;
		}
		final Shell parentShell = MessageUtil.getParentShell();
		final Shell shell = parentShell == null ? new Shell() : parentShell;

		return openRequiredClassesData;

		//return null;
	}

	@Override
	public void selectionChanged(final IAction arg0, final ISelection arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(final IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
