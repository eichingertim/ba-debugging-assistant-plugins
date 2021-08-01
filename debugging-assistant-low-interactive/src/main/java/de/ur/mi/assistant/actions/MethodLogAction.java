package de.ur.mi.assistant.actions;

import de.ur.mi.assistant.utils.CodeInspector;
import de.ur.mi.assistant.utils.StatementGenerator;

/**
 * Action that produces a log-statement with the following information:
 * - TAG: Filename
 * - Methodinformations: Name, Params
 * - Placeholder for individual Message
 */
public class MethodLogAction extends BaseAction {

    @Override
    protected StatementGenerator.Holder getStatementHolder(CodeInspector.InspectedElement el) {
        return StatementGenerator.buildMethodStatement(el);
    }
}
