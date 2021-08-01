package de.ur.mi.assistant.utils.strings;

public interface WizardWindowStrings {

    String WIZARD_COMPLETE_TITLE = "Wizard abgeschlossen";
    String WIZARD_COMPLETE_MESSAGE = "<html>Das erstellte Log-Statement wurde erfolgreich eingef&uuml;gt.<br><br>" +
            "Wenn du zufrieden bist mit den bereits eingef&uuml;gten Log-Statements, w&auml;hle <b>App ausf&uuml;hren und Logcat &ouml;ffnen</b>. " +
            "Ansonsten w&auml;hle <b>Weitere Statements erstellen</b>, um noch mehr Loggings zu erzeugen.</html>";
    String WIZARD_COMPLETE_OPTION_EXECUTE_APP = "<html>App ausf&uuml;hren und Logcat &ouml;ffnen</html>";
    String WIZARD_COMPLETE_OPTION_ADD_MORE = "<html>Weitere Statements erstellen</html>";

    String MORE_INFO_PARAMS_MESSAGE = "<html><body style='width: 200px;'>Die ausgew&auml;hlten Parameter werden im Log-String mit " +
            "<i>String.valueOf</i> umrundet. Wenn ein Parameter <i>null</i> ist wird dies auch so in der " +
            "Konsole angezeigt. Ansonsten wird bei nicht primitiven Datentypen - also Objekten - " +
            "die <i>toString()</i> Methode aufgerufen und entsprechend deren R&uuml;ckgabe angezeigt." +
            "</body></html>";

    String MORE_INFO_CLASS_FIELD_MESSAGE = "<html><body style='width: 200px;'>" +
            "Verwende Variablen/Felder einer Klasse, um deren Verlauf bei der Ausf&uuml;hrung der App zu verfolgen." +
            "So kannst du &Uuml;berpr&uuml;fen, ob alle Werte deiner Erwartung entsprechen." +
            "<br><br>Die ausgew&auml;hlten Variablen/Felder einer Klasse werden im Log-String mit " +
            "<i>String.valueOf</i> umrundet. Wenn ein Feld <i>null</i> ist wird dies auch so in der " +
            "Konsole angezeigt. Ansonsten wird bei nicht primitiven Datentypen - also Objekten - " +
            "die <i>toString()</i> Methode aufgerufen und entsprechend deren R&uuml;ckgabe angezeigt.</body></html>";

    String MORE_INFO_TAG_MESSAGE = "<html><body style='width: 200px;'>Der TAG eines Log-Statements ist daf&uuml;r da " +
            "ein Log-Statement einer Gruppe zuzuordnen, um diese sp&auml;ter dann wiederzufinden. " +
            "Empfohlen als TAG wird daher meistens der Klassen- oder Dateiname in der das Log-Statement e" +
            "nthalten ist. So kann sp&auml;ter in der Logcat speziell nach diesem Tag gefiltert werden.</body></html>";

    String MORE_INFO_CLASS_NAME = "<html><body style='width: 200px;'>Verwende den Klassennamen, um das Log-Statement sp&auml;ter schneller lokalisieren" +
            "zu k&ouml;nnen und dadurch den Debugging-Prozess zu beschleunigen.</body></html>";

    String MORE_INFO_INDIV_MSG_MESSAGE = "<html><body style='width: 200px;'>F&uuml;ge optional eine individuelle Nachricht " +
            "hinzu. Diese kann beispielsweise lauten: <i>&Uuml;berpr&uuml;fung einer NullPointerException</i>. " +
            "So weißt du auch sp&auml;ter noch warum du das Log-Statement hier eingef&uuml;gt hast</body></html>";


}
