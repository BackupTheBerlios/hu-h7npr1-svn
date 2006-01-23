/******************************************************************
 * CODE FILE   : CijferlijstApplet.java
 * Project     : RMI (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 19-01-2006
 * Beschrijving: Class CijferlijstApplet - main client applet
 */
package client;

import interfaces.CijferlijstService;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.rmi.Naming;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class CijferlijstApplet
extends JApplet
{
	private JTable studentTable;
	private TableModel studentTableModel;
	private JTextArea logArea;
	private JScrollPane logScrollPane;
	private JScrollPane studentScrollPane;
	private JLabel logLabel;
	private JButton saveButton;
	private JButton cancelButton;
	private JButton deleteButton;
	private JButton newButton;
	private JLabel nameLabel;
	private JLabel gradeLabel;
	private JTextField gradeField;
	private JTextField nameField;

	private CijferlijstService cls = null;
	private int cijferID = 0;

	public CijferlijstApplet()
	{
		super();
	}
	
	public void init()
	{
		initGUI();
		initData();
		log("Applet started");
	}

	private void initGUI() {
		try {
			getContentPane().setLayout(null);
			this.setSize(602, 287);
			{
				nameField = new JTextField();
				getContentPane().add(nameField);
				nameField.setBounds(266, 35, 259, 28);
				nameField.setEnabled(false);
			}
			{
				gradeField = new JTextField();
				getContentPane().add(gradeField);
				gradeField.setBounds(532, 35, 63, 28);
				gradeField.setEnabled(false);
			}
			{
				nameLabel = new JLabel();
				getContentPane().add(nameLabel);
				nameLabel.setText("Name:");
				nameLabel.setBounds(266, 7, 63, 28);
			}
			{
				gradeLabel = new JLabel();
				getContentPane().add(gradeLabel);
				gradeLabel.setText("Grade:");
				gradeLabel.setBounds(532, 7, 63, 28);
			}
			{
				newButton = new JButton();
				getContentPane().add(newButton);
				newButton.setText("New");
				newButton.setBounds(266, 133, 77, 28);
				newButton.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent evt) {
						newButtonMouseClicked(evt);
					}
				});
			}
			{
				deleteButton = new JButton();
				getContentPane().add(deleteButton);
				deleteButton.setText("Delete");
				deleteButton.setBounds(350, 133, 77, 28);
				deleteButton.setEnabled(false);
				deleteButton.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent evt) {
						deleteButtonMouseClicked(evt);
					}
				});
			}
			{
				cancelButton = new JButton();
				getContentPane().add(cancelButton);
				cancelButton.setText("Cancel");
				cancelButton.setBounds(434, 133, 77, 28);
				cancelButton.setEnabled(false);
				cancelButton.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent evt) {
						cancelButtonMouseClicked(evt);
					}
				});
			}
			{
				saveButton = new JButton();
				getContentPane().add(saveButton);
				saveButton.setText("Save");
				saveButton.setBounds(518, 133, 77, 28);
				saveButton.setEnabled(false);
				saveButton.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent evt) {
						saveButtonMouseClicked(evt);
					}
				});
			}
			{
				logLabel = new JLabel();
				getContentPane().add(logLabel);
				logLabel.setText("log:");
				logLabel.setBounds(7, 161, 63, 28);
			}
			{
				studentScrollPane = new JScrollPane();
				{
					studentTableModel = new DefaultTableModel(
						new String[][] {},
						new String[] { "id", "Name", "Grade" });
					studentTable = new JTable();
					studentScrollPane.setViewportView(studentTable);
					studentTable.setModel(studentTableModel);
					studentTable.setBounds(336, 77, 252, 154);
					studentTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					studentTable.getColumnModel().getColumn(0).setPreferredWidth(40);
					studentTable.getColumnModel().getColumn(1).setPreferredWidth(150);
					studentTable.getColumnModel().getColumn(2).setPreferredWidth(45);
					studentTable.addMouseListener(new MouseAdapter() 
					{
						public void mouseClicked(MouseEvent evt)
						{
							studentTableMouseClicked(evt);
						}
					});
				}
				getContentPane().add(studentScrollPane);
				studentScrollPane.setBounds(7, 7, 252, 154);
				studentScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				studentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			}
			{
				logScrollPane = new JScrollPane();
				{
					logArea = new JTextArea();
					logScrollPane.setViewportView(logArea);
					logArea.setBounds(7, 91, 588, 56);
				}
				getContentPane().add(logScrollPane);
				logScrollPane.setBounds(7, 189, 588, 91);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initData()
	{
		try
		{
			log("Getting codebase...");
			URL codebase = getCodeBase();
			log("codebase: " + codebase.toString());
			String host = codebase.getHost();
			//String host = "127.0.0.1";
			String service = "rmi://" + host + ":1099/" + CijferlijstService.refName;
			cls = (CijferlijstService)Naming.lookup(service);
			log("Service looked up");
			fillStudentTable(cls.getList());
		}
		catch (Exception e)
		{
			log(e.toString());
		}
	}
	/****************************************************
	 * (Re-) fills the studentTable with the supplied
	 * data (2-dimensional String array)
	 */
	private void fillStudentTable(String[][] contents)
	{
		studentTableModel = new DefaultTableModel(
				contents,
				new String[] { "id", "Name", "Grade" });
		studentTable.setModel(studentTableModel);
		// Set column width
		studentTable.getColumnModel().getColumn(0).setPreferredWidth(40);
		studentTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		studentTable.getColumnModel().getColumn(2).setPreferredWidth(45);
	}

	private void fillFields(String[] inValues)
	{
		/**************************************************
		 * Fill name and grade fields with parameter values
		 */
		if (inValues != null && inValues.length == 3)
		{
			nameField.setText(inValues[1]);
			gradeField.setText(inValues[2]);
		}
		else
			clearFields();
	}

	private void clearFields()
	{
		cijferID = 0;
		nameField.setText("");
		gradeField.setText("");
	}

	private void log(String logText)
	{
		logArea.append(logText + '\n');
		// Workaround for lack of auto-scroll in Linux
		logArea.setCaretPosition(logArea.getText().length()-1);
	}
	
	/****************************************************
	 * Clicking a row in the studentTable, retrieves the
	 * data from the database and puts it into the
	 * textfields (nameField & gradeField)
	 * The parameter cijferID gets to contain the current
	 * id from the selected row
	 */
	private void studentTableMouseClicked(MouseEvent evt)
	{
		int row = studentTable.getSelectedRow();
		cijferID = Integer.parseInt(studentTable.getValueAt(row, 0).toString());
		try
		{
			fillFields(cls.getOne(cijferID));
			setControls("TBLCLICKED");
		}
		catch (Exception e)
		{
			log(e.toString());
		}
	}
	
	private void newButtonMouseClicked(MouseEvent evt)
	{
		// Deselect rows (if 1 or more exist)
		if (studentTable.getRowCount() > 0)
			studentTable.removeRowSelectionInterval(0, studentTable.getRowCount()-1);
		setControls("NEW");
	}
	
	private void deleteButtonMouseClicked(MouseEvent evt)
	{
		try
		{
			//DELETE SHOWN ENTRY
			cls.deleteOne(cijferID);
			setControls("DELETED");
			fillStudentTable(cls.getList());
		}
		catch (Exception e)
		{
			log(e.toString());
		}
	}

	private void cancelButtonMouseClicked(MouseEvent evt)
	{
		// Deselect rows (if 1 or more exist)
		//if (studentTable.getRowCount() > 0)
		//	studentTable.removeRowSelectionInterval(0, studentTable.getRowCount()-1);
		try
		{
			setControls("CANCELLED");
			// Refresh table (so that changes by other users are reflected in the table)
			fillStudentTable(cls.getList());
		}
		catch (Exception e)
		{
			log(e.toString());
		}
	}
	
	private void saveButtonMouseClicked(MouseEvent evt)
	{
		String name = nameField.getText();
		// Workaround for invalid entries in gradeField
		double grade = 0;
		try
		{
			grade = Double.parseDouble(gradeField.getText());
		}
		catch (NumberFormatException nfe){}

		// Check if name is filled in
		if (!name.equals(""))
		{
			try
			{
				//INSERT OR UPDATE SHOWN ENTRY
				cls.setOne(cijferID, name, grade);
				setControls("SAVED");
				fillStudentTable(cls.getList());
			}
			catch (Exception e)
			{
				log(e.toString());
			}
		}
		else
			nameField.requestFocus();
	}
	
	private void setControls(String actionPerformed)
	{
		boolean ap_tblclicked = actionPerformed.equalsIgnoreCase("TBLCLICKED");
		boolean ap_new        = actionPerformed.equalsIgnoreCase("NEW");
		boolean ap_deleted    = actionPerformed.equalsIgnoreCase("DELETED");
		boolean ap_cancelled  = actionPerformed.equalsIgnoreCase("CANCELLED");
		boolean ap_saved      = actionPerformed.equalsIgnoreCase("SAVED");

		if (ap_new || ap_deleted || ap_cancelled || ap_saved)
			clearFields();

		newButton.setEnabled(ap_deleted || ap_cancelled || ap_saved);
		deleteButton.setEnabled(ap_tblclicked);
		cancelButton.setEnabled(ap_new || ap_tblclicked);
		saveButton.setEnabled(ap_new || ap_tblclicked);
		nameField.setEnabled(ap_new || ap_tblclicked);
		gradeField.setEnabled(ap_new || ap_tblclicked);
		nameField.requestFocus(ap_new || ap_tblclicked);
	}
}
