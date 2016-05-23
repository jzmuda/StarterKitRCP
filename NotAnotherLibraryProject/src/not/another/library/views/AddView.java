package not.another.library.views;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import not.another.library.project.data.BookStatusVo;
import not.another.library.project.data.BookVo;


public class AddView extends ViewPart {
	public static final String ID = "NotAnotherLibraryProject.addview";

	private static String[] status ={ "FREE", "LOAN", "MISSING"};

	private BookVo chosenBook;
	private Text txtAuthors;
	private Text txtTitle;
	private CCombo combo;
	private Shell shell;
	private Button btnOk;
	private String problem;
	private Label lblBunnies;


	public AddView() {
		chosenBook=new BookVo();
		chosenBook.setStatus(BookStatusVo.FREE);
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		shell=parent.getShell();
		Label lblAuthors = new Label(parent, SWT.NONE);
		lblAuthors.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAuthors.setText("Authors");

		txtAuthors = new Text(parent, SWT.BORDER);
		txtAuthors.setText("");
		txtAuthors.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblTitle = new Label(parent, SWT.NONE);
		lblTitle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTitle.setText("Title");

		txtTitle = new Text(parent, SWT.BORDER);
		txtTitle.setText("");
		txtTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblStatus = new Label(parent, SWT.NONE);
		lblStatus.setText("Status");

		combo = new CCombo(parent, SWT.BORDER);
		combo.setEditable(false);
		combo.add(status[0]);
		combo.add(status[1]);
		combo.add(status[2]);
		combo.setVisibleItemCount(3);
		combo.select(0);
		addEditListeners();

		btnOk = new Button(parent, SWT.NONE);
		btnOk.setText("OK");
		btnOk.setVisible(false);
		
		lblBunnies = new Label(parent, SWT.NONE);
		lblBunnies.setText("Bunnies");


		btnOk.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				saveChanges();
				
					
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				saveChanges();
			}
		});

	}

	private void addEditListeners() {
		txtAuthors.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				chosenBook.setAuthors(txtAuthors.getText());
				checkOkVisible();
			}


		});
		txtTitle.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				chosenBook.setTitle(txtTitle.getText());
				checkOkVisible();
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

	private void checkOkVisible() {
		btnOk.setVisible(false);
		if(!(txtAuthors.getText().isEmpty() || txtTitle.getText().isEmpty())) {
			btnOk.setVisible(true);
		}			
	}

	@Override
	public void setFocus() {
		txtTitle.setFocus();
	}

	private void saveChanges() {
		problem="";
		Job job = new Job("Add Job") {
			protected IStatus run(IProgressMonitor monitor) {
				try {
					not.another.library.project.Application.bookProvider.addBook(chosenBook);
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
					problem="Problem is : "+event.getResult().getMessage();
				}
			}
		});
		//job.setSystem(true);
		job.setPriority(Job.SHORT);
		job.schedule(); // start as soon as possible
		waitForResponse();
	}

	private void waitForResponse() {
		int i =0;
		while(problem.isEmpty() && ! (problem == null) && i<1000) {
			lblBunnies.setText(""+i);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				problem=e.getMessage();
				showErrorMessageBox();
			}
			i++;
		}
		if(problem.equals("OK")) {
			showSuccessMessageBox();
		}
		else {
			showErrorMessageBox();
		}
	}

	private void showSuccessMessageBox() {
		MessageBox dialog = 
				new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
		dialog.setText("Book has been added!");
		dialog.setMessage(chosenBook.toString());
		dialog.open();
	}

	private void showErrorMessageBox() {
		MessageBox dialog = 
				new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		dialog.setText("An Exception has been caught");
		dialog.setMessage(problem);
		dialog.open();
	}

}
