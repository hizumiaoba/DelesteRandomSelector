/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.ranfa.lib.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import lombok.NonNull;

/**
 * ワーカースレッド用のスレッドファクトリー。
 * 
 * @author Ranfa
 *
 *@since 4.0.0
 */
public class CountedThreadFactory implements ThreadFactory {

	/**
	 * スレッド識別用のsupplier
	 */
	private final Supplier<String> identifier;
	
	/**
	 * スレッドの作業内容の識別用文字列
	 */
	private String specifier;
	
	/**
	 * ワーカーカウント用のカウンター
	 */
	private final AtomicLong count = new AtomicLong(1);
	
	/**
	 * デーモン識別用のBoolean
	 */
	private final boolean isDaemon;
	
	/**
	 * 
	 */
	public CountedThreadFactory() {
		this(() -> "Default", "Thread");
	}
	
	public CountedThreadFactory(Supplier<String> identifier, String specifier) {
		this(identifier, specifier, true);
	}
	
	public CountedThreadFactory(Supplier<String> identifier, String specifier, boolean isDaemon) {
		this.identifier = identifier;
		this.specifier = specifier;
		this.isDaemon = isDaemon;
	}
	
	@Override
	public Thread newThread(@NonNull Runnable r) {
		final Thread thread = new Thread(r, identifier.get() + " " + specifier + "-Worker " + count.getAndIncrement());
		thread.setDaemon(isDaemon);
		return thread;
	}
}
