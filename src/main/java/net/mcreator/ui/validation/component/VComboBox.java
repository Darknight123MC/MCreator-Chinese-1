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

package net.mcreator.ui.validation.component;

import net.mcreator.ui.traslatable.TranslatablePool;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.Validator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class VComboBox<T> extends JComboBox<T> implements IValidable {

	//validation code
	private Validator validator = null;

	public VComboBox() {
		setRenderer(new TranslatableRender<>());
	}

	public VComboBox(T[] items) {
		super(items);
		setRenderer(new TranslatableRender<>());
	}

	public void enableRealtimeValidation() {
		getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				getValidationStatus();
			}
		});
	}

	// Hack to make return value typed
	@Override @SuppressWarnings("unchecked") public T getSelectedItem() {
		return (T) super.getSelectedItem();
	}

	@Override public Validator.ValidationResult getValidationStatus() {
		Validator.ValidationResult validationResult = validator == null ? null : validator.validateIfEnabled(this);

		if (validator != null && validationResult != null) {
			if (validationResult.getValidationResultType() == Validator.ValidationResultType.WARNING) {
				setBorder(BorderFactory.createLineBorder(new Color(238, 229, 113), 1));
			} else if (validationResult.getValidationResultType() == Validator.ValidationResultType.ERROR) {
				setBorder(BorderFactory.createLineBorder(new Color(204, 108, 108), 1));
			} else {
				setBorder(BorderFactory.createLineBorder(new Color(79, 192, 121), 1));
			}
		}

		return validationResult;
	}

	@Override public void setValidator(Validator validator) {
		this.validator = validator;
	}

	@Override public Validator getValidator() {
		return validator;
	}

	//汉化用的渲染器
	public static class TranslatableRender<T> extends DefaultListCellRenderer{
		public TranslatableRender(){
			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
		}

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
			if (value == null){
				return this;
			}
			TranslatablePool pool = TranslatablePool.getPool();
			setText(pool.getValue(value.toString())+"("+value+")".trim());
			return this;
		}
	}
}
