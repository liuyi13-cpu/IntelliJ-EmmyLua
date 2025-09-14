-- TEST1 moduleclass
--local modelA2 = kg_require("NewTest/modelA")
--print(modelA2.global_ENUM.SKILL)


--TEST2 多重继承
local classSub = require("NewTest.classSub")
print(classSub.baseAvar1)
print(classSub.baseBvar1)
print(classSub.Subvar1)