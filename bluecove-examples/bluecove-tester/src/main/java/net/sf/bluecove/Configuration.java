/**
 *  BlueCove - Java library for Bluetooth
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  @version $Id$
 */ 
package net.sf.bluecove;

import java.util.Hashtable;

/**
 * TODO Create an editable Form for this Configuarion settings.
 * 
 * @author vlads
 *
 */
public class Configuration {

	public static boolean discoverDevicesComputers = true;
	
	public static boolean discoverDevicesPhones = true;

	public static boolean searchOnlyBluecoveUuid = true;

	/**
	 * Limit connections to precompiled list of test devices.
	 */
	public static boolean discoverOnlyTestDevices = false;
	
	public static Hashtable testDeviceNames = null;

	public static boolean serverAcceptWhileConnected = false;

	public static boolean continuous = true;

	public static boolean continuousDiscovery = true;
	
	public static boolean testConnections = false;
	
	public static boolean testServiceAttributes = true;

	/**
	 * Apperantly Motorola Service Attribute String is not working.
	 */
	public static boolean testIgnoreNotWorkingServiceAttributes = true;

	public static boolean testServerForceDiscoverable = true;
	
    static {
		testDeviceNames = new Hashtable();
		testDeviceNames.put("00E003506231", "Nokia D1");
		testDeviceNames.put("00E0035046C1", "Nokia D2");
		testDeviceNames.put("0015A8DDF300", "Moto M1 v360");
		testDeviceNames.put("0050F2E8D4A6", "Desk MS");
		testDeviceNames.put("000D3AA5E36C", "Lapt MS");
		testDeviceNames.put("0020E027CE32", "Lapt HP");
		
        testDeviceNames.put("0017841C5A8F", "Moto L7");
        testDeviceNames.put("00123755AE71", "N 6265i (t)");
        testDeviceNames.put("0013706C93D3", "N 6682 (r)");
        testDeviceNames.put("0017005354DB", "M i870 (t)");
        testDeviceNames.put("001700F07CF2", "M i605 (t)");  
        
        testDeviceNames.put("001813184E8B", "SE W810i (r-ml)");
        
        
        testDeviceNames.put("00149ABD52E7", "M V551 A");
        testDeviceNames.put("00149ABD538D", "M V551 N");
        testDeviceNames.put("0007E05387E5", "Palm");
        
        testDeviceNames.put("000B0D1796FC", "GPS");
        testDeviceNames.put("000D3AA4F7F9", "My Keyboard");
        testDeviceNames.put("0050F2E7EDC8", "My Mouse 1");
        testDeviceNames.put("0020E03AC5B2", "bob1");
        testDeviceNames.put("000D88C03ACA", "bob2");
	}
}
