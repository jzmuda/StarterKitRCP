package not.another.library.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.IBeanValueProperty;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import not.another.library.dialogs.DeleteBookDialog;
import not.another.library.dialogs.EditBookDialog;
import not.another.library.project.data.BookVo;
import not.another.library.views.filters.SearchFilter;

public class BookViev extends ViewPart {

	private static String[] columnNames ={ "id", "authors", "title", "status"};
	private static int[] defaultColumnWidths ={50,365,365,80};
	private String problem="";
	public static final String ID = "NotAnotherLibraryProject.booksview";
	public static final String OTHER_ID = "NotAnotherLibraryProject.startview";
	public static final String ADD_ID = "NotAnotherLibraryProject.addview";
	private static IWorkbenchPage page;
	private static IViewPart otherView;

	private Text txtSearchCriteriaName;
	private Text txtSearchCriteriaTitle;
	private Label lblBunnies;
	private TableViewer tableViewer;
	private Table table;
	private WritableList bookList;
	private SearchFilter searchFilter;
	private Composite parent;
	private Shell shell;
	private List<BookVo> allBooks=new ArrayList<BookVo>();

	public BookViev() {
		page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		otherView=page.findView(OTHER_ID);
		page.hideView(otherView);
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent=parent;
		shell=parent.getShell();
		parent.setLayout(new GridLayout(2, false));
		bookList = new WritableList(new ArrayList<>(), BookVo.class);
		Label lblAuthors = new Label(parent, SWT.NONE);
		lblAuthors.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAuthors.setText("Authors");

		txtSearchCriteriaName = new Text(parent, SWT.BORDER);
		txtSearchCriteriaName.setToolTipText("Search parameter for book authors");
		GridData gd_txtSearchCriteriaName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtSearchCriteriaName.widthHint = 121;
		txtSearchCriteriaName.setLayoutData(gd_txtSearchCriteriaName);

		Label lblTitle = new Label(parent, SWT.NONE);
		lblTitle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTitle.setText("Title");

		txtSearchCriteriaTitle = new Text(parent, SWT.BORDER);
		txtSearchCriteriaTitle.setToolTipText("Search parameter for book title");
		txtSearchCriteriaTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnAddBook = new Button(parent, SWT.NONE);
		btnAddBook.setToolTipText("Add a book");
		btnAddBook.setText("Add Book");

		Button btnRefresh = new Button(parent, SWT.NONE);
		btnRefresh.setToolTipText("Refresh the list");
		btnRefresh.setText("Refresh");
		
		lblBunnies = new Label(parent, SWT.NONE);
		lblBunnies.setText("Bunnies");

		addSelectionListenersToButtons(btnAddBook, btnRefresh);

		createTableViewer();

		searchFilter = new SearchFilter(txtSearchCriteriaName.getText(),txtSearchCriteriaTitle.getText());
		tableViewer.addFilter(searchFilter);
		addSearchListeners();
	}

	private void addSelectionListenersToButtons(Button btnAddBook, Button btnRefresh) {
		btnRefresh.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				getBooksFromServer();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				getBooksFromServer();
			}
		});


		btnAddBook.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				addBook();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				addBook();
			}
		});
	}


	private void addBook() {		
		try {
			page.showView(ADD_ID);
		} catch (PartInitException e) {
			showErrorMessageBox(e);
		}
	}

	private void addSearchListeners() {
		txtSearchCriteriaTitle.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				searchFilter.setTitlePrefix(txtSearchCriteriaTitle.getText());
				tableViewer.refresh();
			}
		});
		txtSearchCriteriaName.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				searchFilter.setAuthorPrefix(txtSearchCriteriaName.getText());
				tableViewer.refresh();
			}
		});
	}

	private void createTableViewer() {
		getBooksFromServer();
		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		createColumns();
		IBeanValueProperty[] values = BeanProperties.values(columnNames);
		ViewerSupport.bind(tableViewer, bookList, values);

		addTableMenu();
	}

	private void addTableMenu() {
		Menu menu = new Menu(table);
		table.setMenu(menu);

		MenuItem mntmEdit = new MenuItem(menu, SWT.NONE);
		mntmEdit.setText("Edit");
		mntmEdit.addSelectionListener(editBook());

		MenuItem mntmDelete = new MenuItem(menu, SWT.NONE);
		mntmDelete.setText("Delete");
		mntmDelete.addSelectionListener(deleteBook());

		MenuItem mntmHashCode = new MenuItem(menu, SWT.NONE);
		mntmHashCode.setText("Hash");
		mntmHashCode.addSelectionListener(showHashCode());
	}

	private void createColumns() {
		for( int i=0; i<columnNames.length; i++)
			createTableViewerColumn(columnNames[i], defaultColumnWidths[i]);
	}

	private TableViewerColumn createTableViewerColumn(String columnName, int columnWidth) {
		final TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn tableColumn = tableViewerColumn.getColumn();
		tableColumn.setText(columnName);
		tableColumn.setWidth(columnWidth);
		tableColumn.setResizable(true);
		tableColumn.setMoveable(true);
		return tableViewerColumn;	
	}

	private SelectionAdapter editBook() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!tableViewer.getSelection().isEmpty()) {
					IStructuredSelection selection = tableViewer.getStructuredSelection();
					BookVo book = (BookVo) selection.getFirstElement();
					EditBookDialog dialog  = new EditBookDialog(shell, book);
					dialog.open();
					getBooksFromServer();
				}
			}
		};
	}

	private SelectionAdapter deleteBook() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!tableViewer.getSelection().isEmpty()) {
					IStructuredSelection selection = tableViewer.getStructuredSelection();
					BookVo book = (BookVo) selection.getFirstElement();
					DeleteBookDialog dialog  = new DeleteBookDialog(shell, book);
					dialog.open();
					getBooksFromServer();
				}
			}
		};
	}

	private SelectionAdapter showHashCode() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!tableViewer.getSelection().isEmpty()) {
					IStructuredSelection selection = tableViewer.getStructuredSelection();
					BookVo book = (BookVo) selection.getFirstElement();
					showHashCodeMessageBox(book);
				}
			}
		};
	}

	private void getBooksFromServer() {
		problem="";
		Job job = new Job("GET Job") {
			protected IStatus run(IProgressMonitor monitor) {
				try {
					allBooks = not.another.library.project.Application.bookProvider.getAllBooks();
					return Status.OK_STATUS;
				} catch (IOException exception) {
					return new Status(IStatus.WARNING, "AddView", exception.getMessage(), exception);
				}

			}
		};
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				if (event.getResult().isOK()) {
					problem="OK";
				}
				else {
					problem=event.getResult().getMessage();
					
				}
			}
		});
		//job.setSystem(true);
		job.setPriority(Job.SHORT);
		job.schedule(); // start as soon as possible
		/**
		 * Make the APP GUI do something until the job finishes.
		 */
		waitForResponse();
	}

	private void waitForResponse() {
		int i =0;
		
		while(problem.isEmpty()) {
			lblBunnies.setText("Bunnies:"+i);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				problem=e.getMessage();
				showErrorMessageBox();
			}
			i++;
		}
		if(problem.equals("OK")) {
			bookList.clear();
			bookList.addAll(allBooks);
		}
		else {
			showErrorMessageBox();
		}
	}

	private void showHashCodeMessageBox(BookVo book) {
		MessageBox dialog = 
				new MessageBox(shell, SWT.ICON_SEARCH | SWT.OK);
		dialog.setText("The Books hash code");
		dialog.setMessage(""+book.hashCode());
		dialog.open();
	}

	private void showErrorMessageBox(Exception e) {
		MessageBox dialog = 
				new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		dialog.setText("An Exception has been caught");
		dialog.setMessage(e.getMessage());
		dialog.open();
	}

	private void showErrorMessageBox() {
		MessageBox dialog = 
				new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		dialog.setText("An Exception has been caught");
		dialog.setMessage(problem);
		dialog.open();
	}

	@Override
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}
}
