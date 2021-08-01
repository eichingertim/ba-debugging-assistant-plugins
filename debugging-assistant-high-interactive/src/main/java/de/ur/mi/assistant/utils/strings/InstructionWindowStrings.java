package de.ur.mi.assistant.utils.strings;

public interface InstructionWindowStrings {
    String TITLE = "<html>MI Debugging Assistant</html>";

    String IW_SUB_HEADING = "<html>Der Debugging Assistant hilft dir das effiziente Debugging mit " +
            "Log-Statements zu verbessern. Die nachfolgenden Schritte stellen ein Muster dar, wie Log-Statements" +
            " eingesetzt werden k&ouml;nnen, um nach Fehlern bzw. Fehlverhalten im Programm zu suchen.</html>";

    String IW_TITLE_STEP_1 ="<html>1. Interessante Stellen identifizieren</html>";
    String IW_DESCRIPTION_STEP_1 = "<html>Finde Stellen die du untersuchen m&ouml;chtest. Beispiele w&auml;ren hier die" +
            " <b>onCreate</b>-Methode, um zu sehen wie der Zustand der Activity beim Start ist. Es k&ouml;nnten auch " +
            "Methoden interessant sein. Beispielsweise <b>Getter</b>-Methoden von Klassenmodellen. Außerdem" +
            " k&ouml;nnen auch spezielle Stellen verfolgt werden, die beispielsweise einen Fehler geworfen haben." +
            " &Ouml;ffne den <b>Logcat</b>-Tab in der unteren Tool-Window-Bar. Versuche dort die Stack-Trace des " +
            "Fehlers (meistens mit Schriftfarbe <b>rot</b>) zu finden. Versuche nun herauszufinden um welche " +
            "Art von Fehler es sich handelt und welche Stellen interessant zu verfolgen sind</html>";

    String IW_TITLE_STEP_2 = "<html>2. Log-Statement erstellen und einf&uuml;gen</html>";
    String IW_DESCRIPTION_STEP_2 = "<html>Wenn du eine Stelle gefunden hast, kannst du per Shortcut " +
            "<b><i>Strg+Shift+D</i></b> oder per Rechts-Klick darauf, &uuml;ber den Eintrag " +
            "<b>Debugging-Assistant</b> -> <b>Log-Statement hier erstellen</b> den" +
            " Vorgang zum Erstellen eines solchen Log-Statements starten.</html>";

    String IW_TITLE_STEP_3 = "<html>3. Schritt 1 und Schritt 2 wiederholen</html>";
    String IW_DESCRIPTION_STEP_3="<html>Wiederhole die Schritte 1 und 2 bis du zufrieden mit den erstellten " +
            "Log-Statements an deinen identifizierten Stellen bist. Fahre anschließend mit Schritt " +
            "4 fort.</html>";

    String IW_TITLE_STEP_4 = "<html>4. Programm ausf&uuml;hren und Logcat verfolgen</html>";
    String IW_DESCRIPTION_STEP_4="<html>F&uuml;hre das Programm aus und &ouml;ffne die Logcat, um die erzeugten Ausgaben deiner " +
            "Log-Statements bei der Ausf&uuml;hrung des Programms zu verfolgen. &Uuml;ber einen <b>Filter</b> der Locat kannst du" +
            " die Ausgabe eingrenzen. Hier kannst du beispielsweise den Tag eingeben von Log-Statements die du " +
            "erstellt hast. Dann siehst du nur die erzeugte Ausgabe dieser speziellen Log-Statements</html>";

    String IW_TITLE_USEFULLY_ACTIONS = "<html>N&uuml;tzliche Aktionen</html>";
    String IW_DESCRIPTION_USEFULLY_ACTIONS = "<html>Mit den folgenden Buttons kannst du n&uuml;tzliche Fenster &ouml;ffnen " +
            "oder Aktionen ausf&uuml;hren, die entweder in der Anleitung oben schon erw&auml;hnt wurden, " +
            "oder dir zus&auml;tzlichen Nutzen bringen.</html>";

    String TOOL_WINDOW_LOG_CAT = "Logcat";

    String URL_FEEDBACK = "https://forms.gle/ahoN7w7rDSc7bE1d8";
}
