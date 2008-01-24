/**
 * BlueCove BlueZ module - Java library for Bluetooth on Linux
 *  Copyright (C) 2008 Mina Shokry
 *  Copyright (C) 2007 Vlad Skarzhevskyy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @version $Id$
 */
#define CPP__FILE "BlueCoveBlueZ_LocalDevice.cc"

#include "BlueCoveBlueZ.h"

#include <bluetooth/hci.h>
#include <bluetooth/hci_lib.h>
#include <sys/ioctl.h>

JNIEXPORT jint JNICALL Java_com_intel_bluetooth_BluetoothStackBlueZ_nativeGetDeviceID
(JNIEnv *env, jobject thisObject, jint id, jlong findLocalDeviceBTAddress) {
	bool findDevice = (id >=0) || (findLocalDeviceBTAddress > 0);
	if (findDevice) {
	    int s = socket(AF_BLUETOOTH, SOCK_RAW, BTPROTO_HCI);
	    if (s < 0) {
            throwBluetoothStateException(env, "Failed to create Bluetooth socket. [%d] %s", errno, strerror(errno));
            return 0;
        }
        hci_dev_list_req *dl;
	    hci_dev_req *dr;
	    dl = (hci_dev_list_req*)malloc(HCI_MAX_DEV * sizeof(*dr) + sizeof(*dl));
	    if (!dl) {
	        throwBluetoothStateException(env, "Out of memory");
	        close(s);
            return 0;
	    }
        dl->dev_num = HCI_MAX_DEV;
	    dr = dl->dev_req;
	    if (ioctl(s, HCIGETDEVLIST, dl) < 0) {
		    free(dl);
	    	close(s);
		    throwBluetoothStateException(env, "Failed to list Bluetooth devices. [%d] %s", errno, strerror(errno));
		    return 0;
	    }
	    int dev_id = -1;
	    int flag = HCI_UP;
	    for (int i = 0; i < dl->dev_num; i++, dr++) {
		    if (hci_test_bit(flag, &dr->dev_opt)) {
		        if (id == i) {
		            dev_id = dr->dev_id;
		            break;
		        }
		        if (findLocalDeviceBTAddress > 0) {
		            // Select device by address
		            int dd = hci_open_dev(dr->dev_id);
		            if (dd >= 0) {
		                bdaddr_t address;
			            hci_read_bd_addr(dd, &address, 1000);
			            hci_close_dev(dd);
			            if (deviceAddrToLong(&address) == findLocalDeviceBTAddress) {
			                dev_id = dr->dev_id;
		                    break;
			            }
			        }
		        }
		    }
		}

	    free(dl);
	    close(s);
	    if (dev_id < 0) {
	        throwBluetoothStateException(env, "Bluetooth Device is not found");
	    }
        return dev_id;
    } else {
	    int dev_id = hci_get_route(NULL);
	    if (dev_id < 0) {
	        debug("hci_get_route : %i", dev_id);
	        throwBluetoothStateException(env, "Bluetooth Device is not available");
	        return 0;
	    } else {
	        return dev_id;
        }
    }
}

JNIEXPORT jint JNICALL Java_com_intel_bluetooth_BluetoothStackBlueZ_nativeOpenDevice
(JNIEnv *env, jobject thisObject, jint deviceID) {
	int deviceDescriptor = hci_open_dev(deviceID);
	if (deviceDescriptor < 0) {
	    debug("hci_open_dev : %i", deviceDescriptor);
		throwBluetoothStateException(env, "HCI device open failed");
		return 0;
	}
	return deviceDescriptor;
}

JNIEXPORT void JNICALL Java_com_intel_bluetooth_BluetoothStackBlueZ_nativeCloseDevice
(JNIEnv *env, jobject thisObject, jint deviceDescriptor) {
	hci_close_dev(deviceDescriptor);
}

JNIEXPORT jlong JNICALL Java_com_intel_bluetooth_BluetoothStackBlueZ_getLocalDeviceBluetoothAddressImpl
(JNIEnv *env, jobject, jint deviceDescriptor) {
	bdaddr_t address;
	int error = hci_read_bd_addr(deviceDescriptor, &address, LOCALDEVICE_ACCESS_TIMEOUT);
	if (error != 0) {
	    switch (error) {
        case HCI_HARDWARE_FAILURE:
            throwBluetoothStateException(env, "Bluetooth Device is not available");
	    default:
	        throwBluetoothStateException(env, "Bluetooth Device is not ready. [%d] %s", errno, strerror(errno));
        }
	    return 0;
	}
	return deviceAddrToLong(&address);
}

JNIEXPORT jstring JNICALL Java_com_intel_bluetooth_BluetoothStackBlueZ_nativeGetDeviceName
(JNIEnv *env, jobject thisObject, jint deviceDescriptor) {
	char* name = (char*)malloc(DEVICE_NAME_MAX_SIZE);
	jstring nameString = NULL;
	if (!hci_local_name(deviceDescriptor, 100, name, LOCALDEVICE_ACCESS_TIMEOUT)) {
		nameString = env->NewStringUTF(name);
	}
	free(name);
	return nameString;
}

JNIEXPORT jint JNICALL Java_com_intel_bluetooth_BluetoothStackBlueZ_nativeGetDeviceClass
(JNIEnv *env, jobject thisObject, jint deviceDescriptor) {
	uint8_t deviceClass[3];
	if (!hci_read_class_of_dev(deviceDescriptor, deviceClass, LOCALDEVICE_ACCESS_TIMEOUT)) {
		return deviceClassBytesToInt(deviceClass);
	} else {
	    return 0xff000000;
	}
}

JNIEXPORT jint JNICALL Java_com_intel_bluetooth_BluetoothStackBlueZ_nativeSetLocalDeviceDiscoverable
(JNIEnv *env, jobject thisObject, jint deviceDescriptor, jint mode) {
	uint8_t lap[3];
	lap[0] = mode & 0xff;
	lap[1] = (mode & 0xff00)>>8;
	lap[2] = (mode & 0xff0000)>>16;
	return hci_write_current_iac_lap(deviceDescriptor, 1, lap, LOCALDEVICE_ACCESS_TIMEOUT);
}

JNIEXPORT jint JNICALL Java_com_intel_bluetooth_BluetoothStackBlueZ_nativeGetLocalDeviceDiscoverable
(JNIEnv *env, jobject thisObject, jint deviceDescriptor) {
	uint8_t lap[3];
	uint8_t num_iac;
	int error = hci_read_current_iac_lap(deviceDescriptor,&num_iac,lap,LOCALDEVICE_ACCESS_TIMEOUT);
    //M.S.	I don't know why to check for num_iac to be less than or equal to one but avetana to this.
	if ((error < 0) || (num_iac > 1)) {
		throwRuntimeException(env, "Unable to retrieve the local discovery mode. It may be because you are not root");
		return 0;
	}
	return (lap[0] & 0xff) | ((lap[1] & 0xff) << 8) | ((lap[2] & 0xff) << 16);
}

