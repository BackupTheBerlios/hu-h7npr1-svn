/******************************************************************
 * CODE FILE   : CijferlijstApplet.java
 * Project     : RMI (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 19-01-2005
 * Beschrijving: Class CijferlijstApplet - main client applet
 */
package client;
//import com.jgoodies.forms.layout.CellConstraints;
//import com.jgoodies.forms.layout.FormLayout;

import interfaces.CijferlijstService;

import java.applet.AppletStub;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.rmi.Naming;
import java.util.Enumeration;
import java.util.Hashtable;
import java.lang.reflect.Array;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import server.CijferlijstData;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class CijferlijstApplet
extends javax.swing.JApplet
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
			}
			{
				gradeField = new JTextField();
				getContentPane().add(gradeField);
				gradeField.setBounds(532, 35, 63, 28);
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
			//String host = codebase.getHost();
			String host = "192.168.0.93";
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
		}
		catch (Exception e)
		{
			log(e.toString());
		}
	}
	
	private void newButtonMouseClicked(MouseEvent evt)
	{
		studentTable.changeSelection(99999,99999,false,false);
		clearFields();
		nameField.setCaretPosition(0);
	}
	
	private void deleteButtonMouseClicked(MouseEvent evt)
	{
		try
		{
			//DELETE SHOWN ENTRY
			cls.deleteOne(cijferID);
			clearFields();
			fillStudentTable(cls.getList());
		}
		catch (Exception e)
		{
			log(e.toString());
		}
	}

	private void cancelButtonMouseClicked(MouseEvent evt)
	{
		studentTable.changeSelection(99999,99999,false,false);
		clearFields();
	}
	
	private void saveButtonMouseClicked(MouseEvent evt)
	{
		try
		{
			//INSERT OR UPDATE SHOWN ENTRY
			cls.setOne(cijferID, nameField.getText(), Double.parseDouble(gradeField.getText()));
			clearFields();
			fillStudentTable(cls.getList());
		}
		catch (Exception e)
		{
			log(e.toString());
		}
	}
}
