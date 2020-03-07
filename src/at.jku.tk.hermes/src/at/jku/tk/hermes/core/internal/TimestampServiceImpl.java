package at.jku.tk.hermes.core.internal;

/* Imports ********************************************************************/

import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.TimeInfo;

import at.jku.tk.hermes.core.TimestampService;

/* Class **********************************************************************/

public class TimestampServiceImpl implements TimestampService {

	/* Singleton **************************************************************/

	private static class Holder {
		private static final TimestampServiceImpl INSTANCE = new TimestampServiceImpl();
	}

	public static TimestampServiceImpl getInstance() {
		return Holder.INSTANCE;
	}

	/* Constants **************************************************************/

	private static final NTPUDPClient ntpClient = new NTPUDPClient();
	private static final long taskPeriod = 60 * 1000;

	/* Fields *****************************************************************/

	private long ntpOffset = 0;

	/* Constructors ***********************************************************/

	private TimestampServiceImpl() {
		// NTP
		ntpClient.setDefaultTimeout((int) (taskPeriod * 0.66));
		try { // NTP port may already be bound
			ntpClient.open(NtpV3Packet.NTP_PORT);
		} catch (Exception e) {
		}
		// task
		new Timer(true).scheduleAtFixedRate(
				new TimerTask() {
					@Override
					public void run() {
						try {
							TimeInfo info = ntpClient.getTime(InetAddress.getByName(ParameterServiceImpl.getInstance().getNtpTimeServer()));
							info.computeDetails();
							ntpOffset = info.getOffset() == null ? 0 : info.getOffset();
						} catch (Exception e) {
						}
					}
				},
				0,
				taskPeriod);
	}

	/* Methods ****************************************************************/

	/* Implementation */

	public long getAccurateTimestamp() {
		/*
		return System.currentTimeMillis() + ntpOffset;
		 */
		long timestamp = System.currentTimeMillis() + ntpOffset;
		return timestamp - timestamp % 100; // last 2 digits are not accurate, therefore cut them out
	}

}
