package sample.de.gridviewtest

import android.content.Context
import androidx.annotation.WorkerThread
import java.io.File


class CacheClearer {

    @WorkerThread
    fun deleteCache(context: Context) {
        try {
            val dir = context.getCacheDir()
            deleteDir(dir)
        } catch (e: Exception) {
        }

    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory()) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            return dir.delete()
        } else return if (dir != null && dir.isFile()) {
            dir.delete()
        } else {
            false
        }
    }
}

