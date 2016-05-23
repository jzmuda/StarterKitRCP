package not.another.library.views;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

import not.another.library.dialogs.ServerErrorDialog;
import not.another.library.project.data.provider.BookProviderImpl;

public class StartView extends ViewPart {
	public static final String ID = "NotAnotherLibraryProject.startview";
	public static final String OTHER_ID = "NotAnotherLibraryProject.booksview";
	private static IWorkbenchPage page;
	private Label lblLabelurl;
	
	public StartView() {
		page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.VERTICAL));

		initializeTitleLabels(parent);

		Button btnEnter = initializeEnterButton(parent);

		initializeFillerLabels(parent);
		
		btnEnter.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if(not.another.library.project.Application.bookProvider==null)
					initializeBookProvider(parent);
				else
					checkConnection(parent);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if(not.another.library.project.Application.bookProvider==null)
					initializeBookProvider(parent);
				else
					checkConnection(parent);
			}

		});

	}
	
	private void showServerErrorDialog(Composite parent, Exception ex) {
		ServerErrorDialog dialog = new ServerErrorDialog(parent.getShell(), ex.getMessage());
		dialog.open();
		lblLabelurl.setText(not.another.library.project.Application.repoUrl);
	}

	private void switchActiveView() throws PartInitException {
		page.showView(OTHER_ID);
	}
	

	private void checkConnection(Composite parent) {
		try {
			not.another.library.project.Application.bookProvider.checkConnection(not.another.library.project.Application.repoUrl);
			switchActiveView();
		} catch (IOException | PartInitException ex) {
			showServerErrorDialog(parent, ex);
		}
	}

	private void initializeBookProvider(Composite parent) {
		try {
			not.another.library.project.Application.bookProvider=
					new BookProviderImpl(not.another.library.project.Application.repoUrl);
			switchActiveView();
		} catch (IOException | PartInitException ex) {
			showServerErrorDialog(parent, ex);
		}
	}



	private void initializeFillerLabels(Composite parent) {
		Label lblNewLabel = new Label(parent, SWT.NONE);
		lblNewLabel.setBackground(SWTResourceManager.getColor(0, 206, 209));

		Label lblNewLabel_1 = new Label(parent, SWT.NONE);
		lblNewLabel_1.setBackground(SWTResourceManager.getColor(205, 133, 63));

		Label label = new Label(parent, SWT.NONE);
		label.setBackground(SWTResourceManager.getColor(147, 112, 219));
	}

	private Button initializeEnterButton(Composite parent) {
		Button btnEnter = new Button(parent, SWT.NONE);
		btnEnter.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		btnEnter.setToolTipText("Search for books");
		btnEnter.setForeground(SWTResourceManager.getColor(0, 250, 154));
		btnEnter.setText("Enter");
		return btnEnter;
	}

	private void initializeTitleLabels(Composite parent) {
		Label lblWelcomeToAnother = new Label(parent, SWT.SHADOW_IN | SWT.CENTER);
		lblWelcomeToAnother.setAlignment(SWT.CENTER);
		lblWelcomeToAnother.setBackground(SWTResourceManager.getColor(255, 215, 0));
		lblWelcomeToAnother.setFont(SWTResourceManager.getFont("Vijaya", 36, SWT.BOLD | SWT.ITALIC));
		lblWelcomeToAnother.setText("Welcome To Library at");

		lblLabelurl = new Label(parent, SWT.SHADOW_IN | SWT.CENTER);
		lblLabelurl.setBackground(SWTResourceManager.getColor(50, 205, 50));
		lblLabelurl.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		lblLabelurl.setFont(SWTResourceManager.getFont("Times New Roman", 14, SWT.NORMAL));
		lblLabelurl.setText(not.another.library.project.Application.repoUrl);
	}

	@Override
	public void setFocus() {
		lblLabelurl.setFocus();
	}

}
