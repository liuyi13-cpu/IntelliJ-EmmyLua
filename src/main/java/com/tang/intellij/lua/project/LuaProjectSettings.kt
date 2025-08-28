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

package com.tang.intellij.lua.project

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "LuaProjectSettings", storages = [Storage("emmy_project.xml")])
@Service(Service.Level.PROJECT)
class LuaProjectSettings() : PersistentStateComponent<LuaProjectSettings> {
    var sourceRoot = arrayOf<String>();

    override fun getState(): LuaProjectSettings {
        return this
    }

    override fun loadState(luaSettings: LuaProjectSettings) {
        XmlSerializerUtil.copyBean(luaSettings, this)
    }
    companion object {

        fun getInstance(project: Project): LuaProjectSettings {
            return project.getService(LuaProjectSettings::class.java)
        }

    }
}
// END Modify by liuyi