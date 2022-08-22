# UVCCamera
易用性封装，能重复打开

支持多个设备，每个设备对应一个USBMonitorUtilBase，通过findDevice()找到对应设备并连接，如果只有一个设备可直接返回usbDeviceList.getOrNull(0)
 
