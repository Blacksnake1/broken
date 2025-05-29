package com.gianghv.utils

object ConstantsAds {
    const val TIME_OUT: Long = 10000

    fun testDevices(): List<String> {
        return listOf(
            "DE84AB3B057C90AF8FBD9446091BD425",
            "B989BCAD52A558D37AFC3C508D411920",
            "0B9F875B3EBF0F07D8943433B82D7CF5",
            "D1001C40D12FB59956A18DF8A1503981",
            "5A8167409A32C7D3AB959286717678D3",
            "CC09F07C28216F699FDED6BA5B5D1DC5",
            "29657BA4F8C7933429901148C62F5C0A",
            "708460235ADAAE8E83F4A80732AA04FC",
            "DE84AB3B057C90AF8FBD9446091BD425",
            "7AC3EF4B7EC73FFFC746DC27BF61F5D8",
//            "9BCE4BB076DC0E1FCF486C8D0234E853",
//            "731CD9DF4DD44CF2A6B612BE6E7BA566", //adr14 Hà
//            "61437B0A3FF89CD7A024968BD65F71DB", //a03
//            "1610B57E3C811750E039B6AD36E911D8", //s20 Hằng
//            "C30D75682C7E97E056E9454582416B04", //j5 prime
        )
    }

    var isShowAdsFull = false
    var isShowOpenAds = false
    var isShowSplashOpenAds = false
    var adsClicked = false
    var isLoadingInterAds = false
}