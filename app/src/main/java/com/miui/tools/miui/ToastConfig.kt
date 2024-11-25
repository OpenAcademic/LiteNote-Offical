/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.miui.tools.miui

import android.app.PendingIntent
import android.widget.Toast

data class ToastConfig(
    var text: String,
    var textColor: String,
    var image : String ,
    var duration : Long = 3000L,
    var intent :  PendingIntent? = null
)

