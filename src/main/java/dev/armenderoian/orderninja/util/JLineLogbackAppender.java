package dev.armenderoian.orderninja.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import dev.armenderoian.orderninja.OrderNinja;

import java.util.Arrays;

public final class JLineLogbackAppender extends ConsoleAppender<ILoggingEvent> {
	@Override
	protected void append(ILoggingEvent eventObject) {
		if (!this.started) return;

		Arrays.stream(new String(encoder.encode(eventObject)).split("\n\r"))
				.forEach(OrderNinja.getConsole()::printAbove);
	}
}