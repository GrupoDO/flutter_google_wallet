package fr.sncf_connect.wallet.flutter_google_wallet

import android.app.Activity
import android.content.Context
import androidx.annotation.NonNull
import com.google.android.gms.pay.Pay
import com.google.android.gms.pay.PayApiAvailabilityStatus
import com.google.android.gms.pay.PayClient
import com.google.android.gms.tasks.Tasks
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodChannel


class FlutterGoogleWalletPlugin: FlutterPlugin, Messages.GoogleWalletApi, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var context: Context
  private lateinit var activity: Activity
  private lateinit var walletClient: PayClient

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    Messages.GoogleWalletApi.setup(flutterPluginBinding.binaryMessenger, this)
    context = flutterPluginBinding.applicationContext
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    Messages.GoogleWalletApi.setup(binding.binaryMessenger, null)
  }

  override fun initWalletClient() {
    walletClient = Pay.getClient(activity)
  }

  override fun getWalletApiAvailabilityStatus(): Boolean {
    val payApiAvailabilityStatus = Tasks.await(walletClient
            .getPayApiAvailabilityStatus(PayClient.RequestType.SAVE_PASSES))
    return payApiAvailabilityStatus == PayApiAvailabilityStatus.AVAILABLE
  }

  override fun savePasses(jsonPass: String, addToGoogleWalletRequestCode: Long) {
    walletClient.savePasses(jsonPass, activity, addToGoogleWalletRequestCode.toInt())
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity;
  }

  override fun onDetachedFromActivityForConfigChanges() {}

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {}

  override fun onDetachedFromActivity() {}
}
