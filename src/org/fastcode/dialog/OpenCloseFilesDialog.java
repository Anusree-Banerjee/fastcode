package org.fastcode.dialog;

import static org.fastcode.common.FastCodeConstants.COMMON_CLASS_SUFFIX;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.ENCLOSING_FOLDER_STR;
import static org.fastcode.common.FastCodeConstants.FC_PLUGIN;
import static org.fastcode.common.FastCodeConstants.HYPHEN;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.util.MessageUtil.getChoiceFromMultipleValues;
import static org.fastcode.util.SourceUtil.getFolderFromPath;
import static org.fastcode.util.StringUtil.isEmpty;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.fastcode.common.FastCodeColor;
import org.fastcode.common.FastCodeFolder;
import org.fastcode.common.FastCodeProject;
import org.fastcode.common.OpenCloseFilesData;
import org.fastcode.popup.actions.snippet.FastCodeCache;
import org.fastcode.setting.GlobalSettings;

public class OpenCloseFilesDialog extends TrayDialog {
	private Combo			folderCombo;
	private Button			folderBrowseButton;
	private Button			closeOthers;
	private Text			errorMessageText;
	private Text			patternText;
	protected Shell			shell;
	OpenCloseFilesData		openRequiredClassesData;
	protected final String	defaultMessage	= NEWLINE;
	private String			errorMessage;
	private Label			projectLabel;
	private Combo			projectCombo;
	Map<String, IProject>	prjMap			= new HashMap<String, IProject>();
	IFolder					currentFolder;

	/**
	 * @param parent
	 *
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		final GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		parent.setLayout(layout);

		createErrorMessageText(parent);
		createProjectSelectionPane(parent);

		createFolderSelectionPane(parent);
		createPattern(parent);
		createCloseOthers(parent);
		this.folderCombo.setFocus();

		final String projectName = this.projectCombo.getText();
		if (!isEmpty(projectName)) {
			isPrjInSync(this.prjMap.get(projectName));
		}

		return parent;
	}

	@Override
	protected void okPressed() {
		if (isEmpty(this.folderCombo.getText())) {
			setErrorMessage("Please select a folder");
			return;
		} else {
			setErrorMessage(this.defaultMessage);
		}

		if (isEmpty(this.patternText.getText())) {
			setErrorMessage("Please enter pattern");
			return;
		} else {
			setErrorMessage(this.defaultMessage);
		}
		super.okPressed();
	}
	private void createCloseOthers(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Close Others:               ");
		label.setLayoutData(gridDataLabel);

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		this.closeOthers = new Button(composite, SWT.CHECK);
		this.closeOthers.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final Button b = (Button) event.getSource();
				OpenCloseFilesDialog.this.openRequiredClassesData.setCloseOthers(b.getSelection());
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});

	}

	private void createPattern(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setSize(300, 500);

		final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Pattern:                         ");
		label.setLayoutData(gridDataLabel);

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		this.patternText = new Text(composite, SWT.BORDER);
		this.patternText.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 350;

		this.patternText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				final Text text = (Text) e.widget;
				final String value = text.getText();
				if (isEmpty(value)) {
					setErrorMessage("Please enter pattern");
				} else {
					setErrorMessage(OpenCloseFilesDialog.this.defaultMessage);
				}
				//System.out.println(value.replace(ASTERISK, BACK_SLASH + ASTERISK));
				OpenCloseFilesDialog.this.openRequiredClassesData.setPattern(value/*.replace(ASTERISK, BACK_SLASH + ASTERISK)*/);
			}
		});
	}

	/**
	 * @param shell
	 * @param createVariableData
	 */
	public OpenCloseFilesDialog(final Shell shell, final OpenCloseFilesData openRequiredClassesData) {
		super(shell);
		this.shell = shell;
		this.openRequiredClassesData = openRequiredClassesData;
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("Open Required Classes");

	}

	private void createFolderSelectionPane(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);
		//composite.setSize(300, 500);

		final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Select Folder:            ");
		label.setLayoutData(gridDataLabel);

		final GridData gridDataText = new GridData(GridData.FILL_HORIZONTAL);
		gridDataText.grabExcessHorizontalSpace = true;

		this.folderCombo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);// new
		// Text(composite,
		// SWT.BORDER);
		//this.packageCombo.setSize(50, 20);
		this.folderCombo.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 450;
		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();

		if (this.openRequiredClassesData.getCompUnit() == null) {
			if (this.openRequiredClassesData.getEditorPart() != null) {
				final IFile file = (IFile) this.openRequiredClassesData.getEditorPart().getEditorInput().getAdapter(IFile.class);

				try {
					if (file != null) {
						final String srcPath = file
								.getProjectRelativePath()
								.toString()
								.substring(0,
										file.getProjectRelativePath().toString().indexOf(file.getProjectRelativePath().lastSegment()) - 1);
						final IFolder folder = this.openRequiredClassesData.getProject().getFolder(srcPath);
						if (folder != null) {
							this.currentFolder = folder;
							this.folderCombo.add(ENCLOSING_FOLDER_STR + HYPHEN + this.currentFolder.getFullPath().toString());

							//this.folderNameCombo[count].select(0);
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}

		}

		if (!fastCodeCache.getFolderSet().isEmpty()) {
			for (final IFolder folder : fastCodeCache.getFolderSet()) {
				if (this.currentFolder != null && this.currentFolder.equals(folder)) {
					continue;
				}

				boolean addItem = true;
				if (this.folderCombo.getItems() != null) {
					for (final String existingFolder : this.folderCombo.getItems()) {
						if (existingFolder.contains(ENCLOSING_FOLDER_STR)) {
							continue;
						}
						if (existingFolder.equals(folder.getFullPath().toString())) {
							addItem = false;
							break;

						}
					}
					if (addItem) {
						this.folderCombo.add(folder.getFullPath().toString());
					}
				}
			}
		}
		this.folderCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				String selectedFolderPath = ((Combo) event.widget).getText();
				if (selectedFolderPath.contains(ENCLOSING_FOLDER_STR)) {
					selectedFolderPath = OpenCloseFilesDialog.this.currentFolder.getFullPath().toString();
				}
				try {
					if (!fastCodeCache.getFolderSet().isEmpty()) {
						for (final IFolder folder : fastCodeCache.getFolderSet()) {
							if (folder.getFullPath().toString().equals(selectedFolderPath)) {
								OpenCloseFilesDialog.this.openRequiredClassesData.setFastCodeFolder(new FastCodeFolder(folder));
							}
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}

		});
		this.folderCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e) {
				String inputFolderPath = ((Combo) e.widget).getText();
				if (!isEmpty(inputFolderPath)) {
					if (inputFolderPath.contains(ENCLOSING_FOLDER_STR)) {
						inputFolderPath = OpenCloseFilesDialog.this.currentFolder.getFullPath().toString();
					}
					for (final IFolder folder : fastCodeCache.getFolderSet()) {
						if (folder.getFullPath().toString().equals(inputFolderPath)) {
							return;
						}
					}
				}
			}

			@Override
			public void focusGained(final FocusEvent arg0) {

			}
		});

		this.folderCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				if (isEmpty(((Combo) e.widget).getText())) {
					setErrorMessage("Please select a folder.");
				} else {
					setErrorMessage(OpenCloseFilesDialog.this.defaultMessage);
				}
			}

		});

		final GridData gridDataButton = new GridData();

		this.folderBrowseButton = new Button(composite, SWT.PUSH);
		this.folderBrowseButton.setText("Browse");
		this.folderBrowseButton.setLayoutData(gridDataButton);

		this.folderBrowseButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final Button b = (Button) event.getSource();
				try {
					Path path = null;
					//final Button b = (Button) event.getSource();
					final ContainerSelectionDialog dialog = new ContainerSelectionDialog(new Shell(), null, true, "Select a folder:");
					dialog.setTitle("Select a Folder");
					dialog.showClosedProjects(false);
					if (dialog.open() != CANCEL) {
						path = (Path) dialog.getResult()[0];
						String srcPath = null;
						if (path != null) {
							final String project = path.segment(0);
							srcPath = path.toString().substring(project.length() + 1);
						}
						final IFolder folder = OpenCloseFilesDialog.this.openRequiredClassesData.getSelectedProject().getProject().getFolder(new Path(srcPath));
						OpenCloseFilesDialog.this.openRequiredClassesData.setFastCodeFolder(new FastCodeFolder(folder));
						boolean addItem = true;
						if (OpenCloseFilesDialog.this.folderCombo.getItems() != null) {
							for (final String existingFolder : OpenCloseFilesDialog.this.folderCombo.getItems()) {
								if (existingFolder.equals(folder.getFullPath().toString())) {
									if (!existingFolder.equals(OpenCloseFilesDialog.this.folderCombo.getText())) {
										OpenCloseFilesDialog.this.folderCombo.select(OpenCloseFilesDialog.this.folderCombo
												.indexOf(existingFolder));
									}
									addItem = false;
									break;
								}
							}
						}
						if (addItem) {
							OpenCloseFilesDialog.this.folderCombo.add(folder.getFullPath().toString());
							OpenCloseFilesDialog.this.folderCombo.select(OpenCloseFilesDialog.this.folderCombo.getItemCount() - 1);
						}
						if (!fastCodeCache.getFolderSet().contains(folder)) {
							fastCodeCache.getFolderSet().add(folder);
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
	}

	/**
	 * @param parent
	 */
	private void createProjectSelectionPane(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData();
		this.projectLabel = new Label(composite, SWT.NONE);
		this.projectLabel.setText("Select Project:              ");
		this.projectLabel.setLayoutData(gridDataLabel);
		//this.projectLabel.setVisible(false);

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		this.projectCombo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		this.projectCombo.setSize(200, 20);
		this.projectCombo.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 500;
		//this.projectCombo.setEnabled(false);
		//this.projectCombo.setVisible(false);
		final IProject projects[] = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (final IProject prj : projects) {
			if (prj == null || !prj.exists() || !prj.isOpen()) {
				continue;
			}
			if (prj.getName().equals(FC_PLUGIN)) {
				continue;
			}
			this.projectCombo.add(prj.getName());
			this.prjMap.put(prj.getName(), prj);
		}

		if (this.openRequiredClassesData.getProject() != null) {
			this.projectCombo.select(this.projectCombo.indexOf(this.openRequiredClassesData.getProject().getName()));
			OpenCloseFilesDialog.this.openRequiredClassesData.setSelectedProject(new FastCodeProject(OpenCloseFilesDialog.this.prjMap
					.get(this.openRequiredClassesData.getProject().getName())));
		}

		this.projectCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final String projectName = OpenCloseFilesDialog.this.projectCombo.getText();
				if (!isEmpty(projectName)) {
					OpenCloseFilesDialog.this.openRequiredClassesData.setSelectedProject(new FastCodeProject(
							OpenCloseFilesDialog.this.prjMap.get(projectName)));
					clearErrorMessage(OpenCloseFilesDialog.this.defaultMessage);
					isPrjInSync(OpenCloseFilesDialog.this.prjMap.get(projectName));
				} else {
					setErrorMessage("Please select a project");
				}
				loadFolders(OpenCloseFilesDialog.this.prjMap.get(projectName));
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});

		/*this.projectCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent arg0) {
				final String projectName = CreateSnippetDialog.this.projectCombo.getText();
				if (!isEmpty(projectName)) {
					isPrjInSync(CreateSnippetDialog.this.prjMap.get(projectName));
				}

			}
		});*/
	}

	protected void loadFolders(final IProject project) {
		try {
			if (project.isOpen() && JavaProject.hasJavaNature(project)) {
				final IJavaProject javaProject = JavaCore.create(project);

				IClasspathEntry[] classpathEntries = null;
				classpathEntries = javaProject.getResolvedClasspath(true);

				for (int i = 0; i < classpathEntries.length; i++) {
					final IClasspathEntry entry = classpathEntries[i];
					if (entry.getContentKind() == IPackageFragmentRoot.K_SOURCE) {
						final String relativePath = entry.getPath().toString();
						final IFolder folderFromPath = getFolderFromPath(project, relativePath);
						System.out.println(folderFromPath);
					}
				}

			}

		} catch (final Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private String getCommonSuffix() {
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final String commonSuffixes = globalSettings.getPropertyValue(COMMON_CLASS_SUFFIX.toUpperCase(), EMPTY_STR);
		final IJavaElement[] classesInPackage;
		final String suffix = null;
		/*try {
			classesInPackage = OpenRequiredClassesDialog.this.createVariableData.getPackageFragment().getChildren();
			final String[] commonSuffixArr = commonSuffixes.split(",");

			for (int k = 0; k < commonSuffixArr.length; k++) {
				for (int i = 0; i < classesInPackage.length; i++) {
					final String tmpClsName = classesInPackage[i].getElementName().substring(0,
							classesInPackage[i].getElementName().indexOf("."));
					if (!tmpClsName.toLowerCase().endsWith(commonSuffixArr[k].toLowerCase().trim())) {
						suffix = EMPTY_STR;
						break;
					}
					suffix = commonSuffixArr[k].trim();
				}
				if (suffix != null && !suffix.equals(EMPTY_STR)) {
					break;
				}
			}

		} catch (final JavaModelException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}*/
		if (suffix != null & suffix.trim().length() > 0) {
			final String[] choices = { "Yes", "No" };

			final String choice = getChoiceFromMultipleValues(this.shell, "Warning", "All the classes in this package end with " + suffix
					+ ". Do you want to add the same suffix to the class", choices);
			if (choice == null || choice.equals("No")) {
				return EMPTY_STR;
			}
		}
		return suffix == null ? EMPTY_STR : suffix;
	}

	/**
	 * @param parent
	 */
	protected void createErrorMessageText(final Composite parent) {

		final GridData errText = new GridData(590, 40);
		errText.grabExcessHorizontalSpace = true;

		this.errorMessageText = new Text(parent, SWT.READ_ONLY | SWT.WRAP);
		//this.errorMessageText.setForeground(new Color(null, 255, 0, 0));
		this.errorMessageText.setForeground(FastCodeColor.getErrorMsgColor());
		this.errorMessageText.setLayoutData(errText);
		setErrorMessage(this.defaultMessage);

	}

	/**
	 * @param errorMessage
	 */
	public void setMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
		if (this.errorMessageText != null && !this.errorMessageText.isDisposed()) {
			this.errorMessageText.setText(errorMessage == null ? " \n " : errorMessage); //$NON-NLS-1$
			boolean hasError = false;
			if (errorMessage != null && !errorMessage.equals(this.defaultMessage)) {
				hasError = true;
			}
			this.errorMessageText.setEnabled(hasError);
			this.errorMessageText.setVisible(hasError);
			this.errorMessageText.getParent().update();
			final Control button = getButton(IDialogConstants.OK_ID);
			if (button != null) {
				button.setEnabled(errorMessage.equals(this.defaultMessage));
			}
		}
	}

	/**
	 * @param project
	 *
	 */
	private boolean isPrjInSync(final IProject project) {
		if (project != null && !project.isSynchronized(IResource.DEPTH_INFINITE)) {
			this.folderCombo.setEnabled(false);
			this.folderBrowseButton.setEnabled(false);
			setErrorMessage("Project " + project.getName() + " is not synchronised. Please synchronise and try again.");
			return false;
		} else {
			this.folderCombo.setEnabled(true);
			this.folderBrowseButton.setEnabled(true);
			clearErrorMessage(this.defaultMessage);
			return true;
		}
	}

	/**
	 * @param errorMessage
	 */
	public void setErrorMessage(final String errorMessage) {
		setMessage(errorMessage);
	}

	/**
	 * @param errorMessage
	 */
	public void clearErrorMessage(final String errorMessage) {
		setMessage(errorMessage);
	}

}
