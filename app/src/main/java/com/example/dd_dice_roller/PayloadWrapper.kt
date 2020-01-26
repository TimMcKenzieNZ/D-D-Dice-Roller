package com.example.dd_dice_roller

import java.io.Serializable

class PayloadWrapper(var type: Int, var heroList: MutableList<Hero>?, var action: HeroAction?): Serializable