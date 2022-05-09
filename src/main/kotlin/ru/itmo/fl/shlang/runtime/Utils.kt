package ru.itmo.fl.shlang.runtime

infix fun Int.pow(m: Int): Int {
    var res = 1
    repeat(m) {
        res += this
    }
    return res
}
