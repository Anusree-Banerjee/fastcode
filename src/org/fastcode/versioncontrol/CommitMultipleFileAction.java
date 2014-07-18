package org.fastcode.versioncontrol;

import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.GROOVY_EXTENSION;
import static org.fastcode.common.FastCodeConstants.JAVA_EXTENSION;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.util.SourceUtil.checkForErrors;
import static org.fastcode.util.SourceUtil.getRepositoryServiceClass;
import static org.fastcode.util.SourceUtil.isFileReferenced;
import static org.fastcode.util.SourceUtil.isFileSaved;
import static org.fastcode.util.SourceUtil.loadComments;
import static org.fastcode.util.StringUtil.evaluateByVelocity;
import static org.fastcode.util.StringUtil.getGlobalSettings;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.VersionControlUtil.getPreviousCommentsFromCache;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.fastcode.common.Action;
import org.fastcode.common.FastCodeCheckinCommentsData;
import org.fastcode.common.FastCodeConstants.ACTION_ENTITY;
import org.fastcode.dialog.FastCodeCheckinCommentsDialog;
import org.fastcode.exception.FastCodeRepositoryException;
import org.fastcode.preferences.VersionControlPreferences;
import org.fastcode.util.FastCodeFileForCheckin;
import org.fastcode.util.RepositoryService;

public class CommitMultipleFileAction implements IActionDelegate, IWorkbenchWindowActionDelegate {

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void init(final IWorkbenchWindow arg0) {
		// TODO Auto-generated method stub

	}

	public void run(final IAction arg0) {
		final VersionControlPreferences versionControlPreferences = VersionControlPreferences.getInstance();
		if (!versionControlPreferences.isEnable()) {
			MessageDialog
					.openInformation(
							new Shell(),
							"Version Control not enabled",
							"Version control is not enabled. Please go to Windows->Preferences->Fast Code->Version Control and give the required details and try again.");
			return;
		}

		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		final ISelection selection = window.getSelectionService().getSelection("org.eclipse.jdt.ui.PackageExplorer");
		final IStructuredSelection structuredSelection = (IStructuredSelection) selection;

		final Object firstElement = structuredSelection.getFirstElement();
		final IProject project = null;
		final List<File> itemsTocheckIn = new ArrayList<File>();

		if (firstElement instanceof IPackageFragment) {
			IPackageFragment packageFragment;
			packageFragment = (IPackageFragment) firstElement;
			System.out.println(packageFragment.getPath());
			/*final String pakURI = packageFragment.getResource().getLocationURI().toString();
			final File pakFile = new File(pakURI.substring(pakURI.indexOf(COLON) + 1));

			project = packageFragment.getJavaProject().getProject();
			itemsTocheckIn.add(pakFile);

			try {
				final RepositoryService checkin = getRepositoryServiceClass();
				final ICompilationUnit[] compilationUnits = packageFragment.getCompilationUnits();
				final StringBuilder notSaved = new StringBuilder(EMPTY_STR);
				for (final ICompilationUnit iCompilationUnit : compilationUnits) {
					final boolean fileReferenced = isFileReferenced((IFile) iCompilationUnit.getResource(), IJavaSearchConstants.TYPE);

					if (fileReferenced) {
						if (!MessageDialog.openQuestion(new Shell(), "File(s) check in",
								"Will check in only the selected file and not any dependencies. Do you want to proceed??")) {
							continue;
						}
					}
					if (!isFileSaved(iCompilationUnit.getElementName(), (IFile) iCompilationUnit.getResource())) {
						notSaved.append(EMPTY_STR.equals(notSaved.toString()) ? iCompilationUnit.getElementName() + "- not saved" + COMMA
								: iCompilationUnit.getElementName() + "- not saved.");
					}
					if (checkForErrors(iCompilationUnit.getResource())) {
						notSaved.append(EMPTY_STR.equals(notSaved.toString()) ? iCompilationUnit.getElementName() + "- has errors" + COMMA
								: iCompilationUnit.getElementName() + "- has errors.");
					}
					final String compUnitURI = iCompilationUnit.getResource().getLocationURI().toString();
					final File fileToCheckIn = new File(compUnitURI.substring(compUnitURI.indexOf(COLON) + 1));
					if (!checkin.doesFileHaveChanges(fileToCheckIn)) {
						continue;
					}
					itemsTocheckIn.add(fileToCheckIn);
				}

				if (!isEmpty(notSaved.toString())) {
					MessageDialog.openError(new Shell(), "File(s) not saved or has error", "The file(s) " + notSaved.toString()
							+ "please take required action and try again.");
					return;
				}

			} catch (final InvocationTargetException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			} catch (final InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			} catch (final JavaModelException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			} catch (final Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}*/

		} else if (firstElement instanceof ICompilationUnit) {
			final ICompilationUnit compilationUnit = (ICompilationUnit) firstElement;

		} else {

		}

		try {

			final RepositoryService checkin = getRepositoryServiceClass();
			final IProject prj = project;
			final IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(final IProgressMonitor monitor) {
					try {
						final FastCodeCheckinCommentsData comboData = new FastCodeCheckinCommentsData();
						final List<String> cmntsFromRepo = checkin.getPreviousComments(prj.getName());
						comboData.setComntsFromRepo(cmntsFromRepo);
						final FastCodeCheckinCommentsDialog fastCodeCombo = new FastCodeCheckinCommentsDialog(new Shell(), comboData);
						if (fastCodeCombo.open() == Window.CANCEL) {
							//return;
							/*comment = getCompilationUnitFromEditor() != null ? "Modified Class " + file.getName() + DOT : "Modified File "
									+ file.getName() + DOT;*/
						}
						String comment;
						if (comboData.isAddPrefixFooter()) {
							final Map<String, Object> placeHolder = new HashMap<String, Object>();
							getGlobalSettings(placeHolder);
							final String prefix = evaluateByVelocity(versionControlPreferences.getComntPrefix(), placeHolder);
							final String footer = evaluateByVelocity(versionControlPreferences.getComntFooter(), placeHolder);
							comment = prefix + NEWLINE + comboData.getFinalComment() + NEWLINE + footer;
						} else {
							comment = comboData.getFinalComment();
						}

						final CheckedTreeSelectionDialog checkedTreeSelectionDialog = new CheckedTreeSelectionDialog(new Shell(),
								new FileLabelProvider(), new FileContentProvider());
						checkedTreeSelectionDialog.setTitle("File(s) Selection");
						checkedTreeSelectionDialog.setMessage("Select the file(s) to checkin");
						checkedTreeSelectionDialog.setInput(itemsTocheckIn);
						//checkedTreeSelectionDialog.setInitialElementSelections(filterActionList(actionList));//checkedTreeSelectionDialog.setInitialElementSelections(actionList);
						checkedTreeSelectionDialog.setExpandedElements(itemsTocheckIn.toArray(new File[0]));
						checkedTreeSelectionDialog.setContainerMode(true);

						if (checkedTreeSelectionDialog.open() == Window.CANCEL) {
							return;
						}
						final StringBuilder existWarningBuilder = new StringBuilder();

						if (checkedTreeSelectionDialog.getResult() != null) {
							for (final Object selection : checkedTreeSelectionDialog.getResult()) {
								final File fileSelected = (File) selection;
								monitor.setTaskName("Auto checkin");
								monitor.subTask("Checking in " + fileSelected.getName());
								checkin.checkInFile(fileSelected, comment, prj);

							}

						}
					} catch (final FastCodeRepositoryException ex) {
						MessageDialog.openError(new Shell(), "Error", "Some error occured --" + ex.getMessage());
						ex.printStackTrace();
					} catch (final Exception ex) {
						MessageDialog.openError(new Shell(), "Error", "Some error occured --" + ex.getMessage());
						ex.printStackTrace();
					} finally {
						monitor.done();
					}
				}
			};

			final IWorkbench wb = PlatformUI.getWorkbench();
			final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();

			new ProgressMonitorDialog(new Shell()).run(false, false, op);
		} catch (final FastCodeRepositoryException ex1) {
			// TODO Auto-generated catch block
			ex1.printStackTrace();
		} catch (final InvocationTargetException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (final InterruptedException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}

	public void selectionChanged(final IAction arg0, final ISelection arg1) {
		// TODO Auto-generated method stub

	}

	public void setActiveEditor(final IAction arg0, final IEditorPart arg1) {
		// TODO Auto-generated method stub

	}

	private class FileLabelProvider implements ILabelProvider {

		public void addListener(final ILabelProviderListener arg0) {
			// TODO Auto-generated method stub

		}

		public void dispose() {
			// TODO Auto-generated method stub

		}

		public boolean isLabelProperty(final Object arg0, final String arg1) {
			// TODO Auto-generated method stub
			return false;
		}

		public void removeListener(final ILabelProviderListener arg0) {
			// TODO Auto-generated method stub

		}

		public Image getImage(final Object arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getText(final Object arg0) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	private class FileContentProvider implements ITreeContentProvider{

		public void dispose() {
			// TODO Auto-generated method stub

		}

		public void inputChanged(final Viewer arg0, final Object arg1, final Object arg2) {
			// TODO Auto-generated method stub

		}

		public Object[] getChildren(final Object inputObj) {
			/*final List<File> itemsTocheckIn = new ArrayList<File>();

			final String pakURI = packageFragment.getResource().getLocationURI().toString();
			final File pakFile = new File(pakURI.substring(pakURI.indexOf(COLON) + 1));

			project = packageFragment.getJavaProject().getProject();
			itemsTocheckIn.add(pakFile);

			try {
				final RepositoryService checkin = getRepositoryServiceClass();
				final ICompilationUnit[] compilationUnits = packageFragment.getCompilationUnits();
				final StringBuilder notSaved = new StringBuilder(EMPTY_STR);
				for (final ICompilationUnit iCompilationUnit : compilationUnits) {
					final boolean fileReferenced = isFileReferenced((IFile) iCompilationUnit.getResource(), IJavaSearchConstants.TYPE);

					if (fileReferenced) {
						if (!MessageDialog.openQuestion(new Shell(), "File(s) check in",
								"Will check in only the selected file and not any dependencies. Do you want to proceed??")) {
							continue;
						}
					}
					if (!isFileSaved(iCompilationUnit.getElementName(), (IFile) iCompilationUnit.getResource())) {
						notSaved.append(EMPTY_STR.equals(notSaved.toString()) ? iCompilationUnit.getElementName() + "- not saved" + COMMA
								: iCompilationUnit.getElementName() + "- not saved.");
					}
					if (checkForErrors(iCompilationUnit.getResource())) {
						notSaved.append(EMPTY_STR.equals(notSaved.toString()) ? iCompilationUnit.getElementName() + "- has errors" + COMMA
								: iCompilationUnit.getElementName() + "- has errors.");
					}
					final String compUnitURI = iCompilationUnit.getResource().getLocationURI().toString();
					final File fileToCheckIn = new File(compUnitURI.substring(compUnitURI.indexOf(COLON) + 1));
					if (!checkin.doesFileHaveChanges(fileToCheckIn)) {
						continue;
					}
					itemsTocheckIn.add(fileToCheckIn);
				}

				if (!isEmpty(notSaved.toString())) {
					MessageDialog.openError(new Shell(), "File(s) not saved or has error", "The file(s) " + notSaved.toString()
							+ "please take required action and try again.");
					return null;
				}

			} catch (final InvocationTargetException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			} catch (final InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			} catch (final JavaModelException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			} catch (final Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			return itemsTocheckIn;*/
			return null;
		}

		public Object[] getElements(final Object arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public Object getParent(final Object arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean hasChildren(final Object arg0) {
			// TODO Auto-generated method stub
			return false;
		}
	}
}