/*
 * Copyright (c) 2011, Willem Cazander
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *   following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *   the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.nongnu.pulsefire.device.ui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.nongnu.pulsefire.device.io.protocol.CommandName;
import org.nongnu.pulsefire.device.ui.JComponentFactory;
import org.nongnu.pulsefire.device.ui.PulseFireUI;
import org.nongnu.pulsefire.device.ui.PulseFireUISettingKeys;

/**
 * JCommandSettingListDialog displays dialog with 2 lists to select multiple commands.
 * 
 * @author Willem Cazander
 */
public class JCommandSettingListDialog extends JDialog implements ActionListener,KeyListener {
	
	private static final long serialVersionUID = 1129423377219936985L;
	private PulseFireUISettingKeys setting = null;
	private List<CommandName> selectCommands = null;
	private List<CommandName> selectedCommands = null;
	private List<CommandName> defaultCommands = null;
	private JButton defaultButton = null;
	private JButton saveButton = null;
	private JButton cancelButton = null;
	private JList<CommandName> selectList = null;
	private JList<CommandName> selectedList = null;
	private DefaultListModel<CommandName> selectListModel = null;
	private DefaultListModel<CommandName> selectedListModel = null;
	private JButton moveLeft = null;
	private JButton moveRight = null;
	private JButton moveUp = null;
	private JButton moveDown = null;
	
	
	public JCommandSettingListDialog(Frame parentFrame,String title,String text,PulseFireUISettingKeys setting,List<CommandName> commands,List<CommandName> defaultSelected) {
		super(parentFrame, true);
		this.setting=setting;
		this.selectCommands=commands;
		this.defaultCommands=defaultSelected;
		this.selectListModel = new DefaultListModel<CommandName>();
		this.selectedListModel = new DefaultListModel<CommandName>();
		
		String settingValue = PulseFireUI.getInstance().getSettingsManager().getSettingString(setting);
		selectedCommands = CommandName.decodeCommandList(settingValue);
		
		setTitle(title);
		setMinimumSize(new Dimension(500,600));
		setPreferredSize(new Dimension(550,600));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				clearAndHide();
			}
		});
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(createTopPanel(text),BorderLayout.NORTH);
		mainPanel.add(createLeftPanel(),BorderLayout.WEST);
		mainPanel.add(createCenterPanel(),BorderLayout.CENTER);
		mainPanel.add(createRightPanel(),BorderLayout.EAST);
		mainPanel.add(createPanelBottom(),BorderLayout.SOUTH);
		getContentPane().add(mainPanel);
		
		initListModels();
		
		pack();
		setLocationRelativeTo(parentFrame);
	}
	
	private JPanel createTopPanel(String text) {
		JPanel panel = new JPanel();
		panel.add(new JLabel(text));
		return panel;
	}
	
	private JPanel createLeftPanel() {
		JPanel panel = JComponentFactory.createJFirePanel("Select");
		
		selectList = new JList<CommandName>(selectListModel);
		selectList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		selectList.addKeyListener(this);
		
		JScrollPane listScroller = new JScrollPane(selectList);
		listScroller.setPreferredSize(new Dimension(200, 450));
		panel.add(listScroller);
		
		return panel;
	}
	private JPanel createCenterPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		
		moveLeft = new JButton("<");
		moveLeft.addActionListener(this);
		buttonPanel.add(moveLeft,BorderLayout.WEST);
		
		moveRight = new JButton(">");
		moveRight.addActionListener(this);
		buttonPanel.add(moveRight,BorderLayout.EAST);
		
		moveUp = new JButton("Up");
		moveUp.addActionListener(this);
		buttonPanel.add(moveUp,BorderLayout.NORTH);
		
		moveDown = new JButton("Down");
		moveDown.addActionListener(this);
		buttonPanel.add(moveDown,BorderLayout.SOUTH);
		
		JLabel fillLabel = new JLabel();
		fillLabel.setSize(200, 200);
		panel.add(fillLabel);
		panel.add(JComponentFactory.createJPanelJWrap(buttonPanel));
		return panel;
	}
	private JPanel createRightPanel() {
		JPanel panel = JComponentFactory.createJFirePanel("Selected");
		//panel.add(new JLabel("Active"));
		
		selectedListModel = new DefaultListModel<CommandName>();
		selectedList = new JList<CommandName>(selectedListModel);
		selectedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		selectedList.addKeyListener(this);
		
		JScrollPane listScroller = new JScrollPane(selectedList);
		listScroller.setPreferredSize(new Dimension(200, 450));
		panel.add(listScroller);
		
		return panel;
	}
	
	private JPanel createPanelBottom() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder());
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		defaultButton = new JButton("Default");
		defaultButton.addActionListener(this);
		panel.add(defaultButton);
		saveButton = new JButton("Save");
		saveButton.addActionListener(this);
		panel.add(saveButton);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		panel.add(cancelButton);
		return panel;
	}
	
	public void clearAndHide() {
		setVisible(false);
	}
	
	/**
	 * Heavy method need to create ListModel
	 */
	private void initListModels() {
		selectedListModel.removeAllElements();
		selectListModel.removeAllElements();
		for (CommandName cn:selectCommands) {
			if (selectedCommands.contains(cn)==false) {
				selectListModel.addElement(cn);
			}
		}
		
		for (CommandName cn:selectedCommands) {
			selectedListModel.addElement(cn);
		}
		orderSelectListModel();
	}
	
	private void orderSelectListModel() {
		DefaultListModel<CommandName> listModel = selectListModel;
		List<CommandName> orderList = new ArrayList<CommandName>(listModel.size());
		for (int i=0;i<listModel.size();i++) {
			Object o = listModel.get(i);
			orderList.add((CommandName)o);
		}
		listModel.removeAllElements();
		Collections.sort(orderList,new Comparator<CommandName>() {
			@Override
			public int compare(CommandName o1, CommandName o2) {
				return o1.name().compareTo(o2.name());
			}
		});
		for (CommandName cn:orderList) {
			listModel.addElement(cn);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==saveButton) {
			List<CommandName> saveList = new ArrayList<CommandName>(selectedListModel.size());
			for (int i=0;i<selectedListModel.size();i++) {
				Object o = selectedListModel.get(i);
				saveList.add((CommandName)o);
			}
			String value = CommandName.encodeCommandList(saveList);
			PulseFireUI.getInstance().getSettingsManager().setSettingString(setting, value);
			clearAndHide();
			return;
		} else if (e.getSource()==cancelButton) {
			clearAndHide();
			return;
		} else if (e.getSource()==defaultButton) {
			selectedCommands.clear();
			selectedCommands.addAll(defaultCommands);
			initListModels();
			return;
		} else if (e.getSource()==moveLeft) {
			for (CommandName o:selectedList.getSelectedValuesList()) {
				selectedListModel.removeElement(o);
				selectListModel.addElement(o);
			}
			orderSelectListModel();
			return;
		} else if (e.getSource()==moveRight) {
			for (CommandName o:selectList.getSelectedValuesList()) {
				if (selectedListModel.contains(o)) {
					continue;
				}
				selectedListModel.addElement(o);
				selectListModel.removeElement(o);
			}
			return;
		} else if (e.getSource()==moveUp) {
			if (selectedList.getSelectedIndex()>=0 && (selectedList.getSelectedIndex()-1)>=0) {
				CommandName move = selectedListModel.get(selectedList.getSelectedIndex()-1);
				selectedListModel.set(selectedList.getSelectedIndex()-1, selectedList.getSelectedValue());
				selectedListModel.set(selectedList.getSelectedIndex(), move);
				selectedList.setSelectedIndex(selectedList.getSelectedIndex()-1);
			}
		} else if (e.getSource()==moveDown) {
			if (selectedList.getSelectedIndex()>=0 && (selectedList.getSelectedIndex()+1)<selectedListModel.size()) {
				CommandName move = selectedListModel.get(selectedList.getSelectedIndex()+1);
				selectedListModel.set(selectedList.getSelectedIndex()+1, selectedList.getSelectedValue());
				selectedListModel.set(selectedList.getSelectedIndex(), move);
				selectedList.setSelectedIndex(selectedList.getSelectedIndex()+1);
			}
		}
	}
	
	@Override
	public void keyReleased(KeyEvent keyEvent) {
		if (keyEvent.getKeyCode()==KeyEvent.VK_ENTER) {
			if (selectList.hasFocus()) {
				for (CommandName o:selectList.getSelectedValuesList()) {
					selectedListModel.addElement(o);
					selectListModel.removeElement(o);
				}
			}
			if (selectedList.hasFocus()) {
				for (CommandName o:selectedList.getSelectedValuesList()) {
					selectedListModel.removeElement(o);
					selectListModel.addElement(o);
				}
				orderSelectListModel();
			}
		}
	}
	
	@Override
	public void keyPressed(KeyEvent keyEvent) {
	}
	
	@Override
	public void keyTyped(KeyEvent keyEvent) {
	}
}
