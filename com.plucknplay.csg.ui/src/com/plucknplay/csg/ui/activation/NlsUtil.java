/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.activation;

import org.eclipse.core.runtime.Platform;

public final class NlsUtil {

	/* --- action related strings --- */

	private static String Action_menu_new = "New"; //$NON-NLS-1$
	private static String Action_global_add_chord = "&Chord"; //$NON-NLS-1$
	private static String Action_global_add_scale = "&Scale"; //$NON-NLS-1$
	private static String Action_add_category = "Add category"; //$NON-NLS-1$
	private static String Action_add_chord = "Add chord"; //$NON-NLS-1$
	private static String Action_add_scale = "Add scale"; //$NON-NLS-1$
	private static String Action_new_category = "Category"; //$NON-NLS-1$
	private static String Action_new_chord = "Chord"; //$NON-NLS-1$
	private static String Action_new_scale = "Scale"; //$NON-NLS-1$
	private static String Action_cut = "Cut"; //$NON-NLS-1$
	private static String Action_duplicate = "Duplicate"; //$NON-NLS-1$
	private static String Action_paste = "Paste"; //$NON-NLS-1$
	private static String Action_rename = "Rename"; //$NON-NLS-1$
	private static String Action_import = "Import..."; //$NON-NLS-1$
	private static String Action_export = "Export..."; //$NON-NLS-1$
	private static String Action_remove = "Remove"; //$NON-NLS-1$
	private static String Action_search_mode = "Edit/Search Mode"; //$NON-NLS-1$

	private static String Action_menu_new_de = "Neu"; //$NON-NLS-1$
	private static String Action_global_add_chord_de = "&Akkord"; //$NON-NLS-1$
	private static String Action_global_add_scale_de = "&Skala"; //$NON-NLS-1$
	private static String Action_add_category_de = "F\u00fcge Kategorie hinzu"; //$NON-NLS-1$
	private static String Action_add_chord_de = "F\u00fcge Akkord hinzu"; //$NON-NLS-1$
	private static String Action_add_scale_de = "F\u00fcge Skala hinzu"; //$NON-NLS-1$
	private static String Action_new_category_de = "Kategorie"; //$NON-NLS-1$
	private static String Action_new_chord_de = "Akkord"; //$NON-NLS-1$
	private static String Action_new_scale_de = "Skala"; //$NON-NLS-1$
	private static String Action_cut_de = "Ausschneiden"; //$NON-NLS-1$
	private static String Action_duplicate_de = "Duplizieren"; //$NON-NLS-1$
	private static String Action_paste_de = "Einf\u00fcgen"; //$NON-NLS-1$
	private static String Action_rename_de = "Umbenennen"; //$NON-NLS-1$
	private static String Action_import_de = "Importieren..."; //$NON-NLS-1$
	private static String Action_export_de = "Exportieren..."; //$NON-NLS-1$
	private static String Action_remove_de = "L\u00f6schen"; //$NON-NLS-1$
	private static String Action_search_mode_de = "Editier-/Suchmodus"; //$NON-NLS-1$

	private static String Action_image_copy = "icons/copy_edit.gif"; //$NON-NLS-1$
	private static String Action_image_cut = "icons/cut_edit.gif"; //$NON-NLS-1$
	private static String Action_image_paste = "icons/paste_edit.gif"; //$NON-NLS-1$
	private static String Action_image_import = "icons/import.gif"; //$NON-NLS-1$
	private static String Action_image_export = "icons/export.gif"; //$NON-NLS-1$
	private static String Action_image_new_category = "icons/new_category.gif"; //$NON-NLS-1$
	private static String Action_image_new_chord = "$nl$/icons/new_chord.gif"; //$NON-NLS-1$
	private static String Action_image_new_scale = "icons/new_scale.gif"; //$NON-NLS-1$
	private static String Action_image_remove = "icons/remove.gif"; //$NON-NLS-1$
	private static String Action_image_search_mode = "icons/search.gif"; //$NON-NLS-1$

	/* --- activation related strings --- */

	private static String Activation_dialog_image = "logos/logo_64.png"; //$NON-NLS-1$
	private static String Activation_warning_image = "icons/warning.gif"; //$NON-NLS-1$
	private static String Activation_error_image = "icons/error.gif"; //$NON-NLS-1$

	private static String ActivateAction_activate_full_version = "Activate &Full Version..."; //$NON-NLS-1$
	private static String ActivateAction_activate_full_version_de = "&Aktiviere Vollversion..."; //$NON-NLS-1$

	private static String Activation_activate_error_msg = "There was a problem while trying to activate the full version.\nPlease check your internet connection and try again."; //$NON-NLS-1$
	private static String Activation_activate_invalid_key_msg = "Invalid activation key! Please try again."; //$NON-NLS-1$
	private static String Activation_activate_msg = "You are allowed to use the test version of the Chord Scale Generator\nas long as you want. To activate the full version please enter the\nactivation key:"; //$NON-NLS-1$
	private static String Activation_activate_note = "Note: The activation requires internet access for a short time."; //$NON-NLS-1$
	private static String Activation_activate_title = "Activate Full Version"; //$NON-NLS-1$
	private static String Activation_activate_url = "http://activate.pluck-n-play.com/activate.php"; //$NON-NLS-1$
	private static String Activation_buy_msg = "If you do not own an activation key yet you can purchase it\non our website:"; //$NON-NLS-1$
	private static String Activation_buy_url = "http://www.pluck-n-play.com/en/buy.html"; //$NON-NLS-1$
	private static String Activation_contact_url = "http://www.pluck-n-play.com/en/reactivation.php"; //$NON-NLS-1$
	private static String Activation_do_not_prompt = "Do not prompt on startup in the future"; //$NON-NLS-1$
	private static String Activation_expired_msg_1 = "Your activation key has expired!"; //$NON-NLS-1$
	private static String Activation_expired_msg_2 = "To prevent the distribution of pirated copies of the Chord Scale\nGenerator the number of activations is limited. You have already\nexceeded the maximum number of possible activations and your\nactivation key has expired. Please fill out the form on our website\nthat is linked below so that we can send you a new activation key."; //$NON-NLS-1$
	private static String Activation_expired_title = "Expired Activation Key"; //$NON-NLS-1$
	private static String Activation_later = "Later"; //$NON-NLS-1$
	private static String Activation_successful_msg = "The full version of the Chord Scale Generator was successfully activated."; //$NON-NLS-1$
	private static String Activation_successful_title = "Successful Activation"; //$NON-NLS-1$
	private static String Activation_unsupported_feature_msg = "The test version of the Chord Scale Generator does not support this feature.\nIn order to perform this feature you first have to activate the full version.\nTo do so, use the corresponding entry in the help menu."; //$NON-NLS-1$
	private static String Activation_unsupported_feature_title = "Unsupported Feature"; //$NON-NLS-1$

	private static String Activation_activate_error_msg_de = "Es gab ein Problem beim Versuch die Vollversion zu aktivieren.\nBitte \u00fcberpr\u00fcfe deinen Internetzugang und versuche es erneut."; //$NON-NLS-1$
	private static String Activation_activate_invalid_key_msg_de = "Ung\u00fcltiger Aktivierungsschl\u00fcssel! Bitte versuche es erneut."; //$NON-NLS-1$
	private static String Activation_activate_msg_de = "Den Chord Scale Generator kannst du solange du m\u00f6chtest als Test-Version\nausprobieren. Um die Vollversion zu aktivieren, gib bitte den Aktivierungs-\nschl\u00fcssel ein:"; //$NON-NLS-1$
	private static String Activation_activate_note_de = "Hinweis: Die Aktivierung ben\u00f6tigt kurzzeitig einen Internetzugang."; //$NON-NLS-1$
	private static String Activation_activate_title_de = "Aktiviere Vollversion"; //$NON-NLS-1$
	private static String Activation_buy_msg_de = "Solltest du noch keinen Aktivierungsschl\u00fcssel besitzen, kannst du diesen auf\nunserer Website erwerben:"; //$NON-NLS-1$
	private static String Activation_buy_url_de = "http://www.pluck-n-play.com/de/kauf.html"; //$NON-NLS-1$
	private static String Activation_contact_url_de = "http://www.pluck-n-play.com/de/reactivation.php"; //$NON-NLS-1$
	private static String Activation_do_not_prompt_de = "Unterdr\u00fccke diesen Dialog in Zukunft beim Start"; //$NON-NLS-1$
	private static String Activation_expired_msg_1_de = "Dein Aktivierungsschl\u00fcssel ist abgelaufen!"; //$NON-NLS-1$
	private static String Activation_expired_msg_2_de = "Um illegalen Raubkopien vorzubeugen, ist die Anzahl der Aktivierungen\nbeim Chord Scale Generator beschr\u00e4nkt. Du hast die Anzahl der m\u00f6glichen\nAktivierungen bereits \u00fcberschritten, sodass dein Aktivierungsschl\u00fcssel\nung\u00fcltig geworden ist. Bitte f\u00fclle das verlinkte Formular auf unserer\nWebsite aus, damit wir dir einen neuen Aktivierungsschl\u00fcssel zukommen\nlassen k\u00f6nnen."; //$NON-NLS-1$
	private static String Activation_expired_title_de = "Abgelaufener Aktivierungsschl\u00fcssel"; //$NON-NLS-1$
	private static String Activation_later_de = "Sp\u00e4ter"; //$NON-NLS-1$
	private static String Activation_successful_msg_de = "Die Vollversion des Chord Scale Generators wurde erfolgreich aktiviert."; //$NON-NLS-1$
	private static String Activation_successful_title_de = "Erfolgreiche Aktivierung"; //$NON-NLS-1$
	private static String Activation_unsupported_feature_msg_de = "Diese Funktion wird in der Test-Version des Chord Scale Generators nicht unterst\u00fctzt.\nUm diese Funktion auszuf\u00fchren, musst du die Vollversion \u00fcber den entsprechenden Eintrag\nim Hilfe-Men\u00fc aktivieren."; //$NON-NLS-1$
	private static String Activation_unsupported_feature_title_de = "Nicht unterst\u00fctzte Funktion"; //$NON-NLS-1$

	/* --- information related strings --- */
	private static String Information_Title = "Information"; //$NON-NLS-1$
	private static String Information_Prompt = "Do not show this information in the future"; //$NON-NLS-1$

	private static String Information_Prompt_de = "Zeige diese Information zuk\u00fcnftig nicht mehr an"; //$NON-NLS-1$

	private static String StatusLineWarning_Reduced_Chords_List = "Please note, the test version only offers a reduced number of chords."; //$NON-NLS-1$
	private static String StatusLineWarning_Reduced_Chords_List_de = "Beachte: In der Test-Version steht nur eine reduzierte Anzahl an Akkorden zur Verf\u00fcgung."; //$NON-NLS-1$

	private static String StatusLineWarning_Reduced_Scales_List = "Please note, the test version only offers a reduced number of scales."; //$NON-NLS-1$
	private static String StatusLineWarning_Reduced_Scales_List_de = "Beachte: In der Test-Version steht nur eine reduzierte Anzahl an Skalen zur Verf\u00fcgung."; //$NON-NLS-1$

	private static String StatusLineWarning_Reduced_Chords_Scales_List = "Please note, the test version only offers a reduced number of chords and scales."; //$NON-NLS-1$
	private static String StatusLineWarning_Reduced_Chords_Scales_List_de = "Beachte: In der Test-Version steht nur eine reduzierte Anzahl an Akkorden und Skalen zur Verf\u00fcgung."; //$NON-NLS-1$

	/* --- general strings --- */
	private static String EN = "en"; //$NON-NLS-1$
	private static String Chords = "Chords"; //$NON-NLS-1$
	private static String Scales = "Scales"; //$NON-NLS-1$

	private static String DE = "de"; //$NON-NLS-1$
	private static String Chords_de = "Akkorde"; //$NON-NLS-1$
	private static String Scales_de = "Skalen"; //$NON-NLS-1$

	/* --- constructor --- */
	private NlsUtil() {
	}

	/* --- GETTER: action related strings --- */

	public static String getMenu_new() {
		return isGerman() ? Action_menu_new_de : Action_menu_new;
	}

	public static String getAction_global_add_chord() {
		return isGerman() ? Action_global_add_chord_de : Action_global_add_chord;
	}

	public static String getAction_global_add_scale() {
		return isGerman() ? Action_global_add_scale_de : Action_global_add_scale;
	}

	public static String getAction_add_category() {
		return isGerman() ? Action_add_category_de : Action_add_category;
	}

	public static String getAction_add_chord() {
		return isGerman() ? Action_add_chord_de : Action_add_chord;
	}

	public static String getAction_add_scale() {
		return isGerman() ? Action_add_scale_de : Action_add_scale;
	}

	public static String getAction_new_category() {
		return isGerman() ? Action_new_category_de : Action_new_category;
	}

	public static String getAction_new_chord() {
		return isGerman() ? Action_new_chord_de : Action_new_chord;
	}

	public static String getAction_new_scale() {
		return isGerman() ? Action_new_scale_de : Action_new_scale;
	}

	public static String getAction_cut() {
		return isGerman() ? Action_cut_de : Action_cut;
	}

	public static String getAction_duplicate() {
		return isGerman() ? Action_duplicate_de : Action_duplicate;
	}

	public static String getAction_paste() {
		return isGerman() ? Action_paste_de : Action_paste;
	}

	public static String getAction_rename() {
		return isGerman() ? Action_rename_de : Action_rename;
	}

	public static String getAction_import() {
		return isGerman() ? Action_import_de : Action_import;
	}

	public static String getAction_export() {
		return isGerman() ? Action_export_de : Action_export;
	}

	public static String getAction_remove() {
		return isGerman() ? Action_remove_de : Action_remove;
	}

	public static String getAction_search_mode() {
		return isGerman() ? Action_search_mode_de : Action_search_mode;
	}

	public static String getAction_image_copy() {
		return Action_image_copy;
	}

	public static String getAction_image_cut() {
		return Action_image_cut;
	}

	public static String getAction_image_paste() {
		return Action_image_paste;
	}

	public static String getAction_image_import() {
		return Action_image_import;
	}

	public static String getAction_image_export() {
		return Action_image_export;
	}

	public static String getAction_image_new_category() {
		return Action_image_new_category;
	}

	public static String getAction_image_new_chord() {
		return Action_image_new_chord;
	}

	public static String getAction_image_new_scale() {
		return Action_image_new_scale;
	}

	public static String getAction_image_remove() {
		return Action_image_remove;
	}

	public static String getAction_image_search_mode() {
		return Action_image_search_mode;
	}

	/* --- GETTER: activation related strings --- */

	public static String getActivation_dialog_image() {
		return Activation_dialog_image;
	}

	public static String getActivation_activate_error_msg() {
		return isGerman() ? Activation_activate_error_msg_de : Activation_activate_error_msg;
	}

	public static String getActivation_warning_image() {
		return Activation_warning_image;
	}

	public static String getActivation_error_image() {
		return Activation_error_image;
	}

	public static String getActivateAction_activate_full_version() {
		return isGerman() ? ActivateAction_activate_full_version_de : ActivateAction_activate_full_version;
	}

	public static String getActivation_activate_invalid_key_msg() {
		return isGerman() ? Activation_activate_invalid_key_msg_de : Activation_activate_invalid_key_msg;
	}

	public static String getActivation_activate_msg() {
		return isGerman() ? Activation_activate_msg_de : Activation_activate_msg;
	}

	public static String getActivation_activate_note() {
		return isGerman() ? Activation_activate_note_de : Activation_activate_note;
	}

	public static String getActivation_activate_title() {
		return isGerman() ? Activation_activate_title_de : Activation_activate_title;
	}

	public static String getActivation_activate_url() {
		return Activation_activate_url;
	}

	public static String getActivation_buy_msg() {
		return isGerman() ? Activation_buy_msg_de : Activation_buy_msg;
	}

	public static String getActivation_buy_url() {
		return isGerman() ? Activation_buy_url_de : Activation_buy_url;
	}

	public static String getActivation_contact_url() {
		return isGerman() ? Activation_contact_url_de : Activation_contact_url;
	}

	public static String getActivation_do_not_prompt() {
		return isGerman() ? Activation_do_not_prompt_de : Activation_do_not_prompt;
	}

	public static String getActivation_expired_msg_1() {
		return isGerman() ? Activation_expired_msg_1_de : Activation_expired_msg_1;
	}

	public static String getActivation_expired_msg_2() {
		return isGerman() ? Activation_expired_msg_2_de : Activation_expired_msg_2;
	}

	public static String getActivation_expired_title() {
		return isGerman() ? Activation_expired_title_de : Activation_expired_title;
	}

	public static String getActivation_later() {
		return isGerman() ? Activation_later_de : Activation_later;
	}

	public static String getActivation_successful_msg() {
		return isGerman() ? Activation_successful_msg_de : Activation_successful_msg;
	}

	public static String getActivation_successful_title() {
		return isGerman() ? Activation_successful_title_de : Activation_successful_title;
	}

	public static String getActivation_unsupported_feature_msg() {
		return isGerman() ? Activation_unsupported_feature_msg_de : Activation_unsupported_feature_msg;
	}

	public static String getActivation_unsupported_feature_title() {
		return isGerman() ? Activation_unsupported_feature_title_de : Activation_unsupported_feature_title;
	}

	public static String getDataLanguagePath() {
		return isGerman() ? DE : EN;
	}

	public static String getDataChordPath() {
		return isGerman() ? Chords_de : Chords;
	}

	public static String getDataScalePath() {
		return isGerman() ? Scales_de : Scales;
	}

	public static String getCheckResultListTitle() {
		return Information_Title;
	}

	public static String getCheckResultListPrompt() {
		return isGerman() ? Information_Prompt_de : Information_Prompt;
	}

	public static String getStatusLineWarningForChords() {
		return isGerman() ? StatusLineWarning_Reduced_Chords_List_de : StatusLineWarning_Reduced_Chords_List;
	}

	public static String getStatusLineWarningForScales() {
		return isGerman() ? StatusLineWarning_Reduced_Scales_List_de : StatusLineWarning_Reduced_Scales_List;
	}

	public static String getStatusLineWarningForChordsAndScales() {
		return isGerman() ? StatusLineWarning_Reduced_Chords_Scales_List_de
				: StatusLineWarning_Reduced_Chords_Scales_List;
	}

	/* --- helper method --- */

	public static boolean isGerman() {
		return Platform.getNL().contains(DE);
	}
}
