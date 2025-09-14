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

package com.tang.intellij.lua.ext

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.RecursionManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.io.StringRef

fun <T> recursionGuard(key: Any, block: Computable<T>, memoize: Boolean = true): T? =
    RecursionManager.doPreventingRecursion(key, memoize, block)

val PsiFile.fileId get() = FileBasedIndex.getFileId(this.originalFile.virtualFile)

val PsiElement.stubOrPsiParent get(): PsiElement {
    if (this is StubBasedPsiElementBase<*>) {
        val psi = this.stub?.parentStub?.psi
        if (psi != null) return psi
    }
    return parent
}

// START Modify by liuyi
fun deserializeList(stream: StubInputStream): MutableList<String>  {
    var list = mutableListOf<String>()
    var size = stream.read()
    if (size > 0) {
        for (i in 0 until size) {
            val base = StringRef.toString(stream.readName())
            list.add(base)
        }
    }
    return list
}

fun serializeList(stream: StubOutputStream, list: MutableList<String>?) {
    if (list != null) {
        var size = list?.size ?: 0
        stream.write(size)
        for (superClass in list!!) {
            stream.writeName(superClass)
        }
    } else {
        stream.write(0)
    }
}