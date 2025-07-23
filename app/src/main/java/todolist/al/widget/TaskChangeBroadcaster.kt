package todolist.al.widget

import android.content.Context
import android.content.Intent

object TaskChangeBroadcaster {
    fun notifyChange(context: Context) {
        val intent = Intent(context, WidgetUpdateReceiver::class.java)
        context.sendBroadcast(intent)
    }
}
