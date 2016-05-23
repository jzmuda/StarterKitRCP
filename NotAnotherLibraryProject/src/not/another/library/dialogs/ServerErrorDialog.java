package not.another.library.dialogs;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class ServerErrorDialog extends TitleAreaDialog {

	  private Text txtURL;
	  private String errorMessage;

	  public ServerErrorDialog(Shell parentShell, String message) {
	    super(parentShell);
	    errorMessage= message;
	  }

	  @Override
	  public void create() {
	    super.create();
	    setTitle("Oops, it seems the server is not responding correctly");
	    setMessage("Please make sure the server is running or provide another URL", IMessageProvider.INFORMATION);
	  }

	  @Override
	  protected Control createDialogArea(Composite parent) {
	    Composite area = (Composite) super.createDialogArea(parent);
	    Composite container = new Composite(area, SWT.NONE);
	    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    GridLayout layout = new GridLayout(2, false);
	    container.setLayout(layout);
	    createMessage(container);
	    createURL(container);
	    
	    return area;
	  }
	  private void createMessage(Composite container) {
		    Label lbtWhat = new Label(container, SWT.NONE);
		    lbtWhat.setForeground(SWTResourceManager.getColor(200, 30, 30));
		    lbtWhat.setText("Error message:");
		    Label lbtError = new Label(container, SWT.BORDER);
		    lbtError.setForeground(SWTResourceManager.getColor(200, 30, 30));
		    lbtError.setText(errorMessage);
		  }
	  private void createURL(Composite container) {
	    Label lbtURL = new Label(container, SWT.NONE);
	    lbtURL.setText("Server URL");
	    GridData dataURL = new GridData();
	    dataURL.grabExcessHorizontalSpace = true;
	    dataURL.horizontalAlignment = GridData.FILL;
	    txtURL = new Text(container, SWT.BORDER);
	    txtURL.setLayoutData(dataURL);
	    txtURL.setText(not.another.library.project.Application.repoUrl);
	  }
	  
	 
	  @Override
	  protected boolean isResizable() {
	    return true;
	  }

	  // save content of the Text fields because they get disposed
	  // as soon as the Dialog closes
	  private void saveInput() {
		  not.another.library.project.Application.repoUrl=txtURL.getText();
	  }

	  @Override
	  protected void okPressed() {
	    saveInput();
	    super.okPressed();
	  }
}