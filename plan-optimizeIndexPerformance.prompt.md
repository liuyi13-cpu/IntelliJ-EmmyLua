## Plan: 优化 EmmyLua 索引性能，支持增量索引

大文件多的项目中，插件长时间"分析项目"的根因有三：**`LuaFileElementType` 未覆写 `getStubVersion()`** 导致 IntelliJ 无法缓存 stub 从而每次全量重建索引；**stub 构建阶段执行了类型推断**（`guessParentType` / `SearchContext.infer`）拖慢每个文件的索引速度；**部分 StubIndex 缺失 `getVersion()`** 导致索引版本判断失效。修复这些问题后，IntelliJ 平台的增量 stub 机制即可生效——只有变更的文件才会被重新解析和索引。

### Steps

1. **为 `LuaFileElementType` 添加 `getStubVersion()` — 启用增量索引的关键**
   在 [LuaFileStub.kt](src/main/java/com/tang/intellij/lua/stubs/LuaFileStub.kt) 第 34 行 `LuaFileElementType` 类中，在 `companion object` 块之后添加：
   `override fun getStubVersion(): Int = LuaLanguage.INDEX_VERSION`
   这是 IntelliJ stub 增量更新机制的前提——平台通过比较此值与缓存版本决定是否需要重建 stub。当前缺失此覆写，默认返回 `0`，缓存频繁失效导致全量重建。

2. **为缺失 `getVersion()` 的三个 StubIndex 补充版本号**
   - [LuaClassMemberIndex.kt](src/main/java/com/tang/intellij/lua/stubs/index/LuaClassMemberIndex.kt) 第 35 行类中添加 `override fun getVersion(): Int = LuaLanguage.INDEX_VERSION`
   - [LuaSuperClassIndex.kt](src/main/java/com/tang/intellij/lua/stubs/index/LuaSuperClassIndex.kt) 第 31 行类中添加同上
   - [LuaAliasIndex.kt](src/main/java/com/tang/intellij/lua/stubs/index/LuaAliasIndex.kt) 第 24 行类中添加同上

   与已有的 `LuaClassIndex`（第 35 行）、`LuaShortNameIndex`（第 30 行）保持一致。

3. **消除 `LuaIndexExprType.createStub` 中的类型推断**
   在 [LuaIndexExprStub.kt](src/main/java/com/tang/intellij/lua/stubs/LuaIndexExprStub.kt) 第 68-78 行，将：
   ```kotlin
   if (stat != null) {
       val ty = SearchContext.withStub(...) { indexExpr.guessParentType(it) }
       TyUnion.each(ty) { if (it is ITyClass) classNameSet.add(it.className) }
   }
   ```
   替换为直接从 AST 提取前缀表达式文本作为类名：
   - 获取 `indexExpr.prefixExpr`，如果是 `LuaNameExpr` 则用其 `name` 作为类名
   - 如果是 `LuaIndexExpr` 且 `isPure()`，则用其全文本 `text` 作为类名
   - 不再调用 `SearchContext.withStub` 和 `guessParentType`
   - 移除对 `SearchContext`、`Ty`、`TyUnion` 的 import（如果不再需要）

4. **消除 `LuaClassMethodType.createStub` 中的类型推断**
   在 [LuaClassMethodStub.kt](src/main/java/com/tang/intellij/lua/stubs/LuaClassMethodStub.kt) 第 47-55 行，将：
   ```kotlin
   val ty = SearchContext.withStub(...) { SearchContext.infer(expr, it) }
   TyUnion.each(ty) { if (it is ITyClass) classNameSet.add(it) }
   if (classNameSet.isEmpty()) classNameSet.add(createSerializedClass(expr.text))
   ```
   简化为直接使用 `createSerializedClass(expr.text)`：
   - `classNameSet.add(createSerializedClass(expr.text))` 即可
   - 去掉 `SearchContext.withStub` 和 `SearchContext.infer` 调用
   - 移除不再需要的 `SearchContext` import

5. **恢复 `LuaIndexExprType.shouldCreateStub` 过滤（仅对非赋值场景）**
   在 [LuaIndexExprStub.kt](src/main/java/com/tang/intellij/lua/stubs/LuaIndexExprStub.kt) 第 60-63 行，取消注释并修改 `shouldCreateStub`：
   ```kotlin
   override fun shouldCreateStub(node: ASTNode): Boolean {
       val psi = node.psi as LuaIndexExpr
       if (psi.assignStat != null) return true  // 赋值语句始终创建 stub
       return psi.isPure()
   }
   ```
   这样 `a.b().c` 等非纯、非赋值的 index 表达式不再创建 stub，减少大量索引开销。

6. **在 `LuaAnnotator.visitNameExpr` 中增加 dumb 模式快速退出**
   在 [LuaAnnotator.kt](src/main/java/com/tang/intellij/lua/annotator/LuaAnnotator.kt) 的 `visitNameExpr` 方法（约第 148 行）最开头添加：
   ```kotlin
   if (DumbService.isDumb(o.project)) return
   ```
   并在文件顶部添加 `import com.intellij.openapi.project.DumbService`。
   防止索引期间 annotator 尝试做 resolve 查询阻塞 UI 线程。

### 修改后需递增 `INDEX_VERSION`

所有改动完成后，在 [LuaLanguage.java](src/main/java/com/tang/intellij/lua/lang/LuaLanguage.java) 第 27 行将 `INDEX_VERSION` 从 `40` 递增到 `41`，以确保用户升级后自动重建一次索引、后续走增量更新。

