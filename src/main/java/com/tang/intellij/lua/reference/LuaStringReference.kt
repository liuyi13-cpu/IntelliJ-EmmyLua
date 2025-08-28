// START Modify by liuyi
/*
 * Copyright (c) 2017. tangzx(love.tangzx@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tang.intellij.lua.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import com.tang.intellij.lua.comment.psi.LuaDocTagSee
import com.tang.intellij.lua.comment.psi.api.LuaComment
import com.tang.intellij.lua.comment.psi.impl.LuaDocTagSeeImpl
import com.tang.intellij.lua.psi.*
import com.tang.intellij.lua.search.SearchContext
import com.tang.intellij.lua.ty.ITyClass
import com.tang.intellij.lua.ty.ITyFunction

/**
 *
 * Created by TangZX on 2016/12/4.
 */
class LuaStringReference internal constructor(element: LuaLiteralExpr) : PsiReferenceBase<LuaLiteralExpr>(element) {

    override fun getRangeInElement(): TextRange {
        return TextRange(1,myElement.textLength)
    }

    override fun isReferenceTo(element: PsiElement): Boolean {
        return myElement.manager.areElementsEquivalent(element, resolve())
    }

    @Throws(IncorrectOperationException::class)
    override fun handleElementRename(newElementName: String): PsiElement {
        return myElement
    }

    override fun resolve(): PsiElement? {
        val myElementName = myElement.stringValue
        val classMethods = PsiTreeUtil.getChildrenOfTypeAsList(myElement.containingFile, LuaClassMethodDef::class.java)
        for (classMethod in classMethods){
            if(classMethod.name == myElementName){
                return classMethod
            }
        }

        val luaCommentList = PsiTreeUtil.getChildrenOfTypeAsList(myElement.containingFile, LuaComment::class.java)
        for(comment in luaCommentList){
            if(comment.children[0] is LuaDocTagSeeImpl){
                val seeRefTag = comment.children[0] as LuaDocTagSee
                val classType = seeRefTag.classNameRef?.resolveType() as? ITyClass
                val ctx = SearchContext.get(seeRefTag.project)
                val classMember = classType?.findMember(myElementName,ctx)
                if(classMember !=null && classMember.guessType(ctx) is ITyFunction){
                    return classMember
                }
            }
        }

        return null
    }


    override fun bindToElement(element: PsiElement): PsiElement? {
        return null
    }

    override fun getVariants(): Array<Any> = emptyArray()

}
// END Modify by liuyi
