/**
 * This class has been generated by Fast Code Eclipse Plugin
 * For more information please go to http://fast-code.sourceforge.net/
 * @author : Gautam
 * Created : 01/02/2012
 */

package org.fastcode.preferences;

import static org.fastcode.common.FastCodeConstants.AT_THE_RATE;
import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.DOUBLE_FORWARD_SLASH;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FALSE_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.FORWARD_SLASH;
import static org.fastcode.common.FastCodeConstants.HSQL;
import static org.fastcode.common.FastCodeConstants.HSQLDB;
import static org.fastcode.common.FastCodeConstants.JDBC;
import static org.fastcode.common.FastCodeConstants.MYSQL;
import static org.fastcode.common.FastCodeConstants.ORACLE;
import static org.fastcode.common.FastCodeConstants.POSTGRESQL;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.SQLSERVER;
import static org.fastcode.common.FastCodeConstants.SYBASE;
import static org.fastcode.common.FastCodeConstants.THIN;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_CONN_DATA;
import static org.fastcode.preferences.PreferenceConstants.P_DBCONN_FIELD_DELIMITER;
import static org.fastcode.preferences.PreferenceConstants.P_DBCONN_RECORD_DELIMITER;
import static org.fastcode.util.FastCodeTNSParser.parseTNSFields;
import static org.fastcode.util.SourceUtil.showWarning;
import static org.fastcode.util.StringUtil.changeFirstLetterToLowerCase;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.internal.ide.dialogs.OpenResourceDialog;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.DatabaseDetails;
import org.fastcode.common.FastCodeTNSFields;
import org.fastcode.util.DataBaseTypeInfo;
import org.fastcode.util.DatabaseUtil;

public class DatabaseConnectionPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private TableViewer				fTableViewer;
	private Table					table;
	private Button					fAddButton;
	private Button					fEditButton;
	private Button					fRemoveButton;
	private Button					fImportButton;
	private Button					fExportButton;
	private List<DatabaseDetails>	connList;
	private final IPreferenceStore	preferenceStore;

	/**
		 *
		 */
	public DatabaseConnectionPreferencePage() {

		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		setPreferenceStore(this.preferenceStore);
		setDescription("Database Connection preference");

	}

	public void init(final IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Control createContents(final Composite ancestor) {
		final Composite parent = new Composite(ancestor, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		parent.setLayout(layout);

		final Composite innerParent = new Composite(parent, SWT.NONE);
		final GridLayout innerLayout = new GridLayout();
		innerLayout.numColumns = 2;
		innerLayout.marginHeight = 0;
		innerLayout.marginWidth = 0;
		innerParent.setLayout(innerLayout);
		final GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		innerParent.setLayoutData(gd);

		final Composite tableComposite = new Composite(innerParent, SWT.NONE);
		final GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 360;
		data.heightHint = convertHeightInCharsToPixels(10);
		tableComposite.setLayoutData(data);

		final TableColumnLayout columnLayout = new TableColumnLayout();
		tableComposite.setLayout(columnLayout);

		this.fTableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);

		this.table = this.fTableViewer.getTable();

		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);

		final GC gc = new GC(getShell());
		gc.setFont(JFaceResources.getDialogFont());

		final TableViewerColumn viewerColumn1 = new TableViewerColumn(this.fTableViewer, SWT.NONE);
		final TableColumn column1 = viewerColumn1.getColumn();
		column1.setText("Database Name");
		int minWidth = computeMinimumColumnWidth(gc, "Database Name");
		columnLayout.setColumnData(column1, new ColumnWeightData(8, minWidth, true));

		final TableViewerColumn viewerColumn3 = new TableViewerColumn(this.fTableViewer, SWT.NONE);
		final TableColumn column3 = viewerColumn3.getColumn();
		column3.setText("Database Type");
		minWidth = computeMinimumColumnWidth(gc, "Database Type");
		columnLayout.setColumnData(column3, new ColumnWeightData(8, minWidth, true));

		final TableViewerColumn viewerColumn4 = new TableViewerColumn(this.fTableViewer, SWT.NONE);
		final TableColumn column4 = viewerColumn4.getColumn();
		column4.setText("URL");
		minWidth = computeMinimumColumnWidth(gc, "URL");
		columnLayout.setColumnData(column4, new ColumnWeightData(24, minWidth, true));

		gc.dispose();

		this.fTableViewer.setLabelProvider(new DBConnectionLabelProvider());
		this.fTableViewer.setContentProvider(new DBConnectionContentProvider());

		this.fTableViewer.setComparator(new ViewerComparator() {

			@Override
			public int compare(final Viewer viewer, final Object object1, final Object object2) {

				if (object1 instanceof DatabaseDetails && object2 instanceof DatabaseDetails) {
					final String left = ((DatabaseDetails) object1).getDatabaseName();
					final String right = ((DatabaseDetails) object2).getDatabaseName();
					final int result = left.compareToIgnoreCase(right);
					if (result != 0) {
						return result;
					}

				}
				return super.compare(viewer, object1, object2);
			}

			@Override
			public final boolean isSorterProperty(final Object element, final String property) {
				return true;
			}

		});

		this.table.addListener(SWT.Selection, new Listener() {

			public void handleEvent(final Event event) {
				if (event.type == SWT.Selection) {
					DatabaseConnectionPreferencePage.this.fEditButton.setEnabled(true);
					DatabaseConnectionPreferencePage.this.fRemoveButton.setEnabled(true);
				}

			}
		});

		this.table.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(final MouseEvent e) {

				final DatabaseDetails conn = DatabaseConnectionPreferencePage.this.connList.get(DatabaseConnectionPreferencePage.this.table
						.getSelectionIndex());
				editConnectionData(conn);

			}

			public void mouseDown(final MouseEvent e) {

			}

			public void mouseUp(final MouseEvent e) {
				// TODO Auto-generated method stub

			}

		});

		final Composite buttons = new Composite(innerParent, SWT.NONE);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttons.setLayout(layout);

		this.fAddButton = new Button(buttons, SWT.PUSH);
		this.fAddButton.setText("New");
		this.fAddButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.fAddButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(final Event e) {
				add();
				refresh();
			}
		});

		this.fEditButton = new Button(buttons, SWT.PUSH);
		this.fEditButton.setText("Edit");
		this.fEditButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.fEditButton.setEnabled(false);
		this.fEditButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(final Event e) {

				final int index = DatabaseConnectionPreferencePage.this.table.getSelectionIndex();
				if (index > -1) {
					final DatabaseDetails conn = DatabaseConnectionPreferencePage.this.connList.get(index);
					editConnectionData(conn);
				}

			}
		});

		this.fRemoveButton = new Button(buttons, SWT.PUSH);
		this.fRemoveButton.setText("Remove");
		this.fRemoveButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.fRemoveButton.setEnabled(false);

		this.fRemoveButton.addListener(SWT.Selection, new Listener() {

			public void handleEvent(final Event e) {
				final int index = DatabaseConnectionPreferencePage.this.table.getSelectionIndex();
				if (index > -1) {
					final DatabaseDetails conn = DatabaseConnectionPreferencePage.this.connList.get(index);
					remove(conn);

				}

			}
		});

		createSeparator(buttons);

		this.fImportButton = new Button(buttons, SWT.PUSH);
		this.fImportButton.setText("Import");
		this.fImportButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.fImportButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(final Event e) {
				try {
					loadFromTNSFile();
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}

		});

		this.fExportButton = new Button(buttons, SWT.PUSH);
		this.fExportButton.setText("Export");
		this.fExportButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// this.fExportButton.setEnabled(false);
		this.fExportButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(final Event e) {
				//put code
			}
		});

		this.fTableViewer.setInput(loadInput());

		innerParent.layout();
		getControl().addListener(SWT.Traverse, new Listener() {

			public void handleEvent(final Event event) {
				if (event.detail == SWT.TRAVERSE_ESCAPE) {

				}
			}
		});

		return parent;
	}

	private List<DatabaseDetails> loadInput() {
		this.connList = DatabaseUtil.loadConnectionsFromPreferenceStore();
		Collections.sort(this.connList);
		return this.connList;

	}

	protected void add() {

		final DatabaseConnectionDialog dialog = new DatabaseConnectionDialog(new Shell(), this.preferenceStore);
		if (dialog.open() == Window.CANCEL) {
			return;
		}

	}

	protected void editConnectionData(final DatabaseDetails conn) {

		final DatabaseConnectionDialog dialog = new DatabaseConnectionDialog(new Shell(), this.preferenceStore, conn);
		if (dialog.open() == Window.CANCEL) {
			return;
		}

		this.fTableViewer.setInput(loadInput());
		DatabaseConnectionPreferencePage.this.fTableViewer.refresh();
	}

	protected void remove(final DatabaseDetails conn) {

		final String con = getString(conn);
		String connString = this.preferenceStore.getString(P_DATABASE_CONN_DATA);
		connString = connString.replace(con, EMPTY_STR);
		this.preferenceStore.setValue(P_DATABASE_CONN_DATA, connString);
		refresh();
		DatabaseConnectionSettings.setReload(true);

	}

	private String getString(final DatabaseDetails conn) {

		final String FIELD_DELIMITER = this.preferenceStore.getString(P_DBCONN_FIELD_DELIMITER);
		return conn.getDatabaseType() + FIELD_DELIMITER + conn.getDatabaseName() + FIELD_DELIMITER + conn.getHostAddress()
				+ FIELD_DELIMITER + conn.getPort() + FIELD_DELIMITER + conn.getUserName() + FIELD_DELIMITER + conn.getPassword()
				+ FIELD_DELIMITER + conn.isDefaultConn();
	}

	private void refresh() {

		this.fTableViewer.setInput(loadInput());
		this.fTableViewer.refresh();
	}

	/**
	 * Creates a separator between buttons.
	 *
	 * @param parent
	 *            the parent composite
	 * @return a separator
	 */
	private Label createSeparator(final Composite parent) {
		final Label separator = new Label(parent, SWT.NONE);
		separator.setVisible(false);
		final GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.BEGINNING;
		gd.heightHint = 4;
		separator.setLayoutData(gd);
		return separator;
	}

	private int computeMinimumColumnWidth(final GC gc, final String string) {

		return gc.stringExtent(string).x + 10; // pad 10 to accommodate table
												// header trimmings

	}

	@Override
	public boolean performOk() {
		final List<DatabaseDetails> dbConnectionsInPref = DatabaseUtil.loadConnectionsFromPreferenceStore();
		boolean defaultSet = false;
		for (final DatabaseDetails databaseConnection : dbConnectionsInPref) {
			if (databaseConnection.isDefaultConn()) {
				defaultSet = true;
				break;
			}
		}

		if (!defaultSet) {
			if (dbConnectionsInPref.size() == 1) {
				final String msg = "There is only one conenction configured. Make it as default connection?";
				if (showWarning(msg, "Proceed", "Cancel")) {
					return super.performOk();
				} else {
					return false;
				}
			} else {
				MessageDialog.openError(new Shell(), "Default Connection not set",
						"Please set any one of the connection as default and then proceed.");
				return false;
			}
		}
		return true;
	}

	private void loadFromTNSFile() throws Exception {
		FileDialog fileDialog = null;
		fileDialog = new FileDialog(new Shell(), SWT.OPEN);
		final String tnsFile = fileDialog.open();
		final List<FastCodeTNSFields> tnsFieldsList = parseTNSFields(new File(tnsFile)); //new File("C:/oracle/product/10.2.0/db_1/network/admin/tnsnames.ora"));//new File(tnsFile.getLocationURI()));
		final String dbType = ORACLE;
		final String RECORD_DELIMITER = this.preferenceStore.getString(P_DBCONN_RECORD_DELIMITER);
		final String FIELD_DELIMITER = this.preferenceStore.getString(P_DBCONN_FIELD_DELIMITER);
		final StringBuilder newConns = new StringBuilder(EMPTY_STR);
		for (final FastCodeTNSFields fastCodeTNSFields : tnsFieldsList) {
			final String newConnString = dbType + FIELD_DELIMITER + fastCodeTNSFields.getServiceName() + FIELD_DELIMITER
					+ fastCodeTNSFields.getHost() + FIELD_DELIMITER + fastCodeTNSFields.getPort() + FIELD_DELIMITER + EMPTY_STR
					+ FIELD_DELIMITER + EMPTY_STR + FIELD_DELIMITER + FALSE_STR;
			newConns.append(EMPTY_STR.equals(newConns.toString()) ? newConnString : RECORD_DELIMITER + newConnString);
		}

		final String existingConns = this.preferenceStore.getString(P_DATABASE_CONN_DATA);
		this.preferenceStore.setValue(P_DATABASE_CONN_DATA, existingConns + RECORD_DELIMITER + newConns.toString());
		//DatabaseConnectionSettings.setReload(true);
		//DataBaseTypeInfo.setReload(true);
		super.performOk();
	}
}

/**
 * Label provider for templates.
 */
class DBConnectionLabelProvider extends LabelProvider implements ITableLabelProvider {

	/*
	 * @see
	 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang
	 * .Object, int)
	 */

	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	/*
	 * @see
	 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang
	 * .Object, int)
	 */

	public String getColumnText(final Object element, final int columnIndex) {
		final DatabaseDetails data = (DatabaseDetails) element;

		switch (columnIndex) {
		case 0:
			return data.getDatabaseName();
		case 1:

			return data.getDatabaseType();
		case 2:
			return getConnURL(data);

		default:
			return EMPTY_STR; //$NON-NLS-1$
		}
	}

	public String getConnURL(final DatabaseDetails conn) {

		final String databaseType = conn.getDatabaseType();
		final String databaseName = conn.getDatabaseName();
		final String hostAddress = conn.getHostAddress();
		final int portNumber = conn.getPort();

		if (databaseType.equals(MYSQL)) {
			return JDBC + COLON + databaseType + COLON + DOUBLE_FORWARD_SLASH + hostAddress + FORWARD_SLASH + databaseName;

		} else if (databaseType.equals(ORACLE)) {

			return JDBC + COLON + databaseType + COLON + THIN + COLON + AT_THE_RATE + hostAddress + COLON + portNumber + COLON
					+ databaseName;

		} else if (databaseType.equals(SQLSERVER)) {

		} else if (databaseType.equals(HSQLDB)) {

			return JDBC + COLON + databaseType + COLON + HSQL + COLON + DOUBLE_FORWARD_SLASH + hostAddress + FORWARD_SLASH + databaseName;

		} else if (databaseType.equals(SYBASE)) {

		} else if (databaseType.equals(POSTGRESQL)) {
			return JDBC + COLON + databaseType + COLON + DOUBLE_FORWARD_SLASH + hostAddress + FORWARD_SLASH + databaseName;

		}

		return EMPTY_STR;
	}

}

class DBConnectionContentProvider implements IStructuredContentProvider {

	/*
	 * @see IStructuredContentProvider#getElements(Object)
	 */

	public Object[] getElements(final Object input) {
		return ((List<DatabaseDetails>) input).toArray();
	}

	/*
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */

	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {

	}

	/*
	 * @see IContentProvider#dispose()
	 */

	public void dispose() {

	}

}
