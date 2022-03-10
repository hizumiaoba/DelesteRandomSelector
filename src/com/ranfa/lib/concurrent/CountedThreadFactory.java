package com.ranfa.lib.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import lombok.NonNull;

public class CountedThreadFactory implements ThreadFactory {

	private final Supplier<String> identifier;
	private final AtomicLong count = new AtomicLong(1);
	private final boolean isDaemon;
	
	public CountedThreadFactory() {
		this(() -> "Default", "Thread");
	}
	
	public CountedThreadFactory(Supplier<String> identifier, String specifier) {
		this(identifier, specifier, true);
	}
	
	public CountedThreadFactory(Supplier<String> identifier, String specifier, boolean isDaemon) {
		this.identifier = () -> identifier.get() + " " + specifier;
		this.isDaemon = isDaemon;
	}
	
	@Override
	public Thread newThread(@NonNull Runnable r) {
		final Thread thread = new Thread(r, identifier.get() + "-Worker " + count.getAndIncrement());
		thread.setDaemon(isDaemon);
		return thread;
	}
}
