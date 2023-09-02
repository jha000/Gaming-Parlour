package com.app.gamingparlour.data

class Monitor {
    var id: String? = null
        private set
    var name: String? = null
        private set
    var totalSlots = 0
        private set

    constructor() {}
    constructor(id: String?, name: String?, totalSlots: Int) {
        this.id = id
        this.name = name
        this.totalSlots = totalSlots
    }
}