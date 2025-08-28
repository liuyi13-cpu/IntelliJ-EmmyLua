// START Modify by liuyi
package com.tang.intellij.lua.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.tang.intellij.lua.lang.type.LuaString
import com.tang.intellij.lua.psi.*
import com.tang.intellij.lua.search.SearchContext

/**
 *
 * Created by wanghuihui on 2024/03/09.
 */
class LuaImportReference internal constructor(callExpr: LuaCallExpr) : PsiReferenceBase<LuaCallExpr>(callExpr) {

    private var classString: String? = null
    private var range = TextRange.EMPTY_RANGE
    private val classElement: PsiElement? = callExpr.firstStringArg

    init {
        if (classElement != null && classElement.textLength > 2) {
            val text = classElement.text
            val luaString = LuaString.getContent(text)
            classString = luaString.value

            if (this.classString != null) {
                val start = classElement.textOffset - callExpr.textOffset + luaString.start
                val end = start + classString!!.length
                range = TextRange(start, end)
            }
        }
    }

    override fun isReferenceTo(element: PsiElement): Boolean {
        return myElement.manager.areElementsEquivalent(element, resolve())
    }

    override fun getRangeInElement(): TextRange {
        return range
    }

    override fun resolve(): PsiElement? {
        val resolve = resolveImportClass(classString, SearchContext.get(myElement.project))
        return if (resolve === myElement) null else resolve
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        classString?.let {
            setClass(newElementName)
        }
        return myElement
    }

    private fun setClass(className: String) {
        if (classElement != null) {
            val stat = LuaElementFactory.createWith(myElement.project, "import($className)") as LuaExprStat
            val stringArg = (stat.expr as? LuaCallExpr)?.firstStringArg
            if (stringArg != null)
                classElement.replace(stringArg)
        }
    }

    override fun bindToElement(element: PsiElement): PsiElement? {
        return null
    }

    override fun getVariants(): Array<Any> = emptyArray()
}
// END Modify by liuyi