/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.ui.dialogs.wysiwyg;

import javafx.scene.layout.Pane;
import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.parts.gui.Button;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.wysiwyg.WYSIWYG;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import net.mcreator.util.locale.TranslatorUtils;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Locale;

public class ButtonDialog extends AbstractWYSIWYGDialog {

	public ButtonDialog(WYSIWYGEditor editor, @Nullable Button button) {
		super(editor.mcreator, button);
		setModal(true);
		setTitle(L10N.t("dialog.gui.button_add_title"));
		JTextField nameField = new JTextField(20);
		JTextField tkField = new JTextField(20);
		JPanel options = new JPanel();
		options.setLayout(new BoxLayout(options, BoxLayout.PAGE_AXIS));

		if (button == null)
			add("North", PanelUtils.centerInPanel(L10N.label("dialog.gui.button_change_width")));
		else
			add("North", PanelUtils.centerInPanel(L10N.label("dialog.gui.button_resize")));

		options.add(PanelUtils.join(L10N.label("dialog.gui.button_text"), nameField));
		options.add(PanelUtils.westAndCenterElement(new JLabel("翻译键值: "),tkField));

		ProcedureSelector eh = new ProcedureSelector(IHelpContext.NONE.withEntry("gui/on_button_clicked"),
				editor.mcreator, L10N.t("dialog.gui.button_event_on_clicked"), ProcedureSelector.Side.BOTH, false,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/guistate:map"));
		eh.refreshList();

		ProcedureSelector displayCondition = new ProcedureSelector(
				IHelpContext.NONE.withEntry("gui/button_display_condition"), editor.mcreator,
				L10N.t("dialog.gui.button_display_condition"), ProcedureSelector.Side.BOTH, false,
				VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/guistate:map"));
		displayCondition.refreshList();

		add("Center",
				new JScrollPane(PanelUtils.centerInPanel(PanelUtils.gridElements(1, 2, 5, 5, eh, displayCondition))));

		add("North", PanelUtils.join(FlowLayout.LEFT, options));

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		add("South", PanelUtils.join(ok, cancel));

		getRootPane().setDefaultButton(ok);

		if (button != null) {
			tkField.setText(button.TK);
			ok.setText(L10N.t("dialog.common.save_changes"));
			nameField.setText(button.name);
			eh.setSelectedProcedure(button.onClick);
			displayCondition.setSelectedProcedure(button.displayCondition);
		}

		cancel.addActionListener(arg01 -> setVisible(false));
		ok.addActionListener(arg01 -> {
			setVisible(false);
			String text = nameField.getText();
			if (text != null && !text.equals("")) {
				if (tkField.getText().isEmpty()) tkField.setText("button."+editor.mcreator.getWorkspace().getWorkspaceSettings()
						.getModID()+"."+ TranslatorUtils.translateCNToEN(nameField.getText()).toLowerCase(Locale.ROOT)
						.replaceAll("[^a-zA-Z\\s]","").replaceAll("\\s","_"));
				if (button == null) {
					int textwidth = (int) (WYSIWYG.fontMC.getStringBounds(text, WYSIWYG.frc).getWidth());
					editor.editor.setPositioningMode(textwidth + 25, 20);
					editor.editor.setPositionDefinedListener(e -> editor.editor.addComponent(
							new Button(text, editor.editor.newlyAddedComponentPosX,
									editor.editor.newlyAddedComponentPosY, text,tkField.getText(), editor.editor.ow, editor.editor.oh,
									eh.getSelectedProcedure(), displayCondition.getSelectedProcedure())));
				} else {
					editor.mcreator.getWorkspace().removeLocalizationEntryByKey(button.TK);
					int idx = editor.components.indexOf(button);
					editor.components.remove(button);
					Button buttonNew = new Button(text, button.getX(), button.getY(), text,tkField.getText(), button.width, button.height,
							eh.getSelectedProcedure(), displayCondition.getSelectedProcedure());
					editor.components.add(idx, buttonNew);
					setEditingComponent(buttonNew);
				}
				editor.mcreator.getWorkspace().setLocalization(tkField.getText(),text);
			}
		});

		pack();
		setLocationRelativeTo(editor.mcreator);
		setVisible(true);
	}

}
