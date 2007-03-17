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

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import net.sf.bluecove.Logger.LoggerAppender;

public class BlueCoveTestCanvas extends Canvas implements CommandListener, LoggerAppender {

	static final Command exitCommand = new Command("Exit", Command.EXIT, 0);
	static final Command printStatsCommand = new Command("1-Print Stats", Command.ITEM, 1);
	
	static final Command startDiscoveryCommand = new Command("*-Discovery", Command.ITEM, 2);
	static final Command startClientCommand = new Command("2-Client Start", Command.ITEM, 2);
	static final Command stopClientCommand = new Command("3-Client Stop", Command.ITEM, 3);
	static final Command startServerCommand = new Command("5-Server Start", Command.ITEM, 4);
	static final Command stopServerCommand = new Command("6-Server Stop", Command.ITEM, 5);
	static final Command startSwitcherCommand = new Command("8-Switcher Start", Command.ITEM, 6);
	static final Command stopSwitcherCommand = new Command("9-Switcher Stop", Command.ITEM, 7);
	static final Command clearCommand = new Command("#-Clear", Command.ITEM, 8);
	
	private boolean showLogDebug = true;
	
	private int line;
	
	private int lineOffsetY;
	
	private int lineOffsetX;
	
	private Switcher switcher;
	
	private Vector logMessages = new Vector();
	
	private int errorCount = 0;
	
	private int logLine = 0;
	
	private int logScrollX;
	
	private int logVisibleLines = 0;
	
	private boolean logLastEvenVisible = true;
	
	public BlueCoveTestCanvas() {
		super();
		super.setTitle("BlueCove");
		
		addCommand(exitCommand);
		addCommand(startDiscoveryCommand);
		addCommand(startClientCommand);
		addCommand(stopClientCommand);
		addCommand(startServerCommand);
		addCommand(stopServerCommand);
		addCommand(printStatsCommand);
		addCommand(startSwitcherCommand);
		addCommand(stopSwitcherCommand);
		addCommand(clearCommand);
		setCommandListener(this);
		Logger.addAppender(this);
	}
	
	public int writeln(Graphics g, String s) {
		int h = (g.getFont().getHeight() + 1);
		int y = lineOffsetY + h * line;
		g.drawString(s, lineOffsetX, y, Graphics.LEFT | Graphics.TOP);
		line ++;
		return y + h;
	}
	
	protected void paint(Graphics g) {
		lineOffsetY = 0;
		lineOffsetX = 0;
		line = 0;
		int width = getWidth();
        int height = getHeight();

		g.setGrayScale(255);
		g.fillRect(0, 0, width, height);
		
		g.setColor(0);
		int lastY = writeln(g, "BlueCove Tester");

		line = 0;
		lineOffsetY = lastY;
		Font font = Font.getFont(Font.FACE_PROPORTIONAL,  Font.STYLE_PLAIN, Font.SIZE_SMALL);
		g.setFont(font);
		
		StringBuffer msg = new StringBuffer();
		msg.append("(");
		msg.append("srv:").append((Switcher.isRunningServer())?"on":"off").append(" ").append(Switcher.serverStartCount);
		msg.append(" cli:").append((Switcher.isRunningClient())?"on":"off").append(" ").append(Switcher.clientStartCount);
		msg.append(" X:").append((Switcher.isRunning())?"on":"off");
		msg.append(" dc:").append(TestResponderClient.discoveryCount);
		msg.append(" er:").append(errorCount);
		msg.append(")");
		lastY = writeln(g, msg.toString());
		
		int lineHeight = g.getFont().getHeight() + 1;
		logVisibleLines = (height - lastY ) / lineHeight;
		lineOffsetX = logScrollX;
		int logIndex = logLine;
		while (((lastY) < height) && (logIndex < logMessages.size())) {
			String message = (String)logMessages.elementAt(logIndex);
			lastY = writeln(g, message);
			logIndex ++;
		} 
		logLastEvenVisible = (logIndex == logMessages.size());
	}
	
	public void appendLog(int level, String message, Throwable throwable) {
		if (!showLogDebug && (level == Logger.DEBUG)) {
			return;
		}
		StringBuffer buf = new StringBuffer();
		switch (level) {
		case Logger.ERROR:
			errorCount ++;
			buf.append("e.");
			break;
		case Logger.WARN:
			buf.append("w.");
			break;
		case Logger.INFO:
			buf.append("i.");
			break;
		}
		buf.append(message);
		if (throwable != null) {
			buf.append(' ');
			String className = throwable.getClass().getName();
			buf.append(className.substring(className.lastIndexOf('.')));
			buf.append(':');
			buf.append(throwable.getMessage());
		}
		
		logMessages.addElement(buf.toString());
		
		int logMax = 1000;
		if (logMessages.size() >= logMax) {
			Vector newLogMessages = new Vector();
			for (int i = logMax - 5; i < logMax; i++) {
				newLogMessages.addElement(logMessages.elementAt(i));
			}
			logMessages = newLogMessages;
			logLine = 0;
		}
		
		if (logLastEvenVisible) {
			if (((logLine + logVisibleLines) < logMessages.size())) {
				setLogEndLine();
			}
			//BlueCoveTestMIDlet.display.flashBacklight(0);
			repaint();
		}
	}
	
	private void setLogEndLine() {
		logLine = logMessages.size() - logVisibleLines;
		if (logLine < 0) {
			logLine = 0;
		}
	}

	protected void keyPressed(int keyCode) {
		switch (keyCode) {
		case '1':
			printStats();
			break;
		case '4':
			logLine = 0;
			break;
		case '0':
			setLogEndLine();
			break;
		case '*':
			Switcher.startDiscovery();
			break;
		case '2':
			Switcher.startClient();
			break;
		case '3':
			Switcher.clientShutdown();
			break;
		case '5':
			Switcher.startServer();
			break;
		case '6':
			Switcher.serverShutdown();
			break;
		case '8':
			startSwitcher();
			break;
		case '9':
			stopSwitcher();
			break;
		case '#':
			clear();
			break;
		default:
			int action = getGameAction(keyCode);
			switch (action) {
			case UP:
				if (logLine > 0) {
					logLine--;
				}
				break;
			case DOWN:
				if ((logLine + logVisibleLines - 1) < logMessages.size()) {
					logLine++;
				}
				break;
			case RIGHT:
				if (logScrollX > -300) {
					logScrollX -= 20;
				}
				break;
			case LEFT:
				if (logScrollX < 0) {
					logScrollX += 20;
				}
				break;
			}
		}
		repaint();
	}
	

	private void stopSwitcher() {
		if (switcher != null) {
			switcher.shutdown();
			switcher = null;
		}
	}

	private void printStats() {
		Logger.info("--- discovery stats ---");
		int deviceCnt = 0;
		int deviceActiveCnt = 0;
		long activeDeadline = System.currentTimeMillis() - 1000 * 60 * 4;
		for (Enumeration iter = RemoteDeviceInfo.devices.elements(); iter.hasMoreElements();) {
			RemoteDeviceInfo dev = (RemoteDeviceInfo) iter.nextElement();
			deviceCnt ++;
			StringBuffer buf = new StringBuffer();
			buf.append(TestResponderClient.niceDeviceName(dev.remoteDevice.getBluetoothAddress()));
			buf.append(" dc:").append(dev.serviceDiscoveredCount);
			buf.append(" first:").append(Logger.timeToString(dev.serviceDiscoveredFirstTime));
			buf.append(" last:").append(Logger.timeToString(dev.serviceDiscoveredLastTime));
			Logger.info(buf.toString());
			buf = new StringBuffer();
			buf.append(" avg ddf:").append(dev.avgDiscoveryFrequencySec());
			buf.append(" sdf:").append(dev.avgServiceDiscoveryFrequencySec());
			buf.append(" ss:").append(dev.avgServiceSearchDurationSec());
			buf.append(" sss:").append(dev.serviceSearchSuccess()).append("%");
			if (dev.serviceDiscoveredLastTime > activeDeadline) {
				deviceActiveCnt ++;
				buf.append(" Active");
			} else {
				buf.append(" Down");
			}
			Logger.info(buf.toString());
		}
		StringBuffer buf = new StringBuffer();
		buf.append("all avg");
		buf.append(" srv:").append(TestResponderServer.avgServerDurationSec());
		buf.append(" di:").append(RemoteDeviceInfo.allAvgDeviceInquiryDurationSec());
		buf.append(" ss:").append(RemoteDeviceInfo.allAvgServiceSearchDurationSec());
		Logger.info(buf.toString());
		buf = new StringBuffer();
		buf.append("devices:").append(deviceCnt).append(" active:").append(deviceActiveCnt);
		Logger.info(buf.toString());
		Logger.info("-----------------------");
		setLogEndLine();
	}
	
	private void clear() {
		logMessages.removeAllElements();
		errorCount = 0;
		logLine = 0;
		logScrollX = 0;
		TestResponderClient.clear();
		Switcher.clear();
		RemoteDeviceInfo.clear();
		repaint();
	}
	
	private void startSwitcher() {
		if (switcher == null) {
			switcher = new Switcher();
		}
		if (!switcher.isRunning) {
			(switcher.thread = new Thread(switcher)).start();
		} else {
			BlueCoveTestMIDlet.message("Warn", "Switcher isRunning");
		}
	}
	
	public void commandAction(Command c, Displayable d) {
		if (c == exitCommand) {
			Switcher.clientShutdown();
			Switcher.serverShutdown();
			BlueCoveTestMIDlet.exit();
			return;
		} else if (c == printStatsCommand) {
			printStats();
		} else if (c == clearCommand) {
			 clear();
		} else if (c == startDiscoveryCommand) {
			Switcher.startDiscovery();
		} else if (c == startClientCommand) {
			Switcher.startClient();
		} else if (c == stopClientCommand) {
			Switcher.clientShutdown();
		} else if (c == stopServerCommand) {
			Switcher.serverShutdown();
		} else if (c == startServerCommand) {
			Switcher.startServer();
		} else if (c == startSwitcherCommand) {
			startSwitcher();
		}  else if (c == stopSwitcherCommand) {
			stopSwitcher();
		}
	}

}
