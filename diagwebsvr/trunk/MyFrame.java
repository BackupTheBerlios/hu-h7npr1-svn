/******************************************************************
 * CODE FILE   : MyFrame.java
 * Project     : Diagnostic WebServer (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 10-10-2005
 * Beschrijving: Gecustomiseerde versie van klasse Frame
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;

class MyFrame
extends Frame
implements WindowListener
{
	private TextField portField;
	private TextField codeBaseField;
	private TextArea ioTextArea;
	public Server httpServer;

	MyFrame(String title)
	{
		setTitle(title);
		setLayout(new BorderLayout());
		addWindowListener(this);

		Panel panelTop = new Panel();
		panelTop.setLayout(new BorderLayout());
		add("North", panelTop);

		Panel panelTopLeft = new Panel();
		panelTopLeft.add(new Label("port :"));
		portField = new TextField("4711");
		panelTopLeft.add(portField);
		panelTopLeft.add(new Label("  codebase :"));
		panelTop.add("West", panelTopLeft);
		
		Panel panelTopCenter = new Panel();
		panelTopCenter.setLayout(new FlowLayout(FlowLayout.LEFT));
		codeBaseField = new TextField(MyFileSystem.getCurrentDir() + File.separator + "www");
                
		panelTopCenter.add(codeBaseField);
		panelTop.add("Center", panelTopCenter);
		
		Panel panelTopRight = new Panel();
		MyButton controlButton = new MyButton(this, "START", Color.GREEN);
		panelTopRight.add(controlButton);
		panelTop.add("East", panelTopRight);

		ioTextArea = new TextArea("Diagnostic WebServer ver. bm_1.0a\n\n");
		add("Center", ioTextArea);
		ioTextArea.setRows(100);

		System.setOut(new PrintStream(new MyOutputStream(this)));

		pack();
		setSize(getWidth(), 500);
		setVisible(true);
	}

	public int getPort()
	{
		try
		{
			return Integer.parseInt(portField.getText());
		}
		catch (NumberFormatException e)
		{
			return -1;
		}
	}
	
	public String getCodeBase()
	{
		return codeBaseField.getText();
	}

	public synchronized void addText(String text)
	{
		ioTextArea.append(text);
		// Workaround for lack of auto-scroll in Linux
		ioTextArea.setCaretPosition(ioTextArea.getText().length());
		// Limit #rows in textArea content
		//if (ioTextArea.getText().replaceAll("\n","\n\n").length() > ioTextArea.getText().length() + 100)
		//	ioTextArea.replaceRange("", 0, ioTextArea.getText().indexOf('\n') + 1);
	}

	// WindowEvents
	public void windowClosing(WindowEvent we)
	{
	  System.exit(0);
	}
	public void windowClosed(WindowEvent we) {}
	public void windowOpened(WindowEvent we) {}
	public void windowIconified(WindowEvent we) {}
	public void windowDeiconified(WindowEvent we) {}
	public void windowActivated(WindowEvent we) {}
	public void windowDeactivated(WindowEvent we) {}
}
