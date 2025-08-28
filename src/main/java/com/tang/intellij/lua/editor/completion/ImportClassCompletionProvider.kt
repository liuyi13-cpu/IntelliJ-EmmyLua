// START Modify by liuyi
package com.tang.intellij.lua.editor.completion

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.tang.intellij.lua.lang.LuaIcons
import com.tang.intellij.lua.psi.search.LuaShortNamesManager
import com.intellij.util.Processor

class ImportClassCompletionProvider : LuaCompletionProvider() {
    override fun addCompletions(session: CompletionSession) {
        val completionResultSet = session.resultSet
        val project = session.parameters.position.project
        LuaShortNamesManager.getInstance(project).
        processClassNames(project, Processor{
            completionResultSet.addElement(LookupElementBuilder.create(it).withIcon(LuaIcons.CLASS))
            true
        })

    }
}
// END Modify by liuyi
