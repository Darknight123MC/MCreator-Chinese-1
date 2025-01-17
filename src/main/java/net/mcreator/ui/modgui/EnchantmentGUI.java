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

package net.mcreator.ui.modgui;

import net.mcreator.element.types.Enchantment;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.EnchantmentListField;
import net.mcreator.ui.minecraft.MCItemListField;
import net.mcreator.ui.traslatable.AdvancedTranslatableComboBox;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.util.MapUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

public class EnchantmentGUI extends ModElementGUI<Enchantment> {

	private final VTextField name = new VTextField(20);
	private final AdvancedTranslatableComboBox<String> type = new AdvancedTranslatableComboBox<>(MapUtils.createMap(
			"BREAKABLE","耐久类","VANISHABLE","消失类","ARMOR","盔甲","ARMOR_FEET","靴子","ARMOR_LEGS",
			"裤腿","ARMOR_CHEST","胸甲","ARMOR-HEAD","头盔","WEAPON","武器","DIGGER","挖掘类","FISHING_ROD","渔具",
			"TRIDENT","三叉戟","BOW","弓","WEARABLE","可穿戴的","CROSSBOW","弩"),Object::toString);
	private final AdvancedTranslatableComboBox<String> rarity = new AdvancedTranslatableComboBox<>(
			new String[] { "COMMON", "UNCOMMON", "RARE", "VERY_RARE" },new String[]{"普通的","不寻常的","稀有的","史诗的"});

	private final JSpinner minLevel = new JSpinner(new SpinnerNumberModel(1, 0, 64000, 1));
	private final JSpinner maxLevel = new JSpinner(new SpinnerNumberModel(1, 0, 64000, 1));

	private final JSpinner damageModifier = new JSpinner(new SpinnerNumberModel(0, 0, 1024, 1));

	private final JCheckBox isTreasureEnchantment = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox isCurse = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox isAllowedOnBooks = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox canGenerateInLootTables = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox canVillagerTrade = L10N.checkbox("elementgui.common.enable");

	private MCItemListField compatibleItems;
	private EnchantmentListField compatibleEnchantments;

	private final ValidationGroup page1group = new ValidationGroup();

	public EnchantmentGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		rarity.setDisplayEnglish(true);
		compatibleItems = new MCItemListField(mcreator, ElementUtil::loadBlocksAndItems);
		compatibleEnchantments = new EnchantmentListField(mcreator);

		JPanel pane1 = new JPanel(new BorderLayout());

		pane1.setOpaque(false);

		isAllowedOnBooks.setOpaque(false);
		isCurse.setOpaque(false);
		isTreasureEnchantment.setOpaque(false);
		canGenerateInLootTables.setOpaque(false);
		canVillagerTrade.setOpaque(false);

		isAllowedOnBooks.setSelected(true);
		canGenerateInLootTables.setSelected(true);
		canVillagerTrade.setSelected(true);

		ComponentUtils.deriveFont(name, 16);

		JPanel selp = new JPanel(new GridLayout(13, 2, 100, 2));
		selp.setOpaque(false);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/name"),
				L10N.label("elementgui.enchantment.name")));
		selp.add(name);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/type"),
				L10N.label("elementgui.enchantment.type")));
		selp.add(type);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/rarity"),
				L10N.label("elementgui.enchantment.rarity")));
		selp.add(rarity);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/min_level"),
				L10N.label("elementgui.enchantment.min_level")));
		selp.add(minLevel);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/max_level"),
				L10N.label("elementgui.enchantment.max_level")));
		selp.add(maxLevel);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/damage_modifier"),
				L10N.label("elementgui.enchantment.damage_modifier")));
		selp.add(damageModifier);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/treasure_enchantment"),
				L10N.label("elementgui.enchantment.treasure_enchantment")));
		selp.add(isTreasureEnchantment);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/curse"),
				L10N.label("elementgui.enchantment.curse")));
		selp.add(isCurse);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/allowed_on_books"),
				L10N.label("elementgui.enchantment.allowed_on_books")));
		selp.add(isAllowedOnBooks);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/generate_in_loot_tables"),
				L10N.label("elementgui.enchantment.can_generate_in_loot_tables")));
		selp.add(canGenerateInLootTables);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/villager_trade"),
				L10N.label("elementgui.enchantment.can_villager_trade")));
		selp.add(canVillagerTrade);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/compatible_enchantments"),
				L10N.label("elementgui.enchantment.compatible_enchantments")));
		selp.add(compatibleEnchantments);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("enchantment/can_apply_to"),
				L10N.label("elementgui.enchantment.can_apply_to")));
		selp.add(compatibleItems);

		pane1.add(PanelUtils.totalCenterInPanel(selp));

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.enchantment.needs_name")));
		name.enableRealtimeValidation();

		page1group.addValidationElement(name);

		addPage(pane1);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();

		ComboBoxUtil.updateComboBoxContents(type,
				ElementUtil.loadEnchantmentTypes().stream().map(DataListEntry::getName).collect(Collectors.toList()));
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		return new AggregatedValidationResult(page1group);
	}

	@Override public void openInEditingMode(Enchantment enchantment) {
		name.setText(enchantment.name);
		type.setSelectedItem(enchantment.type);
		rarity.setSelectedItem(enchantment.rarity);
		minLevel.setValue(enchantment.minLevel);
		maxLevel.setValue(enchantment.maxLevel);
		damageModifier.setValue(enchantment.damageModifier);
		compatibleEnchantments.setListElements(enchantment.compatibleEnchantments);
		compatibleItems.setListElements(enchantment.compatibleItems);
		isTreasureEnchantment.setSelected(enchantment.isTreasureEnchantment);
		isCurse.setSelected(enchantment.isCurse);
		isAllowedOnBooks.setSelected(enchantment.isAllowedOnBooks);
		canGenerateInLootTables.setSelected(enchantment.canGenerateInLootTables);
		canVillagerTrade.setSelected(enchantment.canVillagerTrade);
	}

	@Override public Enchantment getElementFromGUI() {
		Enchantment enchantment = new Enchantment(modElement);
		enchantment.name = name.getText();
		enchantment.type = (String) type.getSelectedItem();
		enchantment.rarity = (String) rarity.getSelectedItem();
		enchantment.minLevel = (int) minLevel.getValue();
		enchantment.maxLevel = (int) maxLevel.getValue();
		enchantment.damageModifier = (int) damageModifier.getValue();
		enchantment.compatibleEnchantments = compatibleEnchantments.getListElements();
		enchantment.compatibleItems = compatibleItems.getListElements();
		enchantment.isTreasureEnchantment = isTreasureEnchantment.isSelected();
		enchantment.isCurse = isCurse.isSelected();
		enchantment.isAllowedOnBooks = isAllowedOnBooks.isSelected();
		enchantment.canGenerateInLootTables = canGenerateInLootTables.isSelected();
		enchantment.canVillagerTrade = canVillagerTrade.isSelected();
		return enchantment;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-enchantment");
	}

}
