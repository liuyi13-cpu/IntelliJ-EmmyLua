// START Modify by liuyi
package com.tang.intellij.lua.editor.completion

import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrefixMatcher
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.Processor
import com.tang.intellij.lua.comment.psi.LuaDocTagSee
import com.tang.intellij.lua.comment.psi.api.LuaComment
import com.tang.intellij.lua.comment.psi.impl.LuaDocTagSeeImpl
import com.tang.intellij.lua.lang.LuaIcons
import com.tang.intellij.lua.psi.*
import com.tang.intellij.lua.psi.impl.LuaAssignStatImpl
import com.tang.intellij.lua.psi.impl.LuaTableExprImpl
import com.tang.intellij.lua.psi.impl.LuaTableFieldImpl
import com.tang.intellij.lua.search.SearchContext
import com.tang.intellij.lua.ty.ITy
import com.tang.intellij.lua.ty.ITyClass
import com.tang.intellij.lua.ty.ITyFunction
import com.tang.intellij.lua.ty.isVisibleInScope

class StringCompletionProvider : ClassMemberCompletionProvider() {
    override fun addCompletions(session: CompletionSession) {
        val completionParameters = session.parameters
        val completionResultSet = session.resultSet
        val position = completionParameters.position
        val prefixMatcher = completionResultSet.prefixMatcher
        val luaCommentList = PsiTreeUtil.getChildrenOfTypeAsList(position.containingFile, LuaComment::class.java)
        for(comment in luaCommentList){
            if(comment.children.size > 0 && comment.children[0] is LuaDocTagSeeImpl){
                val seeRefTag = comment.children[0] as LuaDocTagSee
                val classType = seeRefTag.classNameRef?.resolveType() as? ITyClass
                val ctx = SearchContext.get(seeRefTag.project)
                classType?.processMembers(ctx) { _, member ->
                    if (member.guessType(ctx) is ITyFunction){
                        completionResultSet.addElement(LookupElementBuilder.create(member.name!!).withIcon(LuaIcons.CLASS_METHOD))
                    }
                }
            }
        }

        val classMethods = PsiTreeUtil.getChildrenOfTypeAsList(position.containingFile, LuaClassMethodDef::class.java)
        for (classMethod in classMethods){
            classMethod.name?.let {
                if(prefixMatcher.prefixMatches(it)){
                    completionResultSet.addElement(LookupElementBuilder.create(it).withIcon(LuaIcons.CLASS_METHOD))
                }
            }
        }

        //搜索整个文件的成员
//        val project = position.project
//        val searchContext = SearchContext.get(project)
//        val methodDef = PsiTreeUtil.getParentOfType(position, LuaClassMethodDef::class.java)
//        if (methodDef != null) {
//            addCompletionOnMethod(position,methodDef,searchContext, project, completionResultSet)
//        } else {
//            addCompletionOnTable(position,searchContext,completionResultSet,project)
//        }
//        completionResultSet.stopHere()


    }



    private fun addCompletionOnMethod(position:PsiElement,methodDef:LuaClassMethodDef,searchContext: SearchContext,project:Project,completionResultSet:CompletionResultSet){
        val contextTy = LuaPsiTreeUtil.findContextClass(position)
        methodDef.guessClassType(searchContext)?.let { type ->
            type.processMembers(searchContext) { curType, member ->
                if (curType.isVisibleInScope(project, contextTy, member.visibility)) {
                    addMember(completionResultSet,
                        member,
                        curType,
                        type,
                        MemberCompletionMode.All,
                        project,
                        object : HandlerProcessor() {
                            override fun process(
                                element: LuaLookupElement,
                                member: LuaClassMember,
                                memberTy: ITy?
                            ): LookupElement {
                                return element
                            }
                        })
                }
            }
        }
    }

    private fun addCompletionOnTable( position:PsiElement,searchContext: SearchContext, completionResultSet: CompletionResultSet,
                                      project: Project){
        val tableDef = PsiTreeUtil.getParentOfType(position, LuaTableExpr::class.java)
        val prefixMatcher = completionResultSet.prefixMatcher
        if (tableDef != null) {
            val ty = tableDef.shouldBe(searchContext)
            processClass(ty, searchContext, prefixMatcher, completionResultSet,project,position)
        }
        var cur = tableDef?.parent
        while (true){
            if(cur == null || cur is LuaPsiFile){
                break
            }
            if(cur is LuaTableFieldImpl){
                processClass(cur.guessParentType(searchContext),searchContext, prefixMatcher, completionResultSet,project,position)
            }else if(cur is LuaTableExprImpl){
                processClass(cur.guessType(searchContext),searchContext, prefixMatcher, completionResultSet,project,position)
            }else if(cur is LuaAssignStatImpl){
                val assignees = cur.varExprList.exprList
                for (i in 0 until assignees.size) {
                    val field = assignees[i]
                    if(field is LuaIndexExpr) {
                        processClass(field.guessParentType(searchContext),searchContext, prefixMatcher, completionResultSet,project,position)
                    }
                }
            }
            cur = cur.parent
        }
    }

    private fun processClass(luaClass:ITy,searchContext: SearchContext,prefixMatcher:PrefixMatcher, completionResultSet: CompletionResultSet,
                             project: Project,position: PsiElement){
        val contextTy = LuaPsiTreeUtil.findContextClass(position)
        luaClass.eachTopClass(Processor { luaType ->
            addClass(contextTy, luaType, project, MemberCompletionMode.Dot, completionResultSet, prefixMatcher,
                                            object : HandlerProcessor() {
                                override fun process(
                                    element: LuaLookupElement,
                                    member: LuaClassMember,
                                    memberTy: ITy?
                                ): LookupElement {
                                    return element
                                }
                            })

            true
        })
    }
}
// END Modify by liuyi