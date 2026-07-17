require "ISUI/ISPanel"

local options = PZAPI.ModOptions:create("cjsStealthOverhaul", "CJS Stealth Overhaul")
local showIndicator = options:addTickBox("showLightIndicator", "Show darkness / light indicator", true)

local indicator

local CjsLightIndicator = ISPanel:derive("CjsLightIndicator")

function CjsLightIndicator:new(player)
    local width = 44
    local height = 44
    local instance = ISPanel.new(self, getCore():getScreenWidth() - 300, 12, width, height)
    instance.player = player
    instance.lightLevel = 1
    instance.reduction = 0
    instance.lastSample = 0
    instance.background = false
    instance.moveWithMouse = false
    return instance
end

function CjsLightIndicator:updateSample()
    local now = getTimestampMs()
    if now - self.lastSample < 250 then
        return
    end
    self.lastSample = now

    if CjsStealthOverhaul and self.player and self.player:getCurrentSquare() then
        self.lightLevel = CjsStealthOverhaul.getLightLevel(self.player)
        self.reduction = CjsStealthOverhaul.getDarknessReductionPercent(self.player)
    end
end

function CjsLightIndicator:prerender()
    self:setX(getCore():getScreenWidth() - 300)
    if not showIndicator:getValue() then
        return
    end

    self:updateSample()

    local red
    local green
    local blue
    if self.lightLevel <= 0.25 then
        red, green, blue = 0.25, 0.78, 0.45
    elseif self.lightLevel <= 0.60 then
        red, green, blue = 0.92, 0.72, 0.24
    else
        red, green, blue = 0.90, 0.30, 0.24
    end

    self:drawRect(0, 0, self.width, self.height, 0.82, 0.04, 0.06, 0.07)
    self:drawRectBorder(0, 0, self.width, self.height, 0.85, 0.58, 0.64, 0.61)
    self:drawRect(5, 5, self.width - 10, self.height - 10, 0.88, red, green, blue)
    self:drawTextCentre(tostring(self.reduction) .. "%", self.width / 2, 13, 0.05, 0.07, 0.06, 1.0, UIFont.Small)
end

local function configureJavaPatch()
    if not CjsStealthOverhaul then
        print("[CJS Stealth Overhaul] Java API unavailable; ZombieBuddy may not have approved or loaded the JAR")
        return
    end

    local sandbox = SandboxVars.CjsStealth or {}
    CjsStealthOverhaul.configure(
        sandbox.DarknessReduction or 50,
        sandbox.CoverReduction or 15,
        sandbox.MinimumDistance or 3,
        sandbox.FullEffectDistance or 6,
        sandbox.CorrectCatseyeLight ~= false
    )
end

local function createIndicator(_, player)
    configureJavaPatch()
    if indicator then
        indicator:removeFromUIManager()
    end
    indicator = CjsLightIndicator:new(player)
    indicator:initialise()
    indicator:addToUIManager()
end

Events.OnGameStart.Add(configureJavaPatch)
Events.OnCreatePlayer.Add(createIndicator)
