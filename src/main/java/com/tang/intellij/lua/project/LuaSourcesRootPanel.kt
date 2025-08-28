// START Modify by liuyi
package com.tang.intellij.lua.project

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.tang.intellij.lua.LuaBundle
import java.awt.BorderLayout
import javax.swing.DefaultListModel
import javax.swing.JPanel
import javax.swing.ListSelectionModel

class LuaSourcesRootPanel : JPanel(BorderLayout()) {
    private val dataModel = DefaultListModel<String>()
    private val pathList = JBList(dataModel)
    init {
        pathList.selectionMode = ListSelectionModel.SINGLE_SELECTION

        add(ToolbarDecorator.createDecorator(pathList)
                .setAddAction { addPath() }
                .setEditAction { editPath() }
                .setRemoveAction { removePath() }
                .createPanel(), BorderLayout.CENTER)
        border = IdeBorderFactory.createTitledBorder(LuaBundle.message("ui.settings.source_root"), false)
    }

    var roots: Array<String> get() {
        val list = mutableListOf<String>()
        for (i in 0 until dataModel.size) {
            list.add(dataModel.get(i))
        }
        return list.toTypedArray()
    } set(value) {
        dataModel.clear()
        for (s in value) dataModel.addElement(s)
    }

    private fun addPath() {
        val desc = FileChooserDescriptor(false, true, false, false, false, false)
        val dir = FileChooser.chooseFile(desc, null, null)
        if (dir != null) {
            dataModel.addElement(dir.canonicalPath)
        }
    }

    private fun editPath() {
        val index = pathList.selectedIndex
        val desc = FileChooserDescriptor(false, true, false, false, false, false)
        val dir = FileChooser.chooseFile(desc, null, null)
        if (dir != null) {
            dataModel.set(index, dir.canonicalPath)
        }
    }

    private fun removePath() {
        val index = pathList.selectedIndex
        dataModel.removeElementAt(index)
    }
}
// END Modify by liuyi