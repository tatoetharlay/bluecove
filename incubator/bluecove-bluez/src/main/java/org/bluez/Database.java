/**
 *  BlueCove - Java library for Bluetooth
 *  Copyright (C) 2007 Vlad Skarzhevskyy
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
package org.bluez;

import org.freedesktop.dbus.DBusInterface;
import org.bluez.Error;
import org.freedesktop.dbus.UInt32;
/**
 * SDP queries involves transactions between an SDP server and an SDP client.
 * The server maintains a list of service records that describe the
 * characteristics of services associated with the server.
 * <p>
 * The database interface provides methods to manage local service records(SDP
 * Server). All of the information about a service that is maintained by an SDP
 * server is contained within a single service record. A service record is
 * basically a list of service attributes. Each service attribute describes a
 * single characteristic of a service such as the service type, name,
 * description, ...
 * <p>
 * Client methods are available at Adapter interface. See
 * GetRemoteService?{handles, record} methods for more information how retrieve
 * remote service handles/records.
 * 
 * @author vlads
 * 
 */
public interface Database extends DBusInterface {
	
	/**
	 * This method registers a new service specified by its unique identifier.
	 * This is only needed for services that are not started through the
	 * Bluetooth daemon.
	 */

	void RegisterService(String identifier, String name, String description);

	/**
	 * This method unregisters a service specified by its unique identifier.
	 */
	void UnregisterService(String identifier);

	/**
	 * Adds a new service record and returns the assigned record handle.
	 */
	UInt32 AddServiceRecord(byte[] b) throws Error.InvalidArguments, Error.Failed;

	/**
	 * Adds a new service record and returns the assigned record handle.
	 */
	UInt32 AddServiceRecordFromXML(String record) throws Error.InvalidArguments, Error.Failed;

	/**
	 * Updates a given service record.
	 */
	void UpdateServiceRecord(UInt32 handle, byte[] sdprecord) throws Error.InvalidArguments, Error.NotAvailable,
			Error.Failed;

	/**
	 * Updates a given service record provided in the XML format.
	 * 
	 * @param handle
	 * @param record
	 */
	void UpdateServiceRecordFromXML(UInt32 handle, String sdprecordXML) throws Error.InvalidArguments,
			Error.NotAvailable, Error.Failed;

	/**
	 * Remove a service record identified by its handle.
	 * <p>
	 * It is only possible to remove service records that where added by the
	 * current connection.
	 * 
	 * @param handle
	 */
	void RemoveServiceRecord(UInt32 handle) throws Error.InvalidArguments, Error.NotAuthorized, Error.DoesNotExist,
			Error.Failed;

	/**
	 * This method gets called when a service wants to check if a remote device
	 * is authorized to perform some action. The authorization request is
	 * forwarded to an authorization agent.
	 * <p>
	 * The address parameter is the Bluetooth address of the remote device and
	 * the uuid is the identifier of the profile requesting the authorization.
	 * This parameter can also be left blank.
	 */
	void RequestAuthorization(String address, String uuid);

	/**
	 * This method cancels an authorization process requested by a previous call
	 * to RequestAuthorization(). The address and uuid parameters must match.
	 */
	void CancelAuthorizationRequest(String address, String uuid);


}