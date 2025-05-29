package com.nekosoft.brokenglass.utils

import com.brokenscreen.prankapp.crack.screen.R
import com.nekosoft.brokenglass.data.model.CrankEffectItem
import com.nekosoft.brokenglass.data.model.EffectBrokenModel
import com.nekosoft.brokenglass.data.model.HomeModel
import com.nekosoft.brokenglass.data.model.ScreenModel
import com.nekosoft.brokenglass.data.model.WeaponModel

object ListUtils {
    val crankEffectList = mutableListOf(
        CrankEffectItem.Effect(EffectBrokenModel(R.drawable.smartphone1, "smartphone1")),
        CrankEffectItem.Effect(EffectBrokenModel(R.drawable.smartphone2, "smartphone2")),
        CrankEffectItem.Effect(EffectBrokenModel(R.drawable.smartphone3, "smartphone3")),
        CrankEffectItem.Effect(EffectBrokenModel(R.drawable.smartphone4, "smartphone4")),
        CrankEffectItem.Effect(EffectBrokenModel(R.drawable.smartphone5, "smartphone5")),
        CrankEffectItem.Effect(EffectBrokenModel(R.drawable.smartphone6, "smartphone6")),
        CrankEffectItem.Effect(EffectBrokenModel(R.drawable.smartphone7, "smartphone7")),
        CrankEffectItem.Effect(EffectBrokenModel(R.drawable.smartphone8, "smartphone8")),
        CrankEffectItem.Effect(EffectBrokenModel(R.drawable.smartphone9, "smartphone9")),
        CrankEffectItem.Effect(EffectBrokenModel(R.drawable.smartphone10, "smartphone10"))
    )

    val wallpaperList = mutableListOf(
        ScreenModel(1, "wp1", null, R.drawable.wp1, false, false),
        ScreenModel(2, "wp2", null, R.drawable.wp2, false, false),
    )


    val weaponList = mutableListOf(
        WeaponModel(
            "sniper",
            R.drawable.sniper,
            mutableListOf(R.drawable.sniper_1, R.drawable.sniper_2, R.drawable.sniper_3),
            R.raw.sound_sniper
        ),
        WeaponModel(
            "rifle",
            R.drawable.rifle,
            mutableListOf(R.drawable.rifle_1, R.drawable.rifle_2, R.drawable.rifle_3),
            R.raw.sound_rifle
        ),
        WeaponModel(
            "revolver",
            R.drawable.revolver,
            mutableListOf(R.drawable.revo_1, R.drawable.revo_2, R.drawable.revo_3),
            R.raw.sound_revolver
        ),
        WeaponModel(
            "shotgun",
            R.drawable.shotgun,
            mutableListOf(R.drawable.shotgun_1, R.drawable.shotgun_2, R.drawable.shotgun_3),
            R.raw.sound_shotgun
        ),
        WeaponModel(
            "pistol",
            R.drawable.pistol,
            mutableListOf(R.drawable.pist_1, R.drawable.pist_2, R.drawable.pist_3),
            R.raw.sound_pistol
        ),
        WeaponModel(
            "knife",
            R.drawable.knife,
            mutableListOf(R.drawable.knife_1),
            R.raw.sound_knife
        ),
        WeaponModel(
            "hammer",
            R.drawable.hammer,
            mutableListOf(R.drawable.hamm_1, R.drawable.hamm_2, R.drawable.hamm_3),
            R.raw.sound_hammer
        ),
        WeaponModel(
            "claws",
            R.drawable.claws,
            mutableListOf(R.drawable.claws_1),
            R.raw.sound_crawl
        ),
        WeaponModel(
            "bat",
            R.drawable.bat,
            mutableListOf(R.drawable.bat_1, R.drawable.bat_2, R.drawable.bat_3),
            R.raw.sound_bat
        )
    )
    val itemHomeList = mutableListOf(
        HomeModel(0, "broken_screen", R.drawable.img_btn_broken_screens),
        HomeModel(1, "broken_wallpaper", R.drawable.img_btn_wallpaper),
        HomeModel(2, "prank_screen", R.drawable.img_btn_fire_screen),
        HomeModel(3, "destroy_screen", R.drawable.img_btn_destroy_screen)
    )
    val itemFireList = mutableListOf(
        HomeModel(0, "fire_touch", R.drawable.img_fire_touch),
        HomeModel(1, "fire_blow", R.drawable.img_fire_blow),
        HomeModel(2, "electric_touch", R.drawable.img_electric),
    )
}