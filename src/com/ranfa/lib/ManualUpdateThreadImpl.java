package com.ranfa.lib;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManualUpdateThreadImpl implements Runnable {

	//Declare flag
	private static boolean flag = true;

	//Declare Executor service
	private Executor executor = Executors.newWorkStealingPool();
	private BiConsumer<ArrayList<Song>, ArrayList<Song>> updateConsumer = (list1, list2) -> {
		this.logger.info("Checking database updates...");
		if(list1.size() > list2.size()) {
			long time = System.currentTimeMillis();
			this.logger.info("{} Update detected.", (list1.size() - list2.size()));
			Scraping.writeToJson(list1);
			this.logger.info("Update completed in {} ms", (System.currentTimeMillis() - time));
			this.logger.info("Updated database size: {}", list1.size());
		} else {
			this.logger.info("database is up-to-date.");
		}
	};

	//Declare logger
	private Logger logger = LoggerFactory.getLogger(ManualUpdateThreadImpl.class);

	public ManualUpdateThreadImpl() {
		this.logger.info("ManualUpdateThread is now available.");
	}

	@Override
	public void run() {
		if (flag) {
			flag = false;
		}
		this.logger.info("Checking database updates...");
		CompletableFuture<ArrayList<Song>> webData = CompletableFuture.supplyAsync(Scraping::getWholeData, this.executor);
		CompletableFuture<ArrayList<Song>> localData = CompletableFuture.supplyAsync(Scraping::getFromJson, this.executor);
		try {
			this.updateConsumer.accept(webData.get(), localData.get());
		} catch (InterruptedException | ExecutionException e) {
			this.logger.warn("Update failed.", e);
		}
		flag = true;
	}

	public boolean getFlag() {
		return flag;
	}
}
