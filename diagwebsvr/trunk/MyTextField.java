/******************************************************************
 * CODE FILE   : MyTextField.java
 * Project     : Diagnostic WebServer (H7NPR1)
 * Auteur(s)   : Erwin Beukhof  (1149712)
 *               Stephen Maij   (1145244)
 * Datum       : 13-09-2005
 * Beschrijving: Gecustomiseerde versie van klasse TextField
 */

import java.awt.TextField;

class MyTextField
extends TextField
{
	MyTextField(String s, int x, int y, int w, int h)
	{
		super(s);
		setBounds(x, y, w, h);
	}
}
