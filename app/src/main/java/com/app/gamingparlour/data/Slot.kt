package com.app.gamingparlour.data

class Slot {
    var startTime: String? = null
        private set
    var endTime: String? = null
        private set
    var status: String? = null
        private set
    var userId: String? = null
        private set
    var passcode: String? = null
        private set

    constructor() {}

    constructor(startTime: String?, endTime: String?, status: String?, userId: String?, passcode: String?) {
        this.startTime = startTime
        this.endTime = endTime
        this.status = status
        this.userId = userId
        this.passcode = passcode
    }
}
