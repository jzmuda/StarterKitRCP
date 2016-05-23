package not.another.library.dialogs;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import not.another.library.project.data.BookVo;


public class DeleteBookDialog   extends TitleAreaDialog {
	private BookVo book;
	private Shell shell;
	private String problem="";
	
	public DeleteBookDialog(Shell parentShell, BookVo book) {
		super(parentShell);
		this.book=book;
		this.shell=parentShell;
		problem="";
	}
	@Override
	public void create() {
		super.create();
		setTitle("Delete Book");
		setMessage("Are you sure you want to delete"+book.toString(), IMessageProvider.INFORMATION);
	}
	
	@Override
	protected void okPressed() {
		saveChanges();
		super.okPressed();
	}
	private void saveChanges() {
		problem="";
		Job job = new Job("Add Job") {
			protected IStatus run(IProgressMonitor monitor) {
				try {
					not.another.library.project.Application.bookProvider.deleteBook(book.getId());
					return Status.OK_STATUS;
				} catch (IOException exception) {
					return new Status(IStatus.WARNING, "AddView", exception.getMessage(), exception);
				}

			}
		};
		job.addJobChangeListener(new JobChangeAdapter() {
			public void done(IJobChangeEvent event) {
				if (event.getResult().isOK())
					problem="OK";
				else
					problem="Problem is : "+event.getResult().getMessage();
			}
		});
		//job.setSystem(true);
		job.setPriority(Job.SHORT);
		job.schedule(); // start as soon as possible
		int i =0;
		while(problem.isEmpty()) {
			setTitle("Bunnies: "+i);
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
		dialog.setText("Book has been deleted!");
		dialog.setMessage(book.toString());
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
