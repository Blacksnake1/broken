package com.nekosoft.brokenglass.alarm

enum class AlarmType(val value: Int) {
    PERIOD_START(0),
    PERIOD_END(1),
    FERTILE_START(2),
    FERTILE_END(3),
    OVULATION(4)
}