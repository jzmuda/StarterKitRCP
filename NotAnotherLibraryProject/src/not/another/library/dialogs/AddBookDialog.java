package not.another.library.dialogs;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import not.another.library.project.data.BookStatusVo;
import not.another.library.project.data.BookVo;


/**
 * Dialog to add books.
 * @author JZMUDA
 *
 */

public class AddBookDialog  extends Dialog {
	private static String[] status ={ "FREE", "LOAN", "MISSING"};

	private BookVo chosenBook;
	private Text txtAuthors;
	private Text txtTitle;
	private CCombo combo;
	private Shell shell;

	public AddBookDialog(Shell parentShell) {
		super(parentShell);
		this.shell=parentShell;
		chosenBook=new BookVo();
		chosenBook.setStatus(BookStatusVo.FREE);
	}


	// overriding this methods allows you to set the
	// title of the custom dialog
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Add book");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		area.setLayout(new GridLayout(2, false));
		new Label(area, SWT.NONE);

		Label lblAuthors = new Label(area, SWT.NONE);
		lblAuthors.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAuthors.setText("Authors");

		txtAuthors = new Text(area, SWT.BORDER);
		txtAuthors.setText("");
		txtAuthors.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblTitle = new Label(area, SWT.NONE);
		lblTitle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTitle.setText("Title");

		txtTitle = new Text(area, SWT.BORDER);
		txtTitle.setText("");
		txtTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblStatus = new Label(area, SWT.NONE);
		lblStatus.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStatus.setText("Status");

		combo = new CCombo(area, SWT.BORDER);
		combo.setEditable(false);
		combo.add(status[0]);
		combo.add(status[1]);
		combo.add(status[2]);
		combo.setVisibleItemCount(3);
		combo.select(0);
		addEditListeners();
		return area;
	}



	private void addEditListeners() {
		txtAuthors.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				chosenBook.setAuthors(txtAuthors.getText());
			}
		});
		txtTitle.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				chosenBook.setTitle(txtTitle.getText());
			}
		});
		combo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				chosenBook.setStatus(BookStatusVo.valueOf(status[combo.getSelectionIndex()]));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				chosenBook.setStatus(BookStatusVo.valueOf(status[combo.getSelectionIndex()]));
			}
		});

	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		saveChanges();
		super.okPressed();
	}
	private void saveChanges() {
		Job job = new Job("Add Job") {
			protected IStatus run(IProgressMonitor monitor) {
				try {
					not.another.library.project.Application.bookProvider.addBook(chosenBook);
					showSuccessMessageBox();
					return Status.OK_STATUS;
				} catch (IOException exception) {
					showErrorMessageBox(exception);
					return Status.CANCEL_STATUS;
				}
				
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule(); // start as soon as possible

	}

	private void showSuccessMessageBox() {
		MessageBox dialog = 
				new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
		dialog.setText("Book has been added!");
		dialog.setMessage(chosenBook.toString());
		dialog.open();
	}

	private void showErrorMessageBox(Exception e) {
		MessageBox dialog = 
				new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		dialog.setText("An Exception has been caught");
		dialog.setMessage(e.getMessage());
		dialog.open();
	}

}
