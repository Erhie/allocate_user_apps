package org.erhy.allocateuserapps

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.content.res.Configuration
//import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import org.json.JSONArray
import org.json.JSONException

import java.io.ByteArrayOutputStream
import java.util.ArrayList
import java.util.HashMap

import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar
import android.os.Build
import android.content.pm.ResolveInfo
import java.util.Collections
import android.util.DisplayMetrics
import android.graphics.Canvas

class AllocateUserAppsPlugin(registrar: Registrar): MethodCallHandler {
  private val registrar: PluginRegistry.Registrar

  //constructor
  init {
    this.registrar = registrar
  }

  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar): Unit {
      val channel = MethodChannel(registrar.messenger(), "allocate_user_apps")
      channel.setMethodCallHandler(AllocateUserAppsPlugin(registrar))
    }
  }


  override fun onMethodCall(call: MethodCall, result: Result): Unit {
    if (call.method.equals("getAllApps")) {
      getAllApps(result)
    } else if (call.method.equals("launchApp")) {
      val packageNameJ:String? = call.argument("packageName")
      if ( packageNameJ != null) {
        val packageNameNn:String = packageNameJ!! //from nullable to not nullable
        launchApp(packageNameNn)
      }
      result.success(null)
    } else if (call.method.equals("getPlatformVersion")) {
      myPlatformVersion(result)
    } else if (call.method.equals("getModelIdentification")) {
      myModelIdentification(result)
    } else
      result.notImplemented()
  }

  private fun launchApp(packageName: String) {
    val i = this.registrar.context().getPackageManager().getLaunchIntentForPackage(packageName)
    if (i != null)
      this.registrar.context().startActivity(i)
  }

  fun convertToBytes(image: Bitmap, compressFormat: Bitmap.CompressFormat, quality: Int): ByteArray {
    val byteArrayOS = ByteArrayOutputStream()
    image.compress(compressFormat, quality, byteArrayOS)
    return byteArrayOS.toByteArray()
  }

  fun Drawable.toBitmapDimension36(): Bitmap {
    var maxsidelength:Int = 36
    val width = if (this.bounds.isEmpty) this.intrinsicWidth else this.bounds.width()
    val height = if (this.bounds.isEmpty) this.intrinsicHeight else this.bounds.height()

    if ( width <= maxsidelength && height <= maxsidelength ) {
      if (this is BitmapDrawable) {
        //Log.v( "DEBUG ","Drawable is correct Bitmap width = "+width+" height = "+height)
        return this.bitmap
      }
    }

    return Bitmap.createBitmap( maxsidelength, maxsidelength, Bitmap.Config.ARGB_8888).also { //also
      val canvas = Canvas(it)
      this.setBounds(0, 0, canvas.width, canvas.height)
      this.draw(canvas)
    }
  }

  private fun getAllApps(result: MethodChannel.Result) {

    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    val packageManager = registrar.context().getPackageManager()

    val resList : List<ResolveInfo> = packageManager.queryIntentActivities(intent, 0)

    val _output = ArrayList<Map<String, Any>>()

/* for test to have no apps
    result.success(_output)
    return
*/

    // var forCounter:Int = 0 //for test

    for (resInfo in resList) {
      try {
        val app:ApplicationInfo = packageManager.getApplicationInfo(
                resInfo.activityInfo.packageName, PackageManager.GET_META_DATA)
        if (packageManager.getLaunchIntentForPackage(app.packageName) != null) {
          val res: Resources = packageManager.getResourcesForApplication(app)

          if (res == null) {
            Log.v("DEBUG ", "res was null!")
          }
          else
          {
/*          should be used in API level >= 22
          val icon:Drawable = res.getDrawableForDensity(app.icon,
                  DisplayMetrics.DENSITY_LOW,
                  null)
*/
            Log.v("DEBUG before getDrawableForDensity", "-")
            // in next statement sometimes a exception rise
            /*  This method was deprecated in API level 22, but we want also run on API 16 */
            val icon: Drawable = res.getDrawableForDensity(app.icon,
                    DisplayMetrics.DENSITY_LOW)
            Log.v("DEBUG after getDrawableForDensity", "-")

            val bitmapj: Bitmap = icon.toBitmapDimension36()
            val iconData = convertToBytes(bitmapj,
                    Bitmap.CompressFormat.PNG, 100) //quality is ignored for PNG-format

            val current = HashMap<String, Any>()
            current.put("label", app.loadLabel(packageManager).toString())

            Log.v("DEBUG ", "app.packageName = " + app.packageName)
            current.put("package", app.packageName)

            current.put("icon", iconData)

            _output.add(current)
            Log.v("DEBUG putted: ", "app.packageName = " + app.packageName)
/*
            if ( ++forCounter > 8 )
              break; //test with only one app
*/
          }
        }
      } catch (e: Exception) {
        // nothing is added to _output
      }
    }
    result.success(_output)
  }

  private fun myPlatformVersion(result: MethodChannel.Result) {
    val _output = "Android ${android.os.Build.VERSION.RELEASE}" //android.os.Build.VERSION.RELEASE
    result.success(_output)
  }

  private fun myModelIdentification(result: MethodChannel.Result) {
    val _output = android.os.Build.MODEL
    result.success(_output)
  }
}
