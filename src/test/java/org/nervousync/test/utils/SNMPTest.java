package org.nervousync.test.utils;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.nervousync.beans.snmp.SNMPData;
import org.nervousync.beans.snmp.TargetHost;
import org.nervousync.commons.snmp.SNMPDataOperator;
import org.nervousync.test.BaseTest;
import org.nervousync.utils.DateTimeUtils;
import org.nervousync.utils.SNMPUtils;
import org.nervousync.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import java.io.IOException;
import java.util.*;

public final class SNMPTest extends BaseTest {

	@Test
    @Order(0)
	public void initialize() {
		this.logger.info("Initialize result: {}", SNMPUtils.initialize(1, 2000L));
	}

	@Test
    @Order(10)
	public void addMonitor() {
		//  Please config snmpd to enable current oid can read
		PDU getPDU = new PDU();
		getPDU.setType(PDU.GET);
		getPDU.addOID(new VariableBinding(new OID(".1.3.6.1.2.1.1.1.0")));
		getPDU.addOID(new VariableBinding(new OID(".1.3.6.1.2.1.1.5.0")));
		getPDU.addOID(new VariableBinding(new OID(".1.3.6.1.2.1.2.1.0")));
		getPDU.addOID(new VariableBinding(new OID(".1.3.6.1.4.1.2021.11.11.0")));
		getPDU.addOID(new VariableBinding(new OID(".1.3.6.1.2.1.25.3.3.1.2")));
		getPDU.addOID(new VariableBinding(new OID(".1.3.6.1.2.1.25.2.2.0")));
		getPDU.addOID(new VariableBinding(new OID(".1.3.6.1.4.1.2021.4.3.0")));
		PDU walkPDU = new PDU();
		walkPDU.setType(PDU.GETNEXT);
		walkPDU.addOID(new VariableBinding(new OID(".1.3.6.1.2.1.25.4.2.1.2")));
		walkPDU.addOID(new VariableBinding(new OID(".1.3.6.1.2.1.2.2.1.3")));
		walkPDU.addOID(new VariableBinding(new OID(".1.3.6.1.2.1.25.3.3.1.2")));
		walkPDU.addOID(new VariableBinding(new OID(".1.3.6.1.4.1.2021.9.1.6")));
		this.logger.info("Add monitor result: {}",
				SNMPUtils.getInstance().addMonitor(StringUtils.randomString(16),
						TargetHost.remote("192.168.166.51"), new OutputOperator(), getPDU, walkPDU));
	}

	@Test
    @Order(20)
	public void runningMonitor() throws InterruptedException {
		Thread.sleep(10000L);
	}

	@Test
    @Order(30)
	public void destroy() throws IOException {
		SNMPUtils.getInstance().destroy();
	}

	private static final class OutputOperator implements SNMPDataOperator {

		private final Logger logger = LoggerFactory.getLogger(this.getClass());

		@Override
		public void operateData(SNMPData snmpData) {
			this.logger.info("Identify key: {}, current time: {}", snmpData.getIdentifiedKey(),
					DateTimeUtils.formatDate(new Date(snmpData.getCurrentGMTTime()), DateTimeUtils.DEFAULT_ISO8601_PATTERN));
			Iterator<Map.Entry<String, String>> iterator = snmpData.iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, String> entry = iterator.next();
				this.logger.info("Data key: {}, value: {}", entry.getKey(), entry.getValue());
			}
		}
	}
}
