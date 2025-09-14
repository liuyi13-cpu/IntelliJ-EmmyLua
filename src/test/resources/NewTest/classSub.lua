local classBaseA = require("NewTest.classBaseA")
local classBaseB = require("NewTest.classBaseB")

---@class classSub : classBaseA, classBaseB
local M = {}

function M:ctor()
    self.Subvar1 = 0
end

return M
