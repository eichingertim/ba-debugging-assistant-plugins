package de.ur.mi.assistant.actions;

import de.ur.mi.assistant.utils.CodeInspector;
import de.ur.mi.assistant.utils.StatementGenerator;

/**
 * Action that produces a log-statement with the following information:
 * - TAG: Filename
 * - Methodinformations: Name, Params
 * - Classinformations: Name, Fields
 * - Placeholder for individual Message
 */
public class MethodClassLogAction extends BaseAction {

    @Override
    protected StatementGenerator.Holder getStatementHolder(CodeInspector.InspectedElement el) {
        return StatementGenerator.buildMethodClassLogStatement(el);
    }
}
