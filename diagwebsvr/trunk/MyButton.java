/******************************************************************
 * CODE FILE   : MyButton.java
 * Project     : Diagnostic WebServer (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 18-12-2005
 * Beschrijving: Gecustomiseerde versie van klasse Button
 */

import java.awt.*;
import java.awt.event.*;

class MyButton
extends Button
implements MouseListener
{
	private MyFrame parentObject;
	private Color defaultColor;

	MyButton(MyFrame parent, String s, Color c)
	{
		this(parent, s, c, -1, -1, -1, -1);
	}

	MyButton(MyFrame parent, String s, Color c, int w, int h)
	{
		this(parent, s, c, -1, -1, w, h);
	}
	
	MyButton(MyFrame parent, String s, Color c, int x, int y, int w, int h)
	{
		super(s);
		parentObject = parent;
		defaultColor = c;
		setBackground(defaultColor);

		if (x == -1 && y == -1 && w == -1 && h == -1)
			; // Do nothink
		else if (x ==-1 && y == -1)
			setSize(h, w);
		else
			setBounds(x, y, w, h);

		addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e)  {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e)
	{
		if (this.getBackground() == Color.GREEN)
		{
			// Start Server
			parentObject.httpServer = new Server(parentObject.getPort(), 1000, parentObject.getCodeBase());
			Thread serverThread = new Thread(parentObject.httpServer);
			serverThread.start();

			setLabel("STOP");
			setBackground(Color.RED);
		}
		else
		{
			// Stop Server
			parentObject.httpServer.stop();
			setLabel("START");
			setBackground(Color.GREEN);
		}
	}
}
