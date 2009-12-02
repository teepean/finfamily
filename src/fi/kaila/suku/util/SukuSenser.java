package fi.kaila.suku.util;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fi.kaila.suku.util.SukuTextField.Field;

/**
 * @author Kalle A singleton class that displays an intellisens window below a
 *         SukuTextField
 */
public class SukuSenser implements ListSelectionListener {

	private JWindow sens;
	private JScrollPane scroller;
	private JList lista;
	private Vector<String> model;

	private SukuSenser() {
		sens = new JWindow();
		model = new Vector<String>();

		lista = new JList(model);
		scroller = new JScrollPane(lista);
		lista.addListSelectionListener(this);
		sens.add(scroller, BorderLayout.CENTER);
	}

	private static SukuSenser single = null;

	// private String tag = null;
	// private SukuTextField.Field fld = Field.Fld_Null;
	// private SukuTextField parent;
	private String[] paikat = { "Helsinki", "Espoo", "Tampere", "Porvoo",
			"Bromarf", "Hangö", "Tyrvää", "Tammela", "Peuramaa", "Borgå",
			"Heinola", "Hämeenlinna" };

	/**
	 * @param tag
	 * @param fld
	 * @param parent
	 * @return the handle
	 */
	public static SukuSenser getInstance() {
		if (single == null) {
			single = new SukuSenser();
		}

		single.sens.setVisible(false);

		return single;
	}

	private SukuTextField parent;

	/**
	 * Show intellisens
	 * 
	 * @param txt
	 */
	public void showSens(SukuTextField parent, String tag, Field fld) {
		Rectangle rt = parent.getBounds();
		Point pt = new Point(rt.x, rt.y);
		SwingUtilities.convertPointToScreen(pt, parent.getParent());
		Rectangle rs = new Rectangle();
		rs.x = pt.x;
		rs.y = pt.y + rt.height;
		rs.width = rt.width;
		rs.height = 100;
		this.parent = parent;
		sens.setBounds(rs);
		String txt = parent.getText();
		model.removeAllElements();
		for (int i = 0; i < paikat.length; i++) {
			if (paikat[i].toLowerCase().startsWith(txt.toLowerCase())) {
				model.add(paikat[i]);
			}
		}
		lista.updateUI();
		sens.setVisible(model.size() > 0);
	}

	/**
	 * return top element from list
	 * 
	 * @param parent
	 */
	public void getSens(SukuTextField parent) {
		if (model.size() > 0) {
			parent.setText(model.get(0));
			model.removeAllElements();
		}
		sens.setVisible(false);
	}

	/**
	 * 
	 * 
	 * @param value
	 */
	public void hide() {
		sens.setVisible(false);
	}

	@Override
	public void valueChanged(ListSelectionEvent l) {
		int indexi = lista.getSelectedIndex();
		if (indexi >= 0 && indexi < model.size()) {
			String aux = (String) lista.getSelectedValue();
			if (parent != null) {
				parent.setText(aux);
			}
			sens.setVisible(false);
			parent = null;
		}
	}

}
