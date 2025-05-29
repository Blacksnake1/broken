package com.nekosoft.brokenglass.data.model

sealed class CrankEffectItem {
    data class Effect( val crankEffectModel: EffectBrokenModel) : CrankEffectItem()
    data class Ads( val adModel: Any) : CrankEffectItem()
}