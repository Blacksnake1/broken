package com.nekosoft.brokenglass.utils

object ConstantsApp {
    //turn open
    const val TURN_OPEN_APP = "first open app" // 1 là lần đầu mở app sau khi cài đặt, 2 là lần thứ 2 mở app.
    const val CLICK_OF_TURN_INSTANT = "CLICK_OF_TURN_INSTANT" // 1 là lần đầu mở app sau khi cài đặt, 2 là lần thứ 2 mở app.
    const val OPENED_HOME = "OPENED_HOME" // để kiểm tra xem đã đến màn home chưa, nếu đã vào đến màn home thì mới tính là 1 phiên dùng app
    var IS_NEW_TURN_FOR_SHOW_DIALOG_RATE: Boolean? = null
    const val IS_PASS_INTRO =
        "IS_SHOW_INTRO_V1" // biến check lần đầu mở app đã đi qua màn intro hay chưa.

    // choice Screen Effect
    const val SCREEN_EFFECT = "screen effect"
    const val SELLECT_TOUCH = "SELLECT_TOUCH"
    const val SELLECT_SHAKE = "SELLECT_SHAKE"
    const val EFFECT = "EFFECT"

    // set wallpaper
    const val SET_DOUBLE_SCREEN =
        " set double screen" // key cài wallpaper 2 màn home và lock screen
    const val SET_LOCK_SRCEEN = "set lock srceen" // key cài wallpaper màn lock screen
    const val SET_HOME_SCREEN = "set home screen" // key cài wallpaper màn home screen
    const val OPEN_WALLPAPER = "open wallpaper" // key lần đầu vào wallpaperfragment
    const val LIST_WALLPAPER = "list wallpaper" // key lưu danh sách wallpaper để lấy cho lần 2

    // on-off service

    const val isShowClickNotifi = "isShowClickNotifi"

    const val dialogDontShowTurnOffFireTouch = "dontShowTurnOffFireTouch"
    const val dialogDontShowTurnOffFireBlow = "dontShowTurnOffFireBlow"
    const val dialogDontShowTurnOffElectric = "dontShowTurnOffElectric"

    const val THE_FIRST_OPEN_FIRESCREEN = "COUNT_OPEN_FIRESCREEN" //key của shareperference để check xem là có phải lần đầu vào màn Fire Screen hay không

    var countTurn = 0 // biến tham chiếu để xác định xem là lần đầu từ fireActivity đi thì không show ads
    // mà chỉ show dialog hướng dẫn, còn lần 2 từ fireActivity đi thì sẽ show ads rồi mới chuyển màn
    const val FROM_FIRE_TO = "from fire to"

    const val CURRENT_POSITION = "CURRENT_POSITION"// lưu vị trí hiện tại đang được chọn trong list wallpaper
}