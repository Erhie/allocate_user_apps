import 'dart:async';

import 'package:flutter/services.dart';

class AllocateUserApps {
  static const MethodChannel _channel =
      const MethodChannel('allocate_user_apps');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> get modelIdentification async {
    final String model = await _channel.invokeMethod('getModelIdentification');
    return model;
  }

  static Future<List> get allApps async {
    final List allmyApps = await _channel.invokeMethod('getAllApps');
    return allmyApps;
  }

  static Future<void> launchApp(String packageName) async {
    // Errors occurring on the platform side cause invokeMethod to throw
    // PlatformExceptions.
    try {
      await _channel.invokeMethod('launchApp', <String, dynamic>{
        'packageName': packageName
      });
    } on PlatformException catch (e) {
      throw 'Unable to launchApp ${packageName}: ${e.message}';
    }
  }

}
