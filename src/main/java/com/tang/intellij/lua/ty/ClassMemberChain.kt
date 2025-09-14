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

package com.tang.intellij.lua.ty

import com.tang.intellij.lua.psi.LuaClassMember

// START Modify by liuyi
class ClassMemberChain(val ty: ITyClass) {
    private val members = mutableMapOf<String, LuaClassMember>()
    var superChains: MutableList<ClassMemberChain> = mutableListOf()

    fun add(member: LuaClassMember) {
        val name = member.name ?: return

        // 检查所有父类链中是否存在同名成员
        val override = canOverrideAnySuperMember(name, member)
        if (override) {
            val selfExist = members[name]
            if (selfExist == null || member.worth > selfExist.worth)
                members[name] = member
        }
    }

    private fun canOverrideAnySuperMember(name: String, member: LuaClassMember): Boolean {
        // 如果没有父类链，可以直接添加
        if (superChains.isEmpty()) {
            return true
        }

        // 检查是否可以覆盖任何一个父类链中的同名成员
        // 只有当可以覆盖至少一个父类链中的同名成员时，才添加当前成员
        var canOverride = false

        for (superChain in superChains) {
            val superMember = superChain.findMember(name)
            if (superMember == null || canOverride(member, superMember)) {
                canOverride = true
                break
            }
        }

        return canOverride
    }

    fun findMember(name: String): LuaClassMember? {
        // 先查找当前类的成员
        val member = members[name]
        if (member != null) {
            return member
        }
        // 然后在所有父类链中查找
        for (superChain in superChains) {
            val superMember = superChain.findMember(name)
            if (superMember != null) {
                return superMember
            }
        }
        return null
    }

    private fun process(deep: Boolean, processor: (ITyClass, String, LuaClassMember) -> Unit) {
        for ((t, u) in members) {
            processor(ty, t, u)
        }
        if (deep) {
            for (superChain in superChains) {
                superChain.process(deep, processor)
            }
        }
    }

    fun process(deep: Boolean, processor: (ITyClass, LuaClassMember) -> Unit) {
        val cache = mutableSetOf<String>()
        process(deep) { clazz, name, member ->
            if (cache.add(name))
                processor(clazz, member)
        }
    }

    private fun canOverride(member: LuaClassMember, superMember: LuaClassMember): Boolean {
        return member.worth > superMember.worth || (member.worth == superMember.worth && member.worth > LuaClassMember.WORTH_ASSIGN)
    }
}