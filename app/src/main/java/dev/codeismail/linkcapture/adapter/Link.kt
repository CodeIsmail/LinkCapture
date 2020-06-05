package dev.codeismail.linkcapture.adapter

import java.util.*

class Link (val id: String = UUID.randomUUID().toString(), var linkString: String){
    var lastVisit: String? = ""

    constructor(id: String, linkString: String, lastVisit: String) : this(id, linkString){
        this.lastVisit = lastVisit
    }
}
